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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.ModelElementGraphicManager;
import edu.colorado.phet.mri.view.PlaneWaveGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.view.PhotonGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModuleA extends Module {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = "Module A";
    private static int delay = (int)( 1000 / MriConfig.FPS );
    private static double dt = MriConfig.DT;
    private ModelElementGraphicManager graphicsManager;

    public static class EmRep {
        private EmRep() {
        }
    }

    public static EmRep PHOTON_VIEW = new EmRep();
    public static EmRep WAVE_VIEW = new EmRep();

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    /**
     * Constructor
     */
    public MriModuleA() {
        super( name, new SwingClock( delay, dt ) );

        MriModel model = new MriModel( getClock(), new Rectangle2D.Double( 0, 0, 1024, 768 ) );
        setModel( model );

        model.setSampleMaterial( SampleMaterial.HYDROGEN );

        // Control panel
        setControlPanel( new MriControlPanel( this ) );

        // Make the canvas, world node, and graphics manager
        PhetPCanvas simPanel = new PhetPCanvas( new Dimension( (int)( model.getBounds().getWidth() * MriConfig.scale ),
                                                               (int)( model.getBounds().getHeight() * MriConfig.scale ) ) );
        setSimulationPanel( simPanel );
        PNode worldNode = new PNode();
        simPanel.addWorldChild( worldNode );

        graphicsManager = new ModelElementGraphicManager( simPanel, worldNode );
        graphicsManager.scanModel( model );
        model.addListener( graphicsManager );

        // Make some dipoles
        createDipoles( 16, model.getSampleChamber(), model );

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
    private void createDipoles( int numDipoles, SampleChamber sampleChamber, MriModel model ) {
        Random random = new Random();

        boolean singleDipole = false;

        if( !singleDipole ) {
            for( int i = 0; i < numDipoles; i++ ) {
                double x = random.nextDouble() * ( sampleChamber.getBounds().getWidth() - 100 ) + sampleChamber.getBounds().getX() + 50;
                double y = random.nextDouble() * ( sampleChamber.getBounds().getHeight() - 60 ) + sampleChamber.getBounds().getY() + 20;
                Dipole dipole = new Dipole();
                dipole.setPosition( x, y );
                Spin spin = i % 2 == 0 ? Spin.UP : Spin.DOWN;
                dipole.setSpin( spin );
                model.addModelElement( dipole );
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


    public void setEmRep( EmRep emRep ) {
        if( emRep == WAVE_VIEW ) {
            graphicsManager.setAllOfTypeVisible( PhotonGraphic.class, false );
            graphicsManager.setAllOfTypeVisible( PlaneWaveGraphic.class, true );
        }
        else if( emRep == PHOTON_VIEW ) {
            graphicsManager.setAllOfTypeVisible( PhotonGraphic.class, true );
            graphicsManager.setAllOfTypeVisible( PlaneWaveGraphic.class, false );
        }
    }
}
