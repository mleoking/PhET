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
import java.awt.geom.QuadCurve2D;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.model.AbstractCoil;


/**
 * CoilGraphic is the graphical representation of a coil of wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CoilGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Loop parameters
    private static final Color FORE_COLOR = new Color( 153, 102, 51 ); // light brown
    private static final Color MIDDLE_COLOR = new Color( 92, 52, 12 ); // dark brown
    private static final Color BACK_COLOR = new Color( 40, 23, 3 ); // really dark brown
    private static final int WIRE_WIDTH = 16;
    private static final double LOOP_SPACING_FACTOR = 0.3; // ratio of loop spacing to loop radius
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Component _component;
    private AbstractCoil _coilModel;
    private CompositePhetGraphic _foreground;
    private CompositePhetGraphic _background;
    private boolean _currentFlowAnimationEnabled;
    private Stroke _loopStroke;
    private Color _foreColor, _middleColor, _backColor;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component parent Component
     * @param coilModel the coil that this graphic is watching
     */
    public CoilGraphic( Component component, AbstractCoil coilModel ) {
        assert( component != null );
        assert( coilModel != null );
        
        _component = component;
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ); 
        _foreground = new CompositePhetGraphic( _component );
        _background = new CompositePhetGraphic( _component );
        _foreground.setRenderingHints( hints );
        _background.setRenderingHints( hints );
        
        _currentFlowAnimationEnabled = false;
        _loopStroke = new BasicStroke( WIRE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
        _foreColor = FORE_COLOR;
        _middleColor = MIDDLE_COLOR;
        _backColor = BACK_COLOR;
        
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
    public void setCurrentFlowAnimationEnabled( boolean enabled ) {
        if ( enabled != _currentFlowAnimationEnabled ) {
            _currentFlowAnimationEnabled = enabled;
            update();
        }
    }
    
    /**
     * Determines whether animation of current flow is enabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isCurrentFlowAnimationEnabled() {
        return _currentFlowAnimationEnabled;
    }

    /**
     * Sets the colors used for the loops.
     * Three colors are combined in various gradients to provide a 3D look.
     * <p>
     * Colors are changed only if their corresponding parameter is not null.
     * For example, to change only the foreground color, use setColor(Color.RED,null,null).
     * 
     * @param foreColor the foreground color
     * @param middleColor the middleground color
     * @param backColor the background color
     */
    public void setColors( Color foreColor, Color middleColor, Color backColor ) {
        if ( foreColor != null ) {
            _foreColor = foreColor;
        }
        if ( middleColor != null ) {
            _middleColor = middleColor;
        }
        if ( backColor != null ) {
            _backColor = backColor;
        }
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        // Removing any existing graphics components.
        _foreground.clear();
        _background.clear();
        
        int numberOfLoops = _coilModel.getNumberOfLoops();
        double radius = _coilModel.getRadius();
        
        // Loop spacing
        int loopSpacing = (int)(radius * LOOP_SPACING_FACTOR );
        
        // Center of all loops should remain fixed.
        int firstLoopCenter = -( loopSpacing * (numberOfLoops - 1) / 2 );
        
        // Back of loops
        for ( int i = 0; i < numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * loopSpacing );
            
            Paint paint = new GradientPaint( 0, (int)(radius * .40), _backColor, 0, (int)(radius * .90), _middleColor );
            
            // Back bottom
            {
                Point end1 = new Point( (int)(radius * .25) + offset, 0 );
                Point end2 = new Point( (int)(radius * .15) + offset, (int)(radius * .98) );
                Point control = new Point( (int)(radius * .30) + offset, (int)(radius * .80) );
                QuadCurve2D.Double curve = new QuadCurve2D.Double();
                curve.setCurve( end1, control, end2 );
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _background.addGraphic( shapeGraphic );
            }
            
            // Back top
            {
                Point end1 = new Point( (int)(radius * .25) + offset, 0 );
                Point end2 = new Point( -loopSpacing + (int)(radius * .15) + offset, (int)-radius );
                Point control = new Point( (int)(radius * .15) + offset, (int)(-radius * .70));
                QuadCurve2D.Double curve = new QuadCurve2D.Double();
                curve.setCurve( end1, control, end2 );
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _background.addGraphic( shapeGraphic );
            }

            // Left connection wire
            if ( i == 0 ) {
                Point end1 = new Point( -loopSpacing + (int)(radius * .15) + offset, (int)-radius );
                Point end2 = new Point( end1.x - 15, end1.y - 40 );
                Point control = new Point( end1.x - 20, end1.y - 20 );
                paint = new GradientPaint( end2.x, 0, _middleColor, end1.x, 0, _backColor );
                QuadCurve2D.Double curve = new QuadCurve2D.Double();
                curve.setCurve( end1, control, end2 );
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _background.addGraphic( shapeGraphic );
            }
        }
        
        // Front of loops
        for ( int i = 0; i < numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * loopSpacing );;
            
            Paint paint = new GradientPaint( (int)(-radius * .25) + offset, 0, _foreColor, (int)(-radius * .15) + offset, 0, _middleColor );
            
            // Front bottom
            {
                Point end1 = new Point( (int) ( -radius * .25 ) + offset, 0 );
                Point end2 = new Point( (int) ( radius * .13 ) + offset, (int) radius );
                Point control = new Point( (int) ( -radius * .25 ) + offset, (int) ( radius * 1.30 ) );
                QuadCurve2D.Double curve = new QuadCurve2D.Double();
                curve.setCurve( end1, control, end2 );
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
            }
            
            // Front top
            if ( i < numberOfLoops - 1 )
            {
                Point end1 = new Point( (int) ( -radius * .25 ) + offset, 0 );
                Point end2 = new Point( (int) ( radius * .15 ) + offset, (int) -radius );
                Point control = new Point( (int) ( -radius * .20 ) + offset, (int) ( -radius * 1.30 ) );
                QuadCurve2D.Double curve = new QuadCurve2D.Double();
                curve.setCurve( end1, control, end2 );
                PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                shapeGraphic.setShape( curve );
                shapeGraphic.setStroke( _loopStroke );
                shapeGraphic.setBorderPaint( paint );
                _foreground.addGraphic( shapeGraphic );
            }
            else {
                // Front top of right-most loop (shorter than the others for joining with connection wire)
                {
                    Point end1 = new Point( (int) ( -radius * .25 ) + offset, 0 );
                    Point end2 = new Point( -loopSpacing + (int) ( radius * .25 ) + offset, (int)-radius );
                    Point control = new Point( (int) ( -radius * .25 ) + offset, (int) ( -radius * .8 ) );
                    QuadCurve2D.Double curve = new QuadCurve2D.Double();
                    curve.setCurve( end1, control, end2 );
                    PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                    shapeGraphic.setShape( curve );
                    shapeGraphic.setStroke( _loopStroke );
                    shapeGraphic.setBorderPaint( paint );
                    _foreground.addGraphic( shapeGraphic );
                }

                // Right connection wire
                {
                    paint = _middleColor;
                    Point end1 = new Point( -loopSpacing + (int) ( radius * .25 ) + offset, (int)-radius );
                    Point end2 = new Point( end1.x + 15, end1.y - 40 );
                    Point control = new Point( end1.x + 20, end1.y - 20 );
                    QuadCurve2D.Double curve = new QuadCurve2D.Double();
                    curve.setCurve( end1, control, end2 );
                    PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( _component );
                    shapeGraphic.setShape( curve );
                    shapeGraphic.setStroke( _loopStroke );
                    shapeGraphic.setBorderPaint( paint );
                    _foreground.addGraphic( shapeGraphic );
                }
            }
        }
        
        _foreground.repaint();
        _background.repaint();
    }

}
