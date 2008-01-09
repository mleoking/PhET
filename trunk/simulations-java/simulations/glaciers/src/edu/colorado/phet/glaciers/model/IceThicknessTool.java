/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class IceThicknessTool extends AbstractTool {
    
    private double _thickness;
    private ArrayList _listeners;

    public IceThicknessTool( Point2D position ) {
        super( position );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    public double getThickness() {
        return _thickness;
    }
    
    private void setThickness( double thickness ) {
        if ( thickness != _thickness ) {
            _thickness = thickness;
            notifyThicknessChanged();
        }
    }
    
    protected void handlePositionChanged() {
        updateThickness();
    }
    
    protected void handleClockTimeChanged() {
        updateThickness();
    }
    
    private void updateThickness() {
        double x = getX();
        double t = getCurrentTime();
        //XXX double thickness = _glacier.getIceThickness( x, t );
        //XXX setThickness( thickness );
    }
    
    public interface IceThicknessToolListener {
        public void thicknessChanged();
    }
    
    public void addListener( IceThicknessToolListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( IceThicknessToolListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyThicknessChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (IceThicknessToolListener) _listeners.get( i ) ).thicknessChanged();
        }
    }
}
