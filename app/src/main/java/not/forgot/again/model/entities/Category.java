package not.forgot.again.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "category")
public class Category extends BaseEntity implements Parcelable {

    @ColumnInfo(name = "name")
    private final String name;

    public Category(int id, int localId, String name) {
        super(id, localId);
        this.name = name;
    }

    @Ignore
    public Category(String name) {
        this.name = name;
    }

    @Ignore
    public Category(Category category) {
        this(category.getId(), category.getLocalId(), category.name);
    }

    public static Category copyWithNewId(int id, Category category) {
        return new Category(id, category.getLocalId(), category.name);
    }

    public static Category copyWithNewLocalId(int localId, Category category) {
        return new Category(category.getId(), localId, category.name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Category{" + "localId=" + getLocalId() + ", name='" + name + '\'' + ", id=" + getId() + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeInt(getLocalId());
        dest.writeString(name);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            int id = source.readInt();
            int localId = source.readInt();
            String name = source.readString();
            return new Category(id, localId, name);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
