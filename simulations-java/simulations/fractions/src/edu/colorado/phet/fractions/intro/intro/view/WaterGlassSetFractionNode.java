// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;

/**
 * Shows a fraction as a set of glasses of water.
 *
 * @author Sam Reid
 */
public class WaterGlassSetFractionNode extends VisibilityNode {

    public WaterGlassSetFractionNode( final Property<Integer> numerator, final Property<Integer> denominator, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                int d = denominator.get();

                //6 pies fit on the screen
                int distanceBetweenPies = 10;
                double spaceForPies = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenPies * 5;
                final double DIAMETER = spaceForPies / 6;

                removeAllChildren();
                int numFullCakes = numerator.get() / d;
                int lastCup = numerator.get() % d;
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                for ( int i = 0; i < numFullCakes; i++ ) {
                    box.addChild( new WaterGlassNode( denominator.get(), denominator.get() ) );
                }

                if ( lastCup > 0 ) {
                    box.addChild( new WaterGlassNode( numerator.get() % denominator.get(), denominator.get() ) );
                }

                addChild( box );
            }
        }.observe( numerator, denominator );
    }
}