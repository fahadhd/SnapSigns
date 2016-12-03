package com.snapsigns.nearby_signs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class TagFilterDialogFragment extends DialogFragment {
    private NearbySignsGridActivity mActivity;
    private SnapSigns app;
    private Set<Integer> selectedTags;
    private int colorSelected, colorUnselected;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mActivity = (NearbySignsGridActivity) getActivity();
        app = (SnapSigns) mActivity.getApplication();

        Resources res = getResources();
        colorSelected = res.getColor(R.color.orange_900);
        colorUnselected = res.getColor(R.color.dark_purple);

        View layout = mActivity.getLayoutInflater().inflate(R.layout.tag_filter_dialog, null);

        final TagContainerLayout mTagContainerLayout = (TagContainerLayout) layout.findViewById(R.id.tag_container);
        mTagContainerLayout.setTags(app.getAllTags());
        List<String> filterTags = app.getFilterTags();

        selectedTags = new TreeSet<Integer>();

        for (int i = 0; i < mTagContainerLayout.getChildCount(); i++) {
            TagView tag = (TagView) mTagContainerLayout.getChildAt(i);

            if (filterTags.contains(tag.getText())) {
                selectedTags.add(i);
                tag.setTagBackgroundColor(colorSelected);
            } else {
                tag.setTagBackgroundColor(colorUnselected);
            }
        }

        mTagContainerLayout.setOnTagClickListener(
                new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int position, String text) {
                        TagView tag = (TagView) mTagContainerLayout.getChildAt(position);

                        if (selectedTags.contains(position)) {
                            selectedTags.remove(position);
                            tag.setTagBackgroundColor(colorUnselected);
                        } else {
                            selectedTags.add(position);
                            tag.setTagBackgroundColor(colorSelected);
                        }
                    }

                    @Override
                    public void onTagLongClick(int position, String text) {

                    }

                    @Override
                    public void onTagCrossClick(int position) {

                    }
                }
        );

        layout.findViewById(R.id.select_all_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < mTagContainerLayout.getChildCount(); i++) {
                            if (!selectedTags.contains(i)) {
                                selectedTags.add(i);

                                TagView tag = (TagView) mTagContainerLayout.getChildAt(i);
                                tag.setTagBackgroundColor(colorSelected);
                                tag.invalidate();
                            }
                        }
                    }
                }
        );

        layout.findViewById(R.id.invert_selection_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < mTagContainerLayout.getChildCount(); i++) {
                            TagView tag = (TagView) mTagContainerLayout.getChildAt(i);

                            if (selectedTags.contains(i)) {
                                selectedTags.remove(i);
                                tag.setTagBackgroundColor(colorUnselected);
                            } else {
                                selectedTags.add(i);
                                tag.setTagBackgroundColor(colorSelected);
                            }

                            tag.invalidate();
                        }
                    }
                }
        );

        return new AlertDialog.Builder(mActivity)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> filterTags = new ArrayList<String>();

                        for (int i : selectedTags) {
                            filterTags.add(
                                    mTagContainerLayout.getTagText(i)
                            );
                        }

                        app.setUseFilter(true);
                        app.setFilterTags(filterTags);
                        mActivity.getAdapter().updateDataSet();
                        mActivity.setDataSetChanged(true);

                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();
    }
}
