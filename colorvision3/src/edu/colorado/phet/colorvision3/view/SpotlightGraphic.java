/* SpotlightGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * SpotlightGraphic is the view component for a spotlight.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class SpotlightGraphic extends CompositePhetGraphic implements SimpleObserver
{
  /** How far the bulb is inside the spotlight, in pixels. */
  private static final int BULB_OFFSET = 130;
  
  private Spotlight _model;
  private PhetImageGraphic _spotlightGraphic;
  private AffineTransform _rotate;
    
  /**
   * Sole constructor.
   * 
   * @param apparatus the apparatus panel
   * @param model the associated spotlight model
   */
  public SpotlightGraphic( ApparatusPanel apparatus, Spotlight model )
  {
    super( apparatus );
    
    // Save a reference to the model.
    _model = model;
   
    // Spotlight graphic
    _spotlightGraphic = new PhetImageGraphic( apparatus, ColorVisionConfig.SPOTLIGHT_IMAGE );
    this.addGraphic( _spotlightGraphic );
    
    // Sync the view with the model.
    update();
  }
  
  /**
   * Sets the location of the associated model.
   * 
   * @param x
   * @param y
   */
  public void setLocation( int x, int y )
  {    
    _model.setLocation( x, y );
  }
  
  /**
   * Sets the direction.
   * 
   * @param direction the direction, in degrees
   */
  public void setDirection( double direction )
  {
    _model.setDirection( direction );
  }

  /**
   * Synchronizes the view with the model.
   * This method is called whenever the model changes.
   */
  public void update()
  {
    // Location
    {
      Rectangle spotBounds = _spotlightGraphic.getBounds();
      _spotlightGraphic.setPosition( (int)(_model.getX() - spotBounds.width + BULB_OFFSET),
                                     (int)(_model.getY() - (spotBounds.height/2)) );
    }

    // Direction.
    {
      // Convert to radians.
      double radians = Math.toRadians( _model.getDirection() );
    
      // Rotate about the right-center edge of the spotlight image.
      Rectangle bounds = _spotlightGraphic.getBounds();
      int centerX = (int) (bounds.x + (bounds.width) - BULB_OFFSET);
      int centerY = (int) (bounds.y + (bounds.height/2));
    
      // Create the transform.
      _rotate = AffineTransform.getRotateInstance( radians, centerX, centerY );
    }
    
    super.repaint();
  }
  
  /**
   * Renders the spotlight.
   */
  public void paint( Graphics2D g2 )
  {    
    super.saveGraphicsState( g2 );
    {
      // Draw the spotlight image, rotated to match its direction.
      if ( _rotate != null )
      {
        g2.transform( _rotate );
      }
      _spotlightGraphic.paint( g2 );
    }
    super.restoreGraphicsState();
  }

}


/* end of file */