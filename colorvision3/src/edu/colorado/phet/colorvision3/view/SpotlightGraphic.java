/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

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
 * @version $Revision$
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
  // Last known location of the model.
  private double _x, _y;
  // Last known direction of the model.
  private double _direction;
    
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
    double x = _spotlightModel.getX();
    double y = _spotlightModel.getY();
    double direction = _spotlightModel.getDirection();
    
    // If something we're displaying has changed...
    if ( x != _x || _y != y || _direction != direction )
    {
      BufferedImage image = super.getImage();
      int width = image.getWidth();
      int height = image.getHeight();
      
      // Location (translation)
      _x = (int)( x - width + BULB_OFFSET );
      _y = (int)( y - (height/2) );

      // Direction (rotation about the center of the bulb within the spotlight)
      _direction = direction;
      double radians = Math.toRadians( direction );
      int centerX = (int) (width - BULB_OFFSET);
      int centerY = (int) (height/2);
    
      // Apply translation + rotation transforms
      AffineTransform transform = new AffineTransform();
      transform.translate( 0, 0 );
      transform.translate( _x, _y );
      transform.rotate( radians, centerX, centerY );
      super.setTransform( transform );
      
      super.repaint();
    }
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