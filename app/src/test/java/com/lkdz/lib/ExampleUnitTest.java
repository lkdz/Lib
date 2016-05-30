package com.lkdz.lib;

import org.junit.Test;
import com.lkdz.*;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String str = "0115000001";
        String str1 = CheckDigitsISO7064.CalculateNumericCheckDigit(str, false);
        assertEquals(str+"3", str1);
    }
}