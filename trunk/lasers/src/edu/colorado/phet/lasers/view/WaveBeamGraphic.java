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
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.PhotonSource;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 * <p/>
 * Shows a collimated beam as a rectangle of actualColor. The saturation of the actualColor corresponds to
 * the photon rate of the beam.
 */
public class WaveBeamGraphic extends PhetGraphic implements PhotonSource.RateChangeListener,
                                                            PhotonSource.WavelengthChangeListener {

    Rectangle bounds = new Rectangle();
    private Color actualColor;
    private PhotonSource beam;


    public WaveBeamGraphic( Component component, PhotonSource beam ) {
        super( component );
        this.beam = beam;
        beam.addRateChangeListener( this );
        beam.addWavelengthChangeListener( this );

        // Add a listener to hear when the apparatus panel resizes, so we will make the beam cover
        // teh panel
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
//                update();
            }
        } );
        update();
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.setColor( actualColor );
        g.fill( bounds );
        gs.restoreGraphics();
    }

    private void update() {
        Color baseColor = VisibleColor.wavelengthToColor( beam.getWavelength() );
        int minLevel = 200;
        // The power function here controls the ramp-up of actualColor intensity
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ), .6 ) ) );
        actualColor = getActualColor( baseColor, level );
        bounds.setRect( beam.getBounds().getX(), beam.getBounds().getY(),
                        beam.getBounds().getWidth(), getComponent().getHeight() - beam.getBounds().getY() );
        setBoundsDirty();
        repaint();
    }

    /**
     * Determines the color to paint the rectangle.
     *
     * @param baseColor
     * @param level
     * @return
     */
    private Color getActualColor( Color baseColor, int level ) {
        double grayRefLevel = MakeDuotoneImageOp.getGrayLevel( baseColor );
        int newRGB = MakeDuotoneImageOp.getDuoToneRGB( level, level, level, 255, grayRefLevel, baseColor );
        return new Color( newRGB );
    }

    //----------------------------------------------------------------------------
    // LeftSystemEvent handling
    //----------------------------------------------------------------------------
    public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
        update();
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        update();
    }
}
