/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * WireLoopGraphic is the UI component that represents a wire loop.
 * It is a Shape that is described using constructive area geometry.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WireLoopGraphic extends PhetGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Radius of the loop.
    private double _radius;
    // Shapes for various parts of the loop.
    private Shape _exterior, _interior, _hole;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param radius the radius of the loop
     */
    public WireLoopGraphic( Component component, double radius ) {
        super( component );
        _radius = radius;
        updateShape();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the radius of the loop.
     * 
     * @return the radius
     */
    public double getRadius() {
        return _radius;
    }
    
    /**
     * Gets the width of the loop.
     * 
     * @return the width
     */
    public int getWidth() {
        return _exterior.getBounds().width;
    }
    
    /**
     * Gets the height of the loop.
     * 
     * @return the height
     */
    public int getHeight() {
        return _exterior.getBounds().height;
    }

    /**
     * Determines the bounds.
     * 
     * @return the bounding rectangle
     */
    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        if( _exterior != null ) {
            bounds = getNetTransform().createTransformedShape( _exterior.getBounds() ).getBounds();
            bounds.x -= 1; // WORKAROUND: bounds are off by 1 pixel
            bounds.height += 1; // WORKAROUND: bounds are off by 1 pixel
        }
        return bounds;
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Updates the shape.
     */
    private void updateShape() {
        double x = 0;
        double y = 0;
        
        // Use constructive area geometry to create the exterior shape.
        Area exterior = new Area();
        {
            Ellipse2D e1 = new Ellipse2D.Double( x, y, 20, _radius );
            Rectangle2D r1 = new Rectangle2D.Double( x + 10, y, 10, _radius );
            Ellipse2D e2 = new Ellipse2D.Double( x + 10, y, 20, _radius );
            exterior.add( new Area( e1 ) );
            exterior.add( new Area( e2 ) );
            exterior.add( new Area( r1 ) );
        }
        
        _exterior = exterior;
        _interior = new Ellipse2D.Double( x, y, 20, _radius );
        _hole = new Ellipse2D.Double( x + 4, y + 4, 12, _radius - 8 );

        super.repaint();
    }
    
    /**
     * Draws the filter, based on the current state of the filter model.
     * 
     * @param g2 graphics context
     */
    public void paint( Graphics2D g2 ) {
        if( super.isVisible() ) {
            super.saveGraphicsState( g2 );

            // Request antialiasing.
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHints( hints );

            // Transform
            g2.transform( super.getNetTransform() );
            
            // Draw the exterior
            g2.setPaint( Color.GRAY );
            g2.fill( _exterior );
            g2.setPaint( Color.BLACK );
            g2.setStroke( new BasicStroke(1f) );
            g2.draw( _exterior );

            // Draw the interior
            g2.setPaint( Color.DARK_GRAY );
            g2.fill( _interior );

            // Draw the hole in the middle.
            g2.setPaint( Color.WHITE );
            g2.fill( _hole );
            
            //XXX draw bounds of this graphic
            Rectangle bounds = determineBounds();
            g2.setStroke( new BasicStroke(1f) );
            g2.setPaint( Color.RED );
            g2.draw( bounds );

            super.restoreGraphicsState();
        }
    }

}