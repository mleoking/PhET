/**
 * Class: SingleAtomBaseModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.BeamControl;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.SingleAtomControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.view.LampGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleAtomModule extends BaseLaserModule {
    private Atom atom;
    private PhetImageGraphic pumpingLampGraphic;
    private JPanel pumpingBeamControlPanel;

    public SingleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.SingleAtomModule" ), clock );

        setControlPanel( new SingleAtomControlPanel( this ) );

        // Create beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() + s_boxHeight / 2 );
        CollimatedBeam stimulatingBeam = ( (LaserModel)getModel() ).getSeedBeam();
        Rectangle2D.Double stimulatingBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                           s_boxWidth + s_laserOffsetX * 2, 1 );
        stimulatingBeam.setBounds( stimulatingBeamBounds );
        stimulatingBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        stimulatingBeam.setEnabled( true );
        stimulatingBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( getLaserOrigin().getX() + s_boxWidth / 2,
                                                        s_origin.getY() - 140 );
        pumpingBeam.setBounds( new Rectangle2D.Double( pumpingBeamOrigin.getX(), pumpingBeamOrigin.getY(),
                                                       1, s_boxHeight + s_laserOffsetX * 2 ) );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        pumpingBeam.setEnabled( true );
        //        WaveBeamGraphic beamGraphic = new WaveBeamGraphic( getApparatusPanel(), pumpingBeam, getCavity() );
        //        addGraphic( beamGraphic, 1 );

        // Add the lamps for firing photons
        try {
            Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)stimulatingBeamBounds.getX() - 100,
                                                                  (int)( stimulatingBeamBounds.getY() + stimulatingBeam.getHeight() / 2 - 25 ),
                                                                  100, 50 );
            BufferedImage gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );

            // Stimulating beam lamp
            double scale = Math.min( allocatedBounds.getWidth() / gunBI.getWidth(),
                                     allocatedBounds.getHeight() / gunBI.getHeight() );
            double scaleX = allocatedBounds.getWidth() / gunBI.getWidth();
            double scaleY = allocatedBounds.getHeight() / gunBI.getHeight();
            AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage beamImage = atxOp1.filter( gunBI, null );
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
            PhetImageGraphic stimulatingBeamGraphic = new LampGraphic( stimulatingBeam, getApparatusPanel(), beamImage, atx );
            addGraphic( stimulatingBeamGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the intensity control
            JPanel sbmPanel = new JPanel();
            BeamControl sbm = new BeamControl( stimulatingBeam );
            sbm.setWavelengthLimitingBeam( pumpingBeam );
            Dimension sbmDim = sbm.getPreferredSize();
            sbmPanel.setBounds( (int)allocatedBounds.getX(), (int)( allocatedBounds.getY() + allocatedBounds.getHeight() ),
                                (int)sbmDim.getWidth() + 10, (int)sbmDim.getHeight() + 10 );
            sbmPanel.add( sbm );
            sbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            //            sbmPanel.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            sbmPanel.setOpaque( false );
            getApparatusPanel().add( sbmPanel );


            // Pumping beam lamp
            AffineTransform pumpingBeamTx = new AffineTransform();
            pumpingBeamTx.translate( getLaserOrigin().getX() + beamImage.getHeight() + s_boxWidth / 2 - beamImage.getHeight() / 2, 10 );
            pumpingBeamTx.rotate( Math.PI / 2 );
            BufferedImage pumpingBeamLamp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( beamImage, null );
            pumpingLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), pumpingBeamLamp, pumpingBeamTx );
            pumpingLampGraphic.setVisible( false );
            addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the beam control
            pumpingBeamControlPanel = new JPanel();
            BeamControl pbm = new BeamControl( pumpingBeam );
            Dimension pbmDim = pbm.getPreferredSize();
            pumpingBeamControlPanel.setBounds( (int)( pumpingBeamTx.getTranslateX() + pumpingLampGraphic.getWidth() ), 10,
                                               (int)pbmDim.getWidth() + 10, (int)pbmDim.getHeight() + 10 );
            pumpingBeamControlPanel.add( pbm );
            //            pbmPanell.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pumpingBeamControlPanel.setOpaque( false );
            pumpingBeamControlPanel.setVisible( false );
            getApparatusPanel().add( pumpingBeamControlPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Enable only the stimulating beam to start with
        stimulatingBeam.setEnabled( true );
        pumpingBeam.setEnabled( false );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setSeedPhotonRate( 1 );
        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
        config.setPumpingPhotonRate( 0 );
        config.setReflectivity( 0.7 );
        config.configureSystem( getLaserModel() );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        atom = new Atom();
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2 );
        atom.setVelocity( 0, 0 );
        addAtom( atom );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        getLaserModel().removeModelElement( atom );
        atom.removeFromSystem();
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        super.setThreeEnergyLevels( threeEnergyLevels );
        if( pumpingLampGraphic != null ) {
            pumpingLampGraphic.setVisible( threeEnergyLevels );
            pumpingBeamControlPanel.setVisible( threeEnergyLevels );
            getLaserModel().getPumpingBeam().setEnabled( threeEnergyLevels );
        }
    }
}
