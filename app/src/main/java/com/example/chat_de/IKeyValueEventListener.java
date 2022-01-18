package com.example.chat_de;

public interface IKeyValueEventListener<K, V> {
    void eventListener(K key, V value);
}