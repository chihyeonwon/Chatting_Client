package com.djdjsn.emochat.ui.main.chatroom;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.emoji.Emoji;
import com.djdjsn.emochat.databinding.FragmentChatRoomBinding;
import com.djdjsn.emochat.utils.UiUtils;
import com.djdjsn.emochat.utils.res.ColorRes;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatRoomFragment extends Fragment {

    private FragmentChatRoomBinding binding;
    private ChatRoomViewModel viewModel;
    private NavController navController;

    private CountDownTimer ticker;


    public ChatRoomFragment() {
        super(R.layout.fragment_chat_room);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentChatRoomBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        navController = Navigation.findNavController(view);

        UiUtils.setOnTextChangeListener(binding.editTextMessage, s -> viewModel.onCurrentMessageChanged(s));
        binding.fabSubmitMessage.setOnClickListener(v -> viewModel.onSubmitClick());

        binding.recyclerMessage.setHasFixedSize(true);

        // 메세지 표시
        viewModel.getCounterpartUser().observe(getViewLifecycleOwner(), counterpart -> {

            MessageAdapter messageAdapter = new MessageAdapter(Glide.with(this), counterpart, viewModel.getCurrentUid());
            binding.recyclerMessage.setAdapter(messageAdapter);

            viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
                if (messages != null) {
                    messageAdapter.submitList(messages);
                    // 마지막 메세지로 스크롤한다
                    if (!messages.isEmpty()) {
                        binding.recyclerMessage.postDelayed(() -> {
                            if (binding != null) {
                                binding.recyclerMessage.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                            }
                        }, 500);
                    }
                } else {
                    Toast.makeText(requireContext(), "메세지를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            });
            binding.progressBarMessages.setVisibility(View.INVISIBLE);
        });

        // 이모티콘 표시
        binding.recyclerEmoji.setHasFixedSize(true);
        EmojiAdapter emojiAdapter = new EmojiAdapter(Glide.with(this));
        binding.recyclerEmoji.setAdapter(emojiAdapter);

        emojiAdapter.setOnItemSelectedListener(position -> {
            Emoji emoji = emojiAdapter.getCurrentList().get(position);
            viewModel.onEmojiClick(emoji);
        });

        viewModel.getEmojis().observe(getViewLifecycleOwner(), emojis -> {
            if (emojis != null) {
                emojiAdapter.submitList(emojis);
            }
        });

        // 선택된 이모티콘 하이라이트
        viewModel.getSelectedEmoji().observe(getViewLifecycleOwner(), emoji -> {
            for (int i = 0; i < emojiAdapter.getCurrentList().size(); i++) {
                RecyclerView.ViewHolder viewHolder = binding.recyclerEmoji.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    CardView cardView = (CardView) (viewHolder.itemView);
                    cardView.setCardBackgroundColor(ColorRes.getEmojiCardColor());
                    if (emoji != null) {
                        Emoji cardEmoji = emojiAdapter.getCurrentList().get(i);
                        if (emoji.getFileName().equals(cardEmoji.getFileName())) {
                            cardView.setCardBackgroundColor(ColorRes.getEmojiCardColorHighlighted());
                        }
                    }
                }
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof ChatRoomViewModel.Event.ShowGeneralMessage) {
                String message = ((ChatRoomViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            } else if (event instanceof ChatRoomViewModel.Event.ClearMessageInput) {
                binding.editTextMessage.setText("");
                hideKeyboard(requireView());
            }
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
        viewModel.onResumeChat();
        ticker = new CountDownTimer(86400000, 1000) {
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
        viewModel.onPauseChat();
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
        super.onPause();
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}