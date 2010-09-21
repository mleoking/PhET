/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;

/**
 * DebuggerGraphic displays the bounds and location marker for a set of PhetGraphics.
 * It is intended for use in debugging phetcommon and client applications.
 * The bounds is drawn as a rectangle outline.
 * The location marker is drawn as a crosshair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebuggerGraphic extends PhetGraphic {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _boundsEnabled;
    private Color _boundsColor;
    private BasicStroke _boundsStroke;
    private boolean _locationEnabled;
    private Color _locationColor;
    private BasicStroke _locationStroke;
    private Dimension _locationSize;
    private Hashtable _specifications; // key=PhetGraphic, value=Specification
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /** Drawing specification for a graphic. */
    private static class Specification {
        public Color boundsColor;
        public Color locationColor;
        public Specification( Color boundsColor, Color locationColor ) {
            this.boundsColor = boundsColor;
            this.locationColor = locationColor;
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component parent Component, typically an apparatus panel
     */
    public DebuggerGraphic( Component component ) {
        super( component );
        assert( component != null );
        _boundsEnabled = true;
        _boundsColor = Color.BLUE;
        _boundsStroke = new BasicStroke( 1f );
        _locationEnabled = true;
        _locationColor = Color.RED;
        _locationStroke = new BasicStroke( 1f );
        _locationSize = new Dimension( 10, 10 );
        _specifications = new Hashtable();
    }
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    /**
     * Adds a graphic to the debugger using default colors.
     * 
     * @param graphic
     */
    public void add( PhetGraphic graphic ) {
        add( graphic, _boundsColor, _locationColor );
    }
    
    /**
     * Adds a graphic to the debugger using specific colors.
     * 
     * @param graphic
     * @param boundsColor
     * @param locationColor
     */
    public void add( PhetGraphic graphic, Color boundsColor, Color locationColor ) {
        assert( graphic != null );
        assert( boundsColor != null );
        assert( locationColor != null );
        _specifications.put( graphic, new Specification( boundsColor, locationColor ) );
    }
    
    /**
     * Removes a graphic from the debugger.
     * 
     * @param graphic
     */
    public void remove( PhetGraphic graphic ) {
        assert( graphic != null );
        _specifications.remove( graphic);
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the default color used to draw bounds.
     * 
     * @param boundsColor
     */
    public void setBoundsColor( Color boundsColor ) {
        assert( boundsColor != null );
        _boundsColor = boundsColor;
    }
    
    /**
     * Gets the default color used to draw bounds.
     * 
     * @return the color
     */
    public Color getBoundsColor() {
        return _boundsColor;
    }
    
    /**
     * Sets the width of the stroke used to draw bounds.
     * 
     * @param width
     */
    public void setBoundsStrokeWidth( float width ) {
        _boundsStroke = new BasicStroke( width );
    }
    
    /**
     * Gets the width of the stroke used to draw bounds.
     * 
     * @return the width
     */
    public float getBoundsStrokeWidth() {
        return _boundsStroke.getLineWidth();
    }
    
    /**
     * Sets the color used to draw location.
     * 
     * @param locationColor
     */
    public void setLocationColor( Color locationColor ) {
        assert( locationColor != null );
        _locationColor = locationColor;
    }
    
    /**
     * Gets the color used to draw location.
     * 
     * @return the color
     */
    public Color getLocationColor() {
        return _locationColor;
    }
    
    /**
     * Sets the width of the stroke used to draw the location marker.
     * 
     * @param width
     */
    public void setLocationStrokeWidth( float width ) {
        _locationStroke = new BasicStroke( width );
    }
    
    /**
     * Gets the width of the stroke used to draw the location marker.
     * 
     * @return the width
     */
    public float getLocationStrokeWidth() {
        return _locationStroke.getLineWidth();
    }
    
    /**
     * Sets the size of the location marker.
     * 
     * @param size
     */
    public void setLocationSize( Dimension size ) {
        assert( size != null );
        setLocationSize( size.width, size.height );
    }
    
    /**
     * Sets the size of the location marker.
     * 
     * @param width
     * @param height
     */
    public void setLocationSize( int width, int height ) {
        _locationSize.setSize( width, height );
    }
    
    /**
     * Gets the size of the location marker.
     * 
     * @return the size
     */
    public Dimension getLocationSize() {
        return new Dimension( _locationSize );
    }
    
    /**
     * Enables or disables drawing of the bounds.
     * 
     * @param enabled true or false
     */
    public void setBoundsEnabled( boolean enabled ) {
        _boundsEnabled = enabled;
    }
    
    /**
     * Determines whether drawing of bounds is enabled.
     * 
     * @return true or false
     */
    public boolean isBoundsEnabled() {
        return _boundsEnabled;
    }
    
    /**
     * Enables or disables drawing of the location marker.
     * 
     * @param enabled true or false
     */
    public void setLocationEnabled( boolean enabled ) {
        _locationEnabled = enabled;
    }
    
    /**
     * Determines whether drawing of the location marker is enabled.
     * 
     * @return true or false
     */
    public boolean isLocationEnabled() {
        return _locationEnabled;
    }
    
    /**
     * Determines the bounds of the debugger.
     */
    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        Enumeration e = _specifications.keys();
        while ( e.hasMoreElements() ) {
            PhetGraphic graphic = (PhetGraphic) e.nextElement();
            if ( bounds == null ) {
                bounds = new Rectangle();
            }
            bounds.union( graphic.getBounds() );
        }
        return bounds;
    }

    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Draws the bounds and location marker for each graphic.
     * The bounds is drawn as a rectangle outline.
     * The location marker is drawn as a crosshair.
     * 
     * @param g2 graphics context
     */
    public void paint( Graphics2D g2 ) {
        assert( g2 != null );
        if ( isVisible() && ( isBoundsEnabled() || isLocationEnabled() )) {
            saveGraphicsState( g2 );
            Enumeration e = _specifications.keys();
            while ( e.hasMoreElements() ) {
                // Get the rendering details for the next graphic.
                PhetGraphic graphic = (PhetGraphic) e.nextElement();
                Rectangle bounds = graphic.getBounds();
                Point location = graphic.getLocation();
                Specification s = (Specification)_specifications.get( graphic );
                
                // Outline the bounds.
                if ( isBoundsEnabled() ) {
                    g2.setStroke( _boundsStroke );
                    g2.setPaint( s.boundsColor );
                    g2.draw( bounds );
                }
                
                // Draw a cross, centered at the graphic's location.
                if ( isLocationEnabled() ) {
                    g2.setStroke( _locationStroke );
                    g2.setPaint( s.locationColor );
                    g2.drawLine( location.x, location.y - _locationSize.height/2, location.x, location.y + _locationSize.height/2 );
                    g2.drawLine( location.x - _locationSize.width/2, location.y, location.x + _locationSize.width/2, location.y );
                }
            }
            restoreGraphicsState();
        }
    }
}