package not.forgot.again.presenters;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import not.forgot.again.data.GlobalStore;
import not.forgot.again.presenters.iviews.IBaseView;
import not.forgot.again.presenters.iviews.ILoginView;

public abstract class BasePresenter<T extends IBaseView> extends ViewModel {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private GlobalStore globalStore;
    protected T iView;

    private BehaviorSubject<UiContextRunnable> callbacks = BehaviorSubject.create();
    private ExecutorService backgroundService = Executors.newFixedThreadPool(5);
    private Thread mainUiThread;

    public void setGlobalStore(@NonNull GlobalStore globalStore) {
        this.globalStore = globalStore;
    }

    @NonNull
    protected GlobalStore getGlobalStore() {
        return globalStore;
    }

    public Flowable<UiContextRunnable> uiCallbacksFlow() {
        return callbacks.subscribeOn(Schedulers.io()).toFlowable(BackpressureStrategy.BUFFER);
    }

    protected void runInBackground(Runnable r) {
        backgroundService.execute(r);
    }

    protected synchronized void runCallbackOnUi(Runnable runnable) {
        UiContextRunnable uiContextRunnable = new UiContextRunnable(iView, runnable);
        if (mainUiThread == Thread.currentThread()) {
            callbacks.onNext(uiContextRunnable);
        } else {
            Completable.wrap(co -> {
                callbacks.onNext(new UiContextRunnable(iView, runnable) {
                    @Override
                    public void run() {
                        runnable.run();
                        co.onComplete();
                    }
                });
            }).onErrorComplete().blockingAwait();
        }
    }

    public void onCreateUi(T iView) {
        this.iView = iView;
        this.mainUiThread = Thread.currentThread();
    }
}
