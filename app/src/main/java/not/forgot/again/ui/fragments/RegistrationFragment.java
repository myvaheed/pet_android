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
import not.forgot.again.presenters.RegistrationPresenter;
import not.forgot.again.presenters.iviews.IRegistrationView;

public class RegistrationFragment extends BaseFragment implements IRegistrationView {

    private RegistrationPresenter presenter;

    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private Button btnCreate;
    private TextView tvLogin;

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordAgain = view.findViewById(R.id.etPasswordAgain);
        btnCreate = view.findViewById(R.id.btnCreate);
        tvLogin = view.findViewById(R.id.tvLogin);

        btnCreate.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String name = etName.getText().toString();
            String password = etPassword.getText().toString();
            String passwordAgain = etPasswordAgain.getText().toString();
            presenter.onBtnOnCreateClicked(email, name, password, passwordAgain);
        });

        tvLogin.setOnClickListener(v -> presenter.onTvLoginClicked());

        return view;
    }

    @Override
    public void popBackStack() {
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack();
    }

    @Override
    public void registrationBegin() {
        showLoading();
        btnCreate.setEnabled(false);
        tvLogin.setEnabled(false);
    }

    @Override
    public void registrationEnd() {
        hideLoading();
        tvLogin.setEnabled(true);
        btnCreate.setEnabled(true);
    }

    @Override
    public void navigateToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_registrationFragment_to_mainFragment2);
    }



    @Override
    protected RegistrationPresenter getPresenter() {
        if (presenter == null)
            presenter = new ViewModelProvider(this).get(RegistrationPresenter.class);
        return presenter;
    }
}