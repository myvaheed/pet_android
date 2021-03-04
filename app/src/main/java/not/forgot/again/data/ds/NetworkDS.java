package not.forgot.again.data.ds;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import not.forgot.again.data.network.ApiFactory;
import not.forgot.again.data.network.NFAService;
import not.forgot.again.data.network.responses.Authorization;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

public class NetworkDS implements DS {
    private NFAService nfaService;

    public NetworkDS(NFAService nfaService) {
        this.nfaService = nfaService;
    }

    public Single<Authorization> register(User user) {
        return nfaService.register(user).map(authorization -> {
            nfaService = ApiFactory.recreate(authorization.getApi_token());
            return authorization;
        });
    }

    public Single<Authorization> login(User user) {
        return nfaService.login(user).map(authorization -> {
            nfaService = ApiFactory.recreate(authorization.getApi_token());
            return authorization;
        });
    }

    @Override
    public Single<User> getUser() {
        throw new RuntimeException("User is saved local only");
    }

    @Override
    public Completable upsert(User user) {
        throw new RuntimeException("User is saved local only");
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return nfaService.getCategories().toFlowable();
    }

    @Override
    public Single<Category> upsert(Category category) {
        return nfaService.pushNewCategory(category).map(categoryFromInternet -> {
            int id = categoryFromInternet.getId();
            return Category.copyWithNewId(id, category);
        });
    }

    @Override
    public Flowable<List<Priority>> getAllPriorities() {
        return nfaService.getPriorities().toFlowable();
    }

    @Override
    public Completable upsert(List<Priority> priorities) {
        throw new RuntimeException("No api for insertion or update priority");
    }

    @Override
    public Single<Priority> upsert(Priority priority) {
        throw new RuntimeException("No api for insertion or update priority");
    }

    @Override
    public Flowable<List<Task>> getAllTasks() {
        return nfaService.getTasks().toFlowable();
    }

    @Override
    public Single<Task> upsert(Task task) {
        Single<Task> resultTaskFromInternet;
        if (task.isPushed()) {
            resultTaskFromInternet = nfaService.updateTask(task.getId(), task);
        } else {
            resultTaskFromInternet = nfaService.pushNewTask(task);
        }
        return resultTaskFromInternet.map(taskFromInternet -> {
            int id = taskFromInternet.getId();
            return Task.copyWithId(task, id);
        });
    }

    @Override
    public Completable delete(Task task) {
        return nfaService.deleteTask(task.getId());
    }
}
