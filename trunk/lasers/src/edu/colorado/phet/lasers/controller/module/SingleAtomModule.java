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
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.StimulatingBeamControl;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.BlueBeamGraphic;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleAtomModule extends BaseLaserModule {
    private Atom atom;

    public SingleAtomModule( AbstractClock clock ) {
        super( "One Atom", clock );

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

        // Add the ray gun for firing photons
        try {
            Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)stimulatingBeam.getPosition().getX() - 25,
                                                                  (int)( stimulatingBeam.getPosition().getY() - Photon.s_radius ),
                                                                  100, 100 );
            BufferedImage gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
            double scale = Math.min( allocatedBounds.getWidth() / gunBI.getWidth(),
                                     allocatedBounds.getHeight() / gunBI.getHeight() );
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
            atx.scale( scale, scale );
            PhetImageGraphic gunGraphic = new PhetImageGraphic( getApparatusPanel(), gunBI, atx );
            addGraphic( gunGraphic, LaserConfig.PHOTON_LAYER + 1 );

            JPanel sbmPanel = new JPanel();
            StimulatingBeamControl sbm = new StimulatingBeamControl( getLaserModel() );
            sbmPanel.setBounds( (int)allocatedBounds.getX(), (int)( allocatedBounds.getY() + allocatedBounds.getHeight() ),
                                150, 85 );
            sbmPanel.add( sbm );
            getApparatusPanel().add( sbmPanel );
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
