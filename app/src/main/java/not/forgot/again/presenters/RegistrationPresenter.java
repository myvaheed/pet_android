package not.forgot.again.presenters;

import not.forgot.again.R;
import not.forgot.again.model.entities.User;
import not.forgot.again.model.repositories.NFARepository;
import not.forgot.again.presenters.iviews.IRegistrationView;

public class RegistrationPresenter extends BasePresenter<IRegistrationView> {

    @Override
    public void onCreateUi(IRegistrationView iRegistrationView) {
        iRegistrationView.setTitle(R.string.create_acc);
    }

    public void onTvLoginClicked() {
        iView.popBackStack();
    }

    public void onBtnOnCreateClicked(String email, String name, String password, String passwordAgain) {
        runInBackground(() -> {
            runCallbackOnUi(() -> iView.registrationBegin());
            String error = "";
            if (email.isEmpty()) error += "E-mail can not be empty\n";
            if (name.isEmpty()) error += "Name can not be empty\n";
            if (password.isEmpty()) error += "Password can not be empty\n";
            if (!password.equals(passwordAgain)) error += "Passwords are not equal\n";
            if (!error.isEmpty()) {
                String finalError = error;
                runCallbackOnUi(() -> {
                    iView.registrationEnd();
                    iView.showMessage(finalError);
                });
                return;
            }
            User user = new User(name, password, email);
            NFARepository nfaRepository = getGlobalStore().getNfaRepository();
            nfaRepository.register(user)
                    .doOnSuccess(authorization -> {
                        runCallbackOnUi(() -> {
                            iView.registrationEnd();
                            iView.navigateToMain();
                        });
                    })
                    .doOnError(throwable -> {
                        runCallbackOnUi(() -> {
                            iView.registrationEnd();
                            iView.showMessage(throwable.getMessage());
                        });
                    })
                    .flatMapCompletable(authorization -> nfaRepository.upsert(user))
                    .onErrorComplete()
                    .blockingAwait();
        });
    }
}
