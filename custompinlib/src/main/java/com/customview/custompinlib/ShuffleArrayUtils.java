package com.customview.custompinlib;

import java.util.Random;

/**
 * Created by gney on 17/11/05
 */

public class ShuffleArrayUtils {

    static int[] shuffle(int[] array) {
        int length = array.length;
        Random random = new Random();
        random.nextInt();

        for (int i = 0; i < length; i++) {
            int change = i + random.nextInt(length - i);
            swap(array, i, change);
        }
        return array;
    }

    private static void swap(int[] array, int index, int change) {
        int temp = array[index];
        array[index] = array[change];
        array[change] = temp;
    }
}