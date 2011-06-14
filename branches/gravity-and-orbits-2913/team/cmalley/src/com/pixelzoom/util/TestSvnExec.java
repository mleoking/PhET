package com.pixelzoom.util;

import java.io.IOException;
import java.util.Arrays;


public class TestSvnExec {
    
    public static void main( String[] args ) throws IOException {
        String[] svnCommand = new String[] { "svn", "-version" };
        System.out.println( Arrays.asList( svnCommand ) );
        Runtime.getRuntime().exec( svnCommand );
    }

}
