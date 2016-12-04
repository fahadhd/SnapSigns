package com.snapsigns.nearby_signs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.snapsigns.utilities.Constants;
import com.snapsigns.utilities.FireBaseUtility;


public class NearbySignsGridActivity extends AppCompatActivity {
    private NearbySignsGridAdapter mAdapter;
    private boolean dataSetChanged = false;

    Button  mFilterTagsButton, mClearFilterButton;
    ImageButton mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_signs_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        mExitButton = (ImageButton)findViewById(R.id.exit_grid);
        mFilterTagsButton = (Button) findViewById(R.id.filter_tags);
        mClearFilterButton = (Button) findViewById(R.id.clear_filter);

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mFilterTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TagFilterDialogFragment().show(getFragmentManager(), "Select filter tags");
            }
        });

        mClearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SnapSigns) getApplication()).setUseFilter(false);
                dataSetChanged = true;
                mAdapter.updateDataSet();
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gridview);
        mAdapter = new NearbySignsGridAdapter(this);
        gridview.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nearby_signs_grid_menu, menu);
        return true;
    }


    @Override
    public void finish() {
        if (dataSetChanged) {
            sendBroadcast(new Intent(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS));
        }

        super.finish();
    }

    public void setDataSetChanged(boolean changed) {
        dataSetChanged = changed;
    }

    public NearbySignsGridAdapter getAdapter() {
        return mAdapter;
    }
}
