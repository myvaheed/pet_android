package not.forgot.again.data.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import not.forgot.again.data.db.Db;
import not.forgot.again.data.db.dao.CategoryDao;
import not.forgot.again.data.db.dao.PriorityDao;
import not.forgot.again.data.db.dao.SyncStatusDao;
import not.forgot.again.data.db.dao.TaskDao;
import not.forgot.again.data.db.dao.UserDao;
import not.forgot.again.data.sync.SyncStatus;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

public class LocalDS implements DS {
    private UserDao userDao;
    private PriorityDao priorityDao;
    private CategoryDao categoryDao;
    private TaskDao taskDao;
    private SyncStatusDao syncStatusDao;
    private Db db;

    public LocalDS(Db db) {
        this.db = db;
        userDao = db.userDao();
        priorityDao = db.priorityDao();
        categoryDao = db.categoryDao();
        taskDao = db.taskDao();
        syncStatusDao = db.syncStatusDao();
    }

    public Single<List<SyncStatus>> getAllSyncStatus() {
        return syncStatusDao.getAllSyncStatus();
    }

    public Completable upsert(SyncStatus status) {
        return syncStatusDao.upsert(status);
    }

    public <T> T runInTransaction(Callable<T> callable) {
        return db.runInTransaction(callable);
    }

    public void runInTransaction(Runnable runnable) {
        db.runInTransaction(runnable);
    }

    public Completable delete(SyncStatus status) {
        return syncStatusDao.delete(status);
    }

    @Override
    public Single<User> getUser() {
        return userDao.getUser();
    }

    @Override
    public Completable upsert(User user) {
        return userDao.upsert(user);
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    @Override
    public Single<Category> upsert(Category category) {
        return categoryDao.upsert(category).map(aLong -> {
            int id = (int) ((long) aLong);
            return Category.copyWithNewLocalId(id, category);
        });
    }

    @Override
    public Flowable<List<Priority>> getAllPriorities() {
        return priorityDao.getAllPriorities();
    }

    @Override
    public Completable upsert(List<Priority> priorities) {
        return priorityDao.upsert(priorities);
    }


    @Override
    public Single<Priority> upsert(Priority priority) {
        return priorityDao.upsert(priority).map(aLong -> {
            int id = (int) ((long) aLong);
            return Priority.copyWithNewLocalId(id, priority);
        });
    }

    @Override
    public Flowable<List<Task>> getAllTasks() {
        return taskDao.getAllTasks()
                .flatMap(this::constructTasksWithCategories)
                .flatMap(this::constructTasksWithPriorities);
    }

    @Override
    public Single<Task> upsert(Task task) {
        return taskDao.upsert(task).map(aLong -> {
            int id = (int) ((long) aLong);
            return Task.copyWithLocalId(task, id);
        });
    }

    @Override
    public Completable delete(Task task) {
        return taskDao.delete(task);
    }

    private Flowable<List<Task>> constructTasksWithPriorities(List<Task> tasks) {
        return priorityDao.getAllPriorities().map(priorities -> {
            Map<Integer, Priority> map = new HashMap<>();
            for (Priority priority : priorities) {
                map.put(priority.getLocalId(), priority);
            }
            List<Task> newTasks = new ArrayList<>();
            for (Task task : tasks) {
                int priorityLocalId = task.getPriority().getLocalId();
                Task t = Task.copyWithPriority(task, map.get(priorityLocalId));
                newTasks.add(t);
            }
            return newTasks;
        });
    }

    private Flowable<List<Task>> constructTasksWithCategories(List<Task> tasks) {
        return categoryDao.getAllCategories().map(categories -> {
            Map<Integer, Category> map = new HashMap<>();
            for (Category category : categories) {
                map.put(category.getLocalId(), category);
            }
            List<Task> newTasks = new ArrayList<>();
            for (Task task : tasks) {
                int categoryLocalId = task.getCategory().getLocalId();
                Task t = Task.copyWithCategory(task, map.get(categoryLocalId));
                newTasks.add(t);
            }
            return newTasks;
        });
    }
}
