/* Copyright 2005, University of Colorado */

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
import java.util.ArrayList;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.model.AbstractCoil;
import edu.colorado.phet.faraday.model.Electron;
import edu.colorado.phet.faraday.model.ElectronPathDescriptor;
import edu.colorado.phet.faraday.model.QuadBezierSpline;
import edu.colorado.phet.faraday.util.GenericRescaler;
import edu.colorado.phet.faraday.util.IRescaler;


/**
 * CoilGraphic is the graphical representation of a coil of wire.
 * In order to simulate objects passing "through" the coil, the coil graphic
 * consists of two layers, called the "foreground" and "background".
 * <p>
 * The coil is drawn as a set of curves, with a "wire end" joined at the each
 * end of the coil.  The wire ends is where things can be connected to the coil
 * (eg, a lightbulb or voltmeter). 
 * <p>
 * The coil optionally shows electrons flowing. The number of electrons is 
 * a function of the coil radius and number of loops.  Electrons are part of 
 * the simulation model, and they know about the path that they need to follow.
 * The path is a describe by a set of ElectronPathDescriptors.
 * <p>
 * The set of ElectronPathDescriptors contains the information that the electrons
 * need to determine which layer that are in (foreground or background) 
 * and how to adjust ("scale") their speed so that they appear to flow at
 * the same rate in all curve segments.  (For example, the wire ends are
 * significantly shorter curves that the other segments in the coil.) 
 * <p>
 * WARNING!  The updateCoil method in particular is very complicated, and
 * the curves that it creates have been tuned so that all curve segments 
 * are smoothly joined to form a 3D-looking coil.  If you change values,
 * do so with caution, test frequently, and perform a close visual 
 * inspection of your changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CoilGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Loop parameters
    private static final Color FOREGROUND_COLOR = new Color( 153, 102, 51 ); // light brown
    private static final Color MIDDLEGROUND_COLOR = new Color( 92, 52, 12 ); // dark brown
    private static final Color BACKGROUND_COLOR = new Color( 40, 23, 3 ); // really dark brown
    private static final int WIRE_WIDTH = 16;
    private static final double LOOP_SPACING_FACTOR = 0.3; // ratio of loop spacing to loop radius
    
    // Space between electrons, determines the number of electrons add to each curve.
    private static final int ELECTRON_SPACING = 25;
    private static final int ELECTRONS_IN_LEFT_END = 2;
    private static final int ELECTRONS_IN_RIGHT_END = 2;
    
    // Electron speed rescaler
    private static final double RESCALER_THRESHOLD = 1.0;
    private static final double RESCALER_MIN_EXPONENT = 0.25;
    private static final double RESCALER_MAX_EXPONENT = 0.7;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The parent component
    private Component _component;
    
    // The coil that this graphic represents.
    private AbstractCoil _coilModel;
    
    // The application's base model, needed for adding/removing Electrons.
    private BaseModel _baseModel;
    
    // The foreground graphics layer.
    private CompositePhetGraphic _foreground;
    
    // The background graphics layer.
    private CompositePhetGraphic _background;
    
    // Is electron animation enabled?
    private boolean _electronAnimationEnabled;
    
    // The Stoke used to render the loop (determines its width).
    private Stroke _loopStroke;
    
    // The Colors used to render the loop.
    private Color _foregroundColor, _middlegroundColor, _backgroundColor;
    
    // Description of the path that electrons follow (array of ElectronPathDescriptor).
    private ArrayList _electronPath;
    
    // Electrons in the coil (array of Electron)
    private ArrayList _electrons;
    
    // Used to determine if the number of loops has changed.
    private int _numberOfLoops;
    
    // Uses to determine if the loop radius has changed.
    private double _loopRadius;
    
    // Used to determine if the voltage across the coil has changed.
    private double _voltage;
    
    // Rescales the electron speed.
    private GenericRescaler _rescaler;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component parent Component
     * @param baseModel
     * @param coilModel the coil that this graphic is watching
     * @param rescaler
     */
    public CoilGraphic( Component component, BaseModel baseModel, AbstractCoil coilModel ) {
        assert( component != null );
        assert( baseModel != null );
        assert( coilModel != null );
        
        _component = component;
        _baseModel = baseModel;
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        // Rescaler for smoothing out electron speed.
        _rescaler = new GenericRescaler();
        _rescaler.setThreshold( RESCALER_THRESHOLD );
        _rescaler.setExponents( RESCALER_MIN_EXPONENT, RESCALER_MAX_EXPONENT );
        
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ); 
        _foreground = new CompositePhetGraphic( _component );
        _background = new CompositePhetGraphic( _component );
        _foreground.setRenderingHints( hints );
        _background.setRenderingHints( hints );
        
        _electronAnimationEnabled = true;
        _loopStroke = new BasicStroke( WIRE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
        _foregroundColor = FOREGROUND_COLOR;
        _middlegroundColor = MIDDLEGROUND_COLOR;
        _backgroundColor = BACKGROUND_COLOR;
        
        _electronPath = new ArrayList();
        _electrons = new ArrayList();
        
        _numberOfLoops = -1; // force update
        _loopRadius = -1; // force update
        _voltage = -1;  // force update
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the PhetGraphic that contains the foreground elements of the coil.
     * 
     * @return the foreground graphics
     */
    public PhetGraphic getForeground() {
        return _foreground;
    }
    
    /**
     * Gets the PhetGraphic that contains the background elements of the coil.
     * 
     * @return the background graphics
     */
    public PhetGraphic getBackground() {
        return _background;
    }
    
    /**
     * Enables/disables animation of current flow.
     * 
     * @param enabled true to enabled, false to disable
     */
    public void setElectronAnimationEnabled( boolean enabled ) {
        if ( enabled != _electronAnimationEnabled ) {
            _electronAnimationEnabled = enabled;
            updateElectrons();
        }
    }
    
    /**
     * Determines whether animation of current flow is enabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isElectronAnimationEnabled() {
        return _electronAnimationEnabled;
    }

    /**
     * Sets the colors used for the loops.
     * Three colors are combined in various gradients to provide a 3D look.
     * <p>
     * Colors are changed only if their corresponding parameter is not null.
     * For example, to change only the foreground color, use setColor(Color.RED,null,null).
     * 
     * @param foregroundColor the foreground color
     * @param middlegroundColor the middleground color
     * @param backgroundColor the background color
     */
    public void setColors( Color foregroundColor, Color middlegroundColor, Color backgroundColor ) {
        if ( foregroundColor != null ) {
            _foregroundColor = foregroundColor;
        }
        if ( middlegroundColor != null ) {
            _middlegroundColor = middlegroundColor;
        }
        if ( backgroundColor != null ) {
            _backgroundColor = backgroundColor;
        }
        update();
    }
    
    /**
     * Gets the foreground color used to draw the coil.
     * 
     * @return the foreground color
     */
    public Color getForegroundColor() {
        return _foregroundColor;
    }
    
    /**
     * Gets the middleground color used to draw the coil.
     * 
     * @return the middleground color
     */
    public Color getMiddlegroundColor() {
        return _middlegroundColor;
    }
    
    /**
     * Gets the background color used to draw the coil.
     * 
     * @return the background color
     */
    public Color getBackgroundColor() {
        return _backgroundColor;
    }
    
    /**
     * Sets the visibility of this graphic.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        _foreground.setVisible( visible );
        _background.setVisible( visible );
        update();
    }
    
    /**
     * Gets the visibility of this graphic.
     * It is considered visible if either the foreground or background is visible.
     * 
     * @return true if visible, false if invisible
     */
    public boolean isVisible() {
        return ( _foreground.isVisible() || _background.isVisible() );
    }
    
    /**
     * Repaint this graphic.
     */
    public void repaint() {
        _foreground.repaint();
        _background.repaint();
    }
     
    /**
     * Gets the bounds of the coil graphic.
     * Takes the union of the foreground and background bounds.
     * 
     * @return the bounds
     */
    public Rectangle getBounds() {
        return _foreground.getBounds().union( _background.getBounds() );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the view to match the model.
     * 
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isVisible() ) {
            
            boolean dirty = false;
            
            // Update the physical appearance of the coil.
            if ( coilChanged() ) {
                dirty = true;
                updateCoil();
            }
            
            // Change the speed/direction of electrons to match the voltage.
            if ( _electronAnimationEnabled && electronsChanged() ) {
                dirty = true;
                updateElectrons();
            }
                   
            if ( dirty ) {
                repaint();
            }
        }
    }
    
    /* 
     * Determines if the physical appearance of the coil has changed.
     * 
     * @return true or false
     */
    private boolean coilChanged() {
        boolean changed = false;
        if ( _numberOfLoops != _coilModel.getNumberOfLoops() ||
             _loopRadius != _coilModel.getRadius() ) {
            changed = true;
            _numberOfLoops = _coilModel.getNumberOfLoops();
            _loopRadius = _coilModel.getRadius();
        }
        return changed;
    }
    
    /*
     * Determines if the electron animation has changed.
     * 
     * @return true or false
     */
    private boolean electronsChanged() {
        boolean changed = ! ( _voltage == 0 && _coilModel.getVoltage() == 0 );
        _voltage = _coilModel.getVoltage();
        return changed;
    }
    
    /**
     * Updates the coil, recreating all graphics and electron model elements.
     * <p>
     * WARNING! A lot of time was spent tweaking points so that the curves appear
     * to form one 3D continuous coil.  Be very careful what you change, and visually 
     * inspect the results closely.
     */
    private void updateCoil() {
        
        // Start with a clean slate.
        {
            // Removing any existing graphics components.
            _foreground.clear();
            _background.clear();

            // Clear the electron path description.
            _electronPath.clear();

            // Remove electrons from the model.
            for ( int i = 0; i < _electrons.size(); i++ ) {
                Electron electron = (Electron) _electrons.get( i );
                electron.removeAllObservers();
                _baseModel.removeModelElement( electron );
            }
            _electrons.clear();
        }
        
        final double radius = _coilModel.getRadius();
        
        final int numberOfLoops = _coilModel.getNumberOfLoops();

        // Loop spacing, in pixels.
        final int loopSpacing = (int)(radius * LOOP_SPACING_FACTOR );
        
        // Start at the left-most loop, keeping the coil centered.
        final int xStart = -( loopSpacing * (numberOfLoops - 1) / 2 );
        
        // Create the wire ends & loops from left to right.
        // Curves are created in the order that they are pieced together.
        for ( int i = 0; i < numberOfLoops; i++ ) {
            
            final int xOffset = xStart + ( i * loopSpacing );
            
            if ( i == 0 ) {
                // Left wire end
                {
                    Point endPoint = new Point( -loopSpacing / 2 + xOffset, (int) -radius ); // lower
                    Point startPoint = new Point( endPoint.x - 15, endPoint.y - 40 ); // upper
                    Point controlPoint = new Point( endPoint.x - 20, endPoint.y - 20 );
                    QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );
                    
                    // Scale the speed, since this curve is different than the others in the coil.
                    double length = startPoint.distance( endPoint );
                    double speedScale = radius / length;
                    ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _background, ElectronPathDescriptor.BACKGROUND, speedScale );
                    _electronPath.add( d );
                    
                    // Horizontal gradient, left to right.
                    Paint paint = new GradientPaint( startPoint.x, 0, _middlegroundColor, endPoint.x, 0, _backgroundColor );
                    
                    PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                    shapeGraphic.setShape( curve );
                    shapeGraphic.setStroke( _loopStroke );
                    shapeGraphic.setBorderPaint( paint );
                    _background.addGraphic( shapeGraphic );
                }
                
                // Back top (left-most) is slightly different so it connects to the left wire end.
                {
                    Point startPoint = new Point( -loopSpacing / 2 + xOffset, (int) -radius ); // upper
                    Point endPoint = new Point( (int) ( radius * .25 ) + xOffset, 0 ); // lower
                    Point controlPoint = new Point( (int) ( radius * .15 ) + xOffset, (int) ( -radius * .70 ) );
                    QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                    ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _background, ElectronPathDescriptor.BACKGROUND );
                    _electronPath.add( d );
                    
                    Paint paint = _backgroundColor;
                    
                    PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                    shapeGraphic.setShape( curve );
                    shapeGraphic.setStroke( _loopStroke );
                    shapeGraphic.setBorderPaint( paint );
                    _background.addGraphic( shapeGraphic );
                }   
            }
            else {
                // Back top (no wire end connected)
                Point startPoint = new Point( -loopSpacing + xOffset, (int)-radius ); // upper
                Point endPoint = new Point( (int)(radius * .25) + xOffset, 0 ); // lower
                Point controlPoint = new Point( (int)(radius * .15) + xOffset, (int)(-radius * 1.20));
                QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _background, ElectronPathDescriptor.BACKGROUND );
                _electronPath.add( d );

                // Diagonal gradient, upper left to lower right.
                Paint paint = new GradientPaint( (int)(startPoint.x + (radius * .10)), (int)-(radius), _middlegroundColor, xOffset, (int)-(radius * 0.92), _backgroundColor );
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _background.addGraphic( shapeGraphic );
            }
            
            // Back bottom
            {  
                Point startPoint = new Point( (int)(radius * .25) + xOffset, 0 ); // upper
                Point endPoint = new Point( xOffset, (int) radius ); // lower
                Point controlPoint = new Point( (int)(radius * .35) + xOffset, (int)(radius * 1.20) );
                QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _background, ElectronPathDescriptor.BACKGROUND );
                _electronPath.add( d );

                // Vertical gradient, upper to lower
                Paint paint = new GradientPaint( 0, (int)(radius * 0.92), _backgroundColor, 0, (int)(radius), _middlegroundColor );
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _background.addGraphic( shapeGraphic );
            }
            
            // Front bottom
            {
                Point startPoint = new Point( xOffset, (int) radius ); // lower
                Point endPoint = new Point( (int) ( -radius * .25 ) + xOffset, 0 ); // upper
                Point controlPoint = new Point( (int) ( -radius * .25 ) + xOffset, (int) ( radius * 0.80 ) );
                QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _foreground, ElectronPathDescriptor.FOREGROUND );
                _electronPath.add( d );

                // Horizontal gradient, left to right
                Paint paint = new GradientPaint( (int)(-radius * .25) + xOffset, 0, _foregroundColor, (int)(-radius * .15) + xOffset, 0, _middlegroundColor );
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
            }

            // Front top
            {
                Point startPoint = new Point( (int) ( -radius * .25 ) + xOffset, 0 ); // lower
                Point endPoint = new Point( xOffset, (int) -radius ); // upper
                Point controlPoint = new Point( (int) ( -radius * .25 ) + xOffset, (int) ( -radius * 0.80) );
                QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _foreground, ElectronPathDescriptor.FOREGROUND );
                _electronPath.add( d );
                
                // Horizontal gradient, left to right
                Paint paint = new GradientPaint( (int)(-radius * .25) + xOffset, 0, _foregroundColor, (int)(-radius * .15) + xOffset, 0, _middlegroundColor );
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
            }
            
            // Right wire end
            if ( i == numberOfLoops - 1 ) {
                Point startPoint = new Point( xOffset, (int) -radius ); // lower
                Point endPoint = new Point( startPoint.x + 15, startPoint.y - 40 ); // upper
                Point controlPoint = new Point( startPoint.x + 20, startPoint.y - 20 );
                QuadBezierSpline curve = new QuadBezierSpline( startPoint, controlPoint, endPoint );

                // Scale the speed, since this curve is different than the others in the coil.
                double length = startPoint.distance( endPoint );
                double speedScale = radius / length;
                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _foreground, ElectronPathDescriptor.FOREGROUND, speedScale );
                _electronPath.add( d );

                Paint paint = _middlegroundColor;
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
            }   
        }

        // Add electrons to the coil.
        {
            final double speed = calculateElectronSpeed();
            
            final int leftEndIndex = 0;
            final int rightEndIndex = _electronPath.size() - 1;

            // For each curve...
            for ( int pathIndex = 0; pathIndex < _electronPath.size(); pathIndex++ ) {
                /*
                 * The wire ends are a different size, 
                 * and therefore contain a different number of electrons.
                 */
                int numberOfElectrons;
                if ( pathIndex == leftEndIndex ) {
                    numberOfElectrons = ELECTRONS_IN_LEFT_END;
                }
                else if ( pathIndex == rightEndIndex ) {
                    numberOfElectrons = ELECTRONS_IN_RIGHT_END;
                }
                else {
                    numberOfElectrons = (int) ( radius / ELECTRON_SPACING );
                }

                // Add the electrons to the curve.
                for ( int i = 0; i < numberOfElectrons; i++ ) {

                    double pathPosition = i / (double) numberOfElectrons;

                    // Model
                    Electron electron = new Electron();
                    electron.setPath( _electronPath );
                    electron.setPositionAlongPath( pathIndex, pathPosition );
                    electron.setSpeed( speed );
                    electron.setEnabled( _electronAnimationEnabled );
                    _electrons.add( electron );
                    _baseModel.addModelElement( electron );

                    // View
                    ElectronPathDescriptor descriptor = electron.getPathDescriptor();
                    CompositePhetGraphic parent = descriptor.getParent();
                    ElectronGraphic electronGraphic = new ElectronGraphic( _component, parent, electron );
                    descriptor.getParent().addGraphic( electronGraphic );
                }
            }
        }
    }

    /**
     * Updates the speed and direction of electrons.
     */
    private void updateElectrons() {
        // Speed and direction is a function of the voltage.
        final double speed = calculateElectronSpeed();
        
        // Update all electrons.
        final int numberOfElectrons = _electrons.size();
        for ( int i = 0; i < numberOfElectrons; i++ ) {
            Electron electron = (Electron) _electrons.get( i );
            electron.setEnabled( _electronAnimationEnabled );
            electron.setSpeed( speed );
        }
    }
    
    /**
     * Calculates the speed of electrons, a function of the voltage across the coil.
     * Direction is indicated by the sign of the value.
     * Magnitude of 0 indicates no motion.
     * Magnitude of 1 moves along an entire curve segment in one clock tick.
     * 
     * @return the speed, from -1...+1 inclusive
     */
    private double calculateElectronSpeed() {
        
        double speed = _coilModel.getVoltage() / _coilModel.getMaxVoltage();
        speed = MathUtil.clamp( -1, speed, +1 );
        
        // Rescale the speed to improve the visual effect.
        double sign = ( speed < 0 ) ? -1 : +1;
        speed = sign * _rescaler.rescale( Math.abs( speed ) );

        return speed;
    }
    
    /**
     * Gets the rectangles that define what it means to collide with the coil.
     * There is a rectangle at the top and bottom of the coil. These rectangles
     * are based on the coil's bounds, with a bit of "hand tweaking" to make 
     * them look plausible.
     * 
     * @return array of Rectangle
     */
    public Rectangle[] getCollisionBounds() {
        if ( isVisible() ) {
            Rectangle b = getBounds();
            
            // These values were weaked via trial & error.
            Rectangle topBounds = new Rectangle( b.x + 45, b.y + 38, b.width - 100, 18 );
            Rectangle bottomBounds = new Rectangle( b.x + 45, b.y + b.height - 19, b.width - 68, 18 );
            
            Rectangle[] bounds = { topBounds, bottomBounds };
            return bounds;
        }
        else {
            return null;
        }
    }
}