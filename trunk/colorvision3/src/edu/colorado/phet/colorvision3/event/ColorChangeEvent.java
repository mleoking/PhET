/* ColorChangeEvent.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.event;

import java.util.EventObject;

import edu.colorado.phet.colorvision3.model.VisibleColor;

/**
 * ColorChangeEvent occurs when a color changes in some way.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class ColorChangeEvent extends EventObject
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // The base color.
  protected VisibleColor _color;
  // The intensity of the base color's alpha component.
  protected double _intensity;
  
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
  public ColorChangeEvent( Object source, VisibleColor color, double intensity )
  {
    super( source );
    _color = color;
    _intensity = intensity;
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Gets the color.
   *
   * @param color the color
   */
  public VisibleColor getColor()
  {
    return _color;
  }
  
  /**
   * Gets the intensity.
   * 
   * @param the intensity
   */
  public double getIntensity()
  {
    return _intensity;
  }

}

/* end of file */