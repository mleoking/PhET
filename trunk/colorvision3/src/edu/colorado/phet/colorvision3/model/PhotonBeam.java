/* PhotonBeam.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.model;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * PhotonBeam is the model of a photon beam.
 * A PhotonBeam is composed of a number of Photon instances.
 * Memory allocation of Photons is optimized to reuse instances when possible.
 * The photon beam may be filtered or unfiltered.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class PhotonBeam extends SimpleObservable implements SimpleObserver, ClockTickListener
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Default bounds for photon drop off
  private static final Rectangle BOUNDS_EVERYWHERE = new Rectangle(0,0,10000,10000);
  // Offset of filter's visual center
  private static final int FILTER_CENTER_OFFSET = 12;
  // How far to advance a photon when it hits the filter
  private static final int FILTERED_PHOTON_ADVANCE = 20;
  // Photons per 1% intensity.
  public static final int PHOTONS_PER_INTENSITY = 5;
  // Photon delta.
  public static final int PHOTON_DS = 10;

	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Spotlight model
  private Spotlight _spotlightModel;
  // Optional filter model
  private Filter _filterModel;
  // Bounds for photon drop off
  private Rectangle _bounds;
  // Perceived color, based on most recent photon to leave the bounds
  private VisibleColor _perceivedColor;
  // Color intensity, in percent (0-100)
  private double _perceivedIntensity;
  // Photons (array of Photon)
  private ArrayList _photons;
  // Event listeners
  private EventListenerList _listenerList;
  // Is the beam enabled?
  private boolean _enabled;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Constructor for a filtered beam.
   * 
   * @param spotlightModel the spotlight model
   * @param filterModel the filter model
   */
  public PhotonBeam( Spotlight spotlightModel, Filter filterModel )
  {   
    // Initialize member data.
    _spotlightModel = spotlightModel;
    _filterModel = filterModel;
    _bounds = BOUNDS_EVERYWHERE;
    _perceivedColor = VisibleColor.INVISIBLE;
    _perceivedIntensity = 0.0;
    _photons = new ArrayList();
    _listenerList = new EventListenerList();
    _enabled = true;
  }
  
  /**
   * Constructor for an unfiltered beam.
   * 
   * @param spotlight the spotlight model
   */
  public PhotonBeam( Spotlight spotlight )
  {
    this( spotlight, null );
  }

	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Gets the collection of photons.
   * 
   * @return an ArrayList of Photon instances
   */
  public final ArrayList getPhotons()
  {
    return _photons;
  }
  
  /**
   * Gets the bounds. See setBounds.
   * 
   * @return the bounds
   */
  public Rectangle getBounds()
  {
    return _bounds;
  }
  
  /** 
   * Sets the bounds. The photon beam will not render outside of this region,
   * and photons that leave this region will be marked as available.
   * 
   * @param bounds the bounds
   */
  public void setBounds( Rectangle bounds )
  {
    _bounds = bounds;
    notifyObservers();
  }
  
  /**
   * Gets the base color perceived by the user.
   * This is the color of the most recent photon to leave the bounds.
   * 
   * @return the color
   */
  public VisibleColor getPerceivedColor()
  {
    return _perceivedColor;
  }
  
  /**
   * Gets the intensity perceived by the user.
   * This is the intensity of the most recent photon to leave the bounds.
   * 
   * @return the perceived intensity
   */
  public double getPerceivedIntensity()
  {
    return _perceivedIntensity;
  }
  
  /**
   * Enables or disables the beam.
   * A disabled beam does not send ColorChangeEvents.
   * 
   * @param enabled true to enable, false to disable
   */
  public void setEnabled( boolean enabled )
  {
    _enabled = enabled;
    notifyObservers();
  }
  
  /**
   * Determines whether the beam is enabled.
   * 
   * @return true if enabled, false if disabled
   */
  public boolean isEnabled()
  {
    return _enabled;
  }
 
	//----------------------------------------------------------------------------
	// Animation
  //----------------------------------------------------------------------------

  /**
   * Called each time the simulation clock ticks.
   * Walks the photon list exactly once, and advances or prunes photons
   * based on their location and optional color filtering.  Pruned photons 
   * are available for reuse. The current intensity determines how many 
   * photons must be initialized, either via reuse or by creating new photons.
   * <p>
   * Note that this algorithm assumes that the photons are traveling 
   * left-to-right in its treatment of filtering and coordinate generation.
   * 
   * @param dt time delta, currently ignored
   */
  public void stepInTime( double dt )
  {
    Photon photon;
    int passedCount = 0;
    double lastFilteredX = 0.0;
    double lastFilteredY = 0.0;
    VisibleColor newPerceivedColor = _perceivedColor;
    double newPerceivedIntensity = _perceivedIntensity;
    
    // Determine how many photons to emit.
    // If the intensity is changing to from non-zero to zero, emit one 
    // photon with zero intensity to ensure that zero intensity is 
    // perceived by the viewer.
    int allocCount = (int) (_spotlightModel.getIntensity() / PHOTONS_PER_INTENSITY);
    if ( allocCount == 0 && _perceivedIntensity != 0.0 )
    {
      allocCount = 1;
    }
    
    // Walk the photon array and paint/prune/filter/reinitialize as needed.
    for ( int i = 0; i < _photons.size(); i++ )
    {
      photon = (Photon)_photons.get(i);
              
      // Advance the photon.  If it falls outside of the bounds, then 
      // mark it as available and make note of its color and intensity.
      if ( photon.isInUse() ) 
      {  
        photon.stepInTime( dt );
        
        if ( ! _bounds.contains( photon.getX(), photon.getY() ))
        {
          // Photon is out of bounds, mark for reuse.
          photon.setInUse( false );
          newPerceivedColor = photon.getColor();
          newPerceivedIntensity = photon.getIntensity();
        }
        else if ( _filterModel != null && ! photon.isFiltered() )
        {
          // If we have an active filter and the photon has not been previously
          // filtered, then determine whether the photon should pass the filter
          // and what its new color should be.
          // Note! This assumes the photon is traveling left-to-right horizontally.
          if ( photon.getX() > _filterModel.getX() + FILTER_CENTER_OFFSET )
          {
            VisibleColor color = _filterModel.colorPassed( photon.getColor() );
            if ( color.equals( VisibleColor.INVISIBLE ) )
            {
              // If the filter passes no color, then mark the photon as available.
              photon.setInUse( false );
              lastFilteredX = photon.getX();
              lastFilteredY = photon.getY();
            }
            else
            {
              // Photon's color is the filtered color.
              photon.setColor( color );
              photon.setFiltered( true );
              if ( _filterModel.isEnabled() )
              {
                // Advance it a bit so it looks like it goes through the filter.
                photon.setLocation( photon.getX() + FILTERED_PHOTON_ADVANCE, photon.getY() );
                passedCount++;
              }
            }
          }
        }
      }
      
      // If the photon is not in use and we need to allocate photons,
      // then reuse the photon.
      if ( (! photon.isInUse()) && allocCount > 0 )
      {
        double x = genX( _spotlightModel.getX() );
        double y = _spotlightModel.getY();
        double direction = genDirection( _spotlightModel.getDirection() );
 
        photon.setLocation( x, y );
        photon.setDirection( direction );
        photon.setColor( _spotlightModel.getColor() );
        photon.setIntensity( _spotlightModel.getIntensity() );
        photon.setInUse( true );
        photon.setFiltered( false );
       
        allocCount--;
      }
    } // for each photon

    // If we didn't find enough available photons, allocate some new ones.
    while ( allocCount > 0 )
    {
      double direction = genDirection( _spotlightModel.getDirection() );
      double x = genX( _spotlightModel.getX() );
      photon = new Photon( _spotlightModel.getColor(),
                                  _spotlightModel.getIntensity(), 
                                  x, _spotlightModel.getY(),
                                  direction );
      _photons.add( photon );
      
      allocCount--;
    }
    
    // If filtering is enabled and we passed no photons, then send a photon
    // with "no color", starting at the location of the last photon that the 
    // filter blocked.
    if ( _filterModel != null && _filterModel.isEnabled() && passedCount == 0 && 
        _perceivedColor != null && !_perceivedColor.equals(VisibleColor.INVISIBLE) )
    {
      
      photon = new Photon( VisibleColor.INVISIBLE, 0, lastFilteredX, lastFilteredY, _spotlightModel.getDirection() );
      _photons.add( photon );
    }
    
    // If the perceived color or intensity has changed, then fire a ColorChangeEvent.
    if ( !newPerceivedColor.equals(_perceivedColor) || newPerceivedIntensity != _perceivedIntensity )
    {
      _perceivedColor = newPerceivedColor;
      _perceivedIntensity = newPerceivedIntensity;
      if ( _enabled )
      {
        ColorChangeEvent event = new ColorChangeEvent( this, _perceivedColor, _perceivedIntensity );
        fireColorChangeEvent( event );
      }
    }
   
  } // stepInTime

  /**
   * Generates the X coordinate for a photon by adding some small delta
   * to the provided location.
   * 
   * @param x the current X coordinate
   * @return the new X coordinate
   */
  private double genX( double x )
  {
    return (x + (Math.random() * PHOTON_DS));
  }

  /**
   * Generates a new direction, based on the cutoff angle of the light.
   * 
   * @param direction the starting direction, in degrees
   * @return the new direction
   */
  private double genDirection( double direction )
  {
    // Generate a delta based on the cutoff angle.
    double delta = (Math.random() * _spotlightModel.getCutOffAngle() / 2 );
    // Randomly make the delta positive or negative.
    delta *= ( Math.random() > 0.5 ) ? 1 : -1;
    // Add the delta to the original direction.
    return (direction + delta);
  }

	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Called each time the model changes.
   * Since the photon beam consults the model on every tick of the 
   * simulation clock, this method does nothing.
   */
  public void update()
  {
    // Do nothing.
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

  /**
   * Called each time the simulation clock ticks.
   * 
   * @param clock the clock
   * @param dt the time delta since the last clock tick
   */
  public void clockTicked( AbstractClock clock, double dt )
  {
    stepInTime( dt );
    notifyObservers();
  }
  
}

/* end of file */