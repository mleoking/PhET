/* SpotlightGraphic.java, Copyright 2004 University of Colorado PhET */

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
 * SpotlightGraphic is the view component for a 2D spotlight.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SpotlightGraphic extends CompositePhetGraphic implements SimpleObserver
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // How far the bulb is inside the spotlight, in pixels.
  private static final int BULB_OFFSET = 130;
 
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // The model that describes the spotlight.
  private Spotlight _spotlightModel;
  // The graphic used to visually depict the spotlight.
  private PhetImageGraphic _spotlightGraphic;
  // Rotate transformed used in rendering.
  private AffineTransform _rotate;
    
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param apparatus the apparatus panel
   * @param spotlightModel the associated spotlight model
   */
  public SpotlightGraphic( ApparatusPanel apparatus, Spotlight spotlightModel )
  {
    super( apparatus );
    
    // Save a reference to the model.
    _spotlightModel = spotlightModel;
   
    // Spotlight graphic
    _spotlightGraphic = new PhetImageGraphic( apparatus, ColorVisionConfig.SPOTLIGHT_IMAGE );
    this.addGraphic( _spotlightGraphic );
    
    // Sync the view with the model.
    update();
  }

	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Synchronizes the view with the model.
   * This method is called whenever the model changes.
   */
  public void update()
  {
    // Location
    {
      Rectangle spotBounds = _spotlightGraphic.getBounds();
      int x = (int)( _spotlightModel.getX() - spotBounds.width + BULB_OFFSET );
      int y = (int)( _spotlightModel.getY() - (spotBounds.height/2) );
      _spotlightGraphic.setPosition( x, y );
    }

    // Direction.
    {
      // Convert to radians.
      double radians = Math.toRadians( _spotlightModel.getDirection() );
    
      // Rotate about the right-center edge of the spotlight image.
      Rectangle bounds = _spotlightGraphic.getBounds();
      int centerX = (int) (bounds.x + (bounds.width) - BULB_OFFSET);
      int centerY = (int) (bounds.y + (bounds.height/2));
    
      // Create the transform.
      _rotate = AffineTransform.getRotateInstance( radians, centerX, centerY );
    }
    
    super.repaint();
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Renders the spotlight.
   * 
   * @param g2 the graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( isVisible() )
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

}


/* end of file */