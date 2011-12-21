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
public class HorizontalBarIcon extends ShapeIcon {
    public HorizontalBarIcon( Property<ChosenRepresentation> chosenRepresentation ) {
        super(
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( DIM, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( DIM * 3, 0, DIM, DIM ) );
                }},
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( DIM * 2, 0, DIM, DIM ) );
                }},
                chosenRepresentation, ChosenRepresentation.HORIZONTAL_BAR
        );
    }
}
