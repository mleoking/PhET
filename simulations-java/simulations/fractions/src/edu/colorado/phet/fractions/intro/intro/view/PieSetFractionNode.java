// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction as a set of pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {

    public PieSetFractionNode( final Property<Integer> numerator, final Property<Integer> denominator, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                //6 pies fit on the screen
                int distanceBetweenPies = 10;
                double spaceForPies = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenPies * 5;
                final double DIAMETER = spaceForPies / 6;
                final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) DIAMETER, (int) DIAMETER );

                removeAllChildren();
                int numFullPies = numerator.get() / denominator.get();
                int slicesInLastPie = numerator.get() % denominator.get();
                int numSlices = denominator.get();
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                for ( int i = 0; i < numFullPies; i++ ) {
                    final PieChartNode.PieValue[] slices = new PieChartNode.PieValue[numSlices];
                    for ( int j = 0; j < slices.length; j++ ) {
                        slices[j] = new PieChartNode.PieValue( 1.0 / numSlices, FractionsIntroCanvas.FILL_COLOR );
                    }
                    box.addChild( new PieChartNode( slices, PIE_SIZE ) );
                }

                if ( slicesInLastPie > 0 ) {
                    final PieChartNode.PieValue[] slices = new PieChartNode.PieValue[numSlices];
                    for ( int j = 0; j < slices.length; j++ ) {
                        slices[j] = new PieChartNode.PieValue( 1.0 / numSlices, j < slicesInLastPie ? FractionsIntroCanvas.FILL_COLOR : Color.white );
                    }
                    box.addChild( new PieChartNode( slices, PIE_SIZE ) );
                }

                addChild( box );
            }
        }.observe( numerator, denominator );
    }

    //For layout of the pies, necessary because when sliced into different regions, the bounding boxes can extend beyond the pie.
    //So instead we ignore the full bounds and just space based on diameter
    private static class SpacedHBox extends RichPNode {
        private final double spacing;
        private double x = 0;

        public SpacedHBox( double spacing ) {
            this.spacing = spacing;
        }

        @Override public void addChild( PNode child ) {
            child.setOffset( x, child.getYOffset() );
            super.addChild( child );
            x += spacing;
        }
    }
}
