/* PersonGraphic.java */
package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.Person2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

/**
 * PersonGraphic is the view component that represents the person viewing color.
 * The perceived color is shown as a "thought balloon" that floats 
 * about the rendered head. This view component is a 2D composite graphic.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class PersonGraphic extends CompositePhetGraphic implements SimpleObserver
{ 
  // Coordinates relative to upper-left.
  private static final int HEAD_X_OFFSET = 0;
  private static final int HEAD_Y_OFFSET = 120;
  private static final int THOUGHT_BUBBLE_X_OFFSET = 170;
  private static final int THOUGHT_BUBBLE_Y_OFFSET = 5;
    
  private Person2D _model;
  private PhetImageGraphic _headGraphic;
  private ThoughtBubbleGraphic _thoughtBubbleGraphic;
    
  /**
   * Sole constructor.
   * 
   * @param apparatusPanel the apparatus panel
   * @param model the model of the person
   */
  public PersonGraphic( ApparatusPanel apparatusPanel, Person2D model )
  {
    super( apparatusPanel );
    
    // Save a reference to the model.
    _model = model;
    
    // Read the images.
    BufferedImage headBufferedImage;
    try {
      headBufferedImage = ImageLoader.loadBufferedImage( ColorVisionConfig.HEAD_IMAGE );
    }
    catch( IOException e ) {
      e.printStackTrace();
      throw new RuntimeException( "Image file not found" );
    }
   
    // Head graphic
    _headGraphic = new PhetImageGraphic( apparatusPanel, headBufferedImage );
    this.addGraphic( _headGraphic );
           
    // Thought Bubble graphic
    _thoughtBubbleGraphic = new ThoughtBubbleGraphic( apparatusPanel );
    this.addGraphic( _thoughtBubbleGraphic );
    
    //  Sync the view with the model.
    update();
  }
  
  /**
   * Sets the location of the associated model.
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void setLocation( int x, int y )
  {
    _model.setLocation( x, y );
  }

  /**
   * Sets the color for the associated model.
   * 
   * @param color
   */
  public void setColor( Color color )
  {
    _model.setColor( color );
  }

  /**
   * Synchronizes the view with the model.
   * This method is called whenever the model changes.
   */
  public void update()
  { 
    _headGraphic.setPosition( (int)(_model.getX() + HEAD_X_OFFSET),
                              (int)(_model.getY() + HEAD_Y_OFFSET) );
    _thoughtBubbleGraphic.setPosition( (int)(_model.getX() + THOUGHT_BUBBLE_X_OFFSET), 
                                      (int)(_model.getY() + THOUGHT_BUBBLE_Y_OFFSET) );
    _thoughtBubbleGraphic.setFill( _model.getColor() );   
    
    super.repaint();
  }

}

/* end of file */