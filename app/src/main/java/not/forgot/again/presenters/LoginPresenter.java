package not.forgot.again.presenters;

import not.forgot.again.model.entities.User;
import not.forgot.again.model.repositories.NFARepository;
import not.forgot.again.presenters.iviews.ILoginView;

public class LoginPresenter extends BasePresenter<ILoginView> {

    public void onBtnLoginClicked(String email, String password) {
        runInBackground(() -> {
            runCallbackOnUi(() -> iView.loginBegin());
            String error = "";
            if (email.isEmpty()) error += "Email is empty\n";
            if (password.isEmpty()) error += "Password is empty\n";
            if (!error.isEmpty()) {
                String finalError = error;
                runCallbackOnUi(() -> {
                    iView.loginEnd();
                    iView.showMessage(finalError);
                });
                return;
            }
            User user = new User("", password, email);
            NFARepository nfaRepository = getGlobalStore().getNfaRepository();
            nfaRepository.login(user)
                    .doOnSuccess(authorization -> {
                        runCallbackOnUi(() -> {
                            iView.loginEnd();
                            iView.showMessage("Success");
                            iView.navigateToMain();
                        });
                    })
                    .doOnError(throwable -> {
                        runCallbackOnUi(() -> {
                            iView.loginEnd();
                            iView.showMessage(throwable.getMessage());
                        });
                    })
                    .flatMapCompletable(authorization -> nfaRepository.upsert(user))
//                    .ignoreElement()
                    .onErrorComplete()
                    .blockingAwait();
        });
    }

    public void onTvTRegistrationClicked() {
        iView.navigateToRegister();
    }
}
