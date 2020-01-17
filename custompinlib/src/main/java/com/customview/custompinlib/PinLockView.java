package com.customview.custompinlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by gney on 17/11/05
 */
public class PinLockView extends RecyclerView {

    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int[] DEFAULT_KEY_SET = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    private String mPin = "";
    private int mPinLength;
    private int mHorizontalSpacing, mVerticalSpacing;
    private int mTextColor, mNumbersTextColor, mLettersTextColor, mDeleteButtonPressedColor, mDeleteButtonColor;
    private int mTextSize, mNumbersTextSize, mLettersTextSize, mButtonSize, mDeleteButtonSize;

    private Drawable mButtonBackgroundDrawable;
    private Drawable mDeleteButtonDrawable;
    private boolean mShowDeleteButton, mShowLetters;
    private boolean mNumbersTextBold, mLettersTextBold;
    private boolean mDeleteButtonDefault;
    private boolean mDeprecatedColorOptions;


    private IndicatorDots mIndicatorDots;
    private PinLockAdapter mAdapter;
    private PinLockListener mPinLockListener;
    private CustomizationOptionsBundle mCustomizationOptionsBundle;
    private int[] mCustomKeySet;

    private PinLockAdapter.OnNumberClickListener mOnNumberClickListener
            = new PinLockAdapter.OnNumberClickListener() {
        @Override
        public void onNumberClicked(int keyValue) {
            if (mPin.length() < getPinLength()) {
                mPin = mPin.concat(String.valueOf(keyValue));

                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }

                if (mPin.length() == 1) {
                    mAdapter.setPinLength(mPin.length());
                    //mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                    updateDeleteButton();
                }

                if (mPinLockListener != null) {
                    if (mPin.length() == mPinLength) {
                        mPinLockListener.onComplete(mPin);
                    } else {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }
                }
            } else {
                if (!isShowDeleteButton()) {
                    resetPinLockView();
                    mPin = mPin.concat(String.valueOf(keyValue));

                    if (isIndicatorDotsAttached()) {
                        mIndicatorDots.updateDot(mPin.length());
                    }

                    if (mPinLockListener != null) {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }

                } else {
                    if (mPinLockListener != null) {
                        mPinLockListener.onComplete(mPin);
                    }
                }
            }
        }
    };

    private PinLockAdapter.OnDeleteClickListener mOnDeleteClickListener
            = new PinLockAdapter.OnDeleteClickListener() {
        @Override
        public void onDeleteClicked() {
            if (mPin.length() > 0) {
                mPin = mPin.substring(0, mPin.length() - 1);

                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }

                if (mPin.length() == 0) {
                    mAdapter.setPinLength(mPin.length());
                    //mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                    updateDeleteButton();
                }

                if (mPinLockListener != null) {
                    if (mPin.length() == 0) {
                        mPinLockListener.onEmpty();
                        clearInternalPin();
                    } else {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }
                }
            } else {
                if (mPinLockListener != null) {
                    mPinLockListener.onEmpty();
                }
            }
        }

        @Override
        public void onDeleteLongClicked() {
            resetPinLockView();
            if (mPinLockListener != null) {
                mPinLockListener.onEmpty();
            }
        }
    };

    private void updateDeleteButton() {
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    public PinLockView(Context context) {
        super(context);
        init(null, 0);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attributeSet, int defStyle) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet,R.styleable.PinLockView);

        try {
            mPinLength = typedArray.getInt(R.styleable.PinLockView_pinLength, DEFAULT_PIN_LENGTH);
            mHorizontalSpacing = (int) typedArray.getDimension(R.styleable.PinLockView_keypadHorizontalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_horizontal_spacing));
            mVerticalSpacing = (int) typedArray.getDimension(R.styleable.PinLockView_keypadVerticalSpacing, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_vertical_spacing));
            mNumbersTextColor = typedArray.getColor(R.styleable.PinLockView_keypadNumbersTextColor, ResourceUtils.getColor(getContext(),R.color.white));
            mLettersTextColor = typedArray.getColor(R.styleable.PinLockView_keypadLettersTextColor, ResourceUtils.getColor(getContext(),R.color.white));
            mDeleteButtonColor = typedArray.getColor(R.styleable.PinLockView_keypadDeleteButtonColor, ResourceUtils.getColor(getContext(),R.color.white));
            mNumbersTextSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadNumbersTextSize, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_text_size));
            mLettersTextSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadLettersTextSize, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_text_size));
            mButtonSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadButtonSize, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_button_size));
            mNumbersTextBold = typedArray.getBoolean(R.styleable.PinLockView_keypadNumbersBold, false);
            mLettersTextBold = typedArray.getBoolean(R.styleable.PinLockView_keypadLettersBold, false);
            mDeleteButtonDefault = typedArray.getBoolean(R.styleable.PinLockView_keypadDefaultDeleteColor, true);
            mShowLetters = typedArray.getBoolean(R.styleable.PinLockView_keypadShowLetters, false);
            mDeleteButtonSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadDeleteButtonSize, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_delete_button_size));
            mButtonBackgroundDrawable = typedArray.getDrawable(R.styleable.PinLockView_keypadButtonBackgroundDrawable);
            mDeleteButtonDrawable = typedArray.getDrawable(R.styleable.PinLockView_keypadDeleteButtonDrawable);
            mShowDeleteButton = typedArray.getBoolean(R.styleable.PinLockView_keypadShowDeleteButton, true);
            mDeleteButtonPressedColor = typedArray.getColor(R.styleable.PinLockView_keypadDeleteButtonPressedColor, ResourceUtils.getColor(getContext(),R.color.greyish));
            mDeprecatedColorOptions = typedArray.getBoolean(R.styleable.PinLockView_keypadUseDeprecatedColorOptions, true);
            mTextColor = typedArray.getColor(R.styleable.PinLockView_keypadTextColor, ResourceUtils.getColor(getContext(),R.color.white));
            mTextSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadTextSize, ResourceUtils.getDimensionInPx(getContext(),R.dimen.default_text_size));

        } finally {
            typedArray.recycle();
        }

        mCustomizationOptionsBundle = new CustomizationOptionsBundle();
        mCustomizationOptionsBundle.setNumbersTextColor(mNumbersTextColor);
        mCustomizationOptionsBundle.setNumbersTextSize(mNumbersTextSize);
        mCustomizationOptionsBundle.setLettersTextColor(mLettersTextColor);
        mCustomizationOptionsBundle.setLettersTextSize(mLettersTextSize);
        mCustomizationOptionsBundle.setShowLetters(mShowLetters);
        mCustomizationOptionsBundle.setDeleteButtonColor(mDeleteButtonColor);
        mCustomizationOptionsBundle.setIsNumbersTextBold(mNumbersTextBold);
        mCustomizationOptionsBundle.setIsLettersTextBold(mLettersTextBold);
        mCustomizationOptionsBundle.setTextColor(mTextColor);
        mCustomizationOptionsBundle.setTextSize(mTextSize);
        mCustomizationOptionsBundle.setButtonSize(mButtonSize);
        mCustomizationOptionsBundle.setButtonBackgroundDrawable(mButtonBackgroundDrawable);
        mCustomizationOptionsBundle.setDeleteButtonDrawable(mDeleteButtonDrawable);
        mCustomizationOptionsBundle.setDeleteButtonSize(mDeleteButtonSize);
        mCustomizationOptionsBundle.setShowDeleteButton(mShowDeleteButton);
        mCustomizationOptionsBundle.setDeleteButtonPressesColor(mDeleteButtonPressedColor);
        mCustomizationOptionsBundle.setDeleteButtonDefault(mDeleteButtonDefault);
        mCustomizationOptionsBundle.setUseDeprecated(mDeprecatedColorOptions);

        initView();
    }

    private void initView() {
        setLayoutManager(new LTRGridLayoutManager(getContext(), 3));

        mAdapter = new PinLockAdapter(getContext());
        mAdapter.setOnItemClickListener(mOnNumberClickListener);
        mAdapter.setOnDeleteClickListener(mOnDeleteClickListener);
        mAdapter.setCustomizationOptions(mCustomizationOptionsBundle);
        setAdapter(mAdapter);

        addItemDecoration(new ItemSpaceDecoration(mHorizontalSpacing, mVerticalSpacing, 3, false));
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * Sets a {@link PinLockListener} to the to listen to pin update events
     *
     * @param pinLockListener the listener
     */
    public void setPinLockListener(PinLockListener pinLockListener) {
        this.mPinLockListener = pinLockListener;
    }


    /**
     * Get the length of the current pin length
     *
     * @return the length of the pin
     */
    public int getPinLength() {
        return mPinLength;
    }

    /**
     * Sets the pin length dynamically
     *
     * @param pinLength the pin length
     */
    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;

        if (isIndicatorDotsAttached()) {
            mIndicatorDots.setPinLength(pinLength);
        }
    }

    /**
     * Get the text color in the buttons
     *
     * @return the text color
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set the text color of the buttons dynamically
     *
     * @param textColor the text color
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mCustomizationOptionsBundle.setTextColor(textColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the text in the buttons
     *
     * @return the size of the text in pixels
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * Set the size of text in pixels
     *
     * @param textSize the text size in pixels
     */
    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mCustomizationOptionsBundle.setTextSize(textSize);
        mAdapter.notifyDataSetChanged();
    }
    /**
     * Get the text color in the buttons
     *
     * @return the text color of the numbers
     */
    public int getNumbersTextColor() {
        return mNumbersTextColor;
    }

    /**
     * Set the text color of the buttons dynamically
     *
     * @param textColor the text color for the numbers
     */
    public void setNumbersTextColor(int textColor) {
        this.mNumbersTextColor = textColor;
        mCustomizationOptionsBundle.setNumbersTextColor(textColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the text color in the buttons
     *
     * @return the text color of the letters
     */
    public int getLettersTextColor() {
        return mLettersTextColor;
    }

    /**
     * Set the text color of the buttons dynamically
     *
     * @param textColor the text color for the letters
     */
    public void setLettersTextColor(int textColor) {
        this.mLettersTextColor = textColor;
        mCustomizationOptionsBundle.setLettersTextColor(textColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the numerical text in the buttons
     *
     * @return the size of the text in pixels
     */
    public int getNumbersTextSize() {
        return mNumbersTextSize;
    }

    /**
     * Set the size of numerical text in pixels
     *
     * @param textSize the text size in pixels
     */
    public void setNumbersTextSize(int textSize) {
        this.mNumbersTextSize = textSize;
        mCustomizationOptionsBundle.setNumbersTextSize(textSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the alphabetical text in the buttons
     *
     * @return the size of the text in pixels
     */
    public int getLettersTextSize() {
        return mLettersTextSize;
    }

    /**
     * Set the size of the alphabetical text in pixels
     *
     * @param textSize the text size in pixels
     */
    public void setLettersTextSize(int textSize) {
        this.mLettersTextSize = textSize;
        mCustomizationOptionsBundle.setLettersTextSize(textSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the color of the delete button
     *
     * @return the delete button color
     */
    public int getDeleteButtonColor() {
        return mDeleteButtonColor;
    }

    /**
     * Set the color fo the delete button
     *
     * @param buttonColor the color of the delete button
     */
    public void setDeleteButtonColor(int buttonColor) {
        this.mDeleteButtonColor = buttonColor;
        mCustomizationOptionsBundle.setDeleteButtonColor(buttonColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Checks if the numbers text is bold
     *
     * @return returns true if numbers are bolded, false otherwise
     */
    public boolean isNumbersTextBold() {
        return mNumbersTextBold;
    }

    /**
     * Dynamically set the boldness of numbers text
     *
     * @param isNumbersBold true if the numbers text should be bold, false otherwise
     */
    public void setNumbersTextBold(boolean isNumbersBold) {
        this.mNumbersTextBold = isNumbersBold;
        mCustomizationOptionsBundle.setIsNumbersTextBold(isNumbersBold);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Checks if the letters text is bold
     *
     * @return returns true if letters are bolded, false otherwise
     */
    public boolean isLettersTextBold() {
        return mLettersTextBold;
    }

    /**
     * Dynamically set the boldness of letters text
     *
     * @param isLettersBold true if the letters text should be bold, false otherwise
     */
    public void setLettersTextBold(boolean isLettersBold) {
        this.mLettersTextBold = isLettersBold;
        mCustomizationOptionsBundle.setIsLettersTextBold(isLettersBold);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Are the letters in the keypad buttons shown
     *
     * @return returns true if shown, false otherwise
     */
    public boolean isShownLetters() {
        return mShowLetters;
    }

    /**
     * Dynamically set if the letters in buttons should be shown
     *
     * @param showLetters true if the letters in buttons should be shown, false otherwise
     */
    public void setShownLetters(boolean showLetters) {
        this.mShowLetters = showLetters;
        mCustomizationOptionsBundle.setShowLetters(showLetters);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Is the delete button of default color (white)
     *
     * @return returns true if it is default color, false otherwise
     */
    public boolean isDefaultDeleteColor() {
        return mDeleteButtonDefault;
    }

    /**
     * Dynamically set if the delete button is of default color
     *
     * @param isDefaultColor true if the letters in buttons should be shown, false otherwise
     */
    public void setDefaultDeleteColor(boolean isDefaultColor) {
        this.mDeleteButtonDefault = isDefaultColor;
        mCustomizationOptionsBundle.setDeleteButtonDefault(isDefaultColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Are we using deprecated color options for color of numbers and letters
     *
     * @return returns true if it we are using the deprecated options, false otherwise
     */
    public boolean isUsingDeprecatedColorOptions() {
        return mDeprecatedColorOptions;
    }

    /**
     * Dynamically set if the delete button is of default color
     *
     * @param deprecatedColorOption true if the letters in buttons should be shown, false otherwise
     */
    public void setDeprecatedColorOptions(boolean deprecatedColorOption) {
        this.mDeprecatedColorOptions = deprecatedColorOption;
        mCustomizationOptionsBundle.setUseDeprecated(deprecatedColorOption);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the pin buttons
     *
     * @return the size of the button in pixels
     */
    public int getButtonSize() {
        return mButtonSize;
    }

    /**
     * Set the size of the pin buttons dynamically
     *
     * @param buttonSize the button size
     */
    public void setButtonSize(int buttonSize) {
        this.mButtonSize = buttonSize;
        mCustomizationOptionsBundle.setButtonSize(buttonSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the current background drawable of the buttons, can be null
     *
     * @return the background drawable
     */
    public Drawable getButtonBackgroundDrawable() {
        return mButtonBackgroundDrawable;
    }

    /**
     * Set the background drawable of the buttons dynamically
     *
     * @param buttonBackgroundDrawable the background drawable
     */
    public void setButtonBackgroundDrawable(Drawable buttonBackgroundDrawable) {
        this.mButtonBackgroundDrawable = buttonBackgroundDrawable;
        mCustomizationOptionsBundle.setButtonBackgroundDrawable(buttonBackgroundDrawable);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the drawable of the delete button
     *
     * @return the delete button drawable
     */
    public Drawable getDeleteButtonDrawable() {
        return mDeleteButtonDrawable;
    }

    /**
     * Set the drawable of the delete button dynamically
     *
     * @param deleteBackgroundDrawable the delete button drawable
     */
    public void setDeleteButtonDrawable(Drawable deleteBackgroundDrawable) {
        this.mDeleteButtonDrawable = deleteBackgroundDrawable;
        mCustomizationOptionsBundle.setDeleteButtonDrawable(deleteBackgroundDrawable);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the delete button size in pixels
     *
     * @return size in pixels
     */
    public int getDeleteButtonSize() {
        return mDeleteButtonSize;
    }

    /**
     * Set the size of the delete button in pixels
     *
     * @param deleteButtonSize size in pixels
     */
    public void setDeleteButtonSize(int deleteButtonSize) {
        this.mDeleteButtonSize = deleteButtonSize;
        mCustomizationOptionsBundle.setDeleteButtonSize(deleteButtonSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Is the delete button shown
     *
     * @return returns true if shown, false otherwise
     */
    public boolean isShowDeleteButton() {
        return mShowDeleteButton;
    }

    /**
     * Dynamically set if the delete button should be shown
     *
     * @param showDeleteButton true if the delete button should be shown, false otherwise
     */
    public void setShowDeleteButton(boolean showDeleteButton) {
        this.mShowDeleteButton = showDeleteButton;
        mCustomizationOptionsBundle.setShowDeleteButton(showDeleteButton);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the delete button pressed/focused state color
     *
     * @return color of the button
     */
    public int getDeleteButtonPressedColor() {
        return mDeleteButtonPressedColor;
    }

    /**
     * Set the pressed/focused state color of the delete button
     *
     * @param deleteButtonPressedColor the color of the delete button
     */
    public void setDeleteButtonPressedColor(int deleteButtonPressedColor) {
        this.mDeleteButtonPressedColor = deleteButtonPressedColor;
        mCustomizationOptionsBundle.setDeleteButtonPressesColor(deleteButtonPressedColor);
        mAdapter.notifyDataSetChanged();
    }

    public int[] getCustomKeySet() {
        return mCustomKeySet;
    }

    public void setCustomKeySet(int[] customKeySet) {
        this.mCustomKeySet = customKeySet;

        if (mAdapter != null) {
            mAdapter.setKeyValues(customKeySet);
        }
    }

    public void clearInternalPin() {
        mPin = "";
    }
    public void InternalPin() {
        mPin = mPin.substring(0, mPin.length() - 1);
    }


    public void resetPinLockView() {

        clearInternalPin();

        mAdapter.setPinLength(mPin.length());
        updateDeleteButton();
        //mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);

        if (mIndicatorDots != null) {
            mIndicatorDots.updateDot(mPin.length());
        }
    }


    public boolean isIndicatorDotsAttached() {
        return mIndicatorDots != null;
    }


    public void attachIndicatorDots(IndicatorDots mIndicatorDots) {
        this.mIndicatorDots = mIndicatorDots;
    }
}
