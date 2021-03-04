package not.forgot.again.ui.fragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.android.schedulers.AndroidSchedulers;
import not.forgot.again.AppDelegate;
import not.forgot.again.R;
import not.forgot.again.presenters.BasePresenter;
import not.forgot.again.presenters.iviews.IBaseView;
import not.forgot.again.ui.dialogs.LoadingDialog;

public abstract class BaseFragment extends Fragment implements IBaseView {
    private BasePresenter<? extends IBaseView> presenter;

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();
        presenter.setGlobalStore(AppDelegate.getGlobalStore());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BasePresenter) presenter).onCreateUi(this);
        LiveDataReactiveStreams.fromPublisher(presenter.uiCallbacksFlow()
                .observeOn(AndroidSchedulers.mainThread()))
                .observe(getViewLifecycleOwner(), runnable -> {
                    try {
                        if (runnable.getView() != null && runnable.getView() != BaseFragment.this) { //if fragment is recreated
                            return;
                        }
                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        runnable.run();
                    } finally {
                        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                });
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(requireView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        LoadingDialog.showDialog(requireActivity().getSupportFragmentManager());
    }

    @Override
    public void hideLoading() {
        LoadingDialog.dismissDialog();
    }

    @Override
    public void setTitle(int titleResId) {
        Toolbar toolbar = requireView().findViewById(R.id.toolbar);
        toolbar.setTitle(titleResId);
    }

    protected abstract BasePresenter<? extends IBaseView> getPresenter();
}
