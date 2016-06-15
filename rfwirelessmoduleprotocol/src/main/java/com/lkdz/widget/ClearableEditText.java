package com.lkdz.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.appcompat.R;

/**
 * 转自 http://www.codingke.com/question/1856
 *     http://www.jianshu.com/p/0f6575472cb6
 */
public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private boolean mSoftInputVisible;

    private Drawable mClearTextIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;

    public ClearableEditText(final Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private void init(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.abc_ic_clear_mtrl_alpha);
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //包装该绘图使之能被安卓5.0之前版本着色
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }


    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setError(null);
                setText("");
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
//        原来判断EditText是否处于焦点来显示清除图标，但有点问题。因为此时可能软键盘处于收起状态，从逻辑行为上可认为退出编辑状态
//        所以应该不显示清除图标，可是软键盘是否收起对于EditText自身是没有办法判断，只能等待外部自定义监听事件（监听软键盘状态）
//        通知其是否退出编辑状态，即不显示清除图标。
//        if (isFocused()) {
//            setClearIconVisible(s.length() > 0);
//        }
        if (mClearTextIcon != null) {
            setEditTextState();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private void setClearIconVisible(final boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }

    /**
     * 将软键盘的状态通知给EditText
     * @param softInputVisible 软键盘的状态
     */
    public void setSoftInputVisible (boolean softInputVisible) {
        mSoftInputVisible = softInputVisible;
        setEditTextState();
    }

    private void setEditTextState() {
        if (mSoftInputVisible && getText().length() > 0) {
            setClearIconVisible(true);
        }
        else {
            setClearIconVisible(false);
        }
    }

}