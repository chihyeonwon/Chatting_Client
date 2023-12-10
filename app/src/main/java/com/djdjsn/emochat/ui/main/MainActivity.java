package com.djdjsn.emochat.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.databinding.ActivityMainBinding;
import com.djdjsn.emochat.services.ChatService;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private NavController navController;
    private PreferencesData preferencesData;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 바인딩을 실행한다
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 네비게이션 호스트 프래그먼트로부터 navController 를 확보한다
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            // navController 를 바텀 네비게이션 뷰와 연동한다
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        }

        // 채팅방에서는 바텀 네비게이션 뷰를 숨긴다
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.chatRoomFragment) {
                binding.bottomNavigationView.setVisibility(View.GONE);
            } else {
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        preferencesData = new PreferencesData(getApplication());

        preferencesData.getCurrentUidLive().observe(this, uid -> {
            if (uid == null) {
                finish();
            }
        });

        startChatService();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 뒤로가기 버튼을 눌렀을 때 navController 에게 전달하여 프래그먼트 전환에 처리되도록 한다
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onPause() {
        firebaseAuth.removeAuthStateListener(this);
        super.onPause();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getUid() == null) {
            preferencesData.setCurrentUid(null);
        }
    }

    private void startChatService() {
        Intent intent = new Intent(this, ChatService.class);
        stopService(intent);
        startForegroundService(intent);
    }
}