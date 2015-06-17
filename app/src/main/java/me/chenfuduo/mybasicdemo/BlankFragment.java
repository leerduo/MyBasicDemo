package me.chenfuduo.mybasicdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class BlankFragment extends Fragment {


    String[] books = {"数学","语文","化学","英语","数学之美","物理","生物"};


    MyCallback myCallback;

    public interface MyCallback {
        void onArticleSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //下面的代码保证了宿主Activity必须实现下面的接口，否则会抛出异常
        try {
            myCallback = (MyCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blank, container, false);

        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,books));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCallback.onArticleSelected(position);
            }
        });
        return view;
    }


}
