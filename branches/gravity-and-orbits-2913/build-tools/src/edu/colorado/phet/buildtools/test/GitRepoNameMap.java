// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.test;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class GitRepoNameMap {
    public static void main( String[] args ) {
        ArrayList<String> all = new ArrayList<String>() {{
            add( "build-tools build-tools" );
            add( "simulations-flash/common flash-common" );
            add( "simulations-flash/common-as3 flash-common-as3" );
            add( "simulations-flash/flash-launcher flash-launcher" );
            addAll( listAll( "simulations-flash/simulations" ) );
            add( "simulations-flex/common flex-common" );
            addAll( listAll( "simulations-flex/simulations" ) );
            addAll( listAll( "simulations-java/common" ) );
            addAll( listAll( "simulations-java/simulations" ) );
            add( "statistics statistics" );
            addAll( listAll( "team" ) );
            addAll( listAll( "util" ) );
            add( "wicket-website website" );
        }};
        for ( String s : all ) {
            System.out.println( "./split.sh full " + s );
        }
    }

    private static ArrayList<String> listAll( String subpath ) {
        ArrayList<String> a = new ArrayList<String>();
        File file = new File( "C:/workingcopy/phet/trunk/" + subpath );
        for ( File arg : file.listFiles() ) {
            if ( !arg.getName().equals( ".svn" ) && arg.isDirectory() ) {
                String dstName = arg.getName();
                if ( dstName.equals( "phetcommon" ) ) {
                    dstName = "java-common";
                }
                String s = subpath + "/" + arg.getName() + " " + dstName;

                a.add( s );
            }
        }
        return a;
    }
    //TODO: simulations-flash/contrib/aswing, aswing-a3, away3d, box2d

}
