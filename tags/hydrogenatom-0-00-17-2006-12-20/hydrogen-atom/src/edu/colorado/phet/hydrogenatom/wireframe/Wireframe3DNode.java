/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.wireframe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Wireframe3DNode is a Piccolo node that draws a Wireframe3D model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Wireframe3DNode extends PNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // wireframe model that this node draws
    private Wireframe3D _wireframe;
    // listens for changes to the wireframe model
    private PropertyChangeListener _listener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param wireframe
     */
    public Wireframe3DNode( Wireframe3D wireframe ) {
        super();
        
        // update the bounds when the wireframe model changes
        _listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                String p = event.getPropertyName();
                if ( p == Wireframe3D.PROPERTY_BOUNDS || p == Wireframe3D.PROPERTY_STROKE_WIDTH ) {
                    updateBounds();
                }
            }
        };

        setWireframe( wireframe );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wireframe model that this node draws.
     * 
     * @param wireframe
     */
    public void setWireframe( Wireframe3D wireframe ) {
        // Don't check for ==, Wireframe3D is mutable!
        if ( _wireframe != null ) {
            _wireframe.removePropertyChangeListener( _listener );
        }
        _wireframe = wireframe;
        _wireframe.addPropertyChangeListener( _listener );
        updateBounds();
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /**
     * Paints the wireframe after painting this node and its children.
     * 
     * @param paintContext
     */
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        _wireframe.paint( paintContext.getGraphics() );
    }
    
    //----------------------------------------------------------------------------
    // Bounds management
    //----------------------------------------------------------------------------
    
    /*
     * Updates the bounds to match the wireframe model.
     * Include the width of the stroke used to draw the wireframe.
     */
    private void updateBounds() {
        float strokeWidth = _wireframe.getStrokeWidth();
        double x = _wireframe.getTXMin() - ( strokeWidth / 2 );
        double y = _wireframe.getTYMin() - ( strokeWidth / 2 );
        double w = _wireframe.getTXMax() - _wireframe.getTXMin() + strokeWidth;
        double h = _wireframe.getTYMax() - _wireframe.getTYMin() + strokeWidth;
        setBounds( x, y, w, h );
    }
}
