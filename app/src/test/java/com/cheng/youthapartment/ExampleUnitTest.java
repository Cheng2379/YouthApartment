package com.cheng.youthapartment;

import org.junit.Test;

import com.cheng.youthapartment.bean.properties.LeaseStatus;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        int code = LeaseStatus.SIGNED.getValue();
        System.out.printf("value: " + code);
    }
}