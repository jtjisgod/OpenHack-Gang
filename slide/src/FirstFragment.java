package com.example.user.test5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FirstFragment extends Fragment{
    public FirstFragment()
    {

    }
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_first,container,false);
        return layout;
    }
}