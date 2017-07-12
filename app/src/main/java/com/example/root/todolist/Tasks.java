package com.example.root.todolist;

public class Tasks {
    private int _id;
    private String _taskname;
    private String _date;
    private String _color;
    private int _notify;

    public Tasks(){

    }
    public Tasks(String taskName,String date,String color, int notify) {
        this._taskname = taskName;
        this._date = date;
        this._color = color;
        this._notify = notify;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_color(String color) {
        this._color = color;
    }

    public String get_taskname() {
        return _taskname;
    }

    public void set_taskname(String _taskname) {
        this._taskname = _taskname;
    }

    public String get_date() {
        return _date;
    }

    public String get_color() {
        return _color;
    }

    public int get_notify() {
        return _notify;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}