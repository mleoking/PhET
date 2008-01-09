/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.glaciers.view.Viewport.ViewportListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ViewportNode is a view of a viewport.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewportNode extends PPath {
    
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private Viewport _viewport;
    private ModelViewTransform _mvt;
    private Rectangle2D _rView; // reusable rectangle
    
    public ViewportNode( Viewport viewport, float strokeWidth, ModelViewTransform mvt ) {
        super();
        
        _viewport = viewport;
        _viewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                updateRectangle();
            }
        });
        
        _mvt = mvt;
        
        setPaint( null );
        final float dashSpacing = 4 * strokeWidth;
        setStroke( new BasicStroke( strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { dashSpacing, dashSpacing }, 0 ) ); // dashed line
        setStrokePaint( STROKE_COLOR );
        
        _rView = new Rectangle2D.Double();
        updateRectangle();
    }
    
    public void cleanup() {}
    
    private void updateRectangle() {
        Rectangle2D rModel = _viewport.getBoundsReference();
        _mvt.modelToView( rModel, _rView );
        setPathTo( _rView );
    }

}