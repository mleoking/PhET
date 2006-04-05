/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

import netx.jnlp.JNLPFile;
import netx.jnlp.LaunchException;
import netx.jnlp.ParseException;
import netx.jnlp.cache.CacheEntry;
import netx.jnlp.cache.CacheUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Mar 29, 2006
 * Time: 8:33:24 PM
 * Copyright (c) Mar 29, 2006 by Sam Reid
 */

public class TestLaunch {
    public static void main( String[] args ) throws IOException, ParseException {

        URL location = new URL( "http://www.colorado.edu/physics/phet/dev/waveinterference/0.03/waveinterference.jnlp" );
        File localFile = CacheUtil.getCacheFile( location, null );
        System.out.println( "localFile.getAbsolutePath() = " + localFile.getAbsolutePath() );
        System.out.println( "localFile.exists() = " + localFile.exists() );

        boolean current = CacheUtil.isCurrent( location, null, null );
        System.out.println( "current = " + current );
//        CacheEntry cacheEntry = new CacheEntry( location, null );//doesn't download jnlp to cache
        JNLPFile jnlpFile = new JNLPFile( location );//downloads the jnlp file to the cache.

        current = CacheUtil.isCurrent( location, null, null );
        System.out.println( "current = " + current );
    }
}
