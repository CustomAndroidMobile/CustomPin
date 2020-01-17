package com.customview.custompinlib;

/**
 * Created by gney on 18/11/05
 */
public interface PinLockListener {

    void onComplete(String pin);

    void onEmpty();

    void onPinChange(int pinLength, String intermediatePin);
}
