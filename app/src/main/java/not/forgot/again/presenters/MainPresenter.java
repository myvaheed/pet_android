package not.forgot.again.presenters;

import android.util.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import not.forgot.again.R;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Task;
import not.forgot.again.model.repositories.NFARepository;
import not.forgot.again.presenters.iviews.IMainView;

public class MainPresenter extends BasePresenter<IMainView> {

    private CompositeDisposable disposables = new CompositeDisposable();
    private boolean isSyncedAtFirstAppear = false;
    private List<Pair<Category, List<Task>>> loadedData = new LinkedList<>();

    @Override
    public void onCreateUi(IMainView iView) {
        super.onCreateUi(iView);
        NFARepository nfaRepository = getGlobalStore().getNfaRepository();

        if (disposables.size() == 0) {
            Disposable disposable = nfaRepository.getAllTasks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(tasks -> {
                        loadedData.clear();
                        Map<Category, List<Task>> categoryListMap = new LinkedHashMap<>();
                        for (Task task : tasks) {
                            if (!categoryListMap.containsKey(task.getCategory())) {
                                categoryListMap.put(task.getCategory(), new LinkedList<>());
                            }
                            categoryListMap.get(task.getCategory()).add(task);
                        }
                        for (Map.Entry<Category, List<Task>> categoryListEntry : categoryListMap.entrySet()) {
                            loadedData.add(new Pair<>(categoryListEntry.getKey(), categoryListEntry.getValue()));
                        }
                        runCallbackOnUi(() -> iView.updatedData(loadedData));
                    }, throwable -> {
                        runCallbackOnUi(() -> iView.showMessage(throwable.getMessage()));
                    });
            disposables.add(disposable);
        } else {
            iView.updatedData(loadedData);
        }

        if (!isSyncedAtFirstAppear) {
            runInBackground(() -> {
                runCallbackOnUi(iView::showLoading);
                Throwable err = nfaRepository.sync().blockingGet();
                runCallbackOnUi(iView::hideLoading);
                if (err == null) {
                    runCallbackOnUi(() -> iView.showMessage("Success synced"));
                } else {
                    runCallbackOnUi(() -> iView.showMessage(err.getMessage()));
                }
                isSyncedAtFirstAppear = true;
            });
        }
        iView.setTitle(R.string.tasks);
    }


    public void onCheckedForTask(Task task) {
        iView.showDialogConfirmationCheckTask(task);
    }

    public void confirmedDone(Task task) {
        Task doneTask = Task.copyWithDone(task, true);
        iView.showLoading();

        runInBackground(() -> {
            Throwable err = getGlobalStore().getNfaRepository()
                    .upsert(doneTask)
                    .ignoreElement()
                    .blockingGet();
            runCallbackOnUi(() -> iView.hideLoading());
            if (err != null) {
                runCallbackOnUi(() -> iView.showMessage(err.getMessage()));
            }
        });
    }

    public void onCvTaskClicked(Task task) {
        iView.navigateToTask(task);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public void onAddNewTaskClicked() {
        iView.navigateToAddTask();
    }

}
