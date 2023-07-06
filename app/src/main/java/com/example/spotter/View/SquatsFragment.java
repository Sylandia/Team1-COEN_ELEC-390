package com.example.spotter.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.spotter.R;

public class SquatsFragment extends Fragment {

    private EditText rightReadingText, leftReadingText, backReadingText;
    private Button returnButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_squats, container, false);

        rightReadingText = view.findViewById(R.id.rightReadingText);
        leftReadingText = view.findViewById(R.id.leftReadingText);
        backReadingText = view.findViewById(R.id.backReadingText);

        view.findViewById(R.id.returnButton).setOnClickListener(returnActivity);


        return view;
    }

    View.OnClickListener returnActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };
}