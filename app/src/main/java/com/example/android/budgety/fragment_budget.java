package com.example.android.budgety;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class fragment_budget extends Fragment {
    List<Budget> budgetList;
    BudgetCardRecyclerViewAdapter myAdapter;

    public fragment_budget() {

        // Required empty public constructor
    }


    public static fragment_budget newInstance() {

        fragment_budget fragment = new fragment_budget();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_fragment_budget, container, false);

        Button newBudget =  (Button) view.findViewById(R.id.new_budget);
        newBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addBudget().show(getFragmentManager(),"");
            }
        });


        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mGridLayoutManager);



        budgetList = new ArrayList<>();
        myAdapter = new BudgetCardRecyclerViewAdapter(budgetList);
        mRecyclerView.setAdapter(myAdapter);



        return view;
    }




    public void addBudget(Budget budget) {
        budgetList.add(0,budget);
        myAdapter.notifyItemInserted(0);
    }


}