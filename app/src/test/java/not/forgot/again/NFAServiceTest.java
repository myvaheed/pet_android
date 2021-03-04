package not.forgot.again;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.disposables.Disposable;
import not.forgot.again.data.network.ApiFactory;
import not.forgot.again.data.network.NFAService;
import not.forgot.again.data.network.responses.Authorization;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

public class NFAServiceTest {

    NFAService nfaService;
    //    User user = new User("sometester3221", "1234", "sometester3221@gmail.com");
    private User user = new User("Q", "q", "Q@gmail.com");

    @Before
    public void init() {
        nfaService = ApiFactory.getNFAService();
    }

    @Test
    public void registration() {
        Random random = new Random();
        String name = String.valueOf(random.nextInt(345)) + random.nextInt(12345);
        String email = String.valueOf(random.nextInt(3435)) + random.nextInt(12345);
        String password = String.valueOf(random.nextInt(3435)) + random.nextInt(12345);
        User user = new User(0, name, password, email);
        Authorization authorization = nfaService.register(user).blockingGet();
        Assert.assertTrue(authorization.getApi_token().length() > 0);
        System.out.println(authorization.getApi_token());
//
//        Authorization authorization = nfaService.register(user).blockingGet();
//        Assert.assertTrue(authorization.getApi_token().length() > 0);
//        System.out.println(authorization.getApi_token()); //ivDkOOGrDbZ17Miq9sqlxIXUTTXasUPoyYrTtklSGNtJHx2JqaSmVS2Ygn92
    }

    @Test
    public void login() {
        Authorization authorization = nfaService.login(user).blockingGet();
        Assert.assertTrue(authorization.getApi_token().length() > 0);
        Assert.assertEquals(authorization.getApi_token(), "lhKtahJKZ5PKPiTgj5YKrEtVdQP5qavDwJkIw8Dv0BtHhsKWxNKCwjX75c5o");
    }

    @Test
    public void priorities() {
        Authorization authorization = nfaService.login(user).blockingGet();
        nfaService = ApiFactory.recreate(authorization.getApi_token());
        List<Priority> priorityList = nfaService.getPriorities().blockingGet();

        priorityList.forEach(System.out::println);
        Assert.assertTrue(priorityList.size() > 0);
    }

    @Test
    public void categories() {
        Authorization authorization = nfaService.login(user).blockingGet();
        nfaService = ApiFactory.recreate(authorization.getApi_token());

//        Category category1 = new Category(3, "some new 5");
//        Category category2 = new Category(3, "some new 6");
//
//        Throwable err = nfaService.pushNewCategory(category1).blockingGet();
//        Assert.assertNull(err);
//        nfaService.pushNewCategory(category2).blockingGet();
//        Assert.assertNull(err);

        List<Category> categories = nfaService.getCategories().blockingGet();
        categories.forEach(System.out::println);
    }

    @Test
    public void tasks() {
        Authorization authorization = nfaService.login(user).blockingGet();
        nfaService = ApiFactory.recreate(authorization.getApi_token());

//        Task task1 = new Task("some task", "fsa", new Date(), true, new Category(51, 3, "sfd"), new Priority(3, 2, "", ""));
//        task1 = nfaService.pushNewTask(task1).blockingGet();
//        Assert.assertTrue(task1.getId() > 0);
//        for (int i = 773; i <= 775; ++i) {
//            nfaService.deleteTask(i).blockingAwait();
//        }
        Task task1 = new Task("some task", "1234rsad", new Date(), false, new Category(51, 3, "sfd"), new Priority(3, 2, "", ""));
        nfaService.updateTask(771, task1)
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .doOnSuccess(System.out::println)
                .ignoreElement()
                .blockingAwait();

        List<Task> tasks = nfaService.getTasks().blockingGet();
        tasks.forEach(System.out::println);
    }
}
