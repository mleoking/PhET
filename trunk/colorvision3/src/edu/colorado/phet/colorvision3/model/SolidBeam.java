/* SolidBeam.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.model;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * SolidBeam is the model of a solid beam of light.
 * The beam may be filtered or unfiltered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class SolidBeam extends SimpleObservable implements SimpleObserver
{
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
   * @param fitlerModel the fitler model
   */
  public SolidBeam( Spotlight spotlightModel, Filter filterModel )
  {   
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
   * @param parent the parent Component
   * @param spotlightModel the spotlight model
   */
  public SolidBeam( Spotlight spotlightModel )
  {
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
  public void setDistance( int distance )
  {
    _distance = distance;
    update();
  }
  
  /** 
   * Gets the distance that the light is emitted from the spotlight location.
   * 
   * @return the distance, in pixels
   */
  public int getDistance()
  {
    return _distance;
  }
  
  /**
   * Enables or disables the beam.
   * A disabled beam will not send ColorChangeEvents.
   * 
   * @param enabled true to enable, false to disable
   */
  public void setEnabled( boolean enabled )
  {
    _enabled = enabled;
    notifyObservers();
  }
  
  /**
   * Determines if the beam is enabled. 
   * 
   * @return true if enabled, false if disabled
   */
  public boolean isEnabled()
  {
    return _enabled;
  }
  
  /**
   * Gets the perceived color of the beam.
   * 
   * @return the color
   */
  public VisibleColor getPerceivedColor()
  {
    return _perceivedColor;
  }
  
  /**
   * Gets the beam's direction.
   * 
   * @return the direction, in degrees
   */
  public double getDirection()
  {
    return _spotlightModel.getDirection();
  }
  
  /**
   * Gets the beam's cut off angle.
   * 
   * @return the cut off angle, in degrees.
   */
  public double getCutOffAngle()
  {
    return _spotlightModel.getCutOffAngle();
  }
  
  /**
   * Gets the X coordinate of the beam's origin.
   * 
   * @return X coordinate
   */
  public double getX()
  {
    return _spotlightModel.getX();
  }
  
  /**
   * Gets the Y coordinate of the beam's origin.
   * 
   * @return Y coordinate
   */
  public double getY()
  {
    return _spotlightModel.getY();
  }
  
	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Synchronizes this model with its associated models,
   * propogates the notification to observers of this model.
   */
  public void update()
  {
    // Color update
    {
      VisibleColor bulbColor = _spotlightModel.getColor();
      
      // Adjust color using filter.
      VisibleColor beamColor = bulbColor;
      if ( _filterModel != null )
      {
        beamColor = _filterModel.colorPassed( bulbColor );
      }
      
      // Notify listeners
      if ( _enabled && ! beamColor.equals(_perceivedColor) )
      {
        ColorChangeEvent event = new ColorChangeEvent( this, beamColor, 100.0 );
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
   * Adds a ColorChangeListener.
   * 
   * @param listener the listener to add
   */
  public void addColorChangeListener( ColorChangeListener listener )
  {
    _listenerList.add( ColorChangeListener.class, listener );
  }
  
  /**
   * Removes a ColorChangeListener.
   * 
   * @param listener the listener to remove
   */
  public void removeColorChangeListener( ColorChangeListener listener )
  {
    _listenerList.remove( ColorChangeListener.class, listener );
  }
  
  /**
   * Fires a ColorChangeEvent.
   * This occurs each time the color or intensity of the photon beam changes.
   * 
   * @param event the event
   */
  private void fireColorChangeEvent( ColorChangeEvent event )
  {
    Object[] listeners = _listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i+=2 )
    {
      if ( listeners[i] == ColorChangeListener.class )
      {
        ((ColorChangeListener)listeners[i+1]).colorChanged( event );
      }
    }
  }
  
}


/* end of file */