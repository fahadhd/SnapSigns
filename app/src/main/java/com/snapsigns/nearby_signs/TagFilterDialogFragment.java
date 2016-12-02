package com.snapsigns.nearby_signs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class TagFilterDialogFragment extends DialogFragment {
    private NearbySignsGridActivity mActivity;
    private SnapSigns app;
    private Set<Integer> selectedTags;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mActivity = (NearbySignsGridActivity) getActivity();
        app = (SnapSigns) mActivity.getApplication();

        View layout = mActivity.getLayoutInflater().inflate(R.layout.tag_filter_dialog, null);

        final TagContainerLayout mTagContainerLayout = (TagContainerLayout) layout.findViewById(R.id.tag_container);
        mTagContainerLayout.setTags(app.getFilterTags());

        selectedTags = new TreeSet<Integer>();

        for (int i = 0; i < mTagContainerLayout.getChildCount(); i++) {
            selectedTags.add(i);
        }

        mTagContainerLayout.setOnTagClickListener(
                new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int position, String text) {
                        TagView tag = ((TagView) mTagContainerLayout.getChildAt(position));

                        if (selectedTags.contains(position)) {
                            selectedTags.remove(position);
                            tag.setTagBackgroundColor(R.color.dark_purple);
                        } else {
                            selectedTags.add(position);
                            tag.setTagBackgroundColor(R.color.orange_900);
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
                                ((TagView) mTagContainerLayout.getChildAt(i))
                                        .setTagBackgroundColor(R.color.orange_900);
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
                                tag.setTagBackgroundColor(R.color.dark_purple);
                            } else {
                                selectedTags.add(i);
                                tag.setTagBackgroundColor(R.color.orange_900);
                            }
                        }
                    }
                }
        );

        return new AlertDialog.Builder(mActivity)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<String> filterTags = new ArrayList<String>();

                        for (int i : selectedTags) {
                            filterTags.add(
                                    ((TagView) mTagContainerLayout.getChildAt(i)).getText()
                            );
                        }

                        app.filterNearbySigns(filterTags);
                        mActivity.getAdapter().notifyDataSetChanged();
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
