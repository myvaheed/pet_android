package not.forgot.again.presenters;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import not.forgot.again.model.entities.User;
import not.forgot.again.model.repositories.NFARepository;
import not.forgot.again.presenters.iviews.ISplashView;

public class SplashPresenter extends BasePresenter<ISplashView> {

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreateUi(ISplashView iSplashView) {
        runInBackground(() -> {
            NFARepository nfaRepository = getGlobalStore().getNfaRepository();
            User user = null;
            try {
                user = nfaRepository.getUser().toFuture().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (user == null) {
                runCallbackOnUi(iSplashView::navigateToLoginFragment);
                return;
            }
            nfaRepository.login(user).doOnSuccess(authorization -> {
                runCallbackOnUi(() -> {
                    iSplashView.showMessage("Online mode");
                    iSplashView.navigateToMain();
                });
            }).doOnError(throwable -> {
                runCallbackOnUi(() -> {
                    iSplashView.showMessage("Offline mode");
                    iSplashView.navigateToMain();
                });
            }).ignoreElement().onErrorComplete().blockingAwait();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

}
