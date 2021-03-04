package not.forgot.again.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import not.forgot.again.R;
import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.TaskPresenter;
import not.forgot.again.presenters.iviews.ITaskView;

public class TaskFragment extends BaseFragment implements ITaskView {

    private TaskPresenter presenter;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvCategory;
    private TextView tvPriority;
    private TextView tvDeadline;
    private TextView tvIsDone;
    private ImageView ivEditTask;

    private final static String KEY_TASK = "KEY_TASK";

    public static Bundle getTaskAsBundle(Task task) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_TASK, task);
        return bundle;
    }

    private Task extractTask(Bundle bundle) {
        return bundle.getParcelable(KEY_TASK);
    }

    public TaskFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvPriority = view.findViewById(R.id.tvPriority);
        tvDeadline = view.findViewById(R.id.tvDeadline);
        tvIsDone = view.findViewById(R.id.tvIsDone);
        ivEditTask = view.findViewById(R.id.ivEditTask);

        ivEditTask.setOnClickListener(v -> presenter.btnEditTaskClicked());

        Task task = extractTask(requireArguments());
        presenter.gotTask(task);
    }

    @Override
    protected TaskPresenter getPresenter() {
        if (presenter == null) {
            presenter = new ViewModelProvider(this).get(TaskPresenter.class);
        }
        return presenter;
    }

    @Override
    public void back() {
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack();
    }

    @Override
    public void fillUi(String title, String description, String deadlineInString, String done, String category, String priority) {
        tvTitle.setText(title);
        tvDescription.setText(description);
        tvDeadline.setText(deadlineInString);
        tvCategory.setText(category);
        tvPriority.setText(priority);
        tvIsDone.setText(done);
    }

    @Override
    public void navigateToEditTask(Task task) {
        Bundle bundle = EditTaskFragment.getTaskAsBundle(task);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_taskFragment_to_editTaskFragment, bundle);
    }

}