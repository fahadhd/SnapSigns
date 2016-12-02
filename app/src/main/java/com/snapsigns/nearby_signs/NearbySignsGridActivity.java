package com.snapsigns.nearby_signs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.snapsigns.R;


public class NearbySignsGridActivity extends AppCompatActivity {
    private NearbySignsGridAdapter mAdapter;
    private boolean dataSetChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_signs_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_tags:
                new TagFilterDialogFragment().show(getFragmentManager(), "Select filter tags");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setDataSetChanged(boolean changed) {
        dataSetChanged = changed;
    }

    public NearbySignsGridAdapter getAdapter() {
        return mAdapter;
    }
}
