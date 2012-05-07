// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import fj.P2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction using a Pattern (see Pattern class)
 *
 * @author Sam Reid
 */
public class PatternNode extends PNode implements IColor {
    private final Color color;

    public PatternNode( final FilledPattern representation, Color color ) {
        this.color = color;
        for ( P2<Shape, Boolean> o : representation.shapes ) {

            //Using a stroke more than 1 here prevents the right side from getting trimmed off during toImage
            addChild( new PhetPPath( o._1(), o._2() ? color : Color.white, new BasicStroke( 1.2f ), Color.black ) );
        }
    }

    @Override public Color getColor() {
        return color;
    }
}