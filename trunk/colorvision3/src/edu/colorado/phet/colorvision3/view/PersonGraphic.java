/* PersonGraphic.java */
package edu.colorado.phet.colorvision3.view;

import java.awt.Color;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.Person2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

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
  private PhetImageGraphic _headBackgroundGraphic, _headForegroundGraphic;
  private ThoughtBubbleGraphic _thoughtBubbleGraphic;
    
  /**
   * Sole constructor.
   * 
   * @param apparatusPanel the apparatus panel
   * @param backgroundLayer layer for background elements
   * @param foregroundLayer layer for foreground elements
   * @param model the model of the person
   */
  public PersonGraphic( 
    ApparatusPanel apparatusPanel,
    double backgroundLayer,
    double foregroundLayer,
    Person2D model )
  {
    super( apparatusPanel );
    
    // Save a reference to the model.
    _model = model;
 
    // Head background graphic
    _headBackgroundGraphic = new PhetImageGraphic( apparatusPanel, ColorVisionConfig.HEAD_BACKGROUND_IMAGE );
    this.addGraphic( _headBackgroundGraphic );
    
    // Head foreground graphic
    _headForegroundGraphic = new PhetImageGraphic( apparatusPanel, ColorVisionConfig.HEAD_FOREGROUND_IMAGE );
    this.addGraphic( _headForegroundGraphic );
           
    // Thought Bubble graphic
    _thoughtBubbleGraphic = new ThoughtBubbleGraphic( apparatusPanel );
    this.addGraphic( _thoughtBubbleGraphic );
    
    // Add to the apparatus panel in correct layers.
    apparatusPanel.addGraphic( _headBackgroundGraphic, backgroundLayer );
    apparatusPanel.addGraphic( _headForegroundGraphic, foregroundLayer );
    apparatusPanel.addGraphic( _thoughtBubbleGraphic, foregroundLayer );
    
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
    _headBackgroundGraphic.setPosition( (int)(_model.getX() + HEAD_X_OFFSET),
                                        (int)(_model.getY() + HEAD_Y_OFFSET) );
    _headForegroundGraphic.setPosition( (int)(_model.getX() + HEAD_X_OFFSET),
                                        (int)(_model.getY() + HEAD_Y_OFFSET) );
    _thoughtBubbleGraphic.setPosition( (int)(_model.getX() + THOUGHT_BUBBLE_X_OFFSET), 
                                      (int)(_model.getY() + THOUGHT_BUBBLE_Y_OFFSET) );
    _thoughtBubbleGraphic.setFill( _model.getColor() );   
    
    super.repaint();
  }

}

/* end of file */