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

import java.awt.geom.Ellipse2D;

/**
 * Tumor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Tumor extends Ellipse2D.Double {

    public Tumor() {
    }

    public Tumor( double x, double y, double w, double h ) {
        super( x, y, w, h );
    }
}
