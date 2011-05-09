// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

/**
 * ColorIntensitySlider is a slider used to control color intensity
 * Intensity is a percentage, with a range of 0-100 inclusive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorIntensitySlider extends JSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /**
     * Horizontal orientation
     */
    public static int HORIZONTAL = JSlider.HORIZONTAL;
    /**
     * Vertical orientation
     */
    public static int VERTICAL = JSlider.VERTICAL;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Color _color;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param color       the color whose intensity is being controlled
     * @param orientation orientation of the control, HORIZONTAL or VERTICAL)
     * @param size        the dimensions of the control
     */
    public ColorIntensitySlider( Color color, int orientation, Dimension size ) {

        _color = color;

        setOrientation( orientation );
        setMinimum( 0 );
        setMaximum( 100 );
        setValue( 0 );
        setPreferredSize( size );
        setOpaque( false );

        // If you don't do this, nothing is drawn.
//        revalidate();
//        repaint();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the color.
     *
     * @param color the color
     */
    public void setColor( Color color ) {
        _color = color;
        super.repaint();
    }

    /**
     * Gets the color.
     *
     * @return the color
     */
    public Color getColor() {
        return _color;
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Paints the component. A gradient fill, based on the model color, is used
     * for the background.
     *
     * @param g the graphics context
     */
    public void paintComponent( Graphics g ) {

        if ( super.isVisible() ) {
            Graphics2D g2 = (Graphics2D) g;

            // Save any graphics state that we'll be touching.
            Paint oldPaint = g2.getPaint();
            Stroke oldStroke = g2.getStroke();

            // Use local variables to improve code readability.
            Component component = this;
            int x = component.getX();
            int y = component.getY();
            int w = component.getWidth();
            int h = component.getHeight();

            // HACK:
            // The trackOffset is the distance from the edge of
            // _containerPanel to the track that the slider moves in.
            // To make the slider knob line up with the correct colors in
            // the background gradient, we make the gradient extend from
            // one end of the track to the other. Since we don't really
            // know where the track starts and ends, we take a guess here.
            // This seems to work on all currently-supported platforms.
            int trackOffset = 15;

            Shape top, bottom, middle, shape;
            Point2D p1, p2;
            if ( getOrientation() == VERTICAL ) {
                // The background shapes.
                top = new Rectangle2D.Double( x, y, w, h / 2 );
                bottom = new Rectangle2D.Double( x, y + ( h / 2 ), w, h / 2 );
                middle = new Rectangle2D.Double( x, y + trackOffset, w, h - ( 2 * trackOffset ) );
                shape = new Rectangle2D.Double( x, y, w, h );
                // The gradient points.
                p1 = new Point2D.Double( x + ( w / 2 ), y + trackOffset );
                p2 = new Point2D.Double( x + ( w / 2 ), y + h - trackOffset );
            }
            else /* HORIZONTAL */ {
                // The background shapes.
                top = new Rectangle2D.Double( x + ( w / 2 ), y, w / 2, h );
                bottom = new Rectangle2D.Double( x, y, w / 2, h );
                middle = new Rectangle2D.Double( x + trackOffset, y, w - ( 2 * trackOffset ), h );
                shape = new Rectangle2D.Double( x, y, w, h );
                // The gradient points.
                p1 = new Point2D.Double( x + w - trackOffset, y + ( h / 2 ) );
                p2 = new Point2D.Double( x + trackOffset, y + ( h / 2 ) );
            }
            GradientPaint gradient = new GradientPaint( p1, _color, p2, Color.BLACK );

            // Render the background.
            g2.setPaint( Color.BLACK );
            g2.fill( bottom );
            g2.setPaint( _color );
            g2.fill( top );
            g2.setPaint( gradient );
            //g2.setPaint( Color.WHITE ); // DEBUG, to see how middle lines up with track ends.
            g2.fill( middle );
            g2.setStroke( new BasicStroke( 1f ) );
            g2.setPaint( Color.white );
            g2.draw( shape );

            // Restore the graphics state.
            g2.setPaint( oldPaint );
            g2.setStroke( oldStroke );

            // Render the component.
            super.paintComponent( g );
        }
    } // paint

}