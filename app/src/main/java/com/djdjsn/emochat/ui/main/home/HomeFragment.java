package com.djdjsn.emochat.ui.main.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.chat.Chat;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.FragmentHomeBinding;
import com.djdjsn.emochat.utils.res.StringRes;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private NavController navController;


    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onBackClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHomeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        navController = Navigation.findNavController(view);

        binding.imageViewSearch.setOnClickListener(v -> viewModel.onSearchClick());

        binding.recyclerFriend.setHasFixedSize(true);
        FriendAdapter adapter = new FriendAdapter();
        binding.recyclerFriend.setAdapter(adapter);

        adapter.setOnItemSelectedListener(new FriendAdapter.OnItemSelectedListener() {
            @Override
            public void onItemChatClick(int position) {
                User friend = adapter.getCurrentList().get(position);
                viewModel.onChatClick(friend);
            }

            @Override
            public void onItemLongClicked(int position) {
                User friend = adapter.getCurrentList().get(position);
                viewModel.onDeleteFriendClick(friend);
            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser != null) {
                binding.textViewUserName.setText(currentUser.getNickname());
            }
        });

        viewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends != null) {
                adapter.submitList(friends);
                binding.textViewFriendsNumber.setText(StringRes.friendsNumber(friends.size()));
                binding.textViewNoFriends.setVisibility(friends.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            } else {
                Toast.makeText(requireContext(), "친구 목록을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
            binding.progressBarFriend.setVisibility(View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof HomeViewModel.Event.NavigateToSearchScreen) {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment());
            } else if (event instanceof HomeViewModel.Event.ShowGeneralMessage) {
                String message = ((HomeViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            } else if (event instanceof HomeViewModel.Event.ConfirmSignOut) {
                showConfirmSignOutDialog();
            } else if (event instanceof HomeViewModel.Event.ConfirmDeleteFriend) {
                User user = ((HomeViewModel.Event.ConfirmDeleteFriend) event).user;
                showConfirmDeleteFriendDialog(user);
            } else if (event instanceof HomeViewModel.Event.NavigateToChatRoom) {
                Chat chat = ((HomeViewModel.Event.NavigateToChatRoom) event).chat;
                navController.navigate(HomeFragmentDirections.actionGlobalChatRoomFragment(chat.getId()));
            }
        });

        getParentFragmentManager().setFragmentResultListener("search_fragment", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    User newUser = (User) result.getSerializable("user");
                    if (newUser != null) {
                        viewModel.onSearchResult(newUser);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showConfirmSignOutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialogInterface, i) -> viewModel.onSignOutConfirmed())
                .setNegativeButton("취소", null)
                .show();
    }

    public void showConfirmDeleteFriendDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("친구 삭제")
                .setMessage(user.getNickname() + "님을 친구에서 삭제하시겠습니까?")
                .setPositiveButton("삭", (dialogInterface, i) -> viewModel.onDeleteFriendConfirmed(user))
                .setNegativeButton("취소", null)
                .show();
    }

}










