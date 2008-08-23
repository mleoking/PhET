/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.phetgraphics;

import java.awt.*;

/**
 * This is a type of GraphicLayer set that is different from GraphicLayerSet in
 * the way it handles interaction. Whereas a GraphicLayerSet responds to the
 * getHandler( Point p ) message by delegating it to the PhetGraphics it contains,
 * CompositePhetGraphic responds to the message itself.
 *
 * @author ?
 * @version $Revision$
 */
public class CompositePhetGraphic extends GraphicLayerSet {

    public CompositePhetGraphic( Component component ) {
        super( component );
    }

    /**
     * Tells if this object contains a specified point, for purposes of
     * event handling.
     *
     * @param p The specified point.
     * @return The PhetGraphic responsible for handling the event.
     */
    protected PhetGraphic getHandler( Point p ) {
        if ( getIgnoreMouse() == false && contains( p.x, p.y ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    //---------------------------------------------------------
    // For Java Beans conformance
    //---------------------------------------------------------

    public CompositePhetGraphic() {
    }
}