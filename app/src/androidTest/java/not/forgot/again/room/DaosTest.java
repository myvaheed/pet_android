package not.forgot.again.room;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import not.forgot.again.data.db.Db;
import not.forgot.again.data.db.dao.SyncStatusDao;
import not.forgot.again.data.ds.LocalDS;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.data.sync.SyncStatus;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DaosTest {

    private Db db;
    private SyncStatusDao syncStatusDao;
    private LocalDS ds;

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation()
                .getContext(), Db.class).build();
        ds = new LocalDS(db);
        syncStatusDao = db.syncStatusDao();
    }

    @Test
    public void whenInsertUserThenReadTheSampleOne() {
        User user = new User("Name", "Sjdfs", "mkasdf@mail.com");
        Throwable err = ds.upsert(user).blockingGet();
        assertNull(err);
        User readUser = ds.getUser().blockingGet();
        assertEquals(user, readUser);
    }

    @Test
    public void whenInsertPrioritiesThenReadTheSample() {
        List<Priority> priorityList = EntitiesHelper.generatePriorities(10);
        Throwable err = ds.upsert(priorityList).blockingGet();
        assertNull(err);
        List<Priority> priorityReadList = ds.getAllPriorities().blockingFirst();
        List<Priority> finalPriorityList = priorityList;
        priorityList = IntStream.rangeClosed(1, priorityList.size())
                .mapToObj(i -> Priority.copyWithNewLocalId(i, finalPriorityList.get(i - 1)))
                .collect(Collectors.toList());
        assertEquals(priorityList, priorityReadList);
    }

    @Test
    public void whenInsertCategoriesThenReadTheSample() {
        List<Category> categoryList = EntitiesHelper.generateCategories(10);
        for (Category category : categoryList) {
            category = ds.upsert(category).blockingGet();
            assertNotNull(category);
        }

        List<Category> categoryReadList = ds.getAllCategories().blockingFirst();
        List<Category> finalCategoryList = categoryList;
        categoryList = IntStream.rangeClosed(1, categoryList.size())
                .mapToObj(i -> Category.copyWithNewLocalId(i, finalCategoryList.get(i - 1)))
                .collect(Collectors.toList());
        assertEquals(categoryReadList, categoryList);
    }

    @Test
    public void whenInsertTasksThenReadTheSample() {
        List<Task> tasksList = EntitiesHelper.generateTasks(3, 5, 10);

        for (Task task : tasksList) {
            Priority p = ds.upsert(task.getPriority()).blockingGet();
            Category c = ds.upsert(task.getCategory()).blockingGet();
            task = Task.copyWithCategory(task, c);
            task = Task.copyWithPriority(task, p);
            ds.upsert(task).blockingGet();
        }

        List<Task> tasksReadList = ds.getAllTasks().blockingFirst();
        List<Task> finalTasksList = tasksList;
        tasksList = IntStream.rangeClosed(1, tasksReadList.size())
                .mapToObj(i -> Task.copyWithLocalId(finalTasksList.get(i - 1), i))
                .collect(Collectors.toList());
        assertEquals(tasksReadList, tasksList);
    }

    @Test
    public void whenInsertSyncStatusThenReadTheSample() {
        List<Task> tasksList = EntitiesHelper.generateTasks(3, 5, 10);

        for (int i = 0; i < tasksList.size(); i++) {
            Task task = tasksList.get(i);
            Priority p = ds.upsert(task.getPriority()).blockingGet();
            Category c = ds.upsert(task.getCategory()).blockingGet();
            task = Task.copyWithCategory(task, c);
            task = Task.copyWithPriority(task, p);
            tasksList.set(i, ds.upsert(task).blockingGet());
        }

        List<SyncStatus> syncStatuses = EntitiesHelper.generateSyncStatuses(tasksList);
        for (SyncStatus status : syncStatuses) {
            Throwable err = syncStatusDao.upsert(status).blockingGet();
            assertNull(err);
        }
        List<SyncStatus> syncStatusesRead = syncStatusDao.getAllSyncStatus().blockingGet();
        assertEquals(syncStatuses, syncStatusesRead);
    }

    @After
    public void closeDb() {
        db.close();
    }
}
