package com.example.codsofttodoapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoTaskAdapter extends RecyclerView.Adapter<ToDoTaskAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DataBaseHandler baseHandler;
    private MainActivity activity;

    public ToDoTaskAdapter(DataBaseHandler baseHandler, MainActivity activity) {
        this.baseHandler = baseHandler;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_task, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        baseHandler.openDatabase();

        final ToDoModel item = todoList.get(position);
        holder.tvTask.setText(item.getTask());

        int color;
        if (item.getStatus() == 1) {
            color = 0xFF008000; // Set text color to #008000 (green)
        } else {
            color = 0xFF000000; // Set text color to #000000 (black)
        }

        holder.tvTask.setTextColor(color);

        // Add a long-press listener to the LinearLayout
        holder.llTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Toggle the status from 0 to 1 and vice versa
                int newStatus = (item.getStatus() == 0) ? 1 : 0;
                baseHandler.updateStatus(item.getId(), newStatus);
                item.setStatus(newStatus); // Update the status in the model

                int newColor;
                if (newStatus == 1) {
                    newColor = 0xFF008000; // Set text color to #008000 (green)
                } else {
                    newColor = 0xFF000000; // Set text color to #000000 (black)
                }

                holder.tvTask.setTextColor(newColor);

                notifyDataSetChanged(); // Refresh the RecyclerView
                return true; // Consume the long-press event
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        baseHandler.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTask;
        LinearLayout llTask; // Add a reference to the LinearLayout

        ViewHolder(View view) {
            super(view);
            tvTask = view.findViewById(R.id.taskTextView);
            llTask = view.findViewById(R.id.taskLayout); // Initialize the LinearLayout reference
        }
    }
}
