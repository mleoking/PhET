/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.geom.Rectangle2D;

/**
 * Prints the piccolo tree.
 */

public class DebugPiccoloTree {
    private PCanvas pCanvas;

    public DebugPiccoloTree() {
        pCanvas = new PCanvas();
        pCanvas.getLayer().addChild( new PText( "Hello" ) );
        PPath child = new PPath( new Rectangle2D.Double( 0, 0, 10, 10 ) );
        child.addChild( new PText( "Child of rect" ) );
        pCanvas.getLayer().addChild( child );
    }

    private void printTree( PNode root, String pre ) {
        System.out.print( pre );
        String className = root.getClass().getName().substring( root.getClass().getName().lastIndexOf( '.' ) + 1 );
        String additionalInfo = getAdditionalInfo( root );
        System.out.println( className + " " + additionalInfo );
        //todo fails for unpackaged nodes.
        for( int i = 0; i < root.getChildrenReference().size(); i++ ) {
            PNode child = (PNode)root.getChildrenReference().get( i );
            printTree( child, pre + "\t" );
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

    public static void main( String[] args ) {
        new DebugPiccoloTree().start();
    }

    private void start() {
        PNode trueRoot = getTrueRoot();
        printTree( trueRoot, "" );
    }

    private PNode getTrueRoot() {
        PNode trueRoot = pCanvas.getRoot();
        while( trueRoot.getParent() != null ) {
            trueRoot = trueRoot.getParent();
        }
        return trueRoot;
    }
}
