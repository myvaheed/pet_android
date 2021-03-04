package not.forgot.again.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import not.forgot.again.R;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.MainPresenter;
import not.forgot.again.presenters.iviews.IMainView;
import not.forgot.again.ui.adapters.RVTasksAdapter;


public class MainFragment extends BaseFragment implements IMainView {

    private MainPresenter presenter;

    private RecyclerView rvData;
    private RVTasksAdapter adapter;

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new RVTasksAdapter(presenter);

        rvData = view.findViewById(R.id.rvData);
        rvData.setAdapter(adapter);
        rvData.setLayoutManager(new LinearLayoutManager(requireContext()));

        FloatingActionButton fba = view.findViewById(R.id.fba);
        fba.setOnClickListener(v -> presenter.onAddNewTaskClicked());

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected MainPresenter getPresenter() {
        if (presenter == null) {
            presenter = new ViewModelProvider(this).get(MainPresenter.class);
        }
        return presenter;
    }

    @Override
    public void updatedData(List<Pair<Category, List<Task>>> categoriesWithTasks) {
        adapter.setData(categoriesWithTasks);
    }

    @Override
    public void navigateToAddTask() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_mainFragment_to_addTaskFragment);
    }

    @Override
    public void navigateToTask(Task task) {
        Bundle bundle = TaskFragment.getTaskAsBundle(task);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_mainFragment_to_taskFragment, bundle);
    }

    @Override
    public void showDialogConfirmationCheckTask(Task task) {
        new AlertDialog.Builder(getContext()).setTitle("Чек")
                .setMessage("Вы действительно завершили задачу " + task.getTitle() + " ?")
                .setCancelable(true)
                .setPositiveButton("Да", (dialog, which) -> {
                    dialog.dismiss();
                    presenter.confirmedDone(task);
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showLoading() {
        super.showLoading();
        rvData.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        rvData.setEnabled(true);
    }
}