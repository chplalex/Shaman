package com.example.myapp.Start;

import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

public class MyMutableLiveData<T> extends MutableLiveData<T> {

    public void updateValue(Object value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setValue((T) value);
        } else {
            postValue((T) value);
        }

    }

}
