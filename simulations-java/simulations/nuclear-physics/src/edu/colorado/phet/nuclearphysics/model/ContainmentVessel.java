/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class is meant to represent a vessel in which nuclear reactions can
 * be contained.  It is intended to be a part of the model, and have a
 * separate view component display it to the user.
 *
 * @author John Blanco
 */
public class ContainmentVessel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final double CONTAINMENT_RANGE = 10;  // In femtometers.
    private static final double APERTURE_HEIGHT = 18;    // In femtometers.
    private static final double APERTURE_WIDTH = CONTAINMENT_RANGE * 2.0;  // In femtometers.
    private static final double MINIMUM_RADIUS = 15;
    
    // The following value controls how many impacts must occur to cause the
    // containment vessel to explode.  The goal, as prescribed by the educators,
    // is that explosion won't occur unless the containment vessel is enlarged
    // somewhat.
    private static final double CONTAINMENT_EXPLOSION_THRESHOLD = 1200;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Radius of the containment vessel, in femtometers.
    private double _originalRadius;
    private double _radius;
    
    // Boolean that controls whether it is on or off.
    private boolean _enabled;
    
    // A rectangle that represents the location of the aperture.
    private Rectangle2D _apertureRect;
    
    // List of listeners.
    private ArrayList _listeners;
    
    // Number of impacts that have occurred, used to decide whether to explode.
    private int _cumulativeImpactAmount;
    
    // State variable that tracks if explosion has occurred.
    private boolean _exploded;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ContainmentVessel(double radius) {
        
        _radius = radius;
        _originalRadius = _radius;
        _enabled = false;
        _exploded = false;
        
        _listeners = new ArrayList();
        _apertureRect = new Rectangle2D.Double();

        updateApertureRect();
    }
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    public boolean getIsEnabled(){
        return _enabled;
    }
    
    public boolean getIsExploded(){
        return _exploded;
    }
    
    public void setIsEnabled(boolean isEnabled){
    	if (isEnabled != _enabled){
            _enabled = isEnabled;
            notifiyEnableStateChanged();
            if (_enabled == true){
            	// The vessel is being enabled.  Reset local data.
            	resetImpactAccumulation();
                setRadius( _originalRadius );
                _exploded = false;
            }
    	}
    }
    
    public double getRadius(){
        return _radius;
    }
    
    public void setRadius(double radius){
        if (radius > MINIMUM_RADIUS){
            _radius = radius;
            notifiyRadiusChanged();
            updateApertureRect();
        }
    }
    
    public Rectangle2D getAperatureRectReference(){
        return _apertureRect;
    }
    
    /**
     * Set the size back to the original size at which it was created.
     */
    public void reset(){
        setRadius( _originalRadius );
        setIsEnabled( false );
        _exploded = false;
        resetImpactAccumulation();
        notifiyResetOccurred();
    }
    
    /**
     * Reset just the accumulation of impacts.
     */
    public void resetImpactAccumulation(){
    	_cumulativeImpactAmount = 0;
    }
    
    /**
     * This method tests whether the provided position is at the boundary of
     * the containment vessel and not in the aperture, and thus essentially
     * 'contained'.
     * 
     * @param position
     * @return
     */
    public boolean isPositionContained(Point2D position){
     
        return isPositionContained(position.getX(), position.getY());
    }
    
    /**
     * This method tests whether the provided position is at the boundary of
     * the containment vessel and not in the aperture, and thus essentially
     * 'contained'.
     * 
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isPositionContained(double xPos, double yPos){
        
        final Point2D position = new Point2D.Double();
        boolean contained = false;
        
        position.setLocation( xPos, yPos );
        
        if (_enabled && !_exploded){
            if ((position.distance( 0, 0 ) >= _radius) &&
                (position.distance( 0, 0 ) < _radius + CONTAINMENT_RANGE) &&
                (!_apertureRect.contains( position ))){
                
                contained = true;
            }
        }
        
        return contained;
    }
    
    /**
     * Records the impact of a nucleus or nucleon with the containment vessel.
     * If enough impacts occur, the containment vessel explodes.
     */
    public void recordImpact(double impact){
        
        _cumulativeImpactAmount += impact;
        
        if (!_exploded && (_cumulativeImpactAmount > CONTAINMENT_EXPLOSION_THRESHOLD)){
            explode();
        }
    }
    
    /**
     * Locate the nearest point at the containment radius to the provided
     * point.
     * 
     * @param point - Point to locate a contained point nearby.
     */
    public Point2D getNearestContainmentPoint(Point2D point){
        double angle = Math.atan2( point.getY(), point.getX() );
        return new Point2D.Double( Math.cos( angle ) * _radius, Math.sin( angle ) * _radius );
    }
    
    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void explode(){
        _exploded = true;
        notifiyExplosionOccurred();
    }
    
    /**
     * Update the location of the aperture based on the radius of the vessel.
     * The location of the aperture is assumed to be on the left side of the
     * containment vessel.
     */
    private void updateApertureRect(){
        _apertureRect.setFrame( -_radius - (APERTURE_WIDTH / 2), -APERTURE_HEIGHT / 2, APERTURE_WIDTH, 
                APERTURE_HEIGHT ); 
    }
    
    private void notifiyRadiusChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((ContainmentVessel.Listener)_listeners.get( i )).radiusChanged(_radius);
        }
    }

    private void notifiyEnableStateChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((ContainmentVessel.Listener)_listeners.get( i )).enableStateChanged(_enabled);
        }
    }

    private void notifiyExplosionOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((ContainmentVessel.Listener)_listeners.get( i )).explosionOccurred();
        }
    }

    private void notifiyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((ContainmentVessel.Listener)_listeners.get( i )).resetOccurred();
        }
    }

    //------------------------------------------------------------------------
    // Listener Support
    //------------------------------------------------------------------------
    
    public static interface Listener {
        void radiusChanged(double radius);
        void enableStateChanged(boolean isEnabled);
        void explosionOccurred();
        void resetOccurred();
    }
    
    public static class Adapter implements Listener {
        public void radiusChanged(double radius){};
        public void enableStateChanged(boolean isEnabled){};
        public void explosionOccurred(){};
        public void resetOccurred(){};
    }
}
