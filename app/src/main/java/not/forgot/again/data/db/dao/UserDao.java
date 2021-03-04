package not.forgot.again.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.Completable;
import io.reactivex.Single;
import not.forgot.again.model.entities.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    Single<User> getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable upsert(User user);
}
