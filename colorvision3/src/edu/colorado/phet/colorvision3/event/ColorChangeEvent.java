/* ColorChangeEvent.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.event;

import java.util.EventObject;

import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * ColorChangeEvent occurs when a color changes in some way.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class ColorChangeEvent extends EventObject
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // The color.
  protected VisibleColor _color;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param source the source of the event
   * @param color the color
   * @param intensity the intensity
   */
  public ColorChangeEvent( Object source, VisibleColor color )
  {
    super( source );
    _color = color;
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Gets the color.
   *
   * @return the color
   */
  public VisibleColor getColor()
  {
    return _color;
  }
  
	//----------------------------------------------------------------------------
	// Conversions
  //----------------------------------------------------------------------------

  /**
   * Provides a String representation of this event.
   * The format of this String may change in the future.
   *
   * @return a String
   */
  public String toString()
  {
    int r = _color.getRed();
    int g = _color.getGreen();
    int b = _color.getBlue();
    int a = _color.getAlpha();
    double w = _color.getWavelength();
    
    return "color=[" + r + "," + g + "," + b + "," + a + "]" +
           " wavelength=" + w +
           " source=[" + super.getSource() + "]";
  }
  
}

/* end of file */