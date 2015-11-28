package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Usman-Durrani on 18-Nov-15.
 */
public class TutorialFragment extends Fragment {
    Activity activity;
    int image;
    ImageView imgTut;
    TextView skip;
    IFragment iFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial, container, false);
        Bundle bundle = getArguments();
        this.image = bundle.getInt("Image");
        imgTut = (ImageView) rootView.findViewById(R.id.imgTutorial);
        skip = (TextView) rootView.findViewById(R.id.skip);


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

            imgTut.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    iFrag.TutImageTouch(view, motionEvent);


                    return false;
                }
            });

            imgTut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iFrag.TutImageClick();
                }
            });
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent main = new Intent(getActivity(), Main.class);
                    SharedPrefs.setAppFirstLaunch(false);
                    startActivity(main);
                    getActivity().finish();

                }
            });
        }

    }
}
