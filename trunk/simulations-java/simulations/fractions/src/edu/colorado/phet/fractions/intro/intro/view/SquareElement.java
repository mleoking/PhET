// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class SquareElement extends ShapeElement {
    public SquareElement( Property<ChosenRepresentation> chosenRepresentation ) {
        super(
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( DIM, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( DIM, DIM, DIM, DIM ) );
                }},
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( 0, DIM, DIM, DIM ) );
                }}, chosenRepresentation, ChosenRepresentation.SQUARE
        );
    }
}
