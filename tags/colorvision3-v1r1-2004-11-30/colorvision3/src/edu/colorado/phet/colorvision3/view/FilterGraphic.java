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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * FilterGraphic is the UI component that represents a filter.
 * It is a Shape that is described using constructive area geometry.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FilterGraphic extends PhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Height of the filter lens.
    private static final int LENS_HEIGHT = 150;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The filter model
    private Filter _filterModel;
    // Shapes for various parts of the filter.
    private Shape _exterior, _interior, _lens;
    // last known location of the model.
    private double _x, _y;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param filterModel the filter model
     */
    public FilterGraphic( Component component, Filter filterModel ) {
        super( component );
        _filterModel = filterModel;
        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Determines the bounds.
     * 
     * @return the bounding rectangle
     */
    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        if( _exterior != null ) {
            bounds = new Rectangle( _exterior.getBounds() );
        }
        return bounds;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view by consulting the model.
     * This is called each time the filter model changes.
     */
    public void update() {
        double x = _filterModel.getX();
        double y = _filterModel.getY();

        // If the filter has moved, reconstruct it.
        if( x != _x || y != y ) {
            // Use constructive area geometry to create the exterior shape.
            Area exterior = new Area();
            {
                Ellipse2D e1 = new Ellipse2D.Double( x, y, 20, LENS_HEIGHT );
                Rectangle2D r1 = new Rectangle2D.Double( x + 10, y, 10, LENS_HEIGHT );
                Ellipse2D e2 = new Ellipse2D.Double( x + 10, y, 20, LENS_HEIGHT );
                exterior.add( new Area( e1 ) );
                exterior.add( new Area( e2 ) );
                exterior.add( new Area( r1 ) );
            }

            _exterior = exterior;
            _interior = new Ellipse2D.Double( x, y, 20, LENS_HEIGHT );
            _lens = new Ellipse2D.Double( x + 4, y + 4, 12, LENS_HEIGHT - 8 );
        }

        // Need to repaint in case color has changed via super.setPaint.
        super.repaint();
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Draws the filter, based on the current state of the filter model.
     * 
     * @param g2 graphics context
     */
    public void paint( Graphics2D g2 ) {
        if( super.isVisible() && _filterModel.isEnabled() ) {
            // Save graphics state.
            RenderingHints oldHints = g2.getRenderingHints();
            Paint oldPaint = g2.getPaint();

            // Request antialiasing.
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHints( hints );

            // Draw the exterior
            g2.setPaint( Color.DARK_GRAY );
            g2.fill( _exterior );

            // Draw the interior
            g2.setPaint( Color.GRAY );
            g2.fill( _interior );

            // Draw the filter
            g2.setPaint( _filterModel.getTransmissionPeak() );
            g2.fill( _lens );

            // Restore graphics state.
            g2.setPaint( oldPaint );
            g2.setRenderingHints( oldHints );

            BoundsOutliner.paint( g2, this ); // DEBUG
        }
    }

}