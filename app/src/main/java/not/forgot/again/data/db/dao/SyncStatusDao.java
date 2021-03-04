package not.forgot.again.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import not.forgot.again.data.sync.SyncStatus;

@Dao
public interface SyncStatusDao {
    @Query("SELECT * FROM sync_status")
    Single<List<SyncStatus>> getAllSyncStatus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable upsert(SyncStatus status);

    @Delete
    Completable delete(SyncStatus status);
}
