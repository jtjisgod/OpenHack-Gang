package com.example.user.test5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ThirdFragment extends Fragment {
    ListView listView;
    public ThirdFragment()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_third, container, false);

        ListView listView = (ListView)layout.findViewById(R.id.listview);
        final ArrayList<String> list = new ArrayList<>();

        for(int i=1;i<=50;i++){
            list.add("test"+i);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1,list
        );

        listView.setAdapter(adapter);
        return layout;
    }
}
