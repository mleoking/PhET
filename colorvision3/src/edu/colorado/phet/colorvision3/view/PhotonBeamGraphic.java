/* PhotonBeamGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.model.Photon;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.colorvision3.model.VisibleColor;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * PhotonBeamGraphic provides a photon view of a Spotlight (a 2D spotlight beam).
 * A PhotonBeamGraphic is composed of a number of Photon instances.
 * Memory allocation of Photons is optimized to reuse instances when possible.
 */
public class PhotonBeamGraphic extends PhetGraphic implements SimpleObserver, ClockTickListener
{
  private static final Rectangle BOUNDS_EVERYWHERE = new Rectangle(0,0,10000,10000);
  private static final int FILTER_CENTER_OFFSET = 12;
  private static final int FILTERED_PHOTON_ADVANCE = 20;
  private static Stroke PHOTON_STROKE = new BasicStroke( 1f );
  
  public static final int PHOTONS_PER_INTENSITY = 5;
  public static final int PHOTON_DS = 10;
  public static final int PHOTON_LINE_LENGTH = 3;

  private Spotlight _spotlight;
  private Filter _filter;
  private Rectangle _bounds;
  private VisibleColor _perceivedColor;
  private double _perceivedIntensity; // in percent (0.0-100.0)
  private ArrayList _photons;
  private EventListenerList _listenerList;
  
  /**
   * Sole constructor.
   * 
   * @param parent the parent component
   * @param spotlight the model that this photon beam will animate
   */
  public PhotonBeamGraphic( Component parent, Spotlight spotlight, Filter filter )
  {
    super( parent );
    
    // Initialize member data.
    _spotlight = spotlight;
    _filter = filter;
    _bounds = BOUNDS_EVERYWHERE;
    _perceivedColor = null;
    _perceivedIntensity = 0.0;
    _photons = new ArrayList();
    _listenerList = new EventListenerList();
  }
  
  public PhotonBeamGraphic( Component parent, Spotlight spotlight )
  {
    this( parent, spotlight, null );
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
   * Renders the photon beam.
   * Note that the graphics state is preserved.
   */
  public void paint( Graphics2D g2 )
  {
    if ( isVisible() )
    {
      super.saveGraphicsState( g2 );
      {
        // Use the same stroke for all photons.
        g2.setStroke( PHOTON_STROKE );
        
        Photon photon = null;
        int x, y, w, h;
        VisibleColor vc;
        for ( int i = 0; i < _photons.size(); i++ )
        {
          photon = (Photon)_photons.get(i);
          if ( photon.isInUse() )
          {
            x = (int) photon.getX();
            y = (int) photon.getY();
            w = (int) photon.getWidth();
            h = (int) photon.getHeight();
            vc = photon.getColor();
            
            // Render the photon.
            g2.setPaint( vc.toColor() ); // XXX Huge performance improvement by converting VisibleColor to Color.
            g2.drawLine( x, y, x-w, y-h );
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
    int allocCount = (int) (_spotlight.getIntensity() / PHOTONS_PER_INTENSITY);
    if ( allocCount == 0 && _perceivedIntensity != 0.0 )
    {
      allocCount = 1;
    }
    
    // Walk the photon array and paint/prune/reinitialize as needed.
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
          photon.setInUse( false );
          newPerceivedColor = photon.getColor();
          newPerceivedIntensity = photon.getIntensity();
        }
        else if ( _filter != null && _filter.isEnabled() && ! photon.isFiltered() )
        {
          // If we have an active filter and the photon has not been previously
          // filtered, then determine whether the photon should pass the filter
          // and what its new color should be.
          // Note! This assumes the photon is traveling left-to-right horizontally.
          if ( photon.getX() > _filter.getX() + FILTER_CENTER_OFFSET )
          {
            VisibleColor color = _filter.colorPassed( photon.getColor() );
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
              // Advance it a bit so it looks like it goes through the filter.
              photon.setLocation( photon.getX() + FILTERED_PHOTON_ADVANCE, photon.getY() );
              passedCount++;
            }
          }
        }
      }
      
      // If the photon is not in use and we need to allocate photons,
      // then reuse the photon.
      if ( (! photon.isInUse()) && allocCount > 0 )
      {
        double x = genX( _spotlight.getX() );
        double y = _spotlight.getY();
        double direction = genDirection( _spotlight.getDirection() );
 
        photon.setLocation( x, y );
        photon.setDirection( direction );
        photon.setColor( _spotlight.getColor() );
        photon.setIntensity( _spotlight.getIntensity() );
        photon.setInUse( true );
        photon.setFiltered( false );
       
        allocCount--;
      }
    } // for each photon

    // If we didn't find enough available photons, allocate some new ones.
    while ( allocCount > 0 )
    {
      double direction = genDirection( _spotlight.getDirection() );
      double x = genX( _spotlight.getX() );
      photon = new Photon( _spotlight.getColor(),
                                  _spotlight.getIntensity(), 
                                  x, _spotlight.getY(),
                                  direction );
      _photons.add( photon );
      
      allocCount--;
    }
    
    // If filtering is enabled and we passed no photons, then send a photon
    // with "no color", starting at the location of the last photon that the 
    // filter blocked.
    if ( _filter != null && _filter.isEnabled() && passedCount == 0 && 
        _perceivedColor != null && !_perceivedColor.equals(VisibleColor.INVISIBLE) )
    {
      
      photon = new Photon( VisibleColor.INVISIBLE, 0, lastFilteredX, lastFilteredY, _spotlight.getDirection() );
      _photons.add( photon );
    }
    
    // If the perceived color or intensity has changed, then fire a ColorChangeEvent.
    if ( newPerceivedColor != _perceivedColor ||
         newPerceivedIntensity != _perceivedIntensity )
    {
      _perceivedColor = newPerceivedColor;
      _perceivedIntensity = newPerceivedIntensity;
      ColorChangeEvent event = new ColorChangeEvent( this, _perceivedColor, _perceivedIntensity );
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
    double delta = (Math.random() * _spotlight.getCutOffAngle() / 2 );
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
   * Adds a ColorChangeListener.
   * 
   * @param listener the listener to add
   */
  public void addIntensityChangeListener( ColorChangeListener listener )
  {
    _listenerList.add( ColorChangeListener.class, listener );
  }
  
  /**
   * Removes a ColorChangeListener.
   * 
   * @param listener the listener to remove
   */
  public void removeIntensityChangeListener( ColorChangeListener listener )
  {
    _listenerList.remove( ColorChangeListener.class, listener );
  }
  
  /**
   * Fires a ColorChangeEvent.
   * This occurs each time the color or intensity of the photon beam changes.
   * 
   * @param event the event
   */
  private void fireIntensityChangeEvent( ColorChangeEvent event )
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
    super.repaint();
  }
  
}

/* end of file */