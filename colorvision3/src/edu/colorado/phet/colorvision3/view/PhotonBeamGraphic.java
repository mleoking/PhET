/* PhotonBeamGraphic.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

import edu.colorado.phet.colorvision3.model.Photon;
import edu.colorado.phet.colorvision3.model.PhotonBeam;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * PhotonBeamGraphic provides a view of a PhotonBeam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class PhotonBeamGraphic extends PhetGraphic implements SimpleObserver
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Photon line length, for rendering.
  public static final int PHOTON_LINE_LENGTH = 3;
  // Stroke used from drawing photons
  private static Stroke PHOTON_STROKE = new BasicStroke( 1f );

	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  private PhotonBeam _photonBeamModel;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param parent the parent Component
   * @param photonBeamModel the photon beam model
   */
  public PhotonBeamGraphic( Component parent, PhotonBeam photonBeamModel )
  {
    super( parent );
    _photonBeamModel = photonBeamModel;
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /*
   * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#determineBounds()
   */
  protected Rectangle determineBounds()
  {
    return _photonBeamModel.getBounds();
  }

	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Updates the view to match the model.
   */
  public void update()
  {
    super.repaint();  
  }

	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Draws the photon beam.
   * 
   * @param g2 graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( isVisible() && _photonBeamModel.isEnabled() )
    {
      super.saveGraphicsState( g2 );
      {
        // Use the same stroke for all photons.
        g2.setStroke( PHOTON_STROKE );
        
        Photon photon = null;
        int x, y, w, h;
        VisibleColor vc;
        
        // For each photon ...
        ArrayList photons = _photonBeamModel.getPhotons();
        for ( int i = 0; i < photons.size(); i++ )
        {
          photon = (Photon) photons.get(i);
          
          // If the photon is in use, render it.
          if ( photon.isInUse() )
          {
            x = (int) photon.getX();
            y = (int) photon.getY();
            w = (int) photon.getWidth();
            h = (int) photon.getHeight();
            vc = photon.getColor();
            
            // WORKAROUND: Huge performance improvement by converting VisibleColor to Color.
            g2.setPaint( vc.toColor() ); 
            g2.drawLine( x, y, x-w, y-h );
          }
        }
      }
      super.restoreGraphicsState();
      
      BoundsOutline.paint( g2, this, Color.YELLOW ); // DEBUG
    }
  } // paint

}


/* end of file */