// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.umd.cs.piccolo.nodes.PText;

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
                int slicesInLastCake = numerator.get() % d;
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                if ( d == 2 || d == 12 ) {
                    for ( int i = 0; i < numFullCakes; i++ ) {
                        box.addChild( new WaterGlassNode( denominator.get(), denominator.get() ) );
                    }

                    if ( slicesInLastCake > 0 ) {
                        box.addChild( new WaterGlassNode( numerator.get(), denominator.get() ) );
                    }
                }
                else {
                    box.addChild( new PText( "No images for cake for denominator = " + d ) );
                }

                addChild( box );
            }
        }.observe( numerator, denominator );
    }
}