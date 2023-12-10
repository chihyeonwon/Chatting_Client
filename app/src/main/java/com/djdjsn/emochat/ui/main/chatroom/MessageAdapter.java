package com.djdjsn.emochat.ui.main.chatroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.djdjsn.emochat.data.message.Message;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.MessageCounterpartItemBinding;
import com.djdjsn.emochat.databinding.MessageSelfItemBinding;
import com.djdjsn.emochat.utils.res.StringRes;

public class MessageAdapter extends ListAdapter<Message, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SELF = 0;
    private static final int VIEW_TYPE_COUNTERPART = 1;


    class MessageSelfViewHolder extends RecyclerView.ViewHolder {

        private final MessageSelfItemBinding binding;

        public MessageSelfViewHolder(MessageSelfItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message model) {

            binding.textViewContent.setVisibility(!model.getContent().isEmpty() ? View.VISIBLE : View.GONE);
            binding.textViewContent.setText(model.getContent());

            String strTime = StringRes.hourMinute(model.getCreated());
            binding.textViewHourMinute.setText(strTime);

            binding.imageViewEmoji.setVisibility(model.getEmojiUrl() != null ? View.VISIBLE : View.GONE);
            if (model.getEmojiUrl() != null) {
                glide.load(Uri.parse(model.getEmojiUrl())).into(binding.imageViewEmoji);
            }

            binding.imageViewIsRead.setAlpha(model.isRead() ? 0.9f : 0.2f);
        }
    }

    class MessageCounterpartViewHolder extends RecyclerView.ViewHolder {

        private final MessageCounterpartItemBinding binding;

        public MessageCounterpartViewHolder(MessageCounterpartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message model) {

            if (counterpart != null) {
                binding.textViewNickname.setText(counterpart.getNickname());
            }

            binding.textViewContent.setVisibility(!model.getContent().isEmpty() ? View.VISIBLE : View.GONE);
            binding.textViewContent.setText(model.getContent());

            String strTime = StringRes.hourMinute(model.getCreated());
            binding.textViewHourMinute.setText(strTime);

            binding.imageViewEmoji.setVisibility(model.getEmojiUrl() != null ? View.VISIBLE : View.GONE);
            if (model.getEmojiUrl() != null) {
                glide.load(Uri.parse(model.getEmojiUrl())).into(binding.imageViewEmoji);
            }
        }
    }


    public interface OnItemSelectedListener {
    }

    private OnItemSelectedListener onItemSelectedListener;
    private final RequestManager glide;
    private final User counterpart;
    private final String currentUid;


    public MessageAdapter(RequestManager glide, User counterpart, String currentUid) {
        super(new DiffUtilCallback());
        this.glide = glide;
        this.counterpart = counterpart;
        this.currentUid = currentUid;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // 뷰타입에 따라 다른 뷰홀더 생성

        if (viewType == VIEW_TYPE_SELF) {
            MessageSelfItemBinding binding = MessageSelfItemBinding.inflate(layoutInflater, parent, false);
            return new MessageSelfViewHolder(binding);
        } else {
            MessageCounterpartItemBinding binding = MessageCounterpartItemBinding.inflate(layoutInflater, parent, false);
            return new MessageCounterpartViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // 뷰타입에 따라 다른 바인드 메소드 호출

        if (holder instanceof MessageSelfViewHolder) {
            MessageSelfViewHolder viewHolder = (MessageSelfViewHolder) holder;
            viewHolder.bind(getItem(position));
        } else if (holder instanceof MessageCounterpartViewHolder) {
            MessageCounterpartViewHolder viewHolder = (MessageCounterpartViewHolder) holder;
            viewHolder.bind(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        // 댓글 작성자가 사용자 또는 상대인지의 여부에 따라 다른 뷰타입 지정

        Message message = getItem(position);
        if (message.getUid().equals(currentUid)) {
            return VIEW_TYPE_SELF;
        } else {
            return VIEW_TYPE_COUNTERPART;
        }
    }

    static class DiffUtilCallback extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    }

}