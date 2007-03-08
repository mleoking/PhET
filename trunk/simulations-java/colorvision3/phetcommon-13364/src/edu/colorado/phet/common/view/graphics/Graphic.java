/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;

/**
 * Represents an object that can be rendered on a Graphics2D.
 * 
 * @author ?
 * @version $Revision$
 */
public interface Graphic {
    /**
     * Render this Graphic on a Graphics2D.
     *
     * @param g the Graphics2D on which to paint.
     */
    public void paint( Graphics2D g );
}
