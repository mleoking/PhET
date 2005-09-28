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
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.PhotonSource;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;

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
    private CollimatedBeam beam;

    /**
     * @param component
     * @param beam
     */
    public BeamCurtainGraphic( Component component, CollimatedBeam beam ) {
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
        // todo: this next line is all screwed up.
        double beamDepth = getComponent().getHeight() - beam.getBounds().getY();
        path.moveTo( (float)beam.getBounds().getMinX(), (float)beam.getBounds().getMinY() );
        path.lineTo( (float)beam.getBounds().getMaxX(), (float)beam.getBounds().getMinY() );
        path.lineTo( (float)( beam.getBounds().getMaxX() + beamDepth * Math.sin( beam.getFanout() / 2 ) ),
                     (float)getComponent().getHeight() );
        path.lineTo( (float)( beam.getBounds().getMinX() - beamDepth * Math.sin( beam.getFanout() / 2 ) ),
                     (float)getComponent().getHeight() );
        path.closePath();
        beamArea = path;
        setShape( beamArea );

        // If the beam is not vertical downward, then rotate the shape so it points in the direction
        // the beam is going
        if( beam.getAngle() != 0 ) {
            double theta = beam.getAngle() - Math.PI / 2;
            setShape( AffineTransform.getRotateInstance( theta,
                                                         beam.getPosition().getX(),
                                                         beam.getPosition().getY() ).createTransformedShape( beamArea ));
        }

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
