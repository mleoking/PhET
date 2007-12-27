/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ViewportNode is a view of a viewport, drag to change viewport's position.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewportNode extends PPath {
    
    private static final Color STROKE_COLOR = Color.RED;
    
    private Viewport _viewport;
    
    public ViewportNode( Viewport viewport, float strokeWidth ) {
        super();
        
        _viewport = viewport;
        
        setPaint( null );
        setStroke( new BasicStroke( strokeWidth ) );
        setStrokePaint( STROKE_COLOR );
        
        _viewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                updateBounds();
            }
        });
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset, _yOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D viewportBounds = _viewport.getBounds();
                _xOffset = event.getPosition().getX() - viewportBounds.getX();
                _yOffset = event.getPosition().getY() - viewportBounds.getY();
                super.startDrag( event );
            }

            protected void drag( PInputEvent event ) {
                Rectangle2D viewportBounds = _viewport.getBounds();
                double x = event.getPosition().getX() - _xOffset;
                double y = event.getPosition().getY() - _yOffset;
                double w = viewportBounds.getWidth();
                double h = viewportBounds.getHeight();
                _viewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
            }
        } );
        
        updateBounds();
    }
    
    private void updateBounds() {
        Rectangle2D viewportBounds = _viewport.getBounds();
        setPathTo( new Rectangle2D.Double( 0, 0, viewportBounds.getWidth(), viewportBounds.getHeight() ) );
        setOffset( viewportBounds.getX(), viewportBounds.getY() );
    }

}