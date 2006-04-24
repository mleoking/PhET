/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleChamber;
import edu.colorado.phet.mri.model.Spin;

import java.util.Random;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModuleA extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = "Simplified NMR";

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    /**
     * Constructor
     */
    public MriModuleA() {
        super( name, new SwingClock( delay, dt ) );

        // Sample Chamber
//        SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
//                                                                   MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
//                                                                   MriConfig.SAMPLE_CHAMBER_WIDTH,
//                                                                   MriConfig.SAMPLE_CHAMBER_HEIGHT ) );
//        addModelElement( sampleChamber );

        // Make some dipoles
        createDipoles( 20, ( (MriModel)getModel() ).getSampleChamber(), ( (MriModel)getModel() ) );

        // Set the initial view
        setEmRep( MriModuleA.WAVE_VIEW );
    }

    /**
     * Creates a number of dipoles and places them at random locations within the sample chamber
     *
     * @param numDipoles
     * @param sampleChamber
     * @param model
     */
    protected void createDipoles( int numDipoles, SampleChamber sampleChamber, MriModel model ) {
        Random random = new Random();

        boolean singleDipole = false;

        if( !singleDipole ) {

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
                    Spin spin = ( ++cnt ) % 2 == 0 ? Spin.UP : Spin.DOWN;
                    Dipole dipole = new Dipole();
                    dipole.setPosition( x, y );
                    dipole.setSpin( spin );
                    model.addModelElement( dipole );
                }
            }
        }
        else {
            Dipole dipole = new Dipole();
            dipole.setPosition( ( sampleChamber.getBounds().getWidth() / 2 ) + sampleChamber.getBounds().getX(),
                                sampleChamber.getBounds().getY() + 100 );
            dipole.setSpin( Spin.DOWN );
            model.addModelElement( dipole );
        }
    }

}
