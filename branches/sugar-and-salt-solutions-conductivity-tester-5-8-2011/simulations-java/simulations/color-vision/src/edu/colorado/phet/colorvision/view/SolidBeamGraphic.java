// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.colorvision.model.SolidBeam;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * SolidBeamGraphic provides a graphic representation of a solid beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolidBeamGraphic extends PhetShapeGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The beam model.
    private SolidBeam _beamModel;
    // Alpha scale, used to make the beam transparent (in percent)
    private double _alphaScale;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param beamModel the beam model
     */
    public SolidBeamGraphic( Component component, SolidBeam beamModel ) {
        super( component, null, null );

        _beamModel = beamModel;
        _alphaScale = 100.0;

        // Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.setRenderingHints( hints );

        super.setPaint( Color.white );
        super.setStroke( null );

        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the alpha scale. This is used to adjust the transparency of the beam,
     * It is applied to the alpha component of the beam's color.
     * 
     * @param alphaScale the alpha scale, in percent
     */
    public void setAlphaScale( double alphaScale ) {
        _alphaScale = alphaScale;
        update();
    }

    /**
     * Gets the alpha scale
     * 
     * @return the alpha scale, in percent
     */
    public double getAlphaScale() {
        return _alphaScale;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronizes the view with model.
     */
    public void update() {
        if( !_beamModel.isEnabled() ) {
            // If the beam is disabled, draw nothing.
            super.setShape( null );
        }
        else {
            // Color
            {
                VisibleColor beamColor = _beamModel.getPerceivedColor();

                // Scale the alpha component to provide transparency.
                int r = beamColor.getRed();
                int g = beamColor.getGreen();
                int b = beamColor.getBlue();
                int a = (int) ( beamColor.getAlpha() * _alphaScale / 100 );

                super.setPaint( new Color( r, g, b, a ) );
            }

            // Shape
            {
                double x = _beamModel.getX();
                double y = _beamModel.getY();
                double distance = _beamModel.getDistance();
                double direction = _beamModel.getDirection(); // in degrees
                double cutOffAngle = _beamModel.getCutOffAngle(); // in degrees

                // Radius of the beam.
                double radius = distance / Math.cos( Math.toRadians( cutOffAngle / 2 ) );

                // First coordinate
                double x0 = x;
                double y0 = y;

                // Second coordinate
                double theta1 = Math.toRadians( direction - ( cutOffAngle / 2 ) );
                double x1 = radius * Math.cos( theta1 );
                double y1 = radius * Math.sin( theta1 );
                x1 += x;
                y1 += y;

                // Third coordinate
                double theta2 = Math.toRadians( direction + ( cutOffAngle / 2 ) );
                double x2 = radius * Math.cos( theta2 );
                double y2 = radius * Math.sin( theta2 );
                x2 += x;
                y2 += y;

                GeneralPath path = new GeneralPath();
                path.moveTo( (int) x0, (int) y0 );
                path.lineTo( (int) x1, (int) y1 );
                path.lineTo( (int) x2, (int) y2 );
                path.closePath();

                super.setShape( path );
            }
        }
    } // update

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Draws the beam.
     * 
     * @param g2 graphics context
     */
    public void paint( Graphics2D g2 ) {
        if( super.isVisible() && _beamModel.isEnabled() ) {
            super.paint( g2 );
            BoundsOutliner.paint( g2, this ); // DEBUG
        }
    }
}