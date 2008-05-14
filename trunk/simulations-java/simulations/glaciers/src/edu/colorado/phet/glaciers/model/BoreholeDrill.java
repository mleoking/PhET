/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrill( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _listeners = new ArrayList();
    }
    
    public void drill() {
        if ( _glacier.getIceThickness( getX() ) > 0 ) {
            notifyDrillAt( getPosition() );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    /*
     * Always snap to the ice surface.
     */
    protected void constrainDrop() {
        double surfaceElevation = _glacier.getSurfaceElevation( getX() );
        setPosition( getX(), surfaceElevation );
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
