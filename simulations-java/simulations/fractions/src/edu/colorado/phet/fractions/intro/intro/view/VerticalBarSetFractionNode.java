// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * Shows the fraction as a set of vertical bars
 *
 * @author Sam Reid
 */
public class VerticalBarSetFractionNode extends ChosenRepresentationNode {
    public VerticalBarSetFractionNode( Property<ChosenRepresentation> chosenRepresentation, final Property<Integer> numerator, final Property<Integer> denominator ) {
        super( chosenRepresentation, ChosenRepresentation.VERTICAL_BAR );

        new RichSimpleObserver() {
            public void update() {

                //6 bars fit on the screen
                int distanceBetween = 25;
                double spaceForBars = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetween * 5;
                final double width = spaceForBars / 6;

                double barHeight = 200;
                double cellHeight = barHeight / denominator.get();

                removeAllChildren();

                int numBars = numerator.get() / denominator.get();
                if ( numerator.get() % denominator.get() != 0 ) {
                    numBars++;
                }
                int numElementsAdded = 0;
                double x = 0;
                double y = 0;
                for ( int i = 0; i < numBars; i++ ) {
                    for ( int k = 0; k < denominator.get(); k++ ) {
                        Color color = numElementsAdded < numerator.get() ? FractionsIntroCanvas.FILL_COLOR : Color.white;
                        addChild( new PhetPPath( new Rectangle2D.Double( x, y, width, cellHeight ), color, new BasicStroke( 2 ), Color.black ) );
                        y = y + cellHeight;
                        numElementsAdded++;
                    }
                    //Move to next bar
                    y = 0;
                    x = x + width + distanceBetween;
                }
            }
        }.observe( numerator, denominator );
    }
}