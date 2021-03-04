package not.forgot.again.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "priority")
public class Priority extends BaseEntity implements Parcelable {

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "color")
    private String color;

    public Priority(int id, int localId, String name, String color) {
        super(id, localId);
        this.name = name;
        this.color = color;
    }

    @Ignore
    public Priority(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Ignore
    public Priority(Priority priority) {
        this(priority.getId(), priority.getLocalId(), priority.name, priority.color);
    }

    public static Priority copyWithNewLocalId(int localId, Priority priority) {
        return new Priority(priority.getId(), localId, priority.name, priority.color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return Objects.equals(name, priority.name) && Objects.equals(color, priority.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return "Priority{" + "localId=" + getLocalId() + ", name='" + name + '\'' + ", color='" + color + '\'' + ", id=" + getId() + '}';
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
        dest.writeString(color);
    }

    public static final Creator<Priority> CREATOR = new Creator<Priority>() {

        @Override
        public Priority createFromParcel(Parcel source) {
            int id = source.readInt();
            int localId = source.readInt();
            String name = source.readString();
            String color = source.readString();
            return new Priority(id, localId, name, color);
        }

        @Override
        public Priority[] newArray(int size) {
            return new Priority[size];
        }
    };
}
