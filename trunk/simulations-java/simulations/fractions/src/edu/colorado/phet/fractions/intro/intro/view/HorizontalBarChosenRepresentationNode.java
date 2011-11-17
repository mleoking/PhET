// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class HorizontalBarChosenRepresentationNode extends ChosenRepresentationNode {
    public HorizontalBarChosenRepresentationNode( Property<ChosenRepresentation> chosenRepresentation, final Property<Integer> numerator, final Property<Integer> denominator ) {
        super( chosenRepresentation, ChosenRepresentation.HORIZONTAL_BAR, numerator, denominator );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                int numBars = numerator.get() / denominator.get();
                if ( numerator.get() % denominator.get() != 0 ) {
                    numBars++;
                }
                int numElementsAdded = 0;
                double x = 0;
                for ( int i = 0; i < numBars; i++ ) {
                    for ( int k = 0; k < denominator.get(); k++ ) {
                        Color color = numElementsAdded < numerator.get() ? Color.green : Color.white;
                        addChild( new PhetPPath( new Rectangle2D.Double( x, 0, 50, 50 ), color, new BasicStroke( 2 ), Color.black ) );
                        x = x + 50;
                        numElementsAdded++;
                    }
                    x = x + 55;
                }
            }
        }.observe( numerator, denominator );
    }
}