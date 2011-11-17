// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class VerticalBarElement extends ShapeElement {
    public VerticalBarElement() {
        super(
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( 0, DIM * 2, DIM, DIM ) );
                }},
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, DIM, DIM, DIM ) );
                    add( new Rectangle2D.Double( 0, DIM * 3, DIM, DIM ) );
                }}
        );
    }
}
