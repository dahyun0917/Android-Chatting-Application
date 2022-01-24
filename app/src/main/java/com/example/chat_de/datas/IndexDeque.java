package com.example.chat_de.datas;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.ListIterator;

// push:       O(1)
// pop:        Amortized O(1)
// getByIndex: O(1)

public class IndexDeque<T> extends AbstractList<T> implements Cloneable {
    @SuppressWarnings("unchecked")
    final private ArrayList<T>[] list = (ArrayList<T>[]) Array.newInstance(ArrayList.class, 2);

    public IndexDeque() {
        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();
    }

    public void pushFront(T data)   { list[0].add(data); }
    public void pushBack(T data)    { list[1].add(data); }
    public void appendFront(AbstractList<T> dataList) {
        if(dataList == null) {
            throw new NullPointerException();
        }

        for(int i = dataList.size() - 1; i >= 0; i--) {
            list[0].add(dataList.get(i));
        }
    }
    public void appendBack(AbstractList<T> dataList) {
        if(dataList == null) {
            throw new NullPointerException();
        }

        for(int i = 0; i < dataList.size(); i++) {
            list[1].add(dataList.get(i));
        }
    }

    public void popFront()  { pop(0); }
    public void popBack()   { pop(1); }
    private void pop(int where) {
        if(size() == 0) {
            throw new NullPointerException();
        }

        if(list[where].size() == 1) {
            list[where].remove(0);
            divide((where + 1) % 2);
        } else if(list[where].size() == 0) {
            if(list[(where + 1) % 2].size() == 1) {
                list[(where + 1) % 2].remove(0);
                return;
            }
            divide((where + 1) % 2);
            list[where].remove(list[where].size() - 1);
        } else {
            list[where].remove(list[where].size() - 1);
        }
    }
    private void divide(int where) {
        if(list[where].size() == 0) {
            return;
        }
        final int sz = list[where].size();
        final int half = sz / 2 + sz % 2;

        ListIterator<T> i = list[where].listIterator(half);
        list[(where + 1) % 2] = new ArrayList<>(sz);
        while (i.hasPrevious()) {
            list[(where + 1) % 2].add(i.previous());
        }

        i = list[where].listIterator(half);
        final ArrayList<T> replace = new ArrayList<>(sz);
        while(i.hasNext()) {
            replace.add(i.next());
        }
        list[where] = replace;
    }

    @Override
    public T get(int position) {
        if(position < 0 || position >= size()) {
            throw new IndexOutOfBoundsException();
        }

        if (position < list[0].size()) {
            return list[0].get(list[0].size() - position - 1);
        } else {
            return list[1].get(position - list[0].size());
        }
    }
    public T getFront() { return get(0); }
    public T getBack()  { return get(size() - 1); }
    @Override
    public int size()   { return list[0].size() + list[1].size(); }
    
    public void clear() {
        list[0].clear();
        list[1].clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

