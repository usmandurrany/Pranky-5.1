package com.fournodes.ud.pranky;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Usman-Durrani on 18-Nov-15.
 */
public class TutorialFragment extends Fragment {
    Activity activity;
    int image;
    ImageView imgTut;
    IFragment iFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial, container, false);
        Bundle bundle = getArguments();
        this.image = bundle.getInt("Image");
        imgTut = (ImageView) rootView.findViewById(R.id.imgTutorial);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;

        try {
            iFrag = (IFragment) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement soundSelectListener");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (activity != null) {

            if (imgTut != null) {
                imgTut.setImageResource(image);
            }

            imgTut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iFrag.TutImageClick();
                }
            });
        }

    }
}
