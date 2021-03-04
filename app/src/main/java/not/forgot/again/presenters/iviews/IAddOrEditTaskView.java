package not.forgot.again.presenters.iviews;

import java.util.List;

import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;

public interface IAddOrEditTaskView extends IBaseView {
    void fillSpinnerCategories(List<Category> categories);
    void fillSpinnerPriorities(List<Priority> priorities);
    void back();
    void backToMain();
    void openDateTimePicker();
    void setDateInString(String date);
    void showDialogToAddNewCategory();
    void fillUi(String title, String desciption, String deadlineInString, int categoryPos, int priorityPos);
    boolean isEdit();
}
