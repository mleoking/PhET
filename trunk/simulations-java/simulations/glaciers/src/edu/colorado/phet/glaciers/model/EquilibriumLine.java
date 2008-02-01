/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
    
    /**
     * Constructor.
     * 
     * @param valley
     * @param climate
     */
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
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /*
     * Sets the position of the equilibrium line to some point on the valley floor.
     * 
     * @param x
     * @param elevation
     */
    private void setPosition( double x, double elevation ) {
        assert( elevation == _valley.getElevation( x ) );
        if ( x != _position.getX() || elevation != _position.getY() ) {
            _position.setLocation( x, elevation );
            notifyPositionChanged();
        }
    }
    
    /**
     * Gets the position of the equilibrium line.
     * This is a point on the valley floor.
     * This method allocates a Point2D object.
     * 
     * @return Point2D copy
     */
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    /**
     * Same a getPosition, but returns a reference to the object's Point2D.
     * 
     * @return Point2D reference
     */
    public Point2D getPositionReference() {
        return _position;
    }
    
    /**
     * Gets the x coordinate of the equilibrium line.
     * 
     * @return meters
     */
    public double getX() {
        return _position.getX();
    }
    
    /**
     * Gets the elevation above sea level of the equilibrium line.
     * 
     * @return meters
     */
    public double getElevation() {
        return _position.getY();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the position by searching for the x coordinate where glacial budget = 0.
     * This uses a "divide and conquer" algorithm, gradually decreasing the sign and 
     * magnitude of dx until we find a glacial budget that is close enough to 0.
     */
    private void updatePosition() {
        double x = 0;
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
            }
            glacialBudget = newGlacialBudget;
            
            if ( x < -1E20 || x > 1E20 ) {
                System.err.println( "EquilibriumLine.updatePosition, x is way outside our range of interest, x=: " + x );
                break;
            }
        }
        setPosition( x, elevation );
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
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (EquilibriumLineListener) i.next() ).positionChanged();
        }
    }
    
}
