package com.djdjsn.emochat.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.databinding.ActivityAuthBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    private static boolean everLaunched = false;

    private ActivityAuthBinding binding;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 바인딩을 실행한다
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 첫 실행시 스플래시 화면을 보인다
        binding.splash.setVisibility(View.GONE);
        if (!everLaunched) {
            everLaunched = true;
            showSplash();
        }
        
        // 네비게이션 호스트 프래그먼트로부터 navController 를 확보한다
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        // 뒤로가기 버튼을 눌렀을 때 navController 에게 전달하여 프래그먼트 전환에 처리되도록 한다
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void showSplash() {

        binding.splash.setVisibility(View.VISIBLE);
        binding.splash.postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.splash.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            binding.splash.startAnimation(fadeOut);
        }, 3000);
    }
}