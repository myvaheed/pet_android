package not.forgot.again.presenters;

import not.forgot.again.presenters.iviews.IBaseView;

public class UiContextRunnable implements Runnable {
    private final IBaseView iBaseView;
    private final Runnable runnable;

    public UiContextRunnable(IBaseView iBaseView, Runnable runnable) {
        this.iBaseView = iBaseView;
        this.runnable = runnable;
    }


    public IBaseView getView() {
        return iBaseView;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
