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
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.BlueBeamGraphic;
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

    public SingleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.SingleAtomModule" ), clock );

        // Create beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() + s_boxHeight / 2 - Photon.s_radius );
        CollimatedBeam stimulatingBeam = ( (LaserModel)getModel() ).getStimulatingBeam();
        stimulatingBeam.setBounds( new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                           s_boxWidth + s_laserOffsetX * 2, Photon.s_radius / 2 ) );
        stimulatingBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        stimulatingBeam.addListener( this );
        stimulatingBeam.setActive( true );
        stimulatingBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX + s_boxWidth / 2 - Photon.s_radius / 2,
                                                        s_origin.getY() - s_laserOffsetX );
        pumpingBeam.setBounds( new Rectangle2D.Double( pumpingBeamOrigin.getX(), pumpingBeamOrigin.getY(),
                                                       s_boxWidth, s_boxHeight + s_laserOffsetX * 2 ) );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( this );
        pumpingBeam.setWidth( Photon.s_radius * 2 );
        pumpingBeam.setActive( true );
        BlueBeamGraphic beamGraphic = new BlueBeamGraphic( getApparatusPanel(), pumpingBeam, getCavity() );
        addGraphic( beamGraphic, 1 );

        // Add the lamps for firing photons
        try {
            Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)stimulatingBeam.getPosition().getX() - 25,
                                                                  (int)( stimulatingBeam.getPosition().getY() - Photon.s_radius ),
                                                                  100, 100 );
            BufferedImage gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );

            // Stimulating beam lamp
            double scale = Math.min( allocatedBounds.getWidth() / gunBI.getWidth(),
                                     allocatedBounds.getHeight() / gunBI.getHeight() );
            AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scale, scale ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage beamImage = atxOp1.filter( gunBI, null );
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
            PhetImageGraphic stimulatingBeamGraphic = new LampGraphic( stimulatingBeam, getApparatusPanel(), beamImage, atx );
            addGraphic( stimulatingBeamGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the intensity control
            JPanel sbmPanel = new JPanel();
            BeamControl sbm = new BeamControl( stimulatingBeam );
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
            addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the beam control
            JPanel pbmPanel = new JPanel();
            BeamControl pbm = new BeamControl( pumpingBeam );
            Dimension pbmDim = pbm.getPreferredSize();
            pbmPanel.setBounds( (int)( pumpingBeamTx.getTranslateX() + pumpingLampGraphic.getWidth() ), 10,
                                (int)pbmDim.getWidth() + 10, (int)pbmDim.getHeight() + 10 );
            pbmPanel.add( pbm );
            //            pbmPanell.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbmPanel.setOpaque( false );
            getApparatusPanel().add( pbmPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 1 );
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
}
