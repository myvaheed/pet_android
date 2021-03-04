package not.forgot.again.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.List;

import not.forgot.again.R;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.AddOrEditTaskPresenter;
import not.forgot.again.presenters.iviews.IAddOrEditTaskView;
import not.forgot.again.ui.adapters.CustomSpinnerAdapter;

public class EditTaskFragment extends BaseFragment implements IAddOrEditTaskView {

    private final static String KEY_TASK = "KEY_TASK";
    public static Bundle getTaskAsBundle(Task task) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_TASK, task);
        return bundle;
    }

    private Task extractTask(Bundle bundle) {
        return bundle.getParcelable(KEY_TASK);
    }

    public EditTaskFragment() {
    }

    private AddOrEditTaskPresenter presenter;

    private EditText etTitle;
    private EditText etDescription;
    private EditText etDeadline;
    private Spinner spCategories;
    private Spinner spPriorities;
    private Button btnSave;
    private Button btnDelete;
    private ImageView ivCalendar;
    private ImageView ivAddCategory;

    private CustomSpinnerAdapter<Category> categorySpinnerAdapter;
    private CustomSpinnerAdapter<Priority> prioritySpinnerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_or_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etDeadline = view.findViewById(R.id.etDeadline);
        spCategories = view.findViewById(R.id.spCategories);
        spPriorities = view.findViewById(R.id.spPriorities);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);
        ivCalendar = view.findViewById(R.id.ivCalendar);
        ivAddCategory = view.findViewById(R.id.ivAddCategory);
        btnDelete.setVisibility(View.VISIBLE);

        ivCalendar.setOnClickListener(v -> presenter.onSetDeadlineClicked());
        ivAddCategory.setOnClickListener(v -> {
            presenter.onBtnAddCategoryClicked();
        });
        etDeadline.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) presenter.onSetDeadlineClicked();
        });
        etDeadline.setOnClickListener((v) -> {
            presenter.onSetDeadlineClicked();
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String deadlineInString = etDeadline.getText().toString();
            Priority priority = prioritySpinnerAdapter.getSelectedItem(spPriorities);
            Category category = categorySpinnerAdapter.getSelectedItem(spCategories);
            presenter.onBtnSaveClicked(title, description, deadlineInString, category, priority);
        });
        btnDelete.setOnClickListener(v -> presenter.onBtnDeleteTaskClicked());

        prioritySpinnerAdapter = new CustomSpinnerAdapter<>(requireContext(), "Priority");
        categorySpinnerAdapter = new CustomSpinnerAdapter<>(requireContext(), "Category");

        spCategories.setAdapter(categorySpinnerAdapter);
        spPriorities.setAdapter(prioritySpinnerAdapter);

        presenter.gotTask(extractTask(requireArguments()));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected AddOrEditTaskPresenter getPresenter() {
        if (presenter == null) {
            presenter = new ViewModelProvider(this).get(AddOrEditTaskPresenter.class);
        }
        return presenter;
    }

    @Override
    public void fillSpinnerCategories(List<Category> categories) {
        categorySpinnerAdapter.updateData(categories);
    }

    @Override
    public void fillSpinnerPriorities(List<Priority> priorities) {
        prioritySpinnerAdapter.updateData(priorities);
    }

    @Override
    public void back() {
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack();
    }

    @Override
    public void backToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack();
        navController.popBackStack();
    }

    @Override
    public void openDateTimePicker() {
        DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            presenter.onDateTimePicked(year, monthOfYear, dayOfMonth);
        }).show(getParentFragmentManager(), null);
    }

    @Override
    public void setDateInString(String date) {
        etDeadline.setText(date);
    }

    @Override
    public void showDialogToAddNewCategory() {
        EditText editText = new EditText(requireContext());
        new AlertDialog.Builder(requireContext()).setTitle("New category")
                .setMessage("Enter name")
                .setView(editText)
                .setPositiveButton("Ok", (dialog, which) -> {
                    presenter.onNewCategoryEntered(editText.getText().toString());
                    dialog.dismiss();
                })
                .setCancelable(true)
                .create()
                .show();
    }

    @Override
    public void fillUi(String title, String description, String deadlineInString, int categoryPos, int priorityPos) {
        etTitle.setText(title);
        etDescription.setText(description);
        etDeadline.setText(deadlineInString);
        spCategories.setSelection(categoryPos + 1); //+1 offset for hint
        spPriorities.setSelection(priorityPos + 1);
    }

    @Override
    public boolean isEdit() {
        return true;
    }

    @Override
    public void setTitle(int titleResId) {
        super.setTitle(titleResId);
        Toolbar toolbar = requireView().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> back());
    }
}