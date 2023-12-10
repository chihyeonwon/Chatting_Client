package com.djdjsn.emochat.ui.main.chatroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.djdjsn.emochat.data.emoji.Emoji;
import com.djdjsn.emochat.databinding.EmojiItemBinding;
import com.djdjsn.emochat.utils.StorageUtils;

public class EmojiAdapter extends ListAdapter<Emoji, EmojiAdapter.EmojiViewHolder> {

    class EmojiViewHolder extends RecyclerView.ViewHolder {

        private final EmojiItemBinding binding;

        public EmojiViewHolder(EmojiItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Emoji model) {
            String imageUrl = StorageUtils.getImageUrl("emojis", model.getFileName());
            glide.load(Uri.parse(imageUrl)).into(binding.imageViewEmoji);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private RequestManager glide;


    public EmojiAdapter(RequestManager glide) {
        super(new DiffUtilCallback());
        this.glide = glide;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        EmojiItemBinding binding = EmojiItemBinding.inflate(layoutInflater, parent, false);
        return new EmojiViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Emoji> {

        @Override
        public boolean areItemsTheSame(@NonNull Emoji oldItem, @NonNull Emoji newItem) {
            return oldItem.getFileName().equals(newItem.getFileName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Emoji oldItem, @NonNull Emoji newItem) {
            return oldItem.equals(newItem);
        }
    }

}