/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;


/**
 * CoilCurveDescriptor
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CurveDescriptor {

    // Value for layer.
    public static final int FOREGROUND = 0;
    public static final int BACKGROUND = 1;
    
    private QuadBezierSpline _curve;
    private CompositePhetGraphic _parent;
    private int _layer;
    
    public CurveDescriptor( QuadBezierSpline curve, CompositePhetGraphic parent, int layer ) {
        assert ( curve != null );
        assert ( parent != null );
        assert ( layer == FOREGROUND || layer == BACKGROUND );
        _curve = curve;
        _parent = parent;
        _layer = layer;
    }
    
    public QuadBezierSpline getCurve() {
        return _curve;
    }
    
    public CompositePhetGraphic getParent() {
        return _parent;
    }
    
    public int getLayer() {
        return _layer;
    }
}
