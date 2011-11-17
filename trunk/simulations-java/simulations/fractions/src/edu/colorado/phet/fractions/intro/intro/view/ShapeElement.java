// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ShapeElement extends PNode {
    //characteristic length
    public static double DIM = 20;

    public ShapeElement( ArrayList<Shape> unfilled, ArrayList<Shape> filled ) {
        for ( Shape shape : unfilled ) {
            addChild( new PhetPPath( shape, Color.white, new BasicStroke( 1 ), Color.gray ) );
        }
        for ( Shape shape : filled ) {
            addChild( new PhetPPath( shape, Color.green, new BasicStroke( 1 ), Color.gray ) );
        }
    }
}
