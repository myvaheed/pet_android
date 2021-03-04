package not.forgot.again.presenters.iviews;

import not.forgot.again.model.entities.Task;

public interface ITaskView extends IBaseView {
    void back();
    void fillUi(String title, String description, String deadlineInString, String done, String category, String priority);
    void navigateToEditTask(Task task);
}
