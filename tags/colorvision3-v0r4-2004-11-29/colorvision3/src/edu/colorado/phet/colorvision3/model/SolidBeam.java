/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision3.model;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.VisibleColorChangeEvent;
import edu.colorado.phet.colorvision3.event.VisibleColorChangeListener;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * SolidBeam is the model of a solid beam of light.
 * The beam may be filtered or unfiltered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolidBeam extends SimpleObservable implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The associated spotlight model.
    private Spotlight _spotlightModel;
    // Optional filter model.
    private Filter _filterModel;
    // Distance from the spotlight that light is emitted, in pixels.
    private int _distance;
    // Event listeners 
    private EventListenerList _listenerList;
    // Is this beam enabled?
    private boolean _enabled;
    // The perceived color.
    private VisibleColor _perceivedColor;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor for a filtered beam.
     * 
     * @param spotlightModel the spotlight model
     * @param filterModel the fitler model
     */
    public SolidBeam( Spotlight spotlightModel, Filter filterModel ) {
        _spotlightModel = spotlightModel;
        _filterModel = filterModel;
        _distance = 0;
        _listenerList = new EventListenerList();
        _enabled = true;
        _perceivedColor = VisibleColor.INVISIBLE;
    }

    /**
     * Constructor for an unfiltered beam.
     * 
     * @param spotlightModel the spotlight model
     */
    public SolidBeam( Spotlight spotlightModel ) {
        this( spotlightModel, null );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the distance that the light is emitted from the spotlight location.
     * 
     * @param distance distance in pixels
     */
    public void setDistance( int distance ) {
        _distance = distance;
        update();
    }

    /** 
     * Gets the distance that the light is emitted from the spotlight location.
     * 
     * @return the distance, in pixels
     */
    public int getDistance() {
        return _distance;
    }

    /**
     * Enables or disables the beam.
     * A disabled beam will not send ColorChangeEvents.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
        notifyObservers();
    }

    /**
     * Determines if the beam is enabled. 
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Gets the perceived color of the beam.
     * 
     * @return the color
     */
    public VisibleColor getPerceivedColor() {
        return _perceivedColor;
    }

    /**
     * Gets the beam's direction.
     * 
     * @return the direction, in degrees
     */
    public double getDirection() {
        return _spotlightModel.getDirection();
    }

    /**
     * Gets the beam's cut off angle.
     * 
     * @return the cut off angle, in degrees.
     */
    public double getCutOffAngle() {
        return _spotlightModel.getCutOffAngle();
    }

    /**
     * Gets the X coordinate of the beam's origin.
     * 
     * @return X coordinate
     */
    public double getX() {
        return _spotlightModel.getX();
    }

    /**
     * Gets the Y coordinate of the beam's origin.
     * 
     * @return Y coordinate
     */
    public double getY() {
        return _spotlightModel.getY();
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronizes this model with its associated models,
     * propogates the notification to observers of this model.
     */
    public void update() {
        // Color update
        {
            VisibleColor bulbColor = _spotlightModel.getColor();

            // Adjust color using filter.
            VisibleColor beamColor = bulbColor;
            if( _filterModel != null ) {
                beamColor = _filterModel.colorPassed( bulbColor );
            }

            // Notify listeners
            if( _enabled && !beamColor.equals( _perceivedColor ) ) {
                VisibleColorChangeEvent event = new VisibleColorChangeEvent( this, beamColor );
                fireColorChangeEvent( event );
            }

            _perceivedColor = beamColor;
        }

        notifyObservers();
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a VisibleColorChangeListener.
     * 
     * @param listener the listener to add
     */
    public void addColorChangeListener( VisibleColorChangeListener listener ) {
        _listenerList.add( VisibleColorChangeListener.class, listener );
    }

    /**
     * Removes a VisibleColorChangeListener.
     * 
     * @param listener the listener to remove
     */
    public void removeColorChangeListener( VisibleColorChangeListener listener ) {
        _listenerList.remove( VisibleColorChangeListener.class, listener );
    }

    /**
     * Fires a VisibleColorChangeEvent.
     * This occurs each time the color or intensity of the photon beam changes.
     * 
     * @param event the event
     */
    private void fireColorChangeEvent( VisibleColorChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == VisibleColorChangeListener.class ) {
                ( (VisibleColorChangeListener) listeners[i + 1] ).colorChanged( event );
            }
        }
    }

}