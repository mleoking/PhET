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
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.*;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

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

        MriModel model = new MriModel();
        setModel( model );
        setControlPanel( new MriControlPanel( this ) );
        PhetPCanvas simPanel = new PhetPCanvas( new Dimension( (int)( model.getBounds().getWidth() * MriConfig.scale ),
                                                               (int)( model.getBounds().getHeight() * MriConfig.scale ) ) );
        setSimulationPanel( simPanel );
        PNode worldNode = new PNode();
        simPanel.addWorldChild( worldNode );

        // Add a GraphicManager for the simulation panel
        model.addListener( new GraphicManager( worldNode ) );

        // Make the basic elements of the model
        makeBaseModelElements( model );

    }

    /**
     * Creates the model elements that constitute the basic model and adds them to the model
     *
     * @param model
     */
    private void makeBaseModelElements( MriModel model ) {
        // Sample Chamber
        SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.sampleChamberLocation.getX(),
                                                                                 MriConfig.sampleChamberLocation.getY(),
                                                                                 MriConfig.sampleChamberWidth,
                                                                                 MriConfig.sampleChamberHeight ) );
        model.addModelElement( sampleChamber );

        // Magnets
        double magnetHeight = 50;
        Point2D upperMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() - magnetHeight * 1.5 );
        Electromagnet upperMagnet = new Electromagnet( upperMagnetLocation,
                                                       sampleChamber.getBounds().getWidth(), magnetHeight );
        model.setUpperMagnet( upperMagnet );
        Point2D lowerMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() + sampleChamber.getBounds().getHeight() + magnetHeight * 1.5 );
        Electromagnet lowerMagnet = new Electromagnet( lowerMagnetLocation,
                                                       sampleChamber.getBounds().getWidth(), magnetHeight );
        model.setLowerMagnet( lowerMagnet );

        // Make some dipoles
        createDipoles( 20, sampleChamber, model );
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
            double y = random.nextDouble() * ( sampleChamber.getBounds().getHeight() - 40) + sampleChamber.getBounds().getY() + 20;
            Dipole dipole = new Dipole();
            dipole.setPosition( x, y );

            dipole.setSpin( random.nextBoolean() ? Spin.UP : Spin.DOWN );

            model.addModelElement( dipole );
        }
    }
}
