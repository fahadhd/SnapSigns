package com.snapsigns.my_signs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.R;

import java.util.ArrayList;

/**
 * Creates a list view of past signs taken. When user clicks on an item in list view it
 * opens the activity SignDetail which displays a full screen view on the sign.
 */
public class MySignsFragment extends BaseFragment {
    ArrayList<ImageSign> mySigns = new ArrayList<>();
    MySignsAdapter adapter;
    ListView mySignsListView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //TODO: Load image data here when we have a database
        //ie new LoadImages().execute();
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_signs_list_view, container, false);

//        adapter = new MySignsAdapter(getActivity(),mySigns);
//        mySignsListView = (ListView) rootView.findViewById(R.id.my_signs_list);
//        mySignsListView.setAdapter(adapter);

        return rootView;
    }

    /**
     * Background thread responsible for loading images from a database.
     * Images will most likely be stored as a blob with their location.
     */
    public class LoadImages extends AsyncTask<Void,Void,ArrayList<ImageSign>> {

        @Override
        protected ArrayList<ImageSign> doInBackground(Void... params) {
            //TODO: Retrieve and return saved user images from database
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageSign> signs) {
            mySigns.clear();
            mySigns.addAll(signs);
        }
    }


}
