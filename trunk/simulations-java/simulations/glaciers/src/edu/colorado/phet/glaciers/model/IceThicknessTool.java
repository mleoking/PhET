/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class IceThicknessTool extends AbstractTool {
    
    private Glacier _glacier;
    private double _thickness;
    private ArrayList _listeners; // list of IceThicknessToolListener

    public IceThicknessTool( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
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
        final double x = getX();
        final double t = getCurrentTime();
        setThickness( _glacier.getIceThickness( x, t ) );
    }
    
    public interface IceThicknessToolListener {
        public void thicknessChanged();
    }
    
    public void addIceThicknessToolListener( IceThicknessToolListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeIceThicknessToolListener( IceThicknessToolListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyThicknessChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (IceThicknessToolListener) _listeners.get( i ) ).thicknessChanged();
        }
    }
}
