package com.lkdz.lib;

import org.junit.Test;

import com.lkdz.lib.rfwirelessmoduleprotocol.Request;
import com.lkdz.lib.rfwirelessmoduleprotocol.Response;
import com.lkdz.util.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        try {
            Request.Builder builder1 = new Request.Builder(Request.FREQUENCY_470)
                    .setBase(123)
                    .setWorkStartDay(10);
            Request.Builder builder2 = new Request.Builder(Request.FREQUENCY_495)
                    .setBase(4000)
                    .setWorkStartDay(20);

            Request request1 = builder1.build();
            Request request2 = builder2.build();

            String ss = StringFormatter.toString(new byte[] {20, -55, 67}, "--");

//            builder1.build().create(Request.READ_METER_WITH_SIGNAL, "0616000001");

            Response.resolve(new byte[] {0x00});
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}