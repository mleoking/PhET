// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.util;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Prints the piccolo tree.
 */
public class DebugPiccoloTree {
    public DebugPiccoloTree() {
    }

    public void printTree( PNode root ) {
        printTree( root, "" );
    }

    public void printTree( PNode root, String pre ) {
        System.out.print( pre );

        String className = getClassName( root );
        String additionalInfo = getAdditionalInfo( root );
        printAll( className, additionalInfo );
        for ( int i = 0; i < root.getChildrenReference().size(); i++ ) {
            printTree( (PNode) root.getChildrenReference().get( i ), pre + "\t" );
        }
        if ( root instanceof PCamera ) {
            PCamera r = (PCamera) root;
            for ( int i = 0; i < r.getLayerCount(); i++ ) {
                printTree( r.getLayer( i ), pre + "\t" );
            }
        }
    }

    public static int piccoloCount( PNode camera ) {
        int sum = 1;
        if ( camera instanceof PCamera ) {
            PCamera cam = (PCamera) camera;
            for ( int i = 0; i < cam.getLayerCount(); i++ ) {
                PNode layer = cam.getLayer( i );
                sum += piccoloCount( layer );
            }
        }
        for ( int i = 0; i < camera.getChildrenCount(); i++ ) {
            sum += piccoloCount( camera.getChild( i ) );
        }
        return sum;
    }

    private void printAll( String className, String additionalInfo ) {
        String total = ( className + " " + additionalInfo );
        System.out.println( total );
    }

    private String getClassName( PNode root ) {
        if ( root.getClass().getName().indexOf( '.' ) >= 0 ) {
            return root.getClass().getName().substring( root.getClass().getName().lastIndexOf( '.' ) + 1 );
        }
        else {
            return root.getClass().getName();
        }
    }

    private String getAdditionalInfo( PNode root ) {
        if ( root instanceof PText ) {
            PText pText = (PText) root;
            return "\"" + pText.getText() + "\"";
        }
        else {
        }
        return "";
    }

}