/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.BeamControl2;
import edu.colorado.phet.lasers.controller.Kaboom;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.UniversalLaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.LampGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class MultipleAtomModule extends BaseLaserModule {

    private double s_maxSpeed = 0.1;
    private ArrayList atoms;
    private UniversalLaserControlPanel laserControlPanel;
    private Kaboom kaboom;
    private BeamControl2 pumpBeamControl;

    /**
     *
     */
    public MultipleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleAtomModule" ), clock );

        setThreeEnergyLevels( true );

        // Set the control panel
        laserControlPanel = new UniversalLaserControlPanel( this, clock );
        setControlPanel( laserControlPanel );

        // Set the size of the cavity
        ResonatingCavity cavity = getCavity();
        Rectangle2D cavityBounds = cavity.getBounds();

        // Set up the beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() );
        CollimatedBeam seedBeam = ( (LaserModel)getModel() ).getSeedBeam();

        Rectangle2D.Double seedBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                    s_boxWidth + s_laserOffsetX * 2, s_boxHeight );
        seedBeam.setBounds( seedBeamBounds );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        seedBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Rectangle2D.Double pumpingBeamBounds = new Rectangle2D.Double( cavity.getBounds().getX() + Photon.RADIUS,
                                                                       cavity.getBounds().getY() / 2,
                                                                       cavityBounds.getWidth() - Photon.RADIUS * 2,
                                                                       s_boxHeight + s_laserOffsetX * 2 );
        pumpingBeam.setBounds( pumpingBeamBounds );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        // Set the max pumping rate
        pumpingBeam.setMaxPhotonsPerSecond( LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE );
        // Start with the beam turned all the way down
        pumpingBeam.setPhotonsPerSecond( 0 );

        // Only the pump beam is enabled
        seedBeam.setEnabled( false );
        pumpingBeam.setEnabled( true );

        // Set up the graphics
        BufferedImage gunBI = null;
        try {
            gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Pumping beam lamps. Note that the images start out horizontal, and then are rotated. This accounts for
        // some funny looking code
        int numLamps = 8;
        double yOffset = 10;
        // The lamps should span the cavity
        double pumpScaleX = ( ( pumpingBeamBounds.getY() ) - yOffset ) / gunBI.getWidth();
        double pumpScaleY = ( pumpingBeamBounds.getWidth() / numLamps ) / gunBI.getHeight();
        AffineTransformOp atxOp2 = new AffineTransformOp( AffineTransform.getScaleInstance( pumpScaleX, pumpScaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage pumpBeamImage = atxOp2.filter( gunBI, null );
        for( int i = 0; i < numLamps; i++ ) {
            AffineTransform tx = new AffineTransform();
            tx.translate( pumpingBeamBounds.getX() + pumpBeamImage.getHeight() * ( i + 1 ),
                          yOffset );
            tx.rotate( Math.PI / 2 );
            BufferedImage img = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( pumpBeamImage, null );
            PhetImageGraphic imgGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), img, tx );
            addGraphic( imgGraphic, LaserConfig.PHOTON_LAYER + 1 );
        }

        // Add the beam control
        Point pumpControlLocation = new Point( (int)( cavity.getBounds().getX() - 150 ), 10 );
        pumpBeamControl = new BeamControl2( getApparatusPanel(), pumpControlLocation, pumpingBeam,
                                            LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE,
                                            null, null );
        getApparatusPanel().addGraphic( pumpBeamControl );

        // Set the averaging time for the energy levels display
        setEnergyLevelsAveragingPeriod( 2000 );

        laserControlPanel.setUpperTransitionView( BaseLaserModule.PHOTON_CURTAIN );

        // Add a kaboom element
        kaboom = new Kaboom( this );
        getModel().addModelElement( kaboom );

        // Add some atoms
        Atom atom = null;
        atoms = new ArrayList();
//        int numAtoms = 0;
        int numAtoms = 30;
        for( int i = 0; i < numAtoms; i++ ) {
            int numEnergyLevels = getThreeEnergyLevels() ? 3 : 2;
            atom = new Atom( getModel(), numEnergyLevels );
            boolean placed = false;

            // Place atoms so they don't overlap
            int tries = 0;
            do {
                placed = true;
                atom.setPosition( ( cavityBounds.getX() + ( Math.random() ) * ( cavityBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                                  ( cavityBounds.getY() + ( Math.random() ) * ( cavityBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
                atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                                  (float)( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
                }
                if( tries > 1000 ) {
                    System.out.println( "Unable to place all atoms" );
                    break;
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * Clears out the current Kaboom instance, and creates a new one
     */
    public void reset() {
        // Superclass behavior
        super.reset();

        // Clear the old kaboom stuff off the apparatus panel and out of the model
        getModel().removeModelElement( kaboom );
        kaboom.clearGraphics( getApparatusPanel() );

        // Make a new kaboom, ready for firing
        kaboom = new Kaboom( this );
        getModel().addModelElement( kaboom );
    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );
        super.setThreeEnergyLevels( true );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
    }


    /**
     * Extends the base class behavior. After letting the base class behavior
     * happen, the pumping beam is enabled, since this is the only beam shown
     * on the multiple atom panel.
     *
     * @param threeEnergyLevels
     */
    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        super.setThreeEnergyLevels( threeEnergyLevels );
        this.threeEnergyLevels = threeEnergyLevels;
        getLaserModel().getPumpingBeam().setEnabled( true );
        if( atoms != null ) {
            int numEnergyLevels = threeEnergyLevels ? 3 : 2;
            for( int i = 0; i < atoms.size(); i++ ) {
                Atom atom = (Atom)atoms.get( i );
                atom.setNumEnergyLevels( numEnergyLevels );
            }
        }
    }

    public void setSwingComponentsVisible( boolean areVisible ) {
        super.setSwingComponentsVisible( areVisible );
        pumpBeamControl.setVisible( areVisible );
    }
}
