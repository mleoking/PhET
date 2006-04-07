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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.*;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModuleA extends Module {

    private static String name = "Module A";
    private static double FPS = 25;
    private static int delay = (int)( 1000 / FPS );

    /**
     * Constructor
     */
    public MriModuleA() {
        super( name, new SwingClock( delay, 1 ) );

        PNode worldNode = new PNode();
        MriModel model = new MriModel( new GraphicManager( worldNode ));
        setModel( model );
        setControlPanel( new MriControlPanel( this ) );

        PhetPCanvas simPanel = new PhetPCanvas( new Dimension( (int)( model.getBounds().getWidth() * MriConfig.scale ),
                                                               (int)( model.getBounds().getHeight() * MriConfig.scale ) ) );
        setSimulationPanel( simPanel );
        simPanel.addWorldChild( worldNode );

        // Make some dipoles
        createDipoles( 15, model.getSampleChamber(), model );

//        BFieldGraphicPanel bfgp = new BFieldGraphicPanel( model.getLowerMagnet() );
//        JDialog jdlg = new JDialog( PhetUtilities.getPhetFrame(), false );
//        jdlg.setContentPane( bfgp );
//        jdlg.pack();
//        jdlg.setVisible( true );

    }

    /**
     * Creates a number of dipoles and places them at random locations within the sample chamber
     * @param numDipoles
     * @param sampleChamber
     * @param model
     */
    private void createDipoles( int numDipoles, SampleChamber sampleChamber, MriModel model ) {
        Random random = new Random();
        for( int i = 0; i < numDipoles; i++ ) {
            double x = random.nextDouble() * ( sampleChamber.getBounds().getWidth() - 100 ) + sampleChamber.getBounds().getX() + 50;
            double y = random.nextDouble() * ( sampleChamber.getBounds().getHeight() - 60) + sampleChamber.getBounds().getY() + 20;
            Dipole dipole = new Dipole();
            dipole.setPosition( x, y );

            dipole.setSpin( i % 2 == 0 ? Spin.UP : Spin.DOWN );
//            dipole.setSpin( random.nextBoolean() ? Spin.UP : Spin.DOWN );

            model.addModelElement( dipole );
        }
    }
}
