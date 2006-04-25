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

import edu.colorado.phet.mri.util.MriUtil;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Tumor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Tumor extends Ellipse2D.Double {
    private List dipoles;

    public Tumor() {
    }

    public Tumor( double x, double y, double w, double h, double spacing ) {
        super( x, y, w, h );
        dipoles = MriUtil.createDipolesForEllipse( this, 20 );
    }

    public List getDipoles() {
        return dipoles;
    }
}
