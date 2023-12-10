package com.djdjsn.emochat.ui.main.chatlist;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.chat.ChatPreview;
import com.djdjsn.emochat.databinding.FragmentChatListBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;
    private ChatListViewModel viewModel;
    private NavController navController;


    public ChatListFragment() {
        super(R.layout.fragment_chat_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentChatListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);
        navController = Navigation.findNavController(view);

        binding.recyclerChatPreview.setHasFixedSize(true);
        ChatPreviewAdapter adapter = new ChatPreviewAdapter();
        binding.recyclerChatPreview.setAdapter(adapter);
        
        adapter.setOnItemSelectedListener(position -> {
            ChatPreview chatPreview = adapter.getCurrentList().get(position);
            viewModel.onChatClick(chatPreview);
        });
        
        viewModel.getChatPreviews().observe(getViewLifecycleOwner(), chatPreviews -> {
            if (chatPreviews != null) {
                adapter.submitList(chatPreviews);
                binding.textViewNoChats.setVisibility(chatPreviews.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            } else {
                Toast.makeText(requireContext(), "채팅 목록을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
            binding.progressBarChats.setVisibility(View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof ChatListViewModel.Event.NavigateToChatScreen) {
                String chatId = ((ChatListViewModel.Event.NavigateToChatScreen) event).chatId;
                navController.navigate(ChatListFragmentDirections.actionGlobalChatRoomFragment(chatId));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}