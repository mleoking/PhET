/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision3.view;

import java.awt.*;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * BoundsOutliner is a class used for debugging the bondaries of graphics components.
 * <p>
 * In the paint method of your component (typically at the end), add a call to 
 * BoundsOutliner.paint.  If BoundsOutliner.isEnabled is true, the bounds of your 
 * component will be rendered as an outline, in a Color that you specify.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BoundsOutliner {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Set this system property to enable.
    private static final String PROPERTY = "BoundsOutliner.enable";
    // Default stroke
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    // Default paint
    private static final Paint DEFAULT_PAINT = Color.RED;

    // Global control of bounds rendering.
    private static boolean _enabled = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Not intended for instantiation.
     */
    private BoundsOutliner() {}

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Globally enabled/disables rendering of bounds.
     * 
     * @param enabled true to enable, false to disable
     */
    public static void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }

    /**
     * Determines if rendering of bounds is enabled.
     * 
     * @return true if enabled, false if disabled
     */
    public static boolean isEnabled() {
        return _enabled;
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Draws a graphic component's bounds using a default paint and stroke.
     * If disabled, draws nothing.
     * 
     * @param g2 the 2D graphics context
     * @param component the graphics component
     */
    public static void paint( Graphics2D g2, PhetGraphic component ) {
        if( _enabled ) {
            BoundsOutliner.paint( g2, component, DEFAULT_PAINT, DEFAULT_STROKE );
        }
    }

    /**
     * Draws a graphic component's bounds using a specified paint and default stroke.
     * If disabled, draws nothing.
     * 
     * @param g2 the 2D graphics context
     * @param component the graphics component
     * @param paint the paint to use for the outline
     */
    public static void paint( Graphics2D g2, PhetGraphic component, Paint paint ) {
        if( _enabled ) {
            BoundsOutliner.paint( g2, component, paint, DEFAULT_STROKE );
        }
    }

    /**
     * Draws a graphic component's bounds using a specified paint and stroke.
     * If disabled, draws nothing.
     * 
     * @param g2 the 2D graphics context
     * @param component the graphics component
     * @param paint the paint to use for the outline
     * @param troke the stroke to use for the outline
     */
    public static void paint( Graphics2D g2, PhetGraphic component, Paint paint, Stroke stroke ) {
        if( _enabled ) {
            BoundsOutliner.paint( g2, component.getBounds(), paint, stroke );
        }
    }

    /**
     * Draws the specified bounds using a specified paint and stroke.
     * If disabled, draws nothing.
     * 
     * @param g2 the 2D graphics context
     * @param bounds the bounds
     * @param paint the paint to use for the outline
     * @param stroke the stroke to use for the outline
     */
    public static void paint( Graphics2D g2, Rectangle bounds, Paint paint, Stroke stroke ) {
        if( _enabled ) {
            // Save graphics state
            Paint oldPaint = g2.getPaint();
            Stroke oldStroke = g2.getStroke();

            // Draw outline
            Rectangle r = new Rectangle( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1 );
            g2.setPaint( paint );
            g2.setStroke( stroke );
            g2.draw( r );

            // Restore graphics state
            g2.setPaint( oldPaint );
            g2.setStroke( oldStroke );
        }
    }
}