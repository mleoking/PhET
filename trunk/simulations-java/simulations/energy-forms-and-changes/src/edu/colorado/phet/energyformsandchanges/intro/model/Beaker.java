// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Model element that represents a beaker in the simulation.
 *
 * @author John Blanco
 */
public class Beaker extends ModelElement {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.085; // In meters.
    private static final double HEIGHT = WIDTH;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Point2D position = new Point2D.Double( 0, 0 );

    // Property that is used to control the amount of fluid in the beaker.
    public final Property<Double> fluidLevel = new Property<Double>( 0.3 );

    // Surface upon which any model elements will sit.
    private final Property<HorizontalSurface> topSurface;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param position The position in model space where this burner exists.
     *                 By convention for this simulation, the position is
     *                 defined as the bottom center of the model element.
     */
    public Beaker( Point2D position ) {
        this.position.setLocation( position );
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMinY(), this ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.getX() - WIDTH / 2,
                                       position.getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }
}
