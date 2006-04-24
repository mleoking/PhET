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

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Head
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Head extends Sample {

    private Ellipse2D shape;

    public Head( Ellipse2D shape ) {
        this.shape = shape;
    }

    public Ellipse2D getShape() {
        return shape;
    }

    public Rectangle2D getBounds() {
        return shape.getBounds();
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
                }
            }
        }

        if( true ) {
            return;
        }

        // Lay out the dipoles in a grid
        double aspectRatio = MriConfig.SAMPLE_CHAMBER_WIDTH / MriConfig.SAMPLE_CHAMBER_HEIGHT;
        int numCols = 0;
        int numRows = 0;
        double testAspectRatio = -1;
        while( testAspectRatio < aspectRatio ) {
            numCols++;
            numRows = numDipoles / numCols;
            testAspectRatio = ( (double)numCols ) / numRows;
        }

        double colSpacing = MriConfig.SAMPLE_CHAMBER_WIDTH / ( numCols + 1 );
        double rowSpacing = MriConfig.SAMPLE_CHAMBER_HEIGHT / ( numRows + 1 );
//        int cnt = 0;
        for( int i = 1; i <= numRows; i++ ) {
            for( int j = 1; j <= numCols; j++ ) {
                double x = MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + j * colSpacing;
                double y = MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + i * rowSpacing;
                Spin spin = ( ++cnt ) % 2 == 0 ? Spin.UP : Spin.DOWN;
                Dipole dipole = new Dipole();
                dipole.setPosition( x, y );
                dipole.setSpin( spin );
                model.addModelElement( dipole );
            }
        }
    }
}
