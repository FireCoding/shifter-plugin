package com.kstenschke.shifter.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UtilsLinesListTest {

    @Test
    public void testAddDelimiter() throws Exception {

    }

    @Test
    public void testGetCommonDelimiter() throws Exception {
        List<String> lines;
        UtilsLinesList.DelimiterDetector delimiterDetector;

        // lines are not initialized
        lines = new ArrayList<String>();
        delimiterDetector = new UtilsLinesList.DelimiterDetector(lines);
        assertEquals( null, delimiterDetector.getCommonDelimiter() );

        // all delimiters are ";"
        lines = new ArrayList<String>();
        lines.add("a = 0;");
        lines.add("b = 1;");
        lines.add("c = 2;");

        delimiterDetector = new UtilsLinesList.DelimiterDetector(lines);
        assertEquals( ";", delimiterDetector.getCommonDelimiter() );

        // all delimiters are ";", there is a line w/o content
        lines = new ArrayList<String>();
        lines.add("a = 0;");
        lines.add("");
        lines.add("c = 2;");

        delimiterDetector = new UtilsLinesList.DelimiterDetector(lines);
        assertEquals( ";", delimiterDetector.getCommonDelimiter() );

        // no line has content
        lines = new ArrayList<String>();
        lines.add("");
        lines.add("");

        delimiterDetector = new UtilsLinesList.DelimiterDetector(lines);
        assertEquals( null, delimiterDetector.getCommonDelimiter() );
    }

}