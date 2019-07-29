package com.app.leon.sellabfa.BaseItem;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.leon.sellabfa.R;

import java.util.Stack;

public abstract class BaseFragment extends Fragment {
    View view;
    Typeface typeface;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = FragmentView(inflater, parent, savedInstanceState);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "font/BYekan_3.ttf");
        FrameLayout frameLayout = view.findViewById(R.id.fragmentFrameLayout);
        context = getActivity();
        setFont(frameLayout, typeface);
        initialize();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
        typeface = null;
    }

    public void setFont(ViewGroup viewTree, Typeface typeface) {
        Stack<ViewGroup> stackOfViewGroup = new Stack<ViewGroup>();
        stackOfViewGroup.push(viewTree);
        while (!stackOfViewGroup.isEmpty()) {
            ViewGroup tree = stackOfViewGroup.pop();
            for (int i = 0; i < tree.getChildCount(); i++) {
                View child = tree.getChildAt(i);
                if (child instanceof ViewGroup) {
                    stackOfViewGroup.push((ViewGroup) child);
                } else if (child instanceof Button) {
                    ((Button) child).setTypeface(typeface);
                } else if (child instanceof EditText) {
                    ((EditText) child).setTypeface(typeface);
                } else if (child instanceof TextView) {
                    ((TextView) child).setTypeface(typeface);
                } else if (child instanceof ListView) {
                    TextView textView = (TextView) ((ListView) child).getChildAt(0);
                    textView.setTypeface(typeface);
                    textView = (TextView) ((ListView) child).getChildAt(2);
                    textView.setTypeface(typeface);

                    CheckedTextView checkedTextView = (CheckedTextView) ((ListView) child).getChildAt(0);
                    checkedTextView.setTypeface(typeface);
                    checkedTextView = (CheckedTextView) ((ListView) child).getChildAt(1);
                    checkedTextView.setTypeface(typeface);
                    checkedTextView = (CheckedTextView) ((ListView) child).getChildAt(2);
                    checkedTextView.setTypeface(typeface);
                } else if (child instanceof CheckedTextView) {
                    ((CheckedTextView) child).setTypeface(typeface);
                }
            }
        }
    }

    public abstract View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    public abstract void initialize();

}
