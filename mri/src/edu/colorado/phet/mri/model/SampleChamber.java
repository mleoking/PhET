/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Rectangle2D;

/**
 * SampleChamber
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleChamber extends Particle {
    private Rectangle2D bounds;

    public SampleChamber( Rectangle2D bounds ) {
        this.bounds = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }

    public Rectangle2D getBounds() {
        return bounds;
    }
}
