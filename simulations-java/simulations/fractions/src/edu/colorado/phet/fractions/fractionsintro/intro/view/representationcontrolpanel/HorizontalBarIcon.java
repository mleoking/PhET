// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;

/**
 * Shows the icon on the representation control panel for the horizontal bars.
 *
 * @author Sam Reid
 */
public class HorizontalBarIcon extends ShapeIcon {
    public HorizontalBarIcon( SettableProperty<Representation> chosenRepresentation, Color color ) {
        super( new ArrayList<Shape>(),
               new ArrayList<Shape>() {{
                   add( new Rectangle2D.Double( DIM, 0, DIM * 4, DIM ) );
               }}, new Rectangle2D.Double( DIM, 0, DIM * 4, DIM ),
               chosenRepresentation, Representation.HORIZONTAL_BAR, color
        );
    }
}