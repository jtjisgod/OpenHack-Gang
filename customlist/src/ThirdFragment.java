package com.example.user.test5;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ThirdFragment extends Fragment {
    ListView listView;
    ListViewAdapter adapter;

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
        adapter = new ListViewAdapter();
        listView = (ListView)layout.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목1", "가수1") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album2),
                "제목2", "가수2") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album2),
                "제목3", "가수3") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album2),
                "제목4", "가수4") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목5", "가수5") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목6", "가수6") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목7", "가수7") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목8", "가수8") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목9", "가수9") ;
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.album1),
                "제목10", "가수10") ;
        return layout;
    }
}
