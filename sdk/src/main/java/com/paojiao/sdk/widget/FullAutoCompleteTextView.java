package com.paojiao.sdk.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import com.paojiao.sdk.utils.StringUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/11/18 上午10:49
 */
public class FullAutoCompleteTextView extends AutoCompleteTextView {
    private int myThreshold;

    public FullAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public FullAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FullAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isEmpty(editable.toString())) {
                    performFiltering(getText(), 0);
                    showDropDown();
                }
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                performFiltering(getText(), 0);
                showDropDown();
                return false;
            }
        });
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }


    public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }
        myThreshold = threshold;
    }

    public int getThreshold() {
        return myThreshold;
    }

}
