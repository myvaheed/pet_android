package not.forgot.again.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import not.forgot.again.model.entities.Task;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    Flowable<List<Task>> getAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> upsert(Task task);

    @Delete
    Completable delete(Task task);
}
