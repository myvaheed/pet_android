package not.forgot.again.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.Objects;

import not.forgot.again.data.utils.ConvertersForDb;

@TypeConverters({ConvertersForDb.DateConverter.class, ConvertersForDb.CategoryConverter.class, ConvertersForDb.PriorityConverter.class, ConvertersForDb.BooleanConverter.class})
@Entity(tableName = "task")
public class Task extends BaseEntity implements Parcelable {

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "description")
    private final String description;

    @ColumnInfo(name = "deadline")
    private final Date date;

    @ColumnInfo(name = "done")
    private final Boolean done;

    @ColumnInfo(name = "category_id")
    private final Category category;

    @ColumnInfo(name = "priority_id")
    private final Priority priority;

    public Task(int id, int localId, String title, String description, Date date, boolean done, Category category, Priority priority) {
        super(id, localId);
        this.title = title;
        this.description = description;
        this.date = date;
        this.done = done;
        this.category = category;
        this.priority = priority;
    }

    @Ignore
    public Task(String title, String description, Date date, boolean done, Category category, Priority priority) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.done = done;
        this.category = category;
        this.priority = priority;
    }

    public static Task copyWithId(Task task, int id) {
        return new Task(id, task.getLocalId(), task.title, task.description, task.date, task.done, new Category(task.category), new Priority(task.priority));
    }

    public static Task copyWithLocalId(Task task, int localId) {
        return new Task(task.getId(), localId, task.title, task.description, task.date, task.done, new Category(task.category), new Priority(task.priority));
    }

    public static Task copyWithCategory(Task task, Category category) {
        return new Task(task.getId(), task.getLocalId(), task.title, task.description, task.date, task.done, category, new Priority(task.priority));
    }

    public static Task copyWithPriority(Task task, Priority priority) {
        return new Task(task.getId(), task.getLocalId(), task.title, task.description, task.date, task.done, new Category(task.category), priority);
    }

    public static Task copyWithDone(Task task, boolean isDone) {
        return new Task(task.getId(), task.getLocalId(), task.title, task.description, task.date, isDone, new Category(task.category), new Priority(task.priority));
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public boolean isDone() {
        return done;
    }

    public Category getCategory() {
        return category;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return done == task.done && Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects
                .equals(date, task.date) && Objects.equals(category, task.category) && Objects.equals(priority, task.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, date, done, category, priority);
    }

    @Override
    public String toString() {
        return "Task{" + "localId=" + getLocalId() + ", title='" + title + '\'' + ", description='" + description + '\'' + ", date=" + date + ", done=" + done + ", category=" + category + ", priority=" + priority + ", id=" + getId() + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeInt(getLocalId());
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(done ? 1 : 0);
        dest.writeLong(date.getTime());
        dest.writeParcelable(category, flags);
        dest.writeParcelable(priority, flags);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            int id = source.readInt();
            int localId = source.readInt();
            String title = source.readString();
            String description = source.readString();
            boolean done = source.readInt() == 1;
            Date date = new Date(source.readLong());
            Category category = source.readParcelable(Category.class.getClassLoader());
            Priority priority = source.readParcelable(Priority.class.getClassLoader());
            return new Task(id, localId, title, description, date, done, category, priority);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
