// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;

/**
 * Shows a fraction as a set of pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {

    static final double scale = 2;
    private final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) ( 70 * scale ), (int) ( 70 * scale ) );

    public PieSetFractionNode( final Property<Integer> numerator, final Property<Integer> denominator, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();
                int numFullPies = numerator.get() / denominator.get();
                int slicesInLastPie = numerator.get() % denominator.get();
                int numSlices = denominator.get();
                HBox box = new HBox();

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
}
