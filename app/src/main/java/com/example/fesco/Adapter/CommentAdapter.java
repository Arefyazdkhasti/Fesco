package com.example.fesco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fesco.R;
import com.example.fesco.classes.Comment;
import com.example.fesco.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentsViewHolder> {


    Context context;
    List<Comment> comments = new ArrayList<>();
    private onCommentItemClicked onCommentItemClicked;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public CommentAdapter(Context context, onCommentItemClicked onCommentItemClicked) {
        this.context = context;
        this.onCommentItemClicked = onCommentItemClicked;
    }

    public void setFoods(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.title.setText(comment.getTitle());
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getContent());
        holder.date.setText(comment.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send intents
                Toast.makeText(context, comment.getTitle() + " Clicked", Toast.LENGTH_SHORT).show();
           //     onCommentItemClicked.onCommentClick(comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView username;
        TextView content;
        TextView date;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.comment_title);
            username=itemView.findViewById(R.id.comment_username);
            content=itemView.findViewById(R.id.comment_content);
            date=itemView.findViewById(R.id.comment_date);
        }

    }

    public interface onCommentItemClicked {
        void onCommentClick(Comment comment);
    }
}

