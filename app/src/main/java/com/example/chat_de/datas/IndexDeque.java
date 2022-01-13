package com.example.chat_de.datas;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class IndexDeque<T> implements Cloneable {
    private ArrayList<T> prev;
    private ArrayList<T> next;
    
    public IndexDeque() {
        prev = new ArrayList<>();
        next = new ArrayList<>();
    }

    public void addFirst(T data) {
        prev.add(data);
    }
    public void addLast(T data) {
        next.add(data);
    }
    public void add(T data) {
        next.add(data);
    }
    //안전하지 않으니 사용에 주의
    public void popFront() {
        prev.remove(prev.size() - 1);
    }

    public T get(int position) {
        if (position < prev.size())
            return prev.get(prev.size() - position - 1);
        else
            return next.get(position - prev.size());
    }
    public T getFirst() {
        return prev.get(prev.size() - 1);
    }
    public T getLast() {
        return next.get(next.size() - 1);
    }
    public int size() {
        return prev.size() + next.size();
    }
    
    public void clear() {
        prev.clear();
        next.clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

