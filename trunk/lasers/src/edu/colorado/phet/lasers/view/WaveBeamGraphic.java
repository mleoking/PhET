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
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.PhotonSource;

import java.awt.*;
import java.awt.geom.GeneralPath;
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
public class WaveBeamGraphic extends PhetShapeGraphic implements PhotonSource.RateChangeListener,
                                                            PhotonSource.WavelengthChangeListener {

    Shape beamArea = new Rectangle();
    private Color actualColor;
    private CollimatedBeam beam;

    /**
     *
     * @param component
     * @param beam
     */
    public WaveBeamGraphic( Component component, CollimatedBeam beam ) {
        super( component );
        this.beam = beam;
        beam.addRateChangeListener( this );
        beam.addWavelengthChangeListener( this );
        update();
    }

    protected Rectangle determineBounds() {
        return beamArea.getBounds();
    }

    private void update() {
        Color baseColor = VisibleColor.wavelengthToColor( beam.getWavelength() );
        int minLevel = 200;
        // The power function here controls the ramp-up of actualColor intensity
        int level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ), .6 ) ) );
        actualColor = getActualColor( baseColor, level );

        GeneralPath path = new GeneralPath();
        double beamDepth = getComponent().getHeight() - beam.getBounds().getY();
        path.moveTo( (float)beam.getBounds().getMinX(), (float)beam.getBounds().getMinY() );
        path.lineTo( (float)beam.getBounds().getMaxX(), (float)beam.getBounds().getMinY() );
        path.lineTo( (float)( beam.getBounds().getMaxX() + beamDepth * Math.sin( beam.getFanout() / 2 )),
                     (float)getComponent().getHeight() );
        path.lineTo( (float)( beam.getBounds().getMinX() - beamDepth * Math.sin( beam.getFanout() / 2 )),
                     (float)getComponent().getHeight( ));
        path.closePath();
        beamArea = path;
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
