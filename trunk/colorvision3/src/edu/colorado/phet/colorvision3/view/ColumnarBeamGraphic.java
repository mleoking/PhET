/* ColumnarBeamGraphic.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.colorvision3.model.VisibleColor;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * ColumnarBeamGraphic provides a graphic representation of a columnar beam 
 * of light.  The beam may be filtered or unfiltered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class ColumnarBeamGraphic extends PhetShapeGraphic implements SimpleObserver
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------
  
  // The associated spotlight model.
  private Spotlight _spotlightModel;
  // Optional filter model.
  private Filter _filterModel;
  // Distance from the spotlight that light is emitted, in pixels.
  private int _distance;
  // Alpha scale, used to make the beam transparent (in percent)
  private int _alphaScale;
  // Event listeners 
  EventListenerList _listenerList;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Constructor for a filtered beam.
   * 
   * @param parent the parent Component
   * @param spotlightModel the spotlight model
   * @param fitlerModel the fitler model
   */
  public ColumnarBeamGraphic( Component parent, Spotlight spotlightModel, Filter filterModel )
  {
    super( parent, null, null );
    
    _spotlightModel = spotlightModel;
    _filterModel = filterModel;
    _distance = 0;
    _alphaScale = 50;
    _listenerList = new EventListenerList();
    
    // Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.white );
    super.setStroke( null );
    
    update();
  }
  
  /**
   * Constructor for an unfiltered beam.
   * 
   * @param parent the parent Component
   * @param spotlightModel the spotlight model
   */
  public ColumnarBeamGraphic( Component parent, Spotlight spotlightModel )
  {
    this( parent, spotlightModel, null );
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
   * Sets the alpha scale. This is used to adjust the transparency of the beam,
   * It is applied to the alpha component of the beam's color.
   * 
   * @param alphaScale the alpha scale, in percent
   */
  public void setAlphaScale( int alphaScale )
  {
    _alphaScale = alphaScale;
    update();
  }
  
  /**
   * Gets the alpha scale
   * 
   * @return the alpha scale, in percent
   */
  public int getAlphaScale()
  {
    return _alphaScale;
  }
  
  /**
   * Gets the perceived color of the beam.
   * 
   * @return the color
   */
  public VisibleColor getPerceivedColor()
  {
    VisibleColor bulbColor = _spotlightModel.getColor();
    
    // Adjust color using filter.
    VisibleColor beamColor = bulbColor;
    if ( _filterModel != null )
    {
      beamColor = _filterModel.colorPassed( bulbColor );
    }
    
    return beamColor;
  }
  
	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Synchronizes the view with the associated models. 
   */
  public void update()
  { 
    // Shape update
    {
      double direction = _spotlightModel.getDirection(); // in degrees
      double cutOffAngle = _spotlightModel.getCutOffAngle(); // in degrees

      // Radius of the beam.
      double radius = _distance / Math.cos( Math.toRadians( cutOffAngle/2 ) );
    
      // First coordinate
      double x0 = _spotlightModel.getX();
      double y0 = _spotlightModel.getY();
    
      // Second coordinate
      double theta1 = Math.toRadians( direction - (cutOffAngle/2) );
      double x1 = radius * Math.cos( theta1 );
      double y1 = radius * Math.sin( theta1 );
      x1 += _spotlightModel.getX();
      y1 += _spotlightModel.getY();
      
      // Third coordinate
      double theta2 = Math.toRadians( direction + (cutOffAngle/2) );
      double x2 = radius * Math.cos( theta2 );
      double y2 = radius * Math.sin( theta2 );
      x2 += _spotlightModel.getX();
      y2 += _spotlightModel.getY();
    
      GeneralPath path = new GeneralPath();
      path.moveTo( (int)x0, (int)y0 );
      path.lineTo( (int)x1, (int)y1 );
      path.lineTo( (int)x2, (int)y2 );
      path.closePath();
    
      super.setShape( path );
    }
    
    // Color update
    {
      VisibleColor beamColor = getPerceivedColor();
      
      // Scale the alpha component to provide transparency.
      int r = beamColor.getRed();
      int g = beamColor.getGreen();
      int b = beamColor.getBlue();
      int a = beamColor.getAlpha() * _alphaScale / 100;
      
      super.setPaint( new Color(r,g,b,a) );
      
      // Notify listeners
      if ( super.isVisible() )
      {
        ColorChangeEvent event = new ColorChangeEvent( this, beamColor, 100.0 );
        fireColorChangeEvent( event );
      }
    }
    
  } // update
  
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