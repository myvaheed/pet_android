package not.forgot.again.ui.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import not.forgot.again.R;
import not.forgot.again.presenters.SplashPresenter;
import not.forgot.again.presenters.iviews.ISplashView;

public class SplashFragment extends BaseFragment implements ISplashView {

    public SplashFragment() {}

    @Override
    protected SplashPresenter getPresenter() {
        return new ViewModelProvider(this).get(SplashPresenter.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        assert (getActivity() != null);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        assert (getActivity() != null);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void navigateToLoginFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_splashFragment_to_loginFragment);
    }

    @Override
    public void navigateToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_splashFragment_to_mainFragment);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void setTitle(int titleId) {
    }
}