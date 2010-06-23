/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage;

import java.awt.geom.Rectangle2D;

/**
 * This interface represents the bounds (in screen coordinates) of an object that contains a Stage.
 * Currently this is only implemented in PlayArea, but future versions may include PNodes that act 
 * as StageContainers themselves.
 *
 * @see PlayArea
 * @author Sam Reid
 */
public interface StageContainer {
    /**
     * Returns the bounds of the StageContainer.  This must be given as a defensive copy, 
     * as clients should be able to mutate this as part of Piccolo's coordinate transform system.
     *
     * @return the bounds of the StageContainer.
     */
    Rectangle2D getContainerBounds();

    /**
     * Adds a Listener that receives callbacks for changes in the size of the StageContainer's bounds.
     *
     * @param listener the callback implementation
     */
    void addContainerBoundsChangeListener(Listener listener);

    /**
     * This interface indicates handling of a StageContainer bounds change.
     */
    public static interface Listener {
        /**
         * This method is called when the StageContainer bounds signal a change.
         */
        void stageContainerBoundsChanged();
    }
}
