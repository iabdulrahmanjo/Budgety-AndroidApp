package com.example.android.budgety;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Privacy extends Fragment {

    public Privacy() {

        // Required empty public constructor
    }




    public static Privacy newInstance() {

        Privacy fragment = new Privacy();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_privacy, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_Privacy);
        toolbar.setTitle(null);
        toolbar.setNavigationIcon(R.drawable.round_keyboard_backspace_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }



}