package not.forgot.again.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import not.forgot.again.data.db.dao.CategoryDao;
import not.forgot.again.data.db.dao.PriorityDao;
import not.forgot.again.data.db.dao.SyncStatusDao;
import not.forgot.again.data.db.dao.TaskDao;
import not.forgot.again.data.db.dao.UserDao;
import not.forgot.again.data.utils.Constants;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.data.sync.SyncStatus;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.entities.User;

@Database(version = 1, entities = {User.class, Task.class, Priority.class, Category.class, SyncStatus.class})
public abstract class Db extends RoomDatabase {
    private static volatile Db INSTANCE = null;

    public abstract UserDao userDao();

    public abstract PriorityDao priorityDao();

    public abstract CategoryDao categoryDao();

    public abstract SyncStatusDao syncStatusDao();

    public abstract TaskDao taskDao();

    public static Db getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (Db.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, Db.class, Constants.DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
