// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;

/**
 * Shows the fraction as a set of vertical bars
 *
 * @author Sam Reid
 */
public class VerticalBarSetFractionNode extends RepresentationNode {
    public VerticalBarSetFractionNode( ObservableProperty<Representation> chosenRepresentation, final ObservableProperty<ContainerSet> containerSet ) {
        super( chosenRepresentation, Representation.VERTICAL_BAR );

        new RichSimpleObserver() {
            public void update() {

                //6 bars fit on the screen
                int distanceBetween = 25;
                double spaceForBars = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetween * 5;
                final double width = spaceForBars / 6;

                double barHeight = 200;
                int denominator = containerSet.get().denominator;
                double cellHeight = barHeight / denominator;

                removeAllChildren();

                double x = 0;

                //Location to start, working from the bottom up.
                final double initY = barHeight - cellHeight;

                //Start at the bottom and work your way up, like with water glasses
                double y = initY;
                for ( int i = 0; i < containerSet.get().containers.length(); i++ ) {
                    boolean containerEmpty = containerSet.get().getContainer( i ).isEmpty();
                    for ( int k = 0; k < denominator; k++ ) {
                        final CellPointer cp = new CellPointer( i, k );
                        boolean filled = containerSet.get().isFilled( cp );
                        Color color = filled ? FractionsIntroCanvas.FILL_COLOR : Color.white;
                        Color strokeColor = containerEmpty ? Color.lightGray : Color.black;
                        Stroke stroke = containerEmpty ? new BasicStroke( 1 ) : new BasicStroke( 2 );
                        addChild( new PhetPPath( new Rectangle2D.Double( x, y, width, cellHeight ), color, stroke, strokeColor ) );
                        y = y - cellHeight;
                    }
                    //Move to next bar
                    y = initY;
                    x = x + width + distanceBetween;
                }
            }
        }.observe( containerSet );
    }
}