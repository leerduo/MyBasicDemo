package me.chenfuduo.mybasicdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OtherFragment extends Fragment {

    public static final String ARG_POSITION = "position";

    private TextView tv;;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        tv = (TextView) view.findViewById(R.id.content);
        Bundle args = new Bundle();
        int position = args.getInt(ARG_POSITION);
        updateTextView(position);
        return view;
    }

    public void updateTextView(int position) {
        switch (position){
            case 0:
                tv.setText("数学");
                break;
            case 1:
                tv.setText("语文");
                break;
            case 2:
                tv.setText("化学");
                break;
            case 3:
                tv.setText("英语");
                break;
            case 4:
                tv.setText("数学之美");
                break;
            case 5:
                tv.setText("物理");
                break;
            case 6:
                tv.setText("生物");
                break;

        }
    }


}
