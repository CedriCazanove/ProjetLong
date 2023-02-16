package com.example.bodysway.ui.acquisition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcquitisionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AcquitisionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Acquisition fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}