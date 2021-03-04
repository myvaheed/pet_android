package not.forgot.again.room;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.data.sync.SyncStatus;
import not.forgot.again.model.entities.Task;

public class EntitiesHelper {
    public static List<Priority> generatePriorities(int num) {
        return IntStream.range(0, num)
                .mapToObj(value -> new Priority(String.valueOf(value), String.valueOf(-value)))
                .collect(Collectors.toList());
    }

    public static List<Category> generateCategories(int num) {
        return IntStream.range(0, num)
                .mapToObj(value -> new Category(String.valueOf(value)))
                .collect(Collectors.toList());
    }

    public static List<Task> generateTasks(int numPriorities, int numCategories, int num) {
        List<Priority> priorities = generatePriorities(numPriorities);
        List<Category> categories = generateCategories(numCategories);
        Random random = new Random();
        return IntStream.range(0, num).mapToObj(value -> {
            String title = String.valueOf(random.nextInt(343)) + random.nextInt(123);
            String description = String.valueOf(random.nextInt(123454)) + random.nextInt(2321);
            Date date = new Date();
            boolean done = random.nextBoolean();
            Category category = categories.get(random.nextInt(categories.size()));
            Priority priority = priorities.get(random.nextInt(priorities.size()));
            return new Task(title, description, date, done, category, priority);
        }).collect(Collectors.toList());
    }

    public static List<SyncStatus> generateSyncStatuses(List<Task> tasks) {
        Random random = new Random();
        return tasks.stream().map(task -> {
            SyncStatus.Status status = SyncStatus.Status.values()[random.nextInt(SyncStatus.Status.values().length)];
            return new SyncStatus(task, status);
        }).collect(Collectors.toList());
    }
}
