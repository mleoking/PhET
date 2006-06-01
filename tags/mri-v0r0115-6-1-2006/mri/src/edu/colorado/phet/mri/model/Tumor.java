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

    public Tumor( double x, double y, double w, double h ) {
        super( x, y, w, h );
        List tempDipoles = MriUtil.createDipolesForEllipse( this, 20 );

        // Make them all TumorDipoles
        dipoles = new ArrayList();
        for( int i = 0; i < tempDipoles.size(); i++ ) {
            Dipole dipole = (Dipole)tempDipoles.get( i );
            TumorDipole tumorDipole = new TumorDipole();
            tumorDipole.setPosition( dipole.getPosition() );
            tumorDipole.setSpin( dipole.getSpin() );
            dipoles.add( tumorDipole );
        }
        tempDipoles.clear();
    }

    public List getDipoles() {
        return dipoles;
    }
}
