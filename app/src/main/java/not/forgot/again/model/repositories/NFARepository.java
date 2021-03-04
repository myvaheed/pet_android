package not.forgot.again.model.repositories;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import not.forgot.again.data.network.responses.Authorization;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

public interface NFARepository {
    Single<Authorization> register(User user);

    Single<Authorization> login(User user);

    Single<User> getUser();

    Completable upsert(User user);

    Flowable<List<Category>> getAllCategories();

    Single<Category> upsert(Category category);

    Flowable<List<Priority>> getAllPriority();

    Completable upsert(List<Priority> priorities);

    Single<Priority> upsert(Priority priority);

    Flowable<List<Task>> getAllTasks();

    Single<Task> upsert(Task task);

    Completable delete(Task task);

    Completable sync();
}
