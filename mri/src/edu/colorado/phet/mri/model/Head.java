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

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Head
 * <p>
 * A type of Sample that can have a tumor in it
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Head extends Sample {

    private Ellipse2D shape;
    private ArrayList dipoles = new ArrayList( );

    public Head( Ellipse2D shape ) {
        this.shape = shape;
    }

    public Ellipse2D getShape() {
        return shape;
    }

    public Rectangle2D getBounds() {
        return shape.getBounds();
    }

    public void addTumor( Tumor tumor, BaseModel model ) {
        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            if( tumor.contains( dipole.getPosition() )) {
                model.removeModelElement( dipole );
            }
        }
    }

    public void createDipoles( MriModel model, int numDipoles ) {

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
                    model.addModelElement( dipole );
                    dipoles.add( dipole );
                }
            }
        }
    }
}
