package com.customview.custompinlib;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * Created by gney on 18/11/05
 */

public class LTRGridLayoutManager extends GridLayoutManager {

    public LTRGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LTRGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public LTRGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    protected boolean isLayoutRTL(){
        return false;
    }
}
