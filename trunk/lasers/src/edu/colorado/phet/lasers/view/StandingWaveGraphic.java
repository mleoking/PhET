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
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
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
                                                                PhotonEmittedListener {
    private Point2D origin;
    private StandingWave standingWave;
    // Angle that is considered horizontal, for purposes of lasing
    private double angleWindow = Math.toRadians( 10 );
    private HashSet lasingPhotons = new HashSet();

    public StandingWaveGraphic( Component component, CollimatedBeam beam, ResonatingCavity cavity, BaseModel model ) {
        super( component );
        origin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        Photon.addClassListener( this );
        double cyclesInCavity = 10;
        standingWave = new StandingWave( component, origin, cavity.getWidth(),
                                         cavity.getWidth() / cyclesInCavity, 100,
                                         getNumLasingPhotons(), Color.red, model );
    }

    private int getNumLasingPhotons() {
        return lasingPhotons.size();
    }

    protected Rectangle determineBounds() {
        return standingWave.determineBounds();
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        standingWave.paint( g );
        gs.restoreGraphics();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Interface implementations
    //
    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        Photon photon = event.getPhoton();
        if( lasingPhotons.contains( photon ) ) {
            lasingPhotons.remove( photon );
            standingWave.setAmplitude( getNumLasingPhotons() );
        }
    }

    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        Photon photon = event.getPhoton();
        photon.addListener( this );
        if( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
            || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow ) {
            lasingPhotons.add( photon );
            standingWave.setAmplitude( getNumLasingPhotons() );
        }
    }
}
