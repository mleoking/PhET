/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.AbstractCoil;
import edu.colorado.phet.faraday.model.Electron;
import edu.colorado.phet.faraday.model.ElectronPathDescriptor;
import edu.colorado.phet.faraday.util.QuadBezierSpline;


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
 */
public class CoilGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Loop parameters
    private static final Color FOREGROUND_COLOR = new Color( 153, 102, 51 ); // light brown
    private static final Color MIDDLEGROUND_COLOR = new Color( 92, 52, 12 ); // dark brown
    private static final Color BACKGROUND_COLOR = new Color( 40, 23, 3 ); // really dark brown
    
    // Space between electrons, determines the number of electrons add to each curve.
    private static final int ELECTRON_SPACING = 25;
    private static final int ELECTRONS_IN_LEFT_END = 2;
    private static final int ELECTRONS_IN_RIGHT_END = 2;
    
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
    
    // Used to determine if the wire width has changed.
    private double _wireWidth;
    
    // Use to determine if the loop spacing has changed.
    private double _loopSpacing;
    
    // Used to determine if the current in the coil has changed.
    private double _current;
    
    // Collision bounds
    private Rectangle[] _collisionBounds;
    
    // Scale used for electron speed.
    private double _electronSpeedScale;
    
    // Whether to connect the ends of the coil.
    private boolean _endsConnected;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component parent Component
     * @param coilModel the coil that this graphic is watching
     * @param baseModel
     */
    public CoilGraphic( Component component, AbstractCoil coilModel, BaseModel baseModel ) {
        assert( component != null );
        assert( coilModel != null );
        assert( baseModel != null );
        
        _component = component;
        _baseModel = baseModel;
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ); 
        _foreground = new CompositePhetGraphic( _component );
        _background = new CompositePhetGraphic( _component );
        _foreground.setRenderingHints( hints );
        _background.setRenderingHints( hints );
        
        _electronAnimationEnabled = true;
        _foregroundColor = FOREGROUND_COLOR;
        _middlegroundColor = MIDDLEGROUND_COLOR;
        _backgroundColor = BACKGROUND_COLOR;
        
        _electronPath = new ArrayList();
        _electrons = new ArrayList();
        
        _numberOfLoops = -1; // force update
        _loopRadius = -1; // force update
        _wireWidth = -1; // force update
        _loopSpacing = -1; // force update
        _current = -1;  // force update
        
        _electronSpeedScale = 1.0;
        _endsConnected = false;
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
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
    
    /**
     * Sets the scale used for electron speed.
     * 
     * @param electronSpeedScale
     */
    public void setElectronSpeedScale( double electronSpeedScale ) {
        assert( electronSpeedScale > 0 );
        if ( electronSpeedScale != _electronSpeedScale ) {
            _electronSpeedScale = electronSpeedScale;
            // Update all electrons.
            final int numberOfElectrons = _electrons.size();
            for ( int i = 0; i < numberOfElectrons; i++ ) {
                Electron electron = (Electron) _electrons.get( i );
                electron.setSpeedScale( _electronSpeedScale );
            }
        }
    }
    
    /**
     * Gets the scale used for electron speed.
     * 
     * @return the scale
     */
    public double getElectronSpeedScale() {
        return _electronSpeedScale;
    }
    
    public void setEndsConnected( boolean endsConnected ) {
        if ( endsConnected != _endsConnected ) {
            _endsConnected = endsConnected;
            updateCoil();
            repaint();
        }
    }
    
    public boolean isEndsConnected() {
        return _endsConnected;
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
             _loopRadius != _coilModel.getRadius() ||
             _wireWidth != _coilModel.getWireWidth() ||
             _loopSpacing != _coilModel.getLoopSpacing() ) {
            changed = true;
            _numberOfLoops = _coilModel.getNumberOfLoops();
            _loopRadius = _coilModel.getRadius();
            _wireWidth = _coilModel.getWireWidth();
            _loopSpacing = _coilModel.getLoopSpacing();
        }
        return changed;
    }
    
    /*
     * Determines if the electron animation has changed.
     * 
     * @return true or false
     */
    private boolean electronsChanged() {
        boolean changed = ! ( _current == 0 && _coilModel.getCurrentAmplitude() == 0 );
        _current = _coilModel.getCurrentAmplitude();
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
        
        Stroke loopStroke = new BasicStroke( (float) _coilModel.getWireWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
        
        final double radius = _coilModel.getRadius();
        
        final int numberOfLoops = _coilModel.getNumberOfLoops();

        final int loopSpacing = (int) _coilModel.getLoopSpacing();
        
        // Start at the left-most loop, keeping the coil centered.
        final int xStart = -( loopSpacing * (numberOfLoops - 1) / 2 );
        
        Point leftEndPoint = null;
        Point rightEndPoint = null;
        
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
                    double speedScale = ( radius / ELECTRON_SPACING ) / ELECTRONS_IN_LEFT_END;
                    ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _background, ElectronPathDescriptor.BACKGROUND, speedScale );
                    _electronPath.add( d );
                    
                    // Horizontal gradient, left to right.
                    Paint paint = new GradientPaint( startPoint.x, 0, _middlegroundColor, endPoint.x, 0, _backgroundColor );
                    
                    PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                    shapeGraphic.setShape( curve );
                    shapeGraphic.setStroke( loopStroke );
                    shapeGraphic.setBorderPaint( paint );
                    _background.addGraphic( shapeGraphic );
                    
                    leftEndPoint = startPoint;
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
                    shapeGraphic.setStroke( loopStroke );
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
                shapeGraphic.setStroke( loopStroke );
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
                shapeGraphic.setStroke( loopStroke );
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
                shapeGraphic.setStroke( loopStroke );
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
                shapeGraphic.setStroke( loopStroke );
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
                double speedScale = ( radius / ELECTRON_SPACING ) / ELECTRONS_IN_RIGHT_END;
                ElectronPathDescriptor d = new ElectronPathDescriptor( curve, _foreground, ElectronPathDescriptor.FOREGROUND, speedScale );
                _electronPath.add( d );

                Paint paint = _middlegroundColor;
                
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
                
                rightEndPoint = endPoint;
            }
        }

        // Connect the ends
        if ( _endsConnected ) {
            
            Line2D line = new Line2D.Double( leftEndPoint.getX(), leftEndPoint.getY(), rightEndPoint.getX(), rightEndPoint.getY() );
            PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
            
            Paint paint = _middlegroundColor;
            
            shapeGraphic.setShape( line );
            shapeGraphic.setStroke( loopStroke );
            shapeGraphic.setBorderPaint( paint );
            _foreground.addGraphic( shapeGraphic );
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
                    electron.setSpeedScale( _electronSpeedScale );
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
        double currentAmplitude = _coilModel.getCurrentAmplitude();
        // Current below the threshold is effectively zero.
        if ( Math.abs( currentAmplitude ) < FaradayConstants.CURRENT_AMPLITUDE_THRESHOLD ) {
            currentAmplitude = 0;
        }
        return currentAmplitude;
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
            
            if ( _collisionBounds == null ) {
                _collisionBounds = new Rectangle[2];
                _collisionBounds[0] = new Rectangle();
                _collisionBounds[1] = new Rectangle();
            }
            
            Rectangle b = getBounds();
            // Rectangle at top of coil.
            _collisionBounds[0].setBounds( b.x + 5, b.y, b.width - 25, 56 );
            // Rectangle at bottom of coil.
            _collisionBounds[1].setBounds( b.x + 35, b.y + b.height - 19, b.width - 58, 18 );
            
            return _collisionBounds;
        }
        else {
            return null;
        }
    }
}