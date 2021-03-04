package not.forgot.again.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    private transient final int localId;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "password")
    private final String password;

    @ColumnInfo(name = "email")
    private final String email;

    public User(int localId, String name, String password, String email) {
        this.localId = localId;
        this.name = name;
        this.password = password;
        this.email = email;
    }
    @Ignore
    public User(String name, String password, String email) {
        this.localId = 0;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getLocalId() {
        return localId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects
                .equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, email);
    }
}
