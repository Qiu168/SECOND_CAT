package com.my_framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class TestPatten {
    @Test
    public void test(){
        Assert.assertTrue(Pattern.matches("^[0-9]+$", "   789    ".trim()));
    }
}
