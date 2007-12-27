/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractToolNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractToolNode extends PComposite {
    
    private AbstractTool _tool;
    private MovableListener _listener;
    
    public AbstractToolNode( AbstractTool tool ) {
        super();
        
        _tool = tool;
        
        _listener = new MovableAdapter() {
            public void positionChanged() {
                updatePosition();
            }
        };
        
        _tool.addListener( _listener );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            
            private double _xOffset, _yOffset;
            
            protected void startDrag( PInputEvent event ) {
                _xOffset = event.getPosition().getX() - _tool.getPosition().getX();
                _yOffset = event.getPosition().getY() - _tool.getPosition().getY();
                super.startDrag( event );
            }

            protected void drag( PInputEvent event ) {
                double x = event.getPosition().getX() - _xOffset;
                double y = event.getPosition().getY() - _yOffset;
                _tool.setPosition( new Point2D.Double( x, y ) );
            }
        } );

        updatePosition();
    }
    
    public void cleanup() {
        _tool.removeListener( _listener );
    }
    
    protected void updatePosition() {
        setOffset( _tool.getPositionReference() );
    }
}
