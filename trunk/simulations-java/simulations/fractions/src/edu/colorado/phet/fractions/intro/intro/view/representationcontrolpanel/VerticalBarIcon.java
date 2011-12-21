// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view.representationcontrolpanel;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.intro.intro.view.ChosenRepresentation;

/**
 * @author Sam Reid
 */
public class VerticalBarIcon extends ShapeIcon {
    public VerticalBarIcon( Property<ChosenRepresentation> chosenRepresentation ) {
        super(
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( 0, DIM * 2, DIM, DIM ) );
                }},
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, DIM, DIM, DIM ) );
                    add( new Rectangle2D.Double( 0, DIM * 3, DIM, DIM ) );
                }}, chosenRepresentation, ChosenRepresentation.VERTICAL_BAR
        );
    }
}
