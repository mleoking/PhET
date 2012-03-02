// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class VerticalBarsNode extends PNode {
    public VerticalBarsNode( final Fraction fraction, double scale ) {
        setScale( scale );

        double width = 75;
        double height = 75;

        int numFilledSlices = fraction.numerator;
        int numSlices = fraction.denominator;
        double sliceWidth = width / numSlices;
        for ( int i = 0; i < numSlices; i++ ) {
            addChild( new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, height ), i < numFilledSlices ? EqualityLabCanvas.lightBlue : Color.white, new BasicStroke( 1 ), Color.black ) );
        }
    }
}