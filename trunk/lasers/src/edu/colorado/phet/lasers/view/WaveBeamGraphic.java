/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import java.awt.*;

/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
public class WaveBeamGraphic extends PhetGraphic implements CollimatedBeam.RateChangeListener,
                                                            CollimatedBeam.WavelengthChangeListener {

    private ResonatingCavity cavity;
    Rectangle bounds = new Rectangle();
    private double maxRate = LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE;
    private Color color;


    public WaveBeamGraphic( Component component, CollimatedBeam beam, ResonatingCavity cavity ) {
        super( component );
        this.cavity = cavity;
        beam.addRateChangeListner( this );
        update( beam );
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

    private void update( CollimatedBeam beam ) {
        int minLevel = 200;
        // The power function here controls the ramp-up of color intensity
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ), .4 ) ) );
//        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( beam.getPhotonsPerSecond() / maxRate ), .4 ) ) );
        color = new Color( level, level, 255 );
        bounds.setRect( beam.getBounds().getX(), beam.getBounds().getY(),
                        beam.getBounds().getWidth(), getComponent().getHeight() - beam.getBounds().getY() );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
        Color baseColor = VisibleColor.wavelengthToColor( event.getWavelength() );

        // Need to figure out how to shade the color. Take a look at MakeDuotoneOp.
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        update( (CollimatedBeam)event.getSource() );
    }
}
