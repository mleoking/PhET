/* BellCurve.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * BellCurve draws a bell curve shape.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$ $Name$
 */
public class BellCurve extends PhetShapeGraphic
{
  /**
   * Sole constructor
   * 
   * @param component the parent Component
   * @param x the X coordinate of the curve's location
   * @param y the Y coordinate of the curve's location
   * @param w the width of the curve, in pixels
   * @param h the height of the curve, in pixels
   */
  public BellCurve( Component component, int x, int y, int w, int h )
  {
    super( component, null, null );
    
    //  Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );

    // Default stroke attributes.
    super.setBorderColor( Color.BLACK );
    super.setStroke( new BasicStroke(1f) );
    
    // Create the path that describes the curve.
    GeneralPath path = new GeneralPath();
    path.moveTo(  -.50f * w + x, 1f * h + y ); // lower left
    path.curveTo( -.25f * w + x, 1f * h + y,
                  -.25f * w + x, 0f * h + y, 
                     0f * w + x, 0f * h + y  ); // left curve
    path.curveTo(  .25f * w + x, 0f * h + y, 
                   .25f * w + x, 1f * h + y,
                   .50f * w + x, 1f * h + y ); // right curve
    super.setShape( path );
  }
  
}


/* end of file */