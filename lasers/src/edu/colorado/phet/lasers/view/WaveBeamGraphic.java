/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class WaveBeamGraphic extends PhetGraphic implements SimpleObserver,
                                                            Photon.LeftSystemEventListener,
                                                            PhotonEmittedListener {
    private CollimatedBeam beam;
    private ResonatingCavity cavity;
    Rectangle bounds = new Rectangle();
    private double maxRate = LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE;
    private Color color;
    private Point2D origin;
    private Line2D wave;
    private Color waveColor;
    private StandingWave standingWave;

    public WaveBeamGraphic( Component component, CollimatedBeam beam, ResonatingCavity cavity, BaseModel model ) {
        super( component );
        this.beam = beam;
        this.cavity = cavity;
        beam.addObserver( this );

        origin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        Photon.addClassListener( this );

        double cyclesInCavity = 10;
        standingWave = new StandingWave( component, origin, cavity.getWidth(),
                                         cavity.getWidth() / cyclesInCavity, 100,
                                         numLasingPhotons, Color.red, model );
        update();
    }

    public void update() {
        double rate = beam.getPhotonsPerSecond();
        int minLevel = 200;
        // The power function here controls the ramp-up of color intensity
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( rate / maxRate ), .4 ) ) );
        //        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( rate / maxRate ), 0.8 ) ) );
        color = new Color( level, level, 255 );
        bounds.setRect( cavity.getMinX(), 0, cavity.getWidth(), getComponent().getHeight() );

        // Draw the wave
        wave = new Line2D.Double( origin.getX(), origin.getY() - numLasingPhotons * 10, origin.getX() + cavity.getWidth(), origin.getY() );
        //        double ratio = 255 / Math.max( color.getRed(), Math.max( color.getGreen(), color.getBlue() )) ;
        //        waveColor = new Color( (int)(color.getRed() * ratio ),
        //                                               (int)(color.getGreen() * ratio),
        //                                               (int)(color.getBlue() * ratio ));
        waveColor = Color.blue;

        setBoundsDirty();
        repaint();
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.setColor( color );
        g.fill( bounds );

        g.setColor( waveColor );
        g.draw( wave );
        standingWave.paint( g );
        gs.restoreGraphics();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Interface implementations
    //
    private double angleWindow = Math.toRadians( 60 );
    private int numLasingPhotons;

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        System.out.println( "photon left system" );
        Photon photon = event.getPhoton();
        if( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
            || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow ) {
            numLasingPhotons--;
            standingWave.setAmplitude( numLasingPhotons );
            System.out.println( "numLasingPhotons = " + numLasingPhotons );
        }
    }

    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        Photon photon = event.getPhoton();
        photon.addListener( this );
        if( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
            || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow ) {
            numLasingPhotons++;
            standingWave.setAmplitude( numLasingPhotons );
            System.out.println( "numLasingPhotons = " + numLasingPhotons );
        }
    }
}
