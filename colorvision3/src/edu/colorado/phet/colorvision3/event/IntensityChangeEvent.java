/* IntensityChangeEvent.java */

package edu.colorado.phet.colorvision3.event;

import java.awt.Color;
import java.util.EventObject;

/**
 * IntensityChangeEvent occurs when the perceived intensity of
 * a photon beam changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class IntensityChangeEvent extends EventObject
{
  protected Color _color;
  protected double _intensity;
  
  /**
   * Sole constructor.
   * 
   * @param source the source of the event
   */
  public IntensityChangeEvent( Object source, Color color, double intensity )
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
  public Color getColor()
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