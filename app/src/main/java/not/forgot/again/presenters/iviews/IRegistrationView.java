package not.forgot.again.presenters.iviews;

public interface IRegistrationView extends IBaseView {
    void popBackStack();
    void registrationBegin();
    void registrationEnd();
    void navigateToMain();
}
