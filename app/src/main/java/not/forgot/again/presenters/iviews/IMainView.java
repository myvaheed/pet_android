package not.forgot.again.presenters.iviews;

import android.util.Pair;

import java.util.List;

import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Task;

public interface IMainView extends IBaseView {
    void updatedData(List<Pair<Category, List<Task>>> categoriesWithTasks);
    void navigateToAddTask();
    void navigateToTask(Task task);
    void showDialogConfirmationCheckTask(Task task);
}
