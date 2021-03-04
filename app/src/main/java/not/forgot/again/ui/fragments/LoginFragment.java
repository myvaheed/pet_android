package not.forgot.again.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import not.forgot.again.R;
import not.forgot.again.presenters.LoginPresenter;
import not.forgot.again.presenters.iviews.ILoginView;


public class LoginFragment extends BaseFragment implements ILoginView {

    private TextView tvRegister;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;

    private LoginPresenter presenter;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected LoginPresenter getPresenter() {
        if (presenter == null)
            presenter = new ViewModelProvider(this).get(LoginPresenter.class);
        return presenter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        tvRegister = view.findViewById(R.id.tvLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        tvRegister.setOnClickListener(v -> {
            presenter.onTvTRegistrationClicked();
        });
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            presenter.onBtnLoginClicked(email, password);
        });

        return view;
    }

    @Override
    public void navigateToRegister() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_registrationFragment);
    }

    @Override
    public void navigateToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_mainFragment);
    }

    @Override
    public void loginBegin() {
        showLoading();
        btnLogin.setEnabled(false);
        tvRegister.setEnabled(false);
    }

    @Override
    public void loginEnd() {
        hideLoading();
        btnLogin.setEnabled(true);
        tvRegister.setEnabled(true);
    }
}