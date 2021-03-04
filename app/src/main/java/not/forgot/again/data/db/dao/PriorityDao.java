package not.forgot.again.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import not.forgot.again.model.entities.Priority;

@Dao
public interface PriorityDao {
    @Query("SELECT * FROM priority")
    Flowable<List<Priority>> getAllPriorities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable upsert(List<Priority> priorities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> upsert(Priority priority);
}
