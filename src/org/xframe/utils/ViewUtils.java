package org.xframe.utils;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    public static List<View> getAllChildren(ViewGroup top) {
        List<View> children = new ArrayList<View>();
        for (int i = 0; i < top.getChildCount(); i++) {
            View child = top.getChildAt(i);
            children.add(child);
            if (child instanceof ViewGroup)
                children.addAll(getAllChildren((ViewGroup) child));
        }
        return children;
    }
}
