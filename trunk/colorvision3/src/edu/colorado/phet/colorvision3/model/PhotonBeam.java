/* PhotonBeam.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.model;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.VisibleColorChangeEvent;
import edu.colorado.phet.colorvision3.event.VisibleColorChangeListener;
import edu.colorado.phet.common.model.ModelElement;
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
public class PhotonBeam extends SimpleObservable implements SimpleObserver, ModelElement
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Offset of filter's visual center
  private static final int FILTER_CENTER_OFFSET = 12;
  // How far to advance a photon when it hits the filter
  private static final int FILTERED_PHOTON_ADVANCE = 20;
  // Photon delta.
  public static final int PHOTON_DS = 10;
  // White photons are culled by this amount when hitting a filter.
  private static final double WHITE_CULL_FACTOR = 0.5;

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
  // Light intensity, in percent (0-100)
  private double _perceivedIntensity;
  // Photons (array of Photon)
  private ArrayList _photons;
  // Event listeners
  private EventListenerList _listenerList;
  // Is the beam enabled?
  private boolean _enabled;
  // Number of photons emitted when the light model is at 100% intensity.
  private int _maxPhotons;
  
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
    _bounds = new Rectangle((int)_spotlightModel.getX(), (int)_spotlightModel.getY(), 100, 100 );
    _perceivedColor = VisibleColor.INVISIBLE;
    _perceivedIntensity = 0.0;
    _photons = new ArrayList();
    _listenerList = new EventListenerList();
    _enabled = true;
    _maxPhotons = 20;
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
   * Gets the photon color perceived by the user.
   * This is the color of the most recent photon to leave the bounds.
   * 
   * @return the color
   */
  public VisibleColor getPerceivedColor()
  {
    return _perceivedColor;
  }
  
  /**
   * Gets the light source intensity perceived by the user.
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
   * A disabled beam does not send notify observers or send ColorChangeEvents to listeners.
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
 
  /**
   * Sets the number of photons emitted when the light source is at 100% intensity.
   * 
   * @param maxPhotons the number of photons
   */
  public void setMaxPhotons( int maxPhotons )
  {
    _maxPhotons = maxPhotons;
  }
  
  /**
   * Gets the number of photons emitted when the light source is at 100% intensity.
   * 
   * @return the number of photons.
   */
  public int getMaxPhotons()
  {
    return _maxPhotons;
  }
  
	//----------------------------------------------------------------------------
	// ModelElement implementation
  //----------------------------------------------------------------------------

  /**
   * Called each time the simulation clock ticks.
   * <p>
   * THIS IS THE MOST COMPLICATED METHOD IN THE ENTIRE COLOR VISION SIMULATION.
   * READ THIS DOCUMENTATION THOROUGHLY BEFORE ATTEMPTING ANY MODIFICATIONS!  
   * <p>
   * Walks the photon list exactly once, and advances or prunes photons
   * based on their location and optional color filtering.  Pruned photons 
   * are available for reuse. The current light intensity determines how many 
   * photons must be emitted, either via reuse or by creating new photons.
   * <p>
   * If the photon beam is enabled, observers are notified each time that
   * the method is called and at least one photon moves.
   * <p>
   * If the photon beam is enabled and at least one photon leaves the 
   * beam's bounds, listeners are sent a ColorChangeEvent.  If more than
   * one photon leaves the bounds, only one ColorChangeEvent is sent,
   * containing the information about the last photon to leave.
   * <p>
   * Note that handling of filtering is problematic.  When a collection
   * of photons hits the filter, the collection has to be culled based 
   * on how the filter passes the photons.  The only way to cull the 
   * collection is to assume that all photons in the collection have 
   * the same color. So we use the attributes of the last photon to
   * be passed by the filter to determine the effect of the filter 
   * on the entire collection.
   * <p>
   * HACK: Technically, a photon cannot have a color of white. A white 
   * light source should emit a collection of photons whose color is
   * distributed over the visible spectrum.  But if we did this, we 
   * would have no way of properly setting the perceived color when 
   * one of these photons leaves the beam bounds.  So we allow photons
   * to have a color of white, and leave it up to whatever is visually
   * representing white photons to decide how to render them.
   * (For example, PhotonBeamGraphic selects a random wavelength
   * for painting a white photon.)
   * <p>
   * HACK: This algorithm's handling of filtering and motion assumes
   * that photons are traveling left-to-right.
   * 
   * @param dt time delta, currently ignored
   */
  public void stepInTime( double dt )
  {
    Photon photon;
    
    // Number of photons that advanced.
    int stepCount = 0;
    // Number of photons that passed through the filter.
    int passedCount = 0;
    // X location of the last photon to be blocked by the filter.
    double lastBlockedX = 0.0;
    // Y location of the last photon to be blocked by the filter.
    double lastBlockedY = 0.0;
    // Color of the last photon to leave the bounds.
    VisibleColor newPerceivedColor = _perceivedColor;
    // Intensity of the last photon to leave the bounds.
    double newPerceivedIntensity = _perceivedIntensity;
    // Photons that passed through the filter, array of Photon.
    ArrayList filteredPhotons = new ArrayList();
    // Percentage of color passed by the filter for the last photon to pass through the filter.
    double lastPercentPassed = 0;
    
    // Does this photon beam have a filter?
    boolean hasFilter = ( _filterModel != null );
    // Does this photon beam have an enabled filter?
    boolean hasEnabledFilter = ( _filterModel != null && _filterModel.isEnabled() );
    
    // Determine how many photons to emit from the light source. 
    int allocCount = 0;
    if ( _spotlightModel.getIntensity() == 0 && _perceivedIntensity != 0 )
    {
      // If the light intensity is changing to from non-zero to zero, 
      // emit one photon with zero intensity to ensure that zero intensity 
      // is perceived by the viewer.
      allocCount = 1;
    }
    else
    {
      // Number of photons is based on the light source intensity.
      allocCount = (int) ((_spotlightModel.getIntensity() / 100) * _maxPhotons);
    }
    
    // For each photon...
    for ( int i = 0; i < _photons.size(); i++ )
    {
      photon = (Photon)_photons.get(i);
              
      // Advance the photon.  If it falls outside of the bounds, then 
      // mark it as available and make note of its color and intensity.
      if ( photon.isInUse() ) 
      {  
        stepCount++;
        photon.stepInTime( dt );
        
        if ( ! _bounds.contains( photon.getX(), photon.getY() ))
        {
          // Photon is out of bounds, mark for reuse.
          photon.setInUse( false );
          newPerceivedColor = photon.getColor();
          newPerceivedIntensity = photon.getIntensity();
        }
        else if ( hasFilter && ! photon.isFiltered() && 
                  photon.getX() > _filterModel.getX() + FILTER_CENTER_OFFSET )
        {
          // HACK: Above expression assumes the photon is traveling left-to-right.      
          // If the photon has just passed the filter, mark it as filtered,
          // regardless of whether the filter is enabled.  A photon is subject 
          // to filtering only at the moment that it passed the filter, and
          // this prevents it from being filtered if the filter is enabled.
          photon.setFiltered( true );
          
          // If the filter is enabled, determine its effect on the photon.
          if ( hasEnabledFilter )
          {
            // Determine what percentage of the photon's wavelength is passed by the filter.
            double percentPassed = _filterModel.percentPassed( photon.getColor() );
            if ( percentPassed == 0 )
            {
              // If the photon's color doesn't pass the filter, 
              // then mark the photon as available.
              photon.setInUse( false );
              lastBlockedX = photon.getX();
              lastBlockedY = photon.getY();
            }
            else 
            {
              // Some of the photon's color is passed by the filter.
              filteredPhotons.add( photon );
              
              // HACK: Advance the photon so it looks like it goes through the filter.
              photon.setLocation( photon.getX() + FILTERED_PHOTON_ADVANCE, photon.getY() );
              
              // Adjust the photon's intensity.
              photon.setIntensity( percentPassed );
              
              passedCount++;
              lastPercentPassed = percentPassed;
            }
          } // if the filter is enabled
        } // if photon has hit the filter
      } // if the photon is in use
      
      // If the photon is not in use and we need to allocate photons,
      // then reuse the photon.
      if ( (! photon.isInUse()) && allocCount > 0 )
      {
        double x = genX( _spotlightModel.getX() );
        double y = _spotlightModel.getY();
        double direction = genDirection( _spotlightModel.getDirection() );
 
        photon.setLocation( x, y );
        photon.setDirection( direction );
        photon.setIntensity( _spotlightModel.getIntensity() );
        photon.setInUse( true );
        photon.setFiltered( false );
        photon.setColor( _spotlightModel.getColor() );
        
        allocCount--;
      }
    } // for each photon

    // Cull filtered photons.
    if ( filteredPhotons.size() > 0 )
    {
      int count = filteredPhotons.size();
      
      // White photons are handled specially.
      // HACK: Use the last photon passed by the filter to decide whether they are all white.
      Photon lastPhotonPassed = (Photon)filteredPhotons.get(count-1);
      double wavelength = lastPhotonPassed.getColor().getWavelength();
      boolean isWhite = (wavelength == VisibleColor.WHITE_WAVELENGTH );

      // Determine how many to cull.
      int cull = 0;
      if ( isWhite )
      {
        // White photons are always culled by the same factor.
        cull = (int)(count * WHITE_CULL_FACTOR);
      }
      else
      {
        // Monochrome photons are culled based on the how much of the
        // last filtered photon was passed.
        cull = (int) (count - (count * lastPercentPassed / 100));
      }
      
      // Cull the photons by marking them as available.
      for ( int i = 0; i < cull; i++ )
      {
        ((Photon)filteredPhotons.get(i)).setInUse( false );
        passedCount--;
      }
      
      // Convert white photons to the filter's color.
      if ( isWhite )
      {
        VisibleColor filterColor = _filterModel.getTransmissionPeak();
        for ( int j = cull; j < count; j++ )
        {
          ((Photon)filteredPhotons.get(j)).setColor( filterColor );
        }
      }
    }
    
    // NOTE: Culling may have marked some photons as available by this point.
    // Rather than make another trip through the photons array, we'll choose
    // to allocate new photons if needed.  The available photons will be 
    // reused the next time this method is called.
    
    // If we didn't find enough available photons, allocate some new ones.
    while ( allocCount > 0 )
    {
      photon = new Photon( _spotlightModel.getColor(), 
                           _spotlightModel.getIntensity(), 
                           genX( _spotlightModel.getX() ), 
                           _spotlightModel.getY(),
                           genDirection( _spotlightModel.getDirection() ) );
      _photons.add( photon );
      
      allocCount--;
    }
    
    // If filtering is enabled and we passed no photons, and there is still 
    // a visible perceived color, then send a photon with "no color", starting 
    // at the location of the last photon that the filter blocked.
    if ( hasEnabledFilter && 
         passedCount == 0 &&
         _perceivedColor != null && 
         _perceivedColor.getWavelength() != VisibleColor.INVISIBLE_WAVELENGTH )
    {
      // NOTE: It would be better, but more costly, to use an available photon.
      photon = new Photon( VisibleColor.INVISIBLE, 0, lastBlockedX, lastBlockedY, _spotlightModel.getDirection() );
      _photons.add( photon );
    }
    
    // If the perceived color or intensity has changed...
    if ( newPerceivedIntensity != _perceivedIntensity || 
         newPerceivedColor.getWavelength() != _perceivedColor.getWavelength() )
    {
      _perceivedColor = newPerceivedColor;
      _perceivedIntensity = newPerceivedIntensity;
      
      // Fire a VisibleColorChangeEvent if the beam is enabled.
      if ( _enabled )
      {
        // Calculate the new color.
        // Take the photon's color and scale its alpha component.
        int r = _perceivedColor.getRed();
        int g = _perceivedColor.getGreen();
        int b = _perceivedColor.getBlue();
        int a = (int) (_perceivedColor.getAlpha() * _perceivedIntensity / 100);
        VisibleColor color = new VisibleColor( r, g, b, a );
        
        // Notify listeneners about the new color.
        VisibleColorChangeEvent event = new VisibleColorChangeEvent( this, color );
        fireColorChangeEvent( event );
      }
    }
    
    // Notify observers if the beam is enabled and at least one photon moved.
    if ( _enabled && stepCount > 0 )
    {
      notifyObservers();
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
   * Called each time the spotlight or filter model changes.
   */
  public void update()
  {
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
  public void addColorChangeListener( VisibleColorChangeListener listener )
  {
    _listenerList.add( VisibleColorChangeListener.class, listener );
  }
  
  /**
   * Removes a VisibleColorChangeListener.
   * 
   * @param listener the listener to remove
   */
  public void removeColorChangeListener( VisibleColorChangeListener listener )
  {
    _listenerList.remove( VisibleColorChangeListener.class, listener );
  }
  
  /**
   * Fires a VisibleColorChangeEvent.
   * This occurs each time the color or intensity of the photon beam changes.
   * 
   * @param event the event
   */
  private void fireColorChangeEvent( VisibleColorChangeEvent event )
  {
    Object[] listeners = _listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i+=2 )
    {
      if ( listeners[i] == VisibleColorChangeListener.class )
      {
        ((VisibleColorChangeListener)listeners[i+1]).colorChanged( event );
      }
    }
  }
  
}

/* end of file */