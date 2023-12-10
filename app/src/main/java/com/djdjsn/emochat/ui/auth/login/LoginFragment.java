package com.djdjsn.emochat.ui.auth.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.FragmentLoginBinding;
import com.djdjsn.emochat.ui.main.MainActivity;
import com.djdjsn.emochat.utils.UiUtils;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private NavController navController;


    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentLoginBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        navController = Navigation.findNavController(view);

        // UI 에 리스너 부여
        UiUtils.setOnTextChangeListener(binding.editTextId, s -> viewModel.onIdChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextPassword, s -> viewModel.onPasswordChanged(s));

        binding.buttonLogin.setOnClickListener(v -> viewModel.onLoginClicked());
        binding.buttonRegister.setOnClickListener(v -> viewModel.onRegisterClicked());

        // 앱 로그인 감지 시 메인 화면으로 이동
        viewModel.getCurrentUid().observe(getViewLifecycleOwner(), uid -> {
            if (uid != null) {
                startMainActivity();
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            NavDirections navDirections;
            if (event instanceof LoginViewModel.Event.ShowGeneralMessage) {
                String message = ((LoginViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                hideKeyboard(requireView());
            } else if (event instanceof LoginViewModel.Event.NavigateToRegisterScreen) {
                navDirections = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
                navController.navigate(navDirections);
            }
        });

        getParentFragmentManager().setFragmentResultListener("register", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    String id = result.getString("id");
                    String password = result.getString("password");
                    User userData = (User) result.getSerializable("user_data");
                    viewModel.onRegisterResult(id, password, userData);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onPause() {
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        super.onPause();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }

    private void startMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}