/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import java.awt.*;

public class BlueBeamGraphic extends PhetGraphic implements SimpleObserver {
    private CollimatedBeam beam;
    private ResonatingCavity cavity;
    Rectangle bounds = new Rectangle();
    private double maxRate = LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE;
    private Color color;

    public BlueBeamGraphic( Component component, CollimatedBeam beam, ResonatingCavity cavity ) {
        super( component );
        this.beam = beam;
        this.cavity = cavity;
        beam.addObserver( this );
        update();
    }

    public void update() {
        double rate = beam.getPhotonsPerSecond();
        int minLevel = 200;
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( rate / maxRate ), 0.25 ) ) );
        color = new Color( level, level, 255 );
        bounds.setRect( cavity.getMinX(), 0, cavity.getWidth(), getComponent().getHeight() );
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
        gs.restoreGraphics();
    }
}
