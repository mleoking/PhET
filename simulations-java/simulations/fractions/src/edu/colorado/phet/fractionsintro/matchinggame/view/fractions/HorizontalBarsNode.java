// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction as a set of horizontal bars
 *
 * @author Sam Reid
 */
public class HorizontalBarsNode extends PNode implements IColor {
    private final Color color;

    public HorizontalBarsNode( final Fraction fraction, double scale, Color color ) {
        this.color = color;
        scale( scale );
        double width = 75;
        double height = 75;

        int numFilledSlices = fraction.numerator;
        int numSlices = fraction.denominator;
        double sliceHeight = height / numSlices;
        for ( int i = 0; i < numSlices; i++ ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, i * sliceHeight, width, sliceHeight ), i < numFilledSlices ? color : Color.white, new BasicStroke( 1 ), Color.darkGray ) );
        }
        //darker border
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 1.7f ), Color.black ) );
    }

    public Color getColor() {
        return color;
    }
}