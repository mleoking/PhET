// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class HorizontalBarElement extends ShapeElement {
    public HorizontalBarElement() {
        super(
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( DIM, 0, DIM, DIM ) );
                    add( new Rectangle2D.Double( DIM * 3, 0, DIM, DIM ) );
                }},
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( DIM * 2, 0, DIM, DIM ) );
                }}
        );
    }
}
