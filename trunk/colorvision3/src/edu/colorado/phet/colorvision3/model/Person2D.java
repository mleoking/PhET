/* Person2D.java */

package edu.colorado.phet.colorvision3.model;

import java.awt.Color;

import edu.colorado.phet.common.util.SimpleObservable;

/**
 * Person2D is the model of the person who is perceiving some color.
 * * Any changes to the models properties (via its setters or getters)
 * results in the notification of all registered observers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class Person2D extends SimpleObservable
{
  private Color _color;
  private double _x, _y;
  
  /**
   * Sole constructor.
   * Creates a person who is located at (0,0) and is seeing no color.
   */
  public Person2D()
  {
    // Initialize member data.
    _color = new Color(0,0,0,0);
    _x = _y = 0.0;
  }

  /**
   * Gets the color perceived by the person.
   * 
   * @return the color
   */
  public Color getColor()
  {
    return _color;
  }
  
  /**
   * Sets the color perceived by the person.
   * 
   * @param color the color
   */
  public void setColor( Color color )
  {
    _color = color;
    notifyObservers();
  }
  
  /**
   * Gets the X coordinate of the person's location.
   * 
   * @return the X coordinate
   */
  public double getX()
  {
    return _x;
  }
  
  /**
   * Gets the Y coordinate of the person's location.
   * 
   * @return the Y coordinate
   */
  public double getY()
  {
    return _y;
  }
  
  /**
   * Sets the person's location.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
    notifyObservers();
  }
  
}


/* end of file */