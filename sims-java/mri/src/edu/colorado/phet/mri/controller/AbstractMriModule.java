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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.BFieldIndicatorB;
import edu.colorado.phet.mri.view.ModelElementGraphicManager;
import edu.colorado.phet.mri.view.PlaneWaveGraphic;
import edu.colorado.phet.mri.view.PhotonGraphic;
import edu.colorado.phet.piccolo.DeferredInitializationModule;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * AbstractMriModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class AbstractMriModule extends DeferredInitializationModule {
    protected static int delay = (int)( 1000 / MriConfig.FPS );
    protected static double dt = MriConfig.DT;
    protected ModelElementGraphicManager graphicsManager;
    public static EmRep PHOTON_VIEW = new EmRep();
    public static EmRep WAVE_VIEW = new EmRep();
    private PNode worldNode;
    private Sample sample;

    /**
     * @param name
     * @param clock
     */
    public AbstractMriModule( String name, IClock clock, Sample sample ) {
        super( name, clock );
        this.sample = sample;
    }

    abstract public boolean auxiliarySquiggleVisible();

    protected void init() {
        MriModel model = new MriModel( getClock(), new Rectangle2D.Double( 0, 0, 1000, 768 ), sample );
        setModel( model );

        model.setSampleMaterial( SampleMaterial.HYDROGEN );

        // Make the canvas, world node, and graphics manager
        Dimension renderingSize = new Dimension( (int)( model.getBounds().getWidth() * MriConfig.scale ),
                                                 (int)( model.getBounds().getHeight() * MriConfig.scale ) );
        PhetPCanvas simPanel = new PhetPCanvas( renderingSize );
//        System.out.println( "renderingSize = " + renderingSize );
        setSimulationPanel( simPanel );
        worldNode = new PNode();
        simPanel.addScreenChild( worldNode );

        graphicsManager = new ModelElementGraphicManager( this, simPanel, worldNode );
        graphicsManager.scanModel( model );
        model.addListener( graphicsManager );

        // Add the field represetntation arrows
        createFieldStrengthArrows( model );
    }

    /**
     *
     */
    private void createFieldStrengthArrows( MriModel model ) {
        int numArrowsX = 7;
        int numArrowsY = 7;

        // The constants in the lines below specify the offsets outside the bounds of the main magnets
        // where the arrows are to be placed
        Point2D ulc = new Point2D.Double( model.getUpperMagnet().getBounds().getMinX() - 50,
                                          model.getUpperMagnet().getBounds().getMaxY() - 25 );
        Point2D lrc = new Point2D.Double( model.getLowerMagnet().getBounds().getMaxX() + 50,
                                          model.getLowerMagnet().getBounds().getMinY() + 25 );
        double spacingX = ( lrc.getX() - ulc.getX() ) / numArrowsX;
        double spacingY = ( lrc.getY() - ulc.getY() ) / numArrowsY;

        for( int i = 0; i < numArrowsX; i++ ) {
            for( int j = 0; j < numArrowsY; j++ ) {
                Point2D location = new Point2D.Double( ulc.getX() + spacingX * ( i + 0.5 ),
                                                       ulc.getY() + spacingY * ( j + 0.5 ) );
                BFieldIndicatorB bfi = new BFieldIndicatorB( (MriModel)getModel(),
                                                             location,
                                                             70,
                                                             null );
                bfi.setOffset( location );
                getGraphicsManager().addGraphic( bfi, getGraphicsManager().getControlLayer() );
            }
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

    protected PNode getWorldNode() {
        return worldNode;
    }

    protected ModelElementGraphicManager getGraphicsManager() {
        return graphicsManager;
    }

    /**
     * Creates a number of dipoles and places them at random locations within the sample chamber
     *
     * @param sample
     * @param model
     */
    protected void createSingleDipole( Sample sample, MriModel model ) {
        Dipole dipole = new Dipole();
        dipole.setPosition( ( sample.getBounds().getWidth() / 2 ) + sample.getBounds().getX(),
                            sample.getBounds().getY() + 100 );
        dipole.setSpin( Spin.UP );
        model.addModelElement( dipole );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private static class EmRep {
        private EmRep() {
        }
    }
}
