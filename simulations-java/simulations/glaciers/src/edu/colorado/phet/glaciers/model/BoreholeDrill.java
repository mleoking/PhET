/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * BoreholeDrill is the model of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrill extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrill( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                // keep drill on glacier surface as the glacier evolves
                if ( !isDragging() ) {
                    constrainDrop();
                }
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        super.cleanup();
    }
    
    public void drill() {
        if ( _glacier.getIceThickness( getX() ) > 0 ) {
            notifyDrillAt( getPosition() );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    protected void constrainDrop() {
        // constrain x to >= headwall
        double x = Math.max( getX(), _glacier.getHeadwallX() );
        // snap y to glacier surface
        double surfaceElevation = _glacier.getSurfaceElevation( x );
        setPosition( x, surfaceElevation );
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface BoreholeDrillListener {
        public void drillAt( Point2D position );
    }
    
    public void addBoreholeDrillListener( BoreholeDrillListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBoreholeDrillListener( BoreholeDrillListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyDrillAt( Point2D position ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BoreholeDrillListener) i.next() ).drillAt( position );
        }
    }
}
