/* SpotlightGraphic.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * SpotlightGraphic is the view component for a 2D spotlight.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SpotlightGraphic extends PhetImageGraphic implements SimpleObserver
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
    super( apparatus, ColorVisionConfig.SPOTLIGHT_IMAGE );
    
    // Save a reference to the model.
    _spotlightModel = spotlightModel;
    
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
    BufferedImage image = super.getImage();
   
    // Location (translation)
    int x = (int)( _spotlightModel.getX() - image.getWidth() + BULB_OFFSET );
    int y = (int)( _spotlightModel.getY() - (image.getHeight()/2) );

    // Direction (rotation about the center of the bulb within the spotlight)
    double radians = Math.toRadians( _spotlightModel.getDirection() );
    int centerX = (int) (image.getWidth() - BULB_OFFSET);
    int centerY = (int) (image.getHeight()/2);
    
    // Apply translation + rotation transforms
    AffineTransform transform = new AffineTransform();
    transform.translate( 0, 0 );
    transform.translate( x, y );
    transform.rotate( radians, centerX, centerY );
    super.setTransform( transform );
    
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
      super.paint( g2 );
      BoundsOutline.paint( g2, this ); // DEBUG
    }
  }

}


/* end of file */