package not.forgot.again.presenters;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.iviews.ITaskView;

public class TaskPresenter extends BasePresenter<ITaskView> {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Task task;

    public void gotTask(Task task) {
        this.task = task;
        String deadlineInString = simpleDateFormat.format(task.getDate());
        String done = task.isDone() ? "Done" : "Not done yet";
        String category = task.getCategory().getName();
        String priority = task.getPriority().getName();
        iView.fillUi(task.getTitle(), task.getDescription(), deadlineInString, done, category, priority);
    }

    public void btnEditTaskClicked() {
        iView.navigateToEditTask(task);
    }
}
