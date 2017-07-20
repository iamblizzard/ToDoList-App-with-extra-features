package com.example.root.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private List<Tasks> tasksList;
    private Context context;
    private android.view.ActionMode mActionMode;
    private Activity activity;
    private FragmentActivity fragmentActivity;

    View v,popup_layout;
    EditText userinput;

    static int position;
    static String task_name;
    static String s;
    static int pos;
    public static DateClass date = new DateClass();

    public TaskAdapter(List<Tasks> taskList, Context context, Activity activity) {

        this.tasksList = taskList;
        this.context = context;
        this.activity= activity;
        this.fragmentActivity = (FragmentActivity)activity;
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

            final android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {


                @Override
                public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                    return false;  //return false if nothing is done
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:

                            position = getAdapterPosition();
                            task_name = tasksList.get(position).get_taskname();
                            AlertDialog dialog = AskOption();
                            dialog.show();
                            mode.finish(); // Action picked, so close the CAB
                            return true;

                        case R.id.edit:

                            position = getAdapterPosition();
                            initiatePopupWindow();
                            mode.finish(); // Action picked, so close the CAB
                            return true;

                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode mode) {
                    mActionMode=null;
                }
            };


            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    position = getAdapterPosition();
                    AlertDialogView(v);
                }

            });

            view.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {

                    if (mActionMode != null) {
                    return false;
                    }

                    mActionMode = activity.startActionMode(mActionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });

        }

    }

    private void initiatePopupWindow() {
        try {
            final MyDBHandler dbHandler = new MyDBHandler(activity);
            final LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            popup_layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) activity.findViewById(R.id.popup_element));

            final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setView(popup_layout)
                    .setTitle("Edit Task")
                    .setPositiveButton("Save", null)
                    .setNeutralButton("Set Date", null)
                    .create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    userinput = (EditText) popup_layout.findViewById(R.id.userinput);
                    date.s = tasksList.get(position).get_date();
                    task_name = tasksList.get(position).get_taskname();
                    userinput.setText(task_name);
                    final int id=tasksList.get(position).get_id();

                    Button save = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    save.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            String input = userinput.getText().toString().trim();

                            if (input != null && !input.isEmpty()) {

                                Tasks task = new Tasks(input, date.s, tasksList.get(position).get_color(),
                                        tasksList.get(position).get_notify());
                                dbHandler.updateTask(task, task_name);
                                tasksList.remove(position);
                                tasksList.add(position, task);
                                notifyDataSetChanged();
                            }

                            alertDialog.dismiss();
                        }
                    });

                    Button date = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                    date.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            DialogFragment newFragment = new MainActivity.DatePickerFragment();
                            newFragment.show(fragmentActivity.getSupportFragmentManager(), "datePicker");
                        }
                    });
                }
            });

            alertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
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
