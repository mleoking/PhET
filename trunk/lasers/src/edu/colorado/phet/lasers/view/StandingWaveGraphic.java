/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;

/**
 * A graphic that shows a standing wave whose amplitude is proportional to the number of photons
 * that are traveling more-or-less horizontally.
 */
public class StandingWaveGraphic extends PhetGraphic implements Photon.LeftSystemEventListener,
                                                                PhotonEmittedListener,
                                                                StandingWave.ChangeListener {
    private Point2D internalWaveOrigin;
    private Point2D externalWaveOrigin;
    private StandingWave internalStandingWave;
    private StandingWave externalStandingWave;
    // Angle that is considered horizontal, for purposes of lasing
    private double angleWindow = LaserConfig.PHOTON_CHEAT_ANGLE;
    private HashSet lasingPhotons = new HashSet();
    private Stroke stroke = new BasicStroke( 2f );
    private PartialMirror mirror;
    private Rectangle bounds;
    private AtomicState atomicState;

    public StandingWaveGraphic( Component component, ResonatingCavity cavity,
                                PartialMirror mirror, BaseModel model, AtomicState atomicState ) {
        super( component );
        this.atomicState = atomicState;

        // Register with the Photon class so we will get notified when photons are created
        Photon.addClassListener( this );

        this.mirror = mirror;
        double cyclesInCavity = 10;
        internalWaveOrigin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        internalStandingWave = new StandingWave( component, internalWaveOrigin, cavity.getWidth(),
                                                 cavity.getWidth() / cyclesInCavity, 100,
                                                 getNumLasingPhotons(), atomicState, model );
        internalStandingWave.addListener( this );
        externalWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth(),
                                                 cavity.getMinY() + cavity.getHeight() / 2 );
        externalStandingWave = new StandingWave( component, externalWaveOrigin, 400,
                                                 cavity.getWidth() / cyclesInCavity, 100,
                                                 getNumLasingPhotons(), atomicState, model );
        externalStandingWave.addListener( this );
    }

    public void waveChanged( StandingWave.ChangeEvent event ) {
        setBoundsDirty();
        repaint();
    }

    private int getNumLasingPhotons() {
        return lasingPhotons.size();
    }

    protected Rectangle determineBounds() {
        Rectangle isb = internalStandingWave.determineBounds();
        Rectangle esb = externalStandingWave.determineBounds();
        bounds = esb.union( isb );
        return bounds;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.setStroke( stroke );
        internalStandingWave.paint( g );
        externalStandingWave.paint( g );

        //        g.setColor( Color.green );
        //        g.draw( bounds );
        gs.restoreGraphics();
    }

    private double getExternalAmplitude() {
        double n = getNumLasingPhotons();
        return mirror.getReflectivity() == 1 ? 0 : n * Math.sqrt( 1 - mirror.getReflectivity() );
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Interface implementations
    //
    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        Photon photon = event.getPhoton();
        if( lasingPhotons.contains( photon ) ) {
            lasingPhotons.remove( photon );
            double n = getNumLasingPhotons();
            internalStandingWave.setAmplitude( n );
            externalStandingWave.setAmplitude( getExternalAmplitude() );
        }
    }

    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        Photon photon = event.getPhoton();
        photon.addListener( this );
        if( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
            || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow ) {
            lasingPhotons.add( photon );
            internalStandingWave.setAmplitude( getNumLasingPhotons() );
            int n = getNumLasingPhotons();
            internalStandingWave.setAmplitude( n );
            externalStandingWave.setAmplitude( getExternalAmplitude() );
        }
    }
}
