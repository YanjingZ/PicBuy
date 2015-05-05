package team11.desiredish.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import team11.desiredish.TakePhoto.PhotoIntentActivity;
import team11.desiredish.R;

/**
 * Created by Yanjing on 10/18/14.
 */
public class TakePictureFragment extends Fragment{
    public final static String EXTRA_MESSAGE = "team11.desiredish.MESSAGE";
    private static final String DEBUG_TAG = "yanjing-debug";
    private String UserID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  null;
        view = inflater.inflate(R.layout.tab_takepicture_layout, container, false);

        UserID=this.getArguments().getString("UserID");
        Log.i(DEBUG_TAG, "TakePictureFrag: UserID:" + UserID);

        Button takePictureButton = (Button)view.findViewById(R.id.Button2);


        // Set OnItemClickListener so we can be notified on button clicks
        takePictureButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PhotoIntentActivity.class);
                intent.putExtra(EXTRA_MESSAGE, UserID);
                startActivity(intent);
            }});

        return view;
    }





}