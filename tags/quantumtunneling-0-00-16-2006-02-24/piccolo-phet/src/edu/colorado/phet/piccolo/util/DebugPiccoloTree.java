/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.util;

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
        for( int i = 0; i < root.getChildrenReference().size(); i++ ) {
            PNode child = (PNode)root.getChildrenReference().get( i );
            printTree( child, pre + "\t" );
        }
    }

    private void printAll( String className, String additionalInfo ) {
        String total = ( className + " " + additionalInfo );
        System.out.println( total );
    }

    private String getClassName( PNode root ) {
        if( root.getClass().getName().indexOf( '.' ) >= 0 ) {
            return root.getClass().getName().substring( root.getClass().getName().lastIndexOf( '.' ) + 1 );
        }
        else {
            return root.getClass().getName();
        }
    }

    private String getAdditionalInfo( PNode root ) {
        if( root instanceof PText ) {
            PText pText = (PText)root;
            return "\"" + pText.getText() + "\"";
        }
        else {
        }
        return "";
    }

}