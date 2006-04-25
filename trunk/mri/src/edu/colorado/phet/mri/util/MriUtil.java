/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.util;

import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.Spin;

import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * MriUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriUtil {


    /**
     * Creates a number of dipoles and locates them uniformly in a specified ellipse
     * @param shape
     * @param numDipoles
     * @return a list of dipoles
     */
    public static List createDipolesForEllipse( Ellipse2D shape, int numDipoles ) {
        ArrayList dipoles = new ArrayList( );
        double area = Math.PI * ( shape.getWidth() / 2 ) * ( shape.getHeight() / 2 );
        double areaPerDipole = area / ( numDipoles );
        double widthDipole = Math.sqrt( areaPerDipole );
        int cnt = 0;

        for( double y = widthDipole / 2; y < shape.getHeight(); y += widthDipole ) {
            for( double x = widthDipole / 2; x < shape.getWidth(); x += widthDipole ) {
                Dipole dipole = new Dipole();
                Point2D p = new Point2D.Double( shape.getX() + x, shape.getY() + y );
                if( shape.contains( p ) ) {
                    dipole.setPosition( shape.getX() + x, shape.getY() + y );
                    Spin spin = ( ++cnt ) % 2 == 0 ? Spin.UP : Spin.DOWN;
                    dipole.setSpin( spin );
                    dipoles.add( dipole );
                }
            }
        }
        return dipoles;
    }

}
