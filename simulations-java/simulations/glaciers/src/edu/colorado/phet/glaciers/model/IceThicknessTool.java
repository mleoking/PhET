/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * IceThicknessTool is the model of an ice thickness tool.
 * It measures the thickness of ice at a position along the glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceThicknessTool extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private double _thickness;
    private ArrayList _listeners; // list of IceThicknessToolListener

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceThicknessTool( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getThickness() {
        return _thickness;
    }
    
    private void setThickness( double thickness ) {
        if ( thickness != _thickness ) {
            _thickness = thickness;
            notifyThicknessChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    protected void handlePositionChanged() {
        updateThickness();
    }
    
    //XXX should this be replaced with a GlacierListener?
    protected void handleTimeChanged() {
        updateThickness();
    }
    
    private void updateThickness() {
        final double x = getX();
        final double t = getCurrentTime();
        final double thickness = _glacier.getIceThickness( x, t );
        setThickness( thickness );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface IceThicknessToolListener {
        public void thicknessChanged();
    }
    
    public void addIceThicknessToolListener( IceThicknessToolListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeIceThicknessToolListener( IceThicknessToolListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyThicknessChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (IceThicknessToolListener) _listeners.get( i ) ).thicknessChanged();
        }
    }
}
