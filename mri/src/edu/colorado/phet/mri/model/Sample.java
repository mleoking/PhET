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
 * Sample
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Sample extends Particle {
    public abstract Rectangle2D getBounds();
}
