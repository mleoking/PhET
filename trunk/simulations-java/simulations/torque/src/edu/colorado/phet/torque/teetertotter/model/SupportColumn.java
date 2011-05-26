// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * This is a column that can be used to support one of the ends of the plank.
 *
 * @author John Blanco
 */
public class SupportColumn extends ModelObject {

    // Length of the base of the column
    private static final double WIDTH = 0.35;

    // Property that indicates whether this column is able to support things.
    // When this property is false, things (such as the plank) should just
    // pass through the column.
    private final BooleanProperty supporting = new BooleanProperty( true );

    /**
     * Constructor.
     *
     * @param height
     * @param initialCenterX
     */
    public SupportColumn( double height, double initialCenterX ) {
        super( new Rectangle2D.Double( initialCenterX - WIDTH / 2, 0, WIDTH, height ) );
    }

    public void addSupportingPropertyObserver( ChangeObserver<Boolean> observer ) {
        supporting.addObserver( observer );
    }
}
