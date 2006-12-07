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

/**
 * SampleChamber
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleChamber extends Sample {
    private Rectangle2D bounds;

    public SampleChamber( Rectangle2D bounds ) {
        this.bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void createDipoles( MriModel model, int numDipoles ) {
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
        int cnt = 0;
        for( int i = 1; i <= numRows; i++ ) {
            for( int j = 1; j <= numCols; j++ ) {
                double x = MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + j * colSpacing;
                double y = MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + i * rowSpacing;
                Spin spin = ( ++cnt ) % 2 == 0 ? Spin.DOWN : Spin.UP;
                Dipole dipole = new Dipole();
                dipole.setPosition( x, y );
                dipole.setSpin( spin );
                model.addModelElement( dipole );
            }
        }

    }
}
