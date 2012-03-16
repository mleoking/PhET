package edu.colorado.phet.localizationstrings.util;

import java.io.*;
import java.util.regex.*;
 
public class WildCardFileFilter implements FileFilter
{
    private String _pattern;
 
    public WildCardFileFilter(String pattern)
    {
        _pattern = pattern.replace("*", ".*").replace("?", ".");
    }
 
    public boolean accept(File file)
    {
    	return Pattern.compile(_pattern).matcher(file.getName()).find();
    }
}