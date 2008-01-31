/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * EquilibriumLine is the model of the equilibrium line.
 * It occurs where the glacial budget is zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquilibriumLine {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ALMOST_ZERO_GLACIAL_BUDGET = 1E-5; // anything <= this value is considered zero
    private static final double SEARCH_DX = 1000; // meters
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Valley _valley;
    private Climate _climate;
    private ClimateListener _climateListener;
    private Point2D _position;
    private ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EquilibriumLine( Valley valley, Climate climate ) {
        super();
        
        _valley = valley;
        
        _climate = climate;
        _climateListener = new ClimateListener() {
            public void temperatureChanged() {
                updatePosition();
            }

            public void snowfallChanged() {
                updatePosition();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _listeners = new ArrayList();
        
        _position = new Point2D.Double();
        updatePosition();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    private void setPosition( double x, double elevation ) {
        if ( x != _position.getX() || elevation != _position.getY() ) {
            _position.setLocation( x, elevation );
            notifyPositionChanged();
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    public Point2D getPositionReference() {
        return _position;
    }
    
    public double getX() {
        return _position.getX();
    }
    
    public double getElevation() {
        return _position.getY();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        double x = _valley.getMinX();
        double elevation = _valley.getElevation( x );
        double glacialBudget = _climate.getGlacialBudget( elevation );
        double newGlacialBudget = 0;
        double dx = SEARCH_DX;
        while ( Math.abs( glacialBudget ) > ALMOST_ZERO_GLACIAL_BUDGET ) {
            
            x += dx;
            elevation = _valley.getElevation( x );
            newGlacialBudget = _climate.getGlacialBudget( elevation );
            
            if ( ( dx > 0 && newGlacialBudget < 0 && newGlacialBudget < glacialBudget ) || 
                 ( dx < 0 && newGlacialBudget > 0 && newGlacialBudget > glacialBudget ) ) {
                dx = -( dx / 2. );
                if ( dx < 0 && Math.abs( dx ) > x ) {
                    dx = -( x / 2 );
                }
            }
            glacialBudget = newGlacialBudget;
            
            //XXX this happens regularly with our snowfall and temperature ranges!
            if ( x < _valley.getMinX() || x > 1E20 /* insanely far down the valley */ ) {
                System.err.println( "model is out of control, x=" + x );
                break;
            }
        }
        setPosition( x, elevation );
        System.out.println( "EquilibriumLine.updatePosition position=" + getPositionReference() + " glacialBudget=" + glacialBudget );//XXX
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface EquilibriumLineListener {
        public void positionChanged();
    }
    
    public void addEquilibriumLineListener( EquilibriumLineListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeEquilibriumLineListener( EquilibriumLineListener listener ) {
        _listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyPositionChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (EquilibriumLineListener) _listeners.get( i ) ).positionChanged();
        }
    }
    
}
