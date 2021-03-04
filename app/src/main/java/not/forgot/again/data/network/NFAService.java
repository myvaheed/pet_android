package not.forgot.again.data.network;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import not.forgot.again.data.network.responses.Authorization;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NFAService {
    @POST("register")
    Single<Authorization> register(@Body User user);

    @POST("login")
    Single<Authorization> login(@Body User user);

    @GET("priorities")
    Single<List<Priority>> getPriorities();

    @GET("categories")
    Single<List<Category>> getCategories();

    @POST("categories")
    Single<Category> pushNewCategory(@Body Category category);

    @GET("tasks")
    Single<List<Task>> getTasks();

    @POST("tasks")
    Single<Task> pushNewTask(@Body Task task);

    @PATCH("tasks/{id}")
    Single<Task> updateTask(@Path("id") int id, @Body Task task);

    @DELETE("tasks/{id}")
    Completable deleteTask(@Path("id") int id);
}
