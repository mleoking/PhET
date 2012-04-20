// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

/**
 * Class that represents a thermometer in the model.
 *
 * @author John Blanco
 */
public class Thermometer extends UserMovableModelElement {

    // Height and width of the face, which is square.
    public static final double WIDTH = 0.015; // In meters.
    public static final double HEIGHT = WIDTH * 4;

    /**
     * Get a rectangle the defines the current shape in model space.  By
     * convention for this simulation, the position is the middle of the
     * bottom of the block's defining rectangle.
     *
     * @return
     */
    public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                       position.get().getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    /**
     * Get the "raw shape" that should be used for depicting this block in the
     * view.  In this context, "raw" means that it is untranslated.  By
     * convention for this simulation, the point (0, 0) is the bottom center
     * of the block.
     *
     * @return
     */
    public static Shape getRawShape() {
        return new Rectangle2D.Double();
    }

    @Override public IUserComponent getUserComponent() {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
