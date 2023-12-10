package com.djdjsn.emochat.ui.main.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.FriendItemBinding;

public class FriendAdapter extends ListAdapter<User, FriendAdapter.FriendViewHolder> {

    class FriendViewHolder extends RecyclerView.ViewHolder {

        private final FriendItemBinding binding;

        public FriendViewHolder(FriendItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imageViewChat.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemChatClick(position);
                }
            });

            binding.getRoot().setOnLongClickListener( v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemLongClicked(position);
                    return true;
                }
                return false;
            });
        }

        public void bind(User model) {
            binding.textViewFriendName.setText(model.getNickname());
        }
    }

    public interface OnItemSelectedListener {
        void onItemChatClick(int position);
        void onItemLongClicked(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;


    public FriendAdapter() {
        super(new DiffUtilCallback());
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FriendItemBinding binding = FriendItemBinding.inflate(layoutInflater, parent, false);
        return new FriendViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    }

}