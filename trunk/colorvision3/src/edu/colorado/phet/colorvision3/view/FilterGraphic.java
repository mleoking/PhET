/* FilterGraphic.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * FilterGraphic is the UI component that represents a filter.
 * It is a Shape that is described using constructive area geometry.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class FilterGraphic extends PhetGraphic implements SimpleObserver
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Height of the filter lens.
  private static final int LENS_HEIGHT = 150;
  
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // The filter model
  private Filter _filterModel;
  // Shapes for various parts of the filter.
  private Shape _exterior, _interior, _lens;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param parent the parent Component
   * @param filterModel the filter model
   */
  public FilterGraphic( Component parent, Filter filterModel )
  {
    super( parent );
    _filterModel = filterModel;
    update();
  }
 
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------
 
  /**
   * Determines the bounds.
   * 
   * @return the bounding rectangle
   */
  protected Rectangle determineBounds()
  {
    Rectangle bounds = null;
    if ( _exterior != null )
    {
      bounds = _exterior.getBounds();
    }
    return bounds;
  }

	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Updates the view by consulting the model.
   * This is called each time the filter model changes.
   */
  public void update()
  {
    int x = (int)_filterModel.getX();
    int y = (int)_filterModel.getY();
    int height = 150;
    
    // Use constructive area geomety to create the exterior shape.
    Area area = new Area();
    {
      Ellipse2D.Double e1 = new Ellipse2D.Double( x, y, 20, LENS_HEIGHT );
      Rectangle2D.Double r1 = new Rectangle2D.Double( x+10, y, 10, LENS_HEIGHT );
      Ellipse2D.Double e2 = new Ellipse2D.Double( x+10, y, 20, LENS_HEIGHT );
      Rectangle2D.Double base = new Rectangle2D.Double( x+10, y+LENS_HEIGHT, 10, 20 );
      area.add( new Area(e1) );
      area.add( new Area(e2) );
      area.add( new Area(r1) );
      area.add( new Area(base) );
    }
    
    _exterior = area;
    _interior = new Ellipse2D.Double( x, y, 20, LENS_HEIGHT );
    _lens = new Ellipse2D.Double( x+4, y+4, 12, LENS_HEIGHT-8 );
    
    super.repaint();
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Draws the filter, based on the current state of the filter model.
   * 
   * @param g2 graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() && _filterModel.isEnabled() )
    {
      super.saveGraphicsState( g2 );
      {
        // Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHints( hints );
        
        // Draw the exterior
        g2.setPaint( Color.DARK_GRAY );
        g2.fill( _exterior );
        
        // Draw the interior
        g2.setPaint( Color.GRAY );
        g2.fill( _interior );
        
        // Draw the filter
        g2.setPaint( _filterModel.getTransmissionPeak() );
        g2.fill( _lens );
      }
      super.restoreGraphicsState();
      
      BoundsOutline.paint( g2, this ); // DEBUG
    }
  }

}


/* end of file */