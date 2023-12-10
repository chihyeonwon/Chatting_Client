package com.djdjsn.emochat.ui.main.chatlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.djdjsn.emochat.data.chat.ChatPreview;
import com.djdjsn.emochat.databinding.ChatPreviewItemBinding;
import com.djdjsn.emochat.utils.res.StringRes;

public class ChatPreviewAdapter extends ListAdapter<ChatPreview, ChatPreviewAdapter.ChatPreviewViewHolder> {

    class ChatPreviewViewHolder extends RecyclerView.ViewHolder {

        private final ChatPreviewItemBinding binding;

        public ChatPreviewViewHolder(ChatPreviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(ChatPreview model) {
            if (model.getRecentUser() != null) {
                binding.textViewCounterpartNickname.setText(model.getRecentUser().getNickname());
            }
            String content = model.getRecentMessageContent();
            if (!content.isEmpty()) {
                binding.textViewLastMessage.setText(content);
            } else {
                binding.textViewLastMessage.setText("이모티콘");
            }
            binding.textViewLastMessage.setAlpha(!content.isEmpty() ? 1.0f : 0.5f);

            binding.textViewDateTime.setText(StringRes.dateTime(model.getRecentMessageMillis()));
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;


    public ChatPreviewAdapter() {
        super(new DiffUtilCallback());
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public ChatPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatPreviewItemBinding binding = ChatPreviewItemBinding.inflate(layoutInflater, parent, false);
        return new ChatPreviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPreviewViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<ChatPreview> {

        @Override
        public boolean areItemsTheSame(@NonNull ChatPreview oldItem, @NonNull ChatPreview newItem) {
            return oldItem.getChatId().equals(newItem.getChatId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatPreview oldItem, @NonNull ChatPreview newItem) {
            return oldItem.equals(newItem);
        }
    }

}