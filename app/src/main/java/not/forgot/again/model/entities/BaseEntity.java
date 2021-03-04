package not.forgot.again.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class BaseEntity {
    @ColumnInfo(name = "id")
    private final int id;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    private final transient int localId;

    @Ignore
    public BaseEntity() {
        this.id = 0;
        this.localId = 0;
    }

    public BaseEntity(int id, int localId) {
        this.id = id;
        this.localId = localId;
    }

    public int getId() {
        return id;
    }

    public int getLocalId() {
        return localId;
    }

    public boolean isPushed() {
        return id != 0;
    }

    public boolean isFromInternet() {
        return localId == 0 && id != 0;
    };
}
