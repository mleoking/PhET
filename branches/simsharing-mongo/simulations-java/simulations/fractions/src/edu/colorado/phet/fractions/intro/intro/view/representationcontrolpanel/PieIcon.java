// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view.representationcontrolpanel;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.intro.intro.view.ChosenRepresentation;

/**
 * @author Sam Reid
 */
public class PieIcon extends ShapeIcon {
    public PieIcon( Property<ChosenRepresentation> chosenRepresentation ) {
        super( new ArrayList<Shape>() {{
                   add( new Arc2D.Double( 0, 0, DIM * 2, DIM * 2, 90, 90, Arc2D.PIE ) );
                   add( new Arc2D.Double( 0, 0, DIM * 2, DIM * 2, 270, 90, Arc2D.PIE ) );
               }},
               new ArrayList<Shape>() {{
                   add( new Arc2D.Double( 0, 0, DIM * 2, DIM * 2, 0, 90, Arc2D.PIE ) );
                   add( new Arc2D.Double( 0, 0, DIM * 2, DIM * 2, 180, 90, Arc2D.PIE ) );
               }}, chosenRepresentation, ChosenRepresentation.PIE
        );
    }
}
