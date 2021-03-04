package not.forgot.again.presenters;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import not.forgot.again.R;
import not.forgot.again.data.GlobalStore;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.iviews.IAddOrEditTaskView;

public class AddOrEditTaskPresenter extends BasePresenter<IAddOrEditTaskView> {
    private GlobalStore globalStore;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private CompositeDisposable disposables = new CompositeDisposable();

    private CountDownLatch waitingForData = new CountDownLatch(2); //priority and category

    private List<Category> categories;
    private List<Priority> priorities;
    private Task task;

    @Override
    public void onCreateUi(IAddOrEditTaskView iView) {
        super.onCreateUi(iView);
        globalStore = getGlobalStore();
        if (disposables.size() == 0) { //not recreating Ui
            disposables.add(globalStore.getNfaRepository()
                    .getAllCategories()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(categories -> {
                        this.categories = categories;
                        runCallbackOnUi(() -> iView.fillSpinnerCategories(categories));
                        waitingForData.countDown();
                    }, throwable -> {
                        waitingForData.countDown();
                        runCallbackOnUi(() -> iView.showMessage(throwable.getMessage()));
                        runCallbackOnUi(iView::backToMain);
                    }));
            disposables.add(globalStore.getNfaRepository()
                    .getAllPriority()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(priorities -> {
                        this.priorities = priorities;
                        runCallbackOnUi(() -> iView.fillSpinnerPriorities(priorities));
                        waitingForData.countDown();
                    }, throwable -> {
                        waitingForData.countDown();
                        runCallbackOnUi(() -> iView.showMessage(throwable.getMessage()));
                        runCallbackOnUi(iView::backToMain);
                    }));
        } else {
            iView.fillSpinnerCategories(categories);
            iView.fillSpinnerPriorities(priorities);
        }

        if (iView.isEdit()) {
            iView.setTitle(R.string.edit_task);
        } else {
            iView.setTitle(R.string.add_task);
        }
    }

    public void gotTask(Task task) {
        this.task = task;
        runInBackground(() -> {
            try {
                waitingForData.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            String title = task.getTitle();
            String description = task.getDescription();
            String deadlineInString = simpleDateFormat.format(task.getDate());
            int categoryPos = categories.indexOf(task.getCategory());
            int priorityPos = priorities.indexOf(task.getPriority());
            runCallbackOnUi(() -> iView.fillUi(title, description, deadlineInString, categoryPos, priorityPos));
        });
    }

    public void onNewCategoryEntered(String name) {
        if (name.isEmpty()) {
            iView.showMessage("Name of category can not be empty");
            return;
        }
        Category category = new Category(name);
        runInBackground(() -> {
            Throwable err = globalStore.getNfaRepository().upsert(category).ignoreElement().blockingGet();
            if (err != null) {
                runCallbackOnUi(() -> iView.showMessage(err.getMessage()));
            }
        });
    }

    //return true if no error
    public boolean checkAndHandleError(String title, String description, Date deadline, Category category, Priority priority) {
        String error = "";
        if (title.isEmpty()) {
            error += "Title is empty\n";
        }
        if (description.isEmpty()) {
            error += "Description is empty\n";
        }
        if (deadline.before(new Date())) {
            error += "Deadline can not be before today\n";
        }
        if (category == null) {
            error += "Category is not chosen\n";
        }
        if (priority == null) {
            error += "Priority is not chosen\n";
        }
        if (error.isEmpty()) {
            return true;
        }
        String finalError = error;
        runCallbackOnUi(() -> iView.showMessage(finalError));
        return false;
    }

    public void onBtnSaveClicked(String title, String description, String deadlineInString, Category category, Priority priority) {
        Date deadline = null;
        try {
            deadline = simpleDateFormat.parse(deadlineInString);
        } catch (ParseException e) {
            iView.showMessage(e.getMessage());
            e.printStackTrace();
            return;
        }
        if (!checkAndHandleError(title, description, deadline, category, priority)) {
            return;
        }
        int localId = 0;
        int id = 0;
        if (iView.isEdit()) {
            localId = task.getLocalId();
            id = task.getId();
        }
        Task task = new Task(id, localId, title, description, deadline, false, category, priority);
        iView.showLoading();
        runInBackground(() -> {
            Throwable throwable = globalStore.getNfaRepository()
                    .upsert(task)
                    .ignoreElement()
                    .blockingGet();
            if (throwable == null) {
                runCallbackOnUi(() -> iView.hideLoading());
                runCallbackOnUi(() -> iView.backToMain());
            } else {
                runCallbackOnUi(() -> iView.showMessage(throwable.getMessage()));
            }
        });
    }

    public void onBtnDeleteTaskClicked() {
        iView.showLoading();
        runInBackground(() -> {
            try {
                waitingForData.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            Throwable throwable = globalStore.getNfaRepository().delete(task).blockingGet();
            if (throwable == null) {
                runCallbackOnUi(() -> iView.hideLoading());
                runCallbackOnUi(() -> iView.backToMain());
            } else {
                runCallbackOnUi(() -> iView.showMessage(throwable.getMessage()));
            }
        });
    }

    public void onSetDeadlineClicked() {
        iView.openDateTimePicker();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public void onDateTimePicked(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String dateInString = simpleDateFormat.format(calendar.getTime());
        iView.setDateInString(dateInString);
    }

    public void onBtnAddCategoryClicked() {
        iView.showDialogToAddNewCategory();
    }
}
