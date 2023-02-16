package com.example.bodysway.ui.resultat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResultatsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ResultatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Resultat fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}