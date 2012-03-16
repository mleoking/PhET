// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.view;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.colorvision.model.Photon;
import edu.colorado.phet.colorvision.model.PhotonBeam;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * PhotonBeamGraphic provides a view of a PhotonBeam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonBeamGraphic extends PhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Photon line length, for rendering.
    public static final int PHOTON_LINE_LENGTH = 3;
    // Stroke used from drawing photons
    private static Stroke PHOTON_STROKE = new BasicStroke( 1f );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhotonBeam _photonBeamModel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param photonBeamModel the photon beam model
     */
    public PhotonBeamGraphic( Component component, PhotonBeam photonBeamModel ) {
        super( component );
        _photonBeamModel = photonBeamModel;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#determineBounds()
     */
    protected Rectangle determineBounds() {
        return _photonBeamModel.getBounds();
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        super.repaint();
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Draws the photon beam.
     * 
     * @param g2 graphics context
     */
    public void paint( Graphics2D g2 ) {
        if( isVisible() && _photonBeamModel.isEnabled() ) {
            // Save graphics state
            Paint oldPaint = g2.getPaint();
            Stroke oldStroke = g2.getStroke();

            // Use the same stroke for all photons.
            g2.setStroke( PHOTON_STROKE );

            Photon photon = null;
            int x, y, w, h;

            // For each photon ...
            ArrayList photons = _photonBeamModel.getPhotons();
            for( int i = 0; i < photons.size(); i++ ) {
                photon = (Photon) photons.get( i );

                // If the photon is in use, render it.
                if( photon.isInUse() ) {

                    // Set the color.
                    // WORKAROUND: We get a huge performance improvement by passing
                    // a java.awt.Color to Graphics2D.paint, instead of a VisibleColor.
                    Color color = null;
                    if( photon.getIntensity() == 0 ) {
                        // Photons with zero intensity are invisible.
                        color = VisibleColor.INVISIBLE.toColor();
                        //color = VisibleColor.WHITE.toColor(); // DEBUG
                    }
                    else if( photon.getColor().getWavelength() == VisibleColor.WHITE_WAVELENGTH ) {
                        // White photons are rendered using a random color.
                        double wavelength = genWavelength();
                        color = VisibleColor.wavelengthToColor( wavelength );
                    }
                    else {
                        color = photon.getColor().toColor();
                    }
                    g2.setPaint( color );
                    
                    // Draw the photon as a line.
                    // The head of the photon is at (x,y), assumes left-to-right motion!
                    x = (int) photon.getX();
                    y = (int) photon.getY();
                    w = (int) photon.getWidth();
                    h = (int) photon.getHeight();
                    g2.drawLine( x, y, x - w, y - h );
                }
            }

            // Restore graphics state
            g2.setPaint( oldPaint );
            g2.setStroke( oldStroke );

            BoundsOutliner.paint( g2, this, Color.YELLOW ); // DEBUG
        }
    } // paint

    /**
     * Generates a random wavelength.
     */
    private double genWavelength() {
        double range = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
        return ( Math.random() * range ) + VisibleColor.MIN_WAVELENGTH;
    }

}