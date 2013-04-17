// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;

/**
 * Shows an ellipse (pie) icon
 *
 * @author Sam Reid
 */
public class PieIcon extends ShapeIcon {
    public PieIcon( SettableProperty<Representation> chosenRepresentation, Color color ) {
        super( new ArrayList<Shape>(),
               new ArrayList<Shape>() {{
                   add( new Ellipse2D.Double( 0, 0, DIM * 2, DIM * 2 ) );
               }}, new Ellipse2D.Double( 0, 0, DIM * 2, DIM * 2 ), chosenRepresentation, Representation.PIE, color
        );
    }
}
