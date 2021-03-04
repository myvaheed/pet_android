package not.forgot.again.data.ds;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

public interface DS {
    //User
    Single<User> getUser();
    Completable upsert(User user);

    //Category
    Flowable<List<Category>> getAllCategories();
    Single<Category> upsert(Category category);

    //Priority
    Flowable<List<Priority>> getAllPriorities();
    Completable upsert(List<Priority> priorities);
    Single<Priority> upsert(Priority priority);

    //Task
    Flowable<List<Task>> getAllTasks();
    Single<Task> upsert(Task task);
    Completable delete(Task task);
}
