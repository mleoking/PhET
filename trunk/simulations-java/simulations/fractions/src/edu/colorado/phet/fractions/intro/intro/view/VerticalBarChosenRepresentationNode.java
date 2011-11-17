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
public class VerticalBarChosenRepresentationNode extends ChosenRepresentationNode {
    double dim = 30;

    public VerticalBarChosenRepresentationNode( Property<ChosenRepresentation> chosenRepresentation, final Property<Integer> numerator, final Property<Integer> denominator ) {
        super( chosenRepresentation, ChosenRepresentation.VERTICAL_BAR );

        new RichSimpleObserver() {
            @Override public void update() {
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
                        addChild( new PhetPPath( new Rectangle2D.Double( x, y, dim, dim ), color, new BasicStroke( 2 ), Color.black ) );
                        y = y + dim;
                        numElementsAdded++;
                    }
                    y = 0;
                    x = x + ( dim + 5 ) * 2;
                }
            }
        }.observe( numerator, denominator );
    }
}