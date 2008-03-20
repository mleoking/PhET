/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.faraday.util.QuadBezierSpline;


/**
 * ElectronPathDescriptor contains a description of one segment of an Electron's path.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronPathDescriptor {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /** The default speed scale. */
    public static final double DEFAULT_SPEED_SCALE = 1.0;
    
    /** Curve is part of the foreground layer. */
    public static final int FOREGROUND = 0;
    
    /** Curve is part of the background layer. */
    public static final int BACKGROUND = 1;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    // The curve
    private QuadBezierSpline _curve;
    
    // The parent graphic
    private CompositePhetGraphic _parent;
    
    // The layer that the curve is in (FOREGROUND or BACKGROUND).
    private int _layer;
    
    // How to scale the speed for this curve (any positive value).
    private double _pathScale;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates an ElectronPathDescriptor.
     * 
     * @param curve the curve
     * @param parent the parent graphic
     * @param layer FOREGROUND or BACKGROUND
     * @param pathScale see getPathScale
     */
    public ElectronPathDescriptor( QuadBezierSpline curve, CompositePhetGraphic parent, int layer, double pathScale ) {
        assert ( curve != null );
        assert ( parent != null );
        assert ( layer == FOREGROUND || layer == BACKGROUND );
        assert ( pathScale > 0 );
        _curve = curve;
        _parent = parent;
        _layer = layer;
        _pathScale = pathScale;
    }
    
    
    /**
     * Creates an ElectronPathDescriptor, using the defaut speed scale.
     * 
     * @param curve
     * @param parent
     * @param layer
     */
    public ElectronPathDescriptor( QuadBezierSpline curve, CompositePhetGraphic parent, int layer ) {
        this( curve, parent, layer, DEFAULT_SPEED_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the curve.
     * 
     * @return the curve
     */
    public QuadBezierSpline getCurve() {
        return _curve;
    }
    
    /**
     * Gets the parent graphic.
     * 
     * @return the parent
     */
    public CompositePhetGraphic getParent() {
        return _parent;
    }
    
    /**
     * Gets the layer.
     * 
     * @return FOREGROUND or BACKGROUND 
     */
    public int getLayer() {
        return _layer;
    }
    
    /**
     * Gets the scaling for this part of the path.
     * This value is used to adjust the speed of electrons along the curve.
     * It's useful in cases where a set of ElectronPathDescriptors contains curves 
     * of different lengths, and the speed needs to be scaled in order to
     * make electron "speed" appear the same on all curves.
     * 
     * @return the speed scale
     */
    public double getPathScale() {
        return _pathScale;
    }
}
