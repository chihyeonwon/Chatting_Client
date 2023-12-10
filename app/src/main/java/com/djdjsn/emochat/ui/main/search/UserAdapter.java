package com.djdjsn.emochat.ui.main.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.UserItemBinding;
import com.djdjsn.emochat.utils.res.StringRes;

import java.util.Locale;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final UserItemBinding binding;

        public UserViewHolder(UserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(User model) {
            String namePhone = String.format(Locale.getDefault(), "%s (%s)",
                    model.getNickname(), StringRes.phone(model.getPhone()));
            binding.textViewUserNamePhone.setText(namePhone);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;


    public UserAdapter() {
        super(new DiffUtilCallback());
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserItemBinding binding = UserItemBinding.inflate(layoutInflater, parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
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