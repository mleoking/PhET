/* PhotonBeamGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.IntensityChangeEvent;
import edu.colorado.phet.colorvision3.event.IntensityChangeListener;
import edu.colorado.phet.colorvision3.model.Spotlight2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * PhotonBeamGraphic provides a photon view of a Spotlight2D (a 2D spotlight beam).
 * A PhotonBeamGraphic is composed of a number of PhotonGraphic instances.
 * Memory allocation of Photons is optimized to reuse instances when possible.
 */
public class PhotonBeamGraphic extends PhetGraphic implements SimpleObserver, ClockTickListener
{
  private static final Rectangle BOUNDS_EVERYWHERE = new Rectangle(0,0,10000,10000);
  public static final int PHOTONS_PER_INTENSITY = 5;
  
  private Spotlight2D _model;
  private Rectangle _bounds;
  private double _perceivedIntensity; // in percent (0.0-100.0)
  private ArrayList _photons;
  private EventListenerList _listenerList;
  
  /**
   * Sole constructor.
   * 
   * @param parent the parent component
   * @param model the model that this photon beam will animate
   */
  public PhotonBeamGraphic( Component parent, Spotlight2D model )
  {
    super( parent );
    
    // Initialize member data.
    _model = model;
    _bounds = BOUNDS_EVERYWHERE;
    _perceivedIntensity = 0.0;
    _photons = new ArrayList();
    _listenerList = new EventListenerList();
  }

  /**
   * Gets the bounds.
   * 
   * @return the bounds
   */
  public Rectangle getBounds()
  {
    return _bounds;
  }
  
  /** 
   * Sets the bounds.  The photon beam will not render outside of this region,
   * and photons that leave this region will be marked as available.
   * 
   * @param bounds the bounds
   */
  public void setBounds( Rectangle bounds )
  {
    _bounds = bounds;
  }
 
  /**
   * Gets the bounds.
   * 
   * @return the bounds
   */
  protected Rectangle determineBounds()
  {
    return _bounds;
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
   * Renders the photon beam.
   * Note that the graphics state is preserved.
   */
  public void paint( Graphics2D g2 )
  {
    if ( isVisible() )
    {
      super.saveGraphicsState( g2 );
      {
        PhotonGraphic photon = null;
        for ( int i = 0; i < _photons.size(); i++ )
        {
          photon = (PhotonGraphic)_photons.get(i);
          if ( photon.isInUse() )
          {
            photon.paint( g2 );
          }
        }
      }
      super.restoreGraphicsState();
    }
  } // paint

  /**
   * Called each time the simulation clock ticks.
   * Walks the photon list exactly once, and advances or prunes photons
   * based on their location.  Pruned photons are available for reuse. 
   * The current intensity determines how many photons must be initialized,
   * either via reuse or by creating new photons.
   * 
   * @param dt time delta, currently ignored
   */
  public void stepInTime( double dt )
  {
    PhotonGraphic photon;
    double newPerceivedIntensity = 0.0;
    
    // Determine how many photons to emit.
    // If the intensity is changing to from non-zero to zero, emit one 
    // photon with zero intensity to ensure that zero intensity is 
    // perceived by the viewer.
    int allocCount = (int) (_model.getIntensity() / PHOTONS_PER_INTENSITY);
    if ( allocCount == 0 && _perceivedIntensity != 0.0 )
    {
      allocCount = 1;
    }
    
    // Walk the photon array and paint/prune/reinitialize as needed.
    for ( int i = 0; i < _photons.size(); i++ )
    {
      photon = (PhotonGraphic)_photons.get(i);
              
      // Advance the photon.  If it falls outside of the bounds, then 
      // mark it as available and use it's intensity as the perceived intensity.
      if ( photon.isInUse() ) 
      {  
        photon.stepInTime( dt );
        if ( ! _bounds.contains( photon.getX(), photon.getY() ))
        {
          photon.setInUse( false );
          newPerceivedIntensity = photon.getIntensity();
        }
      }
      
      // If the photon is not in use and we need to allocate photons,
      // then reuse the photon.
      if ( (! photon.isInUse()) && allocCount > 0 )
      {
        double x = genX( _model.getX() );
        double y = _model.getY();
        double direction = genDirection( _model.getDirection() );
 
        photon.setLocation( x, y );
        photon.setDirection( direction );
        photon.setColor( _model.getColor() );
        photon.setIntensity( _model.getIntensity() );
        photon.setInUse( true );
       
        allocCount--;
      }
    } // for each photon

    // If we didn't find enough available photons, allocate some new ones.
    while ( allocCount > 0 )
    {
      double direction = genDirection( _model.getDirection() );
      photon = new PhotonGraphic( _model.getColor(), _model.getIntensity(), 
                          _model.getX(), _model.getY(),
                          direction );
      _photons.add( photon );
      
      allocCount--;
    }
    
    // If the perceived intensity has changed, then fire an IntensityChangeEvent.
    if ( newPerceivedIntensity != _perceivedIntensity )
    {
      _perceivedIntensity = newPerceivedIntensity;
      IntensityChangeEvent event = new IntensityChangeEvent( this, _model.getColor(), _model.getIntensity() );
      fireIntensityChangeEvent( event );
    }
  } // stepInTime

  /**
   * Generates the X coordinate for a photon by adding some small delta
   * to the provided location.
   * 
   * @param x the current X coordinate
   * @return
   */
  private double genX( double x )
  {
    return (x + (Math.random() * PhotonGraphic.PHOTON_DS));
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
    double delta = (Math.random() * _model.getCutOffAngle() / 2 );
    // Randomly make the delta positive or negative.
    delta *= ( Math.random() > 0.5 ) ? 1 : -1;
    // Add the delta to the original direction.
    return (direction + delta);
  }

  /**
   * Called each time the model changes.
   * Since the photon beam consults the model on every tick of the 
   * simulation clock, this method does nothing.
   */
  public void update()
  {
    // Do nothing.
  }
  
  /**
   * Adds an IntensityChangeListener.
   * 
   * @param listener the listener to add
   */
  public void addIntensityChangeListener( IntensityChangeListener listener )
  {
    _listenerList.add( IntensityChangeListener.class, listener );
  }
  
  /**
   * Removes an IntensityChangeListener.
   * 
   * @param listener the listener to remove
   */
  public void removeIntensityChangeListener( IntensityChangeListener listener )
  {
    _listenerList.remove( IntensityChangeListener.class, listener );
  }
  
  /**
   * Fires an IntensityChangeEvent.
   * This occurs each time the perceived intensity of the photon beam changes.
   * 
   * @param event the event
   */
  private void fireIntensityChangeEvent( IntensityChangeEvent event )
  {
    Object[] listeners = _listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i+=2 )
    {
      if ( listeners[i] == IntensityChangeListener.class )
      {
        ((IntensityChangeListener)listeners[i+1]).intensityChanged( event );
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
    super.repaint();
  }
  
}

/* end of file */