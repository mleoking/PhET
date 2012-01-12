package edu.colorado.phet.buildtools.buildserver;

import java.util.StringTokenizer;

/**
 * Sample commands:
 * build moving-man -nosvnupdate -trunk /usr/local/reids/phet-svn/trunk
 * deploy moving-man -incrementminor -trunk /usr/local/reids/phet-svn/trunk
 * deploy moving-man -incrementmajor -trunk C:/workingcopy/phet/trunk
 * deploy-translations moving-man
 * deploy masses-and-springs -swf /usr/local/reids/build/masses-and-springs.swf
 * svn update //updates build tools, should do before a deploy
 *
 * @author Sam Reid
 */
public class BuildServer {
    //TODO: Convert to use getopt or other command line parsing library

    public static void main( String buildCommand ) {
        StringTokenizer stringTokenizer = new StringTokenizer( buildCommand, " " );
        String command = stringTokenizer.nextToken();//'build' or 'deploy'

    }
}
