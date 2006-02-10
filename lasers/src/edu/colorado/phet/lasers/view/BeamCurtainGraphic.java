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

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.quantum.model.PhotonSource;

import java.awt.*;

/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 * <p/>
 * Shows a collimated beam as an area of actualColor. The saturation of the actualColor corresponds to
 * the photon rate of the beam.
 */
public class BeamCurtainGraphic extends PhetShapeGraphic implements PhotonSource.RateChangeListener,
                                                                    PhotonSource.WavelengthChangeListener {

    Shape beamArea = new Rectangle();
    private Color actualColor;
    private Beam beam;

    /**
     * @param component
     * @param beam
     */
    public BeamCurtainGraphic( Component component, Beam beam ) {
        super( component );
        this.beam = beam;
        beam.addRateChangeListener( this );
        beam.addWavelengthChangeListener( this );
        update();
    }

    protected Rectangle determineBounds() {
        return beamArea.getBounds();
    }

    int colorMax = 255;

    private void update() {
        Color baseColor = VisibleColor.wavelengthToColor( beam.getWavelength() );
        int minLevel = 200;
        // The power function here controls the ramp-up of actualColor intensity
        int level = Math.max( minLevel, colorMax - (int)( ( colorMax - minLevel ) * Math.pow( ( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ), .3 ) ) );
        actualColor = getActualColor( baseColor, level );
        beamArea = beam.getBounds();
        setShape( beamArea );
        setPaint( actualColor );
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
        int newRGB = MakeDuotoneImageOp.getDuoToneRGB( level, level, level, colorMax, grayRefLevel, baseColor );
        return new Color( newRGB );
    }

    //----------------------------------------------------------------------------
    // LeftSystemEvent handling
    //----------------------------------------------------------------------------
    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        update();
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        update();
    }
}
