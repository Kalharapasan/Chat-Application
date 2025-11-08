package com.example.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messageList;
    private DatabaseHelper db;
    private int currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, int currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.db = new DatabaseHelper(context);
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getUserId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        boolean isSent = message.getUserId() == currentUserId;

        // Set username
        holder.tvUsername.setText(message.getUsername());

        // Set content with edited indicator
        String content = message.getContent();
        if (message.isEdited()) {
            content += " (edited)";
        }
        holder.tvContent.setText(content);

        // Set timestamp
        holder.tvTimestamp.setText(message.getTimestamp());

        // Apply different styles for sent vs received messages
        if (isSent) {
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(R.color.message_sent_bg));
            holder.messageContainer.setGravity(android.view.Gravity.END);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.tvUsername.setTextColor(context.getResources().getColor(R.color.primary_blue));
        } else {
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(R.color.message_received_bg));
            holder.messageContainer.setGravity(android.view.Gravity.START);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.tvUsername.setTextColor(context.getResources().getColor(R.color.teal_700));
        }

        // Set click listeners for sent messages
        if (isSent) {
            holder.btnEdit.setOnClickListener(v -> showEditDialog(message, position));
            holder.btnDelete.setOnClickListener(v -> showDeleteDialog(message, position));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private void showEditDialog(Message message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        final EditText input = new EditText(context);
        input.setText(message.getContent());
        input.setPadding(50, 30, 50, 30);
        input.setHint("Enter new message");
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newContent = input.getText().toString().trim();
            if (!newContent.isEmpty()) {
                if (db.updateMessage(message.getId(), newContent)) {
                    message.setContent(newContent);
                    message.setEdited(true);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Message updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                context.getResources().getColor(R.color.primary_blue)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                context.getResources().getColor(R.color.text_gray)
        );
    }

    private void showDeleteDialog(Message message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (db.deleteMessage(message.getId())) {
                messageList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, messageList.size());
                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                context.getResources().getColor(R.color.delete_red)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                context.getResources().getColor(R.color.text_gray)
        );
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout messageContainer;
        CardView messageCard;
        TextView tvUsername, tvContent, tvTimestamp;
        ImageButton btnEdit, btnDelete;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageCard = itemView.findViewById(R.id.messageCard);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}