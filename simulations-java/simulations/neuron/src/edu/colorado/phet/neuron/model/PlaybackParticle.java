/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Class that is used in the model to represent particles during playback.  It
 * is similar to a full blown particle but contains less data and implements
 * less capability, and this is faster and easier to create.  This is intended
 * for use as part of the implementation of the record-and-playback feature.
 *
 * @author John Blanco
 */
public class PlaybackParticle implements IViewableParticle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private final Point2D position = new Point2D.Double();
    private Color representationColor;
    private double opaqueness;
    private final double radius;
    private ParticleType particleType;
    
    protected ArrayList<IParticleListener> listeners = new ArrayList<IParticleListener>();
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

	/**
     * Construct a playback particle.
     * 
     * @param particle - Real particle from which this playback particle
     * should be constructed.
     */
    public PlaybackParticle(Particle particle) {
        position.setLocation( particle.getPosition() );
        opaqueness = particle.getOpaqueness();
        representationColor = particle.getRepresentationColor();
        radius = particle.getRadius();
        particleType = particle.getType();
    }
    
    /**
     * Default constructor.
     */
    public PlaybackParticle(){
        // Construct as potassium by default.  This choice is arbitrary.
        this(new PotassiumIon());
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public void restoreFromMemento(ParticlePlaybackMemento memento){
        setPosition( memento.getPositionRef() );
        // Note - setting the position will take care of the notification.
        
        boolean appearanceChanged = false;
        if (opaqueness != memento.getOpaqueness()){
            opaqueness = memento.getOpaqueness();
            appearanceChanged = true;
        }
        if (particleType != memento.getParticleType()){
            particleType = memento.getParticleType();
            appearanceChanged = true;
        }
        if (representationColor != memento.getRepresentationColor()){
            representationColor = memento.getRepresentationColor();
            appearanceChanged = true;
        }
        if (appearanceChanged){
            notifyAppearanceChanged();
        }
    }
    
    /**
     * This is called to remove this particle from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
     */
    public void removeFromModel(){
    	notifyRemoved();
    	listeners.clear();
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    public Point2D getPositionReference() {
        return position;
    }
    
    private void setPosition(Point2D newPos) {
        if (position.getX() != newPos.getX() || position.getY() != newPos.getY()){
            position.setLocation( newPos );
            notifyPositionChanged();
        }
    }
    
    public Color getRepresentationColor() {
        return representationColor;
    }

    public double getOpaqueness() {
        return opaqueness;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public ParticleType getType() {
        return particleType;
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------
    
    public void addListener(IParticleListener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(IParticleListener listener){
    	listeners.remove(listener);
    }

    private void notifyRemoved(){
        // Copy the list to avoid concurrent modification exceptions.
        ArrayList<IParticleListener> listenersCopy = new ArrayList<IParticleListener>(listeners); 
        // Notify all listeners that this particle was removed from the model.
        for (IParticleListener listener : listenersCopy)
        {
            listener.removedFromModel(); 
        }        
    }
    
    protected void notifyPositionChanged(){
        // Notify all listeners of the position change.
        for (IParticleListener listener : listeners)
        {
            listener.positionChanged(); 
        }        
    }
    
    protected void notifyAppearanceChanged(){
        // Notify all listeners of the opaqueness change.
        for (IParticleListener listener : listeners)
        {
            listener.appearanceChanged(); 
        }        
    }    
}
