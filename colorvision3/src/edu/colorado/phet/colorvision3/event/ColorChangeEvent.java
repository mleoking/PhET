/* ColorChangeEvent.java */

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
  protected VisibleColor _color;
  protected double _intensity;
  
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