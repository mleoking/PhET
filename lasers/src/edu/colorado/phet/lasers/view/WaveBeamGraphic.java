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
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import java.awt.*;

public class WaveBeamGraphic extends PhetGraphic implements CollimatedBeam.RateChangeListener {

    private ResonatingCavity cavity;
    Rectangle bounds = new Rectangle();
    private double maxRate = LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE;
    private Color color;


    public WaveBeamGraphic( Component component, CollimatedBeam beam, ResonatingCavity cavity, BaseModel model ) {
        super( component );
        this.cavity = cavity;
        beam.addListener( this );
        bounds.setRect( cavity.getMinX(), 0, cavity.getWidth(), getComponent().getHeight() );
        update( beam.getPhotonsPerSecond() );
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.setColor( color );
        g.fill( bounds );
        gs.restoreGraphics();
    }

    private void update( double rate ) {
        int minLevel = 200;
        // The power function here controls the ramp-up of color intensity
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( rate / maxRate ), .4 ) ) );
        color = new Color( level, level, 255 );
        bounds.setRect( cavity.getMinX(), 0, cavity.getWidth(), getComponent().getHeight() );
        setBoundsDirty();
        repaint();
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        update( event.getRate() );
    }
}
