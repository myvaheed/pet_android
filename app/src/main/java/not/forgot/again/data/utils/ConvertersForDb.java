package not.forgot.again.data.utils;

import androidx.room.TypeConverter;

import java.util.Date;

import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.data.sync.SyncStatus;

public class ConvertersForDb {
    public static class BooleanConverter {
        @TypeConverter
        public static Boolean toBoolean(Integer i) {
            return i != null && i != 0;
        }

        @TypeConverter
        public static Integer fromBoolean(Boolean bool) {
            return bool == null || !bool ? 0 : 1;
        }
    }

    public static class DateConverter {
        @TypeConverter
        public static Date toDate(Long dateLong) {
            return dateLong == null ? null : new Date(dateLong);
        }

        @TypeConverter
        public static Long fromDate(Date date) {
            return date == null ? null : date.getTime();
        }
    }

    public static class CategoryConverter {
        @TypeConverter
        public static Category toCategory(Integer localId) {
            return new Category(0, localId, "");
        }

        @TypeConverter
        public static Integer fromCategory(Category category) {
            return category.getLocalId();
        }
    }

    public static class PriorityConverter {
        @TypeConverter
        public static Priority toPriority(Integer id) {
            return new Priority(0, id, "", "");
        }

        @TypeConverter
        public static Integer fromPriority(Priority priority) {
            return priority.getLocalId();
        }
    }

    public static class SyncStatusConverter {
        @TypeConverter
        public static SyncStatus.Status toSyncStatus(Integer status) {
            return SyncStatus.Status.values()[status];
        }

        @TypeConverter
        public static Integer fromSyncStatus(SyncStatus.Status status) {
            return status.ordinal();
        }
    }

    public static class ClassConverter {
        @TypeConverter
        public static Class toClass(String name) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        @TypeConverter
        public static String fromClass(Class type) {
            return type.getName();
        }
    }
}
