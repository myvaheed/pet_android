package not.forgot.again.data.impl_repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import not.forgot.again.data.ds.DS;
import not.forgot.again.data.ds.LocalDS;
import not.forgot.again.data.ds.NetworkDS;
import not.forgot.again.data.network.responses.Authorization;
import not.forgot.again.data.sync.SyncStatus;
import not.forgot.again.model.entities.BaseEntity;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;
import not.forgot.again.model.repositories.NFARepository;

public class NFARepositoryImpl implements NFARepository {
    private final LocalDS localDs;
    private final NetworkDS networkDs;
    private volatile boolean isNetworkAvailable;
    private Syncer syncer = new Syncer();
    private volatile boolean isSyncEnabled = false;

    public static class NetworkIsNotAvailable extends Exception {
        public NetworkIsNotAvailable(String action) {
            super("Network is not available to " + action);
        }
    }

    interface ISyncListener {
        void syncBegin();

        void syncEnd();
    }

    public NFARepositoryImpl(LocalDS localDs, NetworkDS networkDs, BehaviorSubject<Boolean> networkObserver) {
        this.localDs = localDs;
        this.networkDs = networkDs;
        isNetworkAvailable = networkObserver.getValue();
        Disposable disposable = networkObserver.subscribe(networkAvailable -> {
            isNetworkAvailable = networkAvailable;
            if (isNetworkAvailable && isSyncEnabled) {
                syncer.sync().observeOn(Schedulers.io()).onErrorComplete().blockingAwait();
            }
        });
    }

    public void setSyncEnabled(boolean enabled) {
        this.isSyncEnabled = enabled;
    }

    public void addSyncListener(ISyncListener iSyncListener) {
        this.syncer.iSyncListeners.add(iSyncListener);
    }

    public void remove(ISyncListener iSyncListener) {
        this.syncer.iSyncListeners.remove(iSyncListener);
    }

    @Override
    public Single<Authorization> register(User user) {
        if (!isNetworkAvailable) return Single.error(new NetworkIsNotAvailable("register"));
        return networkDs.register(user);
    }

    @Override
    public Single<Authorization> login(User user) {
        if (!isNetworkAvailable) return Single.error(new NetworkIsNotAvailable("login"));
        return networkDs.login(user);
    }

    @Override
    public Single<User> getUser() {
        return localDs.getUser();
    }

    @Override
    public Completable upsert(User user) {
        return localDs.upsert(user);
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return localDs.getAllCategories();
    }

    @Override
    public Single<Category> upsert(Category category) {
        return new SyncedModifier<Category>().upsertSynchronized(category);
    }

    @Override
    public Flowable<List<Priority>> getAllPriority() {
        return localDs.getAllPriorities();
    }

    @Override
    public Completable upsert(List<Priority> priorities) {
        List<CompletableSource> completableSources = new ArrayList<>();
        for (Priority priority : priorities) {
            completableSources.add(localDs.upsert(priority)
                    .flatMapCompletable(priority1 -> CompletableObserver::onComplete));
        }
        return Completable.merge(completableSources);
    }

    @Override
    public Single<Priority> upsert(Priority priority) {
        return new SyncedModifier<Priority>().upsertSynchronized(priority);
    }

    @Override
    public Flowable<List<Task>> getAllTasks() {
        return localDs.getAllTasks();
    }

    @Override
    public Single<Task> upsert(Task task) {
        return new SyncedModifier<Task>().upsertSynchronized(task);
    }

    @Override
    public Completable delete(Task task) {
        return new SyncedModifier<Task>().deleteSynchronized(task);
    }

    @Override
    public Completable sync() {
        return syncer.sync();
    }

    private class Syncer {
        private final List<ISyncListener> iSyncListeners = new ArrayList<>();
        private final Object mutex = new Object();

        Completable sync() {
            return Completable.fromRunnable(() -> {
                synchronized (mutex) {
                    //from local to internet
                    List<SyncStatus> syncStatuses = localDs.getAllSyncStatus().blockingGet();
                    //yes, it's not the best way to retrieve all data from db and network
                    //and there are many ways to optimize this moment,
                    //but, c'mon, it is not enterprise project)))

                    Map<Integer, Task> idToTask = new HashMap<>();
                    Map<Integer, Category> idToCategory = new HashMap<>();
                    Map<Integer, Priority> idToPriority = new HashMap<>();

                    List<Task> allLocalTasks = localDs.getAllTasks().blockingFirst();
                    List<Category> allLocalCategories = localDs.getAllCategories().blockingFirst();
                    List<Priority> allLocalPriorities = localDs.getAllPriorities().blockingFirst();

                    for (Task t : allLocalTasks) {
                        idToTask.put(t.getLocalId(), t);
                    }
                    for (Category c : allLocalCategories) {
                        idToCategory.put(c.getLocalId(), c);
                    }
                    for (Priority p : allLocalPriorities) {
                        idToPriority.put(p.getLocalId(), p);
                    }

                    List<CompletableSource> completableSources = new ArrayList<>(syncStatuses.size());
                    for (SyncStatus syncStatus : syncStatuses) {
                        int id = syncStatus.getEntityId();
                        Class type = syncStatus.getTypeEntity();
                        SyncStatus.Status status = syncStatus.getStatus();
                        BaseEntity entity;
                        if (type.equals(Task.class)) {
                            entity = idToTask.get(id);
                        } else {
                            entity = idToCategory.get(id);
                        }
                        switch (status) {
                            case NO_DELETED:
                                completableSources.add(networkDs.delete((Task) entity)
                                        .concatWith(localDs.delete(syncStatus))); //only task can be deleted
                                break;
                            case NO_UPDATED:
                                if (entity instanceof Task) { //only task and category can be upserted to internet
                                    completableSources.add(networkDs.upsert((Task) entity)
                                            .ignoreElement()
                                            .concatWith(localDs.delete(syncStatus)));
                                } else {
                                    completableSources.add(networkDs.upsert((Category) entity)
                                            .ignoreElement()
                                            .concatWith(localDs.delete(syncStatus)));
                                }
                                break;
                            default:
                                throw new RuntimeException("Status is not handled: " + status);
                        }
                    }
                    Completable.merge(completableSources).blockingAwait();

                    //from network to local
                    Set<Priority> allSetPriorities = new HashSet<>(allLocalPriorities);
                    Set<Category> allSetCategories = new HashSet<>(allLocalCategories);
                    Set<Task> allSetTasks = new HashSet<>(allLocalTasks);

                    List<Task> allNetworkTasks = networkDs.getAllTasks().blockingFirst();
                    List<Category> allNetworkCategories = networkDs.getAllCategories()
                            .blockingFirst();
                    List<Priority> allNetworkPriorities = networkDs.getAllPriorities()
                            .blockingFirst();

                    for (Priority p : allNetworkPriorities) {
                        if (!allSetPriorities.contains(p)) {
                            p = localDs.upsert(p).blockingGet();
                            allLocalPriorities.add(p);
                            idToPriority.put(p.getLocalId(), p);
                        }
                    }
                    for (Category c : allNetworkCategories) {
                        if (!allSetCategories.contains(c)) {
                            c = localDs.upsert(c).blockingGet();
                            allLocalCategories.add(c);
                            idToCategory.put(c.getLocalId(), c);
                        }
                    }
                    for (Task t : allNetworkTasks) {
                        if (!allSetTasks.contains(t)) {
                            int cFromDb = getLocalIdOfEntityFromNetwork(idToCategory, t.getCategory());
                            Category c = idToCategory.get(cFromDb);
                            t = Task.copyWithCategory(t, c);

                            int pFromDb = getLocalIdOfEntityFromNetwork(idToPriority, t.getPriority());
                            Priority p = idToPriority.get(pFromDb);
                            t = Task.copyWithPriority(t, p);

                            t = localDs.upsert(t).blockingGet();
                            allLocalTasks.add(t);
                            idToTask.put(t.getLocalId(), t);
                        }
                    }
                }
            }).doOnSubscribe(disposable -> {
                for (ISyncListener listener : iSyncListeners) {
                    listener.syncBegin();
                }
            }).doFinally(() -> {
                for (ISyncListener listener : iSyncListeners) {
                    listener.syncEnd();
                }
            }).subscribeOn(Schedulers.io());
        }
    }

    private int getLocalIdOfEntityFromNetwork(Map<Integer, ? extends BaseEntity> map, BaseEntity value) {
        for (Map.Entry<Integer, ? extends BaseEntity> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("There's no in map value: " + value);
    }

    private class SyncedModifier<T extends BaseEntity> {
        Single<T> upsertSynchronized(T entity) {
            boolean isFromInternet = entity.isFromInternet();
            if (isFromInternet) {
                return upsert(localDs, entity);
            } else {
                return Single.fromCallable(() -> localDs.runInTransaction(() -> {
                    T entityFromDb = upsert(localDs, entity).blockingGet();
                    SyncStatus.Status status = SyncStatus.Status.NO_UPDATED;
                    localDs.upsert(new SyncStatus(entityFromDb, status)).blockingAwait();
                    return entityFromDb;
                })).flatMap(entityFromDb -> observer -> {
                    if (isNetworkAvailable) {
                        try {
                            T entityFromInternet = upsert(networkDs, entityFromDb).blockingGet();
                            localDs.runInTransaction(() -> {
                                upsert(localDs, entityFromInternet).ignoreElement().blockingAwait();
                                localDs.delete(new SyncStatus(entityFromInternet, null))
                                        .blockingAwait();
                                observer.onSuccess(entityFromInternet);
                            });
                        } catch (Throwable throwable) {
                            observer.onError(throwable);
                        }
                    } else {
                        observer.onSuccess(entityFromDb);
                    }
                });
            }
        }

        Completable deleteSynchronized(T entity) {
            boolean isFromInternet = entity.isFromInternet();
            if (isFromInternet) {
                return delete(networkDs, entity);
            } else {
                return Single.fromCallable(() -> localDs.runInTransaction(() -> {
                    delete(localDs, entity).blockingAwait();
                    if (entity.isPushed()) {
                        localDs.upsert(new SyncStatus(entity, SyncStatus.Status.NO_DELETED))
                                .blockingAwait();
                    }
                    return entity;
                })).flatMapCompletable(entityDeletedDb -> observer -> {
                    if (isNetworkAvailable && entityDeletedDb.isPushed()) {
                        try {
                            delete(networkDs, entityDeletedDb).blockingAwait();
                            localDs.delete(new SyncStatus(entityDeletedDb, null)).blockingAwait();
                            observer.onComplete();
                        } catch (Throwable err) {
                            observer.onError(err);
                        }
                    } else {
                        observer.onComplete();
                    }
                });
            }
        }

        private <T extends BaseEntity, D extends DS> Single<T> upsert(D ds, T entity) {
            if (entity instanceof Task) return (Single<T>) ds.upsert((Task) entity);
            if (entity instanceof Category) return (Single<T>) ds.upsert((Category) entity);
            if (entity instanceof Priority) return (Single<T>) ds.upsert((Priority) entity);
            throw new RuntimeException("No more Entities type to upsert");
        }

        private <T extends BaseEntity, D extends DS> Completable delete(D ds, T entity) {
            if (entity instanceof Task) return (Completable) ds.delete((Task) entity);
            throw new RuntimeException("No more Entities type to delete");
        }
    }
}
