/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/phetgraphics/CompositePhetGraphic.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:03 $
 */
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;

/**
 * This is a type of GraphicLayer set that is different from GraphicLayerSet in
 * the way it handles interaction. Whereas a GraphicLayerSet responds to the
 * getHandler( Point p ) message by delegating it to the PhetGraphics it contains,
 * CompositePhetGraphic responds to the message itself.
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
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
        if( getIgnoreMouse() == false && contains( p.x, p.y ) ) {
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