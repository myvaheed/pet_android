package not.forgot.again.presenters.iviews;

public interface IBaseView {
    void showLoading();
    void hideLoading();
    void setTitle(int titleId);
    void showMessage(String message);
}
