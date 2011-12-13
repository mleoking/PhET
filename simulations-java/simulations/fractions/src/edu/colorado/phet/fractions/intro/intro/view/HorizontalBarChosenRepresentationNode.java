// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * Shows the fraction as a set of bars that lie horizontally, taking up two rows if more than 3
 *
 * @author Sam Reid
 */
public class HorizontalBarChosenRepresentationNode extends ChosenRepresentationNode {
    private static final double NUM_BARS_PER_LINE = 3;
    private static final double DISTANCE_BETWEEN_BARS_Y = 20;

    public HorizontalBarChosenRepresentationNode( Property<ChosenRepresentation> chosenRepresentation, final Property<Integer> numerator, final Property<Integer> denominator ) {
        super( chosenRepresentation, ChosenRepresentation.HORIZONTAL_BAR );

        new RichSimpleObserver() {
            public void update() {
                removeAllChildren();

                final int distanceBetweenBars = 55;

                //Find how much space we can use for 3 bars horizontally
                double spaceForBars = FractionsIntroCanvas.STAGE_SIZE.getWidth() - FractionsIntroCanvas.INSET * 2 - distanceBetweenBars * 2;
                double totalBarWidth = spaceForBars / NUM_BARS_PER_LINE;
                final double cellWidth = totalBarWidth / denominator.get();

                int numBars = numerator.get() / denominator.get();
                if ( numerator.get() % denominator.get() != 0 ) {
                    numBars++;
                }
                int numElementsAdded = 0;
                double x = 0;
                double y = 0;
                for ( int i = 0; i < numBars; i++ ) {
                    double barHeight = 50;
                    for ( int k = 0; k < denominator.get(); k++ ) {
                        Color color = numElementsAdded < numerator.get() ? FractionsIntroCanvas.FILL_COLOR : Color.white;
                        addChild( new PhetPPath( new Rectangle2D.Double( x, y, cellWidth, barHeight ), color, new BasicStroke( 2 ), Color.black ) );
                        x = x + cellWidth;
                        numElementsAdded++;
                    }

                    //go to next bar
                    x = x + distanceBetweenBars;

                    //Go to next row
                    if ( i == 2 ) {
                        y = y + barHeight + DISTANCE_BETWEEN_BARS_Y;
                        x = 0;
                    }
                }
            }
        }.observe( numerator, denominator );
    }
}