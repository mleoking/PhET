/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.coreadditions.help;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.colorvision.coreadditions.view.BoundsOutliner;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * WiggleMe is the base class for all "wiggle me" help graphics.
 * The graphic is animated in a clockwise circular motion.  The rate
 * and travel of the animation are adjustable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */

public class WiggleMe extends PhetGraphic implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // The default length of one complete animation cycle, in clock ticks.
    private static int DEFAULT_CYCLE_LENGTH = 10;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The parent component
    private Component _component;
    // The model, used to get clock ticks for animation
    private BaseModel _model;
    // The graphic to be wiggled
    private PhetGraphic _graphic;
    // The upper-left corner of the bounding box that enclosed the animation.
    private Point _location;
    // The current location of the graphic.
    private Point _graphicLocation;
    // The width and height that the wiggle will travel.
    private Dimension _wiggleTravel;
    // The number of animation cycles completed, usually fractional.
    private double _cycles;
    // The number of clock ticks in one complete animation cycle.
    private int _cycleLength;
    // Whether the animation is running.
    private boolean _running;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructs a WiggleMe.
     * 
     * @param component the parent Component
     * @param model the module's model
     * @param graphic the graphic to be wiggled
     */
    public WiggleMe( Component component, BaseModel model, PhetGraphic graphic ) {

        super( component );

        // Initialize instance data.
        _component = component;
        _model = model;
        _graphic = graphic;
        _location = new Point( 0, 0 );
        _graphicLocation = new Point( 0, 0 );
        _wiggleTravel = new Dimension( 10, 10 );
        _cycles = 0;
        _cycleLength = DEFAULT_CYCLE_LENGTH;
        _running = false;
    }

    /**
     * Constructs a WiggleMe with no graphic component.
     * This constructor is intended to be used in subclassing.
     * 
     * @param component the parent Component
     * @param model the module's model
     */
    protected WiggleMe( Component component, BaseModel model ) {
        this( component, model, null );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the graphic that will be wiggled.
     * 
     * @param graphic the graphic
     */
    public void setGraphic( PhetGraphic graphic ) {
        _graphic = graphic;
    }

    /**
     * Gets the graphic.
     * 
     * @return the graphic
     */
    public PhetGraphic getGraphic() {
        return _graphic;
    }

    /**
     * Sets the location of the upper-left corner of bounding box
     * that encloses the animation.
     * 
     * @param location the location
     */
    public void setLocation( Point location ) {
        _location.setLocation( location );
        _graphicLocation.setLocation( location );
        repaint();
    }

    /**
     * Convenience method for setting the location.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( int x, int y ) {
        setLocation( new Point( x, y ) );
    }

    /**
     * Gets the location of the upper-left corner of the bounding box
     * that encloses the animation.
     * 
     * @return the location
     */
    public Point getLocation() {
        return new Point( _location );
    }

    /**
     * Sets the dimension for the animated wiggle motion.
     * This defines the wiggle's width and height.
     * 
     * @param dimension the dimension
     */
    public void setWiggleTravel( Dimension dimension ) {

        _wiggleTravel.setSize( dimension );
    }

    /**
     * Convenience method for setting the wiggle travel.
     * 
     * @param width the wiggle width
     * @param height the wiggle height
     */
    public void setWiggleDimensions( int width, int height ) {
        setWiggleTravel( new Dimension( width, height ) );
    }

    /**
     * Gets the wiggle travel.
     * 
     * @return the dimension that defines the travel
     */
    public Dimension getWiggleTravel() {
        return new Dimension( _wiggleTravel );
    }

    /**
     * Sets the length of the animation cycle.
     * 
     * @param clockTicks the length, in clock ticks
     */
    public void setCycleLength( int clockTicks ) {
        _cycleLength = clockTicks;
    }

    /**
     * Gets the length of the animation cycle.
     * 
     * @return the length, in clock ticks
     */
    public int getCycleLength() {
        return _cycleLength;
    }

    /**
     * Gets the bounds of the animation.
     * 
     * @return the bounds
     */
    public Rectangle getBounds() {
        return determineBounds();
    }

    /**
     * Determines the bounds of the animation.
     * This is the entire area that will be touched during 
     * one complete animation cycle.
     * 
     * @return the bounds
     */
    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        if( _graphic != null ) {
            int x = _location.x;
            int y = _location.y;
            int width = _graphic.getBounds().width + _wiggleTravel.width;
            int height = _graphic.getBounds().height + _wiggleTravel.height;
            bounds = new Rectangle( x, y, width, height );
        }
        return bounds;
    }

    //----------------------------------------------------------------------------
    // Animation control
    //----------------------------------------------------------------------------

    /**
     * Starts the animation.
     * Attempting to start and animation that is already running does nothing.
     */
    public void start() {
        if( !_running ) {
            _running = true;
            _model.addModelElement( this );
        }
    }

    /**
     * Stops the animation.
     * Attempting to stop and animation that is already stopped does nothing.
     */
    public void stop() {
        if( _running ) {
            _running = false;
            _model.removeModelElement( this );
        }
    }

    /**
     * Determines if the animation is running.
     * 
     * @return true if running, false if not running
     */
    public boolean isRunning() {
        return _running;
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /**
     * Handles ticks of the animation clock.
     * 
     * @param dt the time delta
     */
    public void stepInTime( double dt ) {
        if( _graphic != null ) {
            _cycles += ( 1.0 / _cycleLength );
            int x = (int) ( _location.x + ( _wiggleTravel.width / 2 ) + ( _wiggleTravel.width / 2 * Math.cos( _cycles ) ) );
            int y = (int) ( _location.y + ( _wiggleTravel.height / 2 ) + ( _wiggleTravel.height / 2 * Math.sin( _cycles ) ) );
            _graphicLocation.setLocation( x, y );
            repaint();
        }
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Repaints the entire area that the animation might draw in.
     */
    public void repaint() {
        Rectangle bounds = getBounds();
        _component.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
    }

    /**
     * Draws the current state of the animation.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if( super.isVisible() ) {
            // Save graphics state.
            AffineTransform oldTransform = g2.getTransform();

            // Draw the translated graphic.
            g2.translate( _graphicLocation.x, _graphicLocation.y );
            _graphic.paint( g2 );

            // Restore graphics state.
            g2.setTransform( oldTransform );

            BoundsOutliner.paint( g2, this ); // DEBUG
        }
    }

}