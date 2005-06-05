/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.psl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JnlpGatherer
 * <p/>
 * Makes a list of all resources specified in the JNLP files in a specified
 * directory tree
 */
public class JnlpGatherer {

    /**
     * Returns a list of all the JNLP files in a directory
     *
     * @param rootName
     * @return
     */
    public List getJnlpNames( String rootName ) {
        File root = new File( rootName );

        if( !root.isDirectory() || root.getName().toLowerCase().endsWith( ".jnlp" )) {
            throw new RuntimeException( "root not a directory or JNLP file");
        }

        List resourceNames = processFile( root );

        for( int i = 0; i < resourceNames.size(); i++ ) {
            String s = (String)resourceNames.get( i );
            System.out.println( "s = " + s );
        }

        return resourceNames;
    }

    /**
     * If the file is a JNLP file, gathers all the resources. If it's a directory,
     * recourses into it.
     *
     * @param file
     */
    private List processFile( File file ) {
        ArrayList jnlpResourceURLs = new ArrayList();

        if( file.isDirectory() ) {
            File[] filesOfInterest = file.listFiles( new DirJNLPFilter() );
            for( int i = 0; i < filesOfInterest.length; i++ ) {
                jnlpResourceURLs.addAll( processFile( filesOfInterest[i] ) );
            }
        }
        else {
            jnlpResourceURLs.add( file.getAbsolutePath() );
        }

        return jnlpResourceURLs;
    }

    /**
     * FileFilter inner subclass that returns only directories and JNLP files
     */
    private class DirJNLPFilter implements FileFilter {
        public boolean accept( File file ) {
            return file.isDirectory() || file.getName().toLowerCase().endsWith( ".jnlp" );
        }
    }



    public static void main( String[] args ) {
        String root = args[0];

        new JnlpGatherer().getJnlpNames( root );
    }
}
