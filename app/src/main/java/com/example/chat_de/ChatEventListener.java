package com.example.chat_de;

import java.util.ArrayList;

public interface ChatEventListener<T> {
    void eventListener(T item);
}