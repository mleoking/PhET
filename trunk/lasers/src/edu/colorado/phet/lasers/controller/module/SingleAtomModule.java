/* Copyright 2003-2004, University of Colorado */

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
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.BeamControl2;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.UniversalLaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.view.LampGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Class: SingleAtomBaseModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
public class SingleAtomModule extends BaseLaserModule {
    private Atom atom;
    private LampGraphic pumpingLampGraphic;
    private UniversalLaserControlPanel laserControlPanel;
    private BeamControl2 pumpBeamControl;
    private BeamControl2 seedBeamControl;

    public SingleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.SingleAtomModule" ), clock );

        // Create beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX() - 55,
                                                 s_origin.getY() + s_boxHeight / 2 );
        final CollimatedBeam seedBeam = ( (LaserModel)getModel() ).getSeedBeam();
        Rectangle2D.Double stimulatingBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                           s_boxWidth + s_laserOffsetX * 2, 1 );
        seedBeam.setBounds( stimulatingBeamBounds );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        // Start the beam with a very slow rate
        seedBeam.setPhotonsPerSecond( 1 );

        final CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( getLaserOrigin().getX() + s_boxWidth / 2,
                                                        s_origin.getY() - 100 );
        pumpingBeam.setBounds( new Rectangle2D.Double( pumpingBeamOrigin.getX() - Atom.getS_radius(), pumpingBeamOrigin.getY(),
                                                       2 * Atom.getS_radius(), s_boxHeight + s_laserOffsetX * 2 ) );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );

        // Start with the pumping beam turned down all the way
        pumpingBeam.setPhotonsPerSecond( 0 );

        // Enable only the stimulating beam to start with
        seedBeam.setEnabled( true );
        pumpingBeam.setEnabled( false );

        // Add the graphics for beams
        Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)stimulatingBeamBounds.getX() - 45,
                                                              (int)( stimulatingBeamBounds.getY() + seedBeam.getHeight() / 2 - 25 ),
                                                              100, 50 );
        BufferedImage gunBI = null;
        try {
            gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Stimulating beam lamp
        double scaleX = allocatedBounds.getWidth() / gunBI.getWidth();
        double scaleY = allocatedBounds.getHeight() / gunBI.getHeight();
        AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage beamImage = atxOp1.filter( gunBI, null );
        AffineTransform atx = new AffineTransform();
        atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
        LampGraphic stimulatingBeamGraphic = new LampGraphic( seedBeam, getApparatusPanel(), beamImage, atx );
        addGraphic( stimulatingBeamGraphic, LaserConfig.PHOTON_LAYER + 1 );

        // Add controls for the seed beam
        Point controlLocation = new Point( (int)allocatedBounds.getX() - 10, (int)( allocatedBounds.getY() + allocatedBounds.getHeight() + 20 ) );
        seedBeamControl = new BeamControl2( getApparatusPanel(), controlLocation, seedBeam,
                                            LaserConfig.MAXIMUM_SEED_PHOTON_RATE,
                                            null, pumpingBeam );
        getApparatusPanel().addGraphic( seedBeamControl );

        // Pumping beam lamp
        double yOffset = 30;
        AffineTransform pumpingBeamTx = new AffineTransform();
        pumpingBeamTx.translate( getLaserOrigin().getX() + beamImage.getHeight() + s_boxWidth / 2 - beamImage.getHeight() / 2, 10 );
        pumpingBeamTx.rotate( Math.PI / 2 );
        BufferedImage pumpingBeamLamp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( beamImage, null );
        pumpingLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), pumpingBeamLamp, pumpingBeamTx );
        setPumpLampGraphic( pumpingLampGraphic );
        pumpingLampGraphic.setVisible( false );
        addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

        // Add the beam control
        Point pumpControlLocation = new Point( (int)( pumpingBeamTx.getTranslateX() - 200 ), 10 );
        pumpBeamControl = new BeamControl2( getApparatusPanel(), pumpControlLocation, pumpingBeam,
                                            LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE / 8,
                                            seedBeam, null );
        getApparatusPanel().addGraphic( pumpBeamControl );

        // Set the averaging time for the energy levels display
        setEnergyLevelsAveragingPeriod( 0 );
        // Initialize to two energy levels
        setThreeEnergyLevels( false );

        // Add an atom
        int numEnergyLevels = getThreeEnergyLevels() ? 3 : 2;
        atom = new Atom( getLaserModel(), numEnergyLevels );
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2 );
        atom.setVelocity( 0, 0 );
        addAtom( atom );


        // Set up the control panel, and start off with two energy levels
        laserControlPanel = new UniversalLaserControlPanel( this, clock );
        setControlPanel( laserControlPanel );
        laserControlPanel.setUpperTransitionView( BaseLaserModule.PHOTON_DISCRETE );

    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        laserControlPanel.setThreeEnergyLevels( this.threeEnergyLevels );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
    }

    /**
     * Extends the base class behavior. Shows the pumping beam and enables it if
     * three levels are set.
     *
     * @param threeEnergyLevels
     */
    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        super.setThreeEnergyLevels( threeEnergyLevels );
        if( pumpingLampGraphic != null ) {
            pumpingLampGraphic.setVisible( threeEnergyLevels );
            pumpBeamControl.setVisible( threeEnergyLevels );
            getLaserModel().getPumpingBeam().setEnabled( threeEnergyLevels );
        }
        if( atom != null ) {
            int numEnergyLevels = threeEnergyLevels ? 3 : 2;
            atom.setNumEnergyLevels( numEnergyLevels, getLaserModel() );
        }
    }

    /**
     * Sets the visibility of Swing components on this module's apparatus panel
     *
     * @param areVisible
     */
    public void setSwingComponentsVisible( boolean areVisible ) {
        super.setSwingComponentsVisible( areVisible );
        pumpBeamControl.setVisible( areVisible );
        seedBeamControl.setVisible( areVisible );
    }
}
