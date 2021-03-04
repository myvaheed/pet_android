package not.forgot.again.data;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import not.forgot.again.data.db.Db;
import not.forgot.again.data.db.dao.SyncStatusDao;
import not.forgot.again.data.ds.LocalDS;
import not.forgot.again.data.ds.NetworkDS;
import not.forgot.again.data.impl_repositories.NFARepositoryImpl;
import not.forgot.again.data.network.ApiFactory;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.User;

public class NFARepositoryImplTest {
    NFARepositoryImpl repository;
    private SyncStatusDao syncStatusDao;
    private BehaviorSubject<Boolean> networkObserver = BehaviorSubject.createDefault(true);
    private User user = new User("sometester3221", "1234", "sometester3221@gmail.com");
//    private User user = new User("Q", "q", "Q@gmail.com");

    @Before
    public void initRepository() {
        Db db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation()
                .getContext(), Db.class).build();
        LocalDS localDS = new LocalDS(db);
        NetworkDS networkDS = new NetworkDS(ApiFactory.getNFAService());
        repository = new NFARepositoryImpl(localDS, networkDS, networkObserver);
    }

//    @Test
//    public void insertNewCategory() {
//        repository.login(user).blockingGet();
//        CountDownLatch countDownLatch = new CountDownLatch(10);
//        Category category = new Category("To test5");
//        repository.getAllCategories()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(categories -> {
//                    categories.forEach(System.out::println);
//                    countDownLatch.countDown();
//                });
//        repository.upsert(category)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((category1, throwable) -> {
//                    System.out.println("fromUpsert:= " + category1.toString());
//                    countDownLatch.countDown();
//                });
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void sync() {
        repository.setSyncEnabled(true);
        repository.login(user).blockingGet();
        networkObserver.onNext(true);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        repository.getAllCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    categories.forEach(System.out::println);
                    countDownLatch.countDown();
                });
        repository.getAllPriority()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(priorities -> {
                    priorities.forEach(System.out::println);
                    countDownLatch.countDown();
                });
        repository.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tasks -> {
                    tasks.forEach(System.out::println);
                    countDownLatch.countDown();
                });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CountDownLatch countDownLatch2 = new CountDownLatch(3);
        networkObserver.onNext(true);
        try {
            countDownLatch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void closeRepository() {

    }
}
