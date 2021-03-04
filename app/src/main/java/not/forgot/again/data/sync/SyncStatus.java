package not.forgot.again.data.sync;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import java.lang.reflect.Type;
import java.util.Objects;

import not.forgot.again.data.utils.ConvertersForDb;
import not.forgot.again.model.entities.BaseEntity;

@TypeConverters({ConvertersForDb.SyncStatusConverter.class, ConvertersForDb.ClassConverter.class})
@Entity(tableName = "sync_status", primaryKeys = {"entity_id", "type_entity"})
public class SyncStatus {

    public enum Status {
        NO_UPDATED, NO_DELETED;
    }

    @ColumnInfo(name = "entity_id")
    private final int entityId;

    @ColumnInfo(name = "status")
    private final Status status;

    @ColumnInfo(name = "type_entity")
    @NonNull
    private final Class<?> typeEntity;

    public SyncStatus(int entityId, Status status, Class<?> typeEntity) {
        this.entityId = entityId;
        this.status = status;
        this.typeEntity = typeEntity;
    }

    @Ignore
    public SyncStatus(BaseEntity entity, Status status) {
        this.entityId = entity.getLocalId();
        this.status = status;
        this.typeEntity = entity.getClass();
    }

    public int getEntityId() {
        return entityId;
    }

    public Status getStatus() {
        return status;
    }

    public Class<?> getTypeEntity() {
        return typeEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncStatus that = (SyncStatus) o;
        return entityId == that.entityId && status == that.status && typeEntity.equals(that.typeEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, status, typeEntity);
    }

    @Override
    public String toString() {
        return "SyncStatus{" + "entityId=" + entityId + ", status=" + status + ", typeEntity='" + typeEntity.getSimpleName() + '\'' + '}';
    }
}
