package not.forgot.again.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import not.forgot.again.model.entities.Category;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    Flowable<List<Category>> getAllCategories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> upsert(Category category);
}
