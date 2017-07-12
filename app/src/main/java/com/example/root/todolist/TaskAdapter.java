package com.example.root.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private List<Tasks> tasksList;
    private Context context;

    View v;

    static int position;
    static String task_name;
    static int pos;

    public TaskAdapter(List<Tasks> taskList, Context context) {

        this.tasksList = taskList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name,date;
        public RelativeLayout relativelayout;

        public MyViewHolder(final View view) {
            super(view);

            v=view;
            relativelayout = (RelativeLayout) view.findViewById(R.id.recycler_layout);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);

            view.setClickable(true);
            view.setFocusableInTouchMode(true);


            relativelayout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    position = getAdapterPosition();
                    AlertDialogView(v);
                }

            });

            view.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {

                    position = getAdapterPosition();
                    task_name = tasksList.get(position).get_taskname();
                    AlertDialog dialog = AskOption();
                    dialog.show();

                    return true;
                }
            });

        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_layout, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tasks task = tasksList.get(position);
        holder.name.setText(task.get_taskname());
        holder.date.setText(task.get_date());
        String s =task.get_color();
        switch (s) {

            case "medium_priority":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_priority)); break;
            case "high_priority":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.high_priority)); break;
            default:
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.low_priority)); break;
        }
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(context)

                .setTitle("Delete")
                .setMessage("Do you want to Delete Task\n" + Html.fromHtml("<b>"+String.valueOf(task_name)+"</b>"))
                .setIcon(R.drawable.delete_item)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        MyDBHandler dbhandler = new MyDBHandler(context);
                        dbhandler.deleteTask(task_name);

                        tasksList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,tasksList.size());

                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void AlertDialogView(View v) {

        final View view= v;
        final CharSequence[] items = { "Low Priority", "Medium Priority", "High Priority" };
        final String[] item = { "low_priority", "medium_priority", "high_priority" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Set your Priority");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int item) {
                        pos = item;
                    }
                });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MyDBHandler dbhandler = new MyDBHandler(context);

                String name = tasksList.get(position).get_taskname();
                String date = tasksList.get(position).get_date();
                int notify = tasksList.get(position).get_notify();
                switch (pos){

                    case 0: view.setBackgroundColor(ContextCompat.getColor(context, R.color.low_priority)); break;

                    case 1: view.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_priority)); break;

                    case 2: view.setBackgroundColor(ContextCompat.getColor(context, R.color.high_priority)); break;

                    default:
                }

                dbhandler.updateColor(item[pos], tasksList.get(position).get_taskname());
                tasksList.set(position, new Tasks(name, date, item[pos], notify));
                notifyDataSetChanged();

            }
        });

       /* builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT)
                        .show();
            }
        });*/

        AlertDialog alert = builder.create();
        alert.show();
    }

}
