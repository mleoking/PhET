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
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.BeamControl;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.UniversalLaserControlPanel;
import edu.colorado.phet.lasers.help.SingleAtomModuleWiggleMe;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.PropertiesBasedAtom;
import edu.colorado.phet.lasers.model.atom.TwoLevelElementProperties;
import edu.colorado.phet.lasers.model.photon.Beam;
import edu.colorado.phet.lasers.model.photon.PhotonSource;
import edu.colorado.phet.lasers.view.LampGraphic;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private BeamControl pumpBeamControl;
    private BeamControl seedBeamControl;

    public SingleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.SingleAtomModule" ), clock );

        //Set up the seed beam
        Point2D beamOrigin = new Point2D.Double( getCavity().getBounds().getX() - 100,
                                                 getCavity().getBounds().getY() + getCavity().getBounds().getHeight() / 2 );
        final Beam seedBeam = ( (LaserModel)getModel() ).getSeedBeam();
        seedBeam.setPosition( beamOrigin );
        seedBeam.setBeamWidth( 0.5 );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        seedBeam.setFanout( LaserConfig.SEED_BEAM_FANOUT );

        // Start the beam with a very slow rate
        seedBeam.setPhotonsPerSecond( 1 );

        // Set up the pump beam
        final Beam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( getCavity().getBounds().getX() + getCavity().getBounds().getWidth() / 2,
                                                        getCavity().getBounds().getY() - 100 );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        pumpingBeam.setPosition( pumpingBeamOrigin );
        pumpingBeam.setFanout( Math.toRadians( LaserConfig.SEED_BEAM_FANOUT  * 2 ) );
        pumpingBeam.setBeamWidth( seedBeam.getBeamWidth() );

        // Start with the pumping beam turned down all the way
        pumpingBeam.setPhotonsPerSecond( 0 );
        pumpingBeam.setMaxPhotonsPerSecond( (int)pumpingBeam.getMaxPhotonsPerSecond() / 2 );

        // Enable only the stimulating beam to start with
        seedBeam.setEnabled( true );
        pumpingBeam.setEnabled( false );

        // Add the graphics for beams
        Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)seedBeam.getPosition().getX() - 55,
                                                              (int)( seedBeam.getPosition().getY() + seedBeam.getBeamWidth() / 2 - 25 ),
                                                              100, 50 );
        BufferedImage gunBI = null;
        try {
            gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Seed beam lamp graphic
        double scaleX = allocatedBounds.getWidth() / gunBI.getWidth();
        double scaleY = allocatedBounds.getHeight() / gunBI.getHeight();
        AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage beamImage = atxOp1.filter( gunBI, null );
        {
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
            LampGraphic stimulatingBeamGraphic = new LampGraphic( seedBeam, getApparatusPanel(), beamImage, atx );
            addGraphic( stimulatingBeamGraphic, LaserConfig.PHOTON_LAYER + 1 );
        }

        // Add controls for the seed beam
        {
            PhetImageGraphic wireGraphic = new PhetImageGraphic( getApparatusPanel(), LaserConfig.WIRE_IMAGE );
            wireGraphic.setImage( BufferedImageUtils.getRotatedImage( wireGraphic.getImage(), -Math.PI / 2 ) );
            wireGraphic.setLocation( 50, 250 );
            getApparatusPanel().addGraphic( wireGraphic );
            Point controlLocation = new Point( (int)seedBeam.getPosition().getX() + 40,
                                               (int)seedBeam.getPosition().getY() + 70 );
            seedBeamControl = new BeamControl( getApparatusPanel(),
                                               controlLocation,
                                               seedBeam,
                                               LaserConfig.MIN_WAVELENGTH,
                                               LaserConfig.MAX_WAVELENGTH,
                                               LaserConfig.BEAM_CONTROL_IMAGE );
            getApparatusPanel().addGraphic( seedBeamControl );
        }

        // Pumping beam lamp
        AffineTransform pumpingBeamTx = new AffineTransform();
        pumpingBeamTx.translate( getLaserOrigin().getX() + beamImage.getHeight() + s_boxWidth / 2 - beamImage.getHeight() / 2, 10 );
        pumpingBeamTx.rotate( Math.PI / 2 );
        BufferedImage pumpingBeamLamp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( beamImage, null );
        pumpingLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), pumpingBeamLamp, pumpingBeamTx );
        setPumpLampGraphic( pumpingLampGraphic );
        pumpingLampGraphic.setVisible( false );
        addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

        // Add the beam control
        {
            PhetImageGraphic wireGraphic = new PhetImageGraphic( getApparatusPanel(), LaserConfig.WIRE_IMAGE );
            AffineTransform atx = AffineTransform.getScaleInstance( 0.6, 1 );
            AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
            wireGraphic.setImage( atxOp.filter( wireGraphic.getImage(), null ) );
            wireGraphic.setLocation( (int)pumpingBeam.getPosition().getX(),
                                     (int)( pumpingBeam.getPosition().getY() - 20 ) );
            Point pumpControlLocation = new Point( (int)( pumpingBeam.getPosition().getX() + 170 ),
                                                   (int)( pumpingBeam.getPosition().getY() - 90 ) );
            pumpBeamControl = new BeamControl( getApparatusPanel(),
                                               pumpControlLocation,
                                               pumpingBeam,
                                               LaserConfig.MIN_WAVELENGTH,
                                               LaserConfig.MAX_WAVELENGTH,
                                               LaserConfig.BEAM_CONTROL_IMAGE );
            wireGraphic.setLocation( -170, 40 );
            pumpBeamControl.addGraphic( wireGraphic );
            getApparatusPanel().addGraphic( pumpBeamControl );
        }

        // Set the averaging time for the energy levels display
        setEnergyLevelsAveragingPeriod( 0 );
        // Initialize to two energy levels
        setThreeEnergyLevels( false );

        // Add an atom
        int numEnergyLevels = getThreeEnergyLevels() ? 3 : 2;
        atom = new PropertiesBasedAtom( getLaserModel(), new TwoLevelElementProperties() );
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2 );
        atom.setVelocity( 0, 0 );
        addAtom( atom );

        // Set up the control panel, and start off with two energy levels
        laserControlPanel = new UniversalLaserControlPanel( this );
        setControlPanel( laserControlPanel );
        laserControlPanel.setUpperTransitionView( BaseLaserModule.PHOTON_DISCRETE );
        setPumpingPhotonView( BaseLaserModule.PHOTON_DISCRETE );

        // Add the Wiggle-me
//        addWiggleMe( seedBeam );
    }

    /**
     * @param seedBeam
     */
    private void addWiggleMe( final Beam seedBeam ) {
        Point2D wiggleMeLoc = new Point2D.Double( seedBeamControl.getBounds().getMinX() + seedBeamControl.getWidth() / 2,
                                                  seedBeamControl.getBounds().getMaxY() + 30 );
        final SingleAtomModuleWiggleMe wiggleMe = new SingleAtomModuleWiggleMe( getApparatusPanel(),
                                                                                wiggleMeLoc,
                                                                                seedBeamControl.getWidth() / 2 );
        addGraphic( wiggleMe, 100 );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                wiggleMe.stop();
            }
        } );
        seedBeam.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( Beam.RateChangeEvent event ) {
                wiggleMe.stop();
                seedBeam.removeListener( this );
            }
        } );

//        Font font = new JLabel( "" ).getFont();
//        PhetTextGraphic textGraphic = new PhetTextGraphic( getApparatusPanel(), font, "!@#", Color.red );
//        WiggleMe wm = new WiggleMe( new Rectangle( 500, 500 ), getClock(), new Point2D.Double( 200, 600 ), textGraphic );
//        getApparatusPanel().addGraphic( textGraphic );
    }

    /**
     * @param app
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        // TODO: This fixed a race condition that caused the module to come up in 3 energy levels sometimes.
        // This should either be fixed on its own, or revisited when the Discharge Lamps code is merged with Lasers
        try {
            Thread.sleep( 1000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        laserControlPanel.setThreeEnergyLevels( this.threeEnergyLevels );
    }

    /**
     * @param app
     */
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
