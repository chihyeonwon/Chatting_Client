package com.djdjsn.emochat.ui.auth.register;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.FragmentRegisterBinding;
import com.djdjsn.emochat.utils.UiUtils;
import com.djdjsn.emochat.utils.res.StringRes;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;
    private NavController navController;

    private ActivityResultLauncher<String> smsPermissionLauncher;

    private CountDownTimer ticker;


    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smsPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (!result) {
                        Toast.makeText(requireContext(), "인증코드 발송을 위해서는 해당 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentRegisterBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        navController = Navigation.findNavController(view);

        // UI 에 리스너 부여
        UiUtils.setOnTextChangeListener(binding.editTextId, s -> viewModel.onIdChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextPassword, s -> viewModel.onPasswordChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextPasswordConfirm, s -> viewModel.onPasswordConfirmChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextNickname, s -> viewModel.onNicknameChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextPhone, s -> viewModel.onPhoneChanged(s));
        UiUtils.setOnTextChangeListener(binding.editTextCode, s -> viewModel.onVerificationCodeChanged(s));
        binding.buttonRegister.setOnClickListener(v -> viewModel.onRegisterClick());
        binding.buttonSendCode.setOnClickListener(v -> viewModel.onSendCodeClick());
        binding.buttonVerifyCode.setOnClickListener(v -> viewModel.onVerifyCodeClick());
        
        binding.labelPrivacyPolicy.setOnClickListener(v -> viewModel.onPrivacyPolicyClick());

        viewModel.getMillisRemainingForVerification().observe(getViewLifecycleOwner(), millis -> {
            if (millis == null) {
                binding.buttonVerifyCode.setText("인증하기");
            } else {
                int seconds = millis.intValue() / 1000;
                binding.buttonVerifyCode.setText(StringRes.secondsRemainingForVerification(seconds));
            }
            binding.buttonVerifyCode.setEnabled(millis != null);
            binding.editTextCode.setEnabled(millis != null);
        });

        viewModel.isVerificationDone().observe(getViewLifecycleOwner(), done -> {
            binding.buttonSendCode.setEnabled(!done);
            binding.buttonVerifyCode.setEnabled(!done);
            binding.editTextCode.setEnabled(!done);
        });

        viewModel.doAgreePrivacyPolicy().observe(getViewLifecycleOwner(), agree ->
                binding.checkBoxPrivacyPolicy.setChecked(agree));

        // 뷰모델에서 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof RegisterViewModel.Event.ShowGeneralMessage) {
                String message = ((RegisterViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                hideKeyboard(requireView());
            } else if (event instanceof RegisterViewModel.Event.ShowPrivacyPolicy) {
                showPrivacyPolicyDialog();
            } else if (event instanceof RegisterViewModel.Event.NavigateBackWithResult) {
                String id = ((RegisterViewModel.Event.NavigateBackWithResult) event).id;
                String password = ((RegisterViewModel.Event.NavigateBackWithResult) event).password;
                User userData = ((RegisterViewModel.Event.NavigateBackWithResult) event).userData;
                Bundle result = new Bundle();
                result.putString("id", id);
                result.putString("password", password);
                result.putSerializable("user_data", userData);
                getParentFragmentManager().setFragmentResult("register", result);
                navController.popBackStack();
            } else if (event instanceof RegisterViewModel.Event.HideKeyboard) {
                hideKeyboard(requireView());
            } else if (event instanceof RegisterViewModel.Event.SendVerificationCode) {
                String phone = ((RegisterViewModel.Event.SendVerificationCode) event).phone;
                String verificationCode = ((RegisterViewModel.Event.SendVerificationCode) event).verificationCode;
                sendSMS(phone, verificationCode);
            }
        });

        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
    }

    @Override
    public void onResume() {
        super.onResume();
        ticker = new CountDownTimer(86400000, 100) {
            @Override
            public void onTick(long l) {
                viewModel.onTick();
            }

            @Override
            public void onFinish() {
            }
        };
        ticker.start();
    }

    @Override
    public void onPause() {
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showPrivacyPolicyDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("개인정보처리방침")
                .setMessage("이 앱에서 회원이 제공하는 어떤 개인정보도 앱의 운용이 아닌 다른 목적으로 사용되지 않습니다")
                .setPositiveButton("동의함", (dialogInterface, i) -> viewModel.onPrivacyPolicyAgreed())
                .setNegativeButton("동의 안 함", null)
                .show();
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void sendSMS(String phone, String requestCode) {

        if (requireActivity().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String msg = "[EmoChat 회원가입] 인증코드는 '" + requestCode + "' 입니다";
            smsManager.sendTextMessage(phone, null, msg, null, null);
            viewModel.onVerificationCodeSent();
        } catch (Exception e) {
            e.printStackTrace();
            viewModel.onVerificationCodeNotSent();
        }
    }

}




