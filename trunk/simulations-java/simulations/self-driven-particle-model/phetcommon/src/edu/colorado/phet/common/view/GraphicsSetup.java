/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/GraphicsSetup.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view;

import java.awt.*;

/**
 * Operates on a Graphics2D object to prepare for rendering.
 * 
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public interface GraphicsSetup {
    /**
     * Applies this setup on the specified graphics object.
     *
     * @param graphics
     */
    void setup( Graphics2D graphics );
}
