/* PersonGraphic.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.Person;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * PersonGraphic is the view component that represents the person viewing color.
 * The perceived color is shown as a "thought balloon" that floats 
 * about the rendered head.  The head consists of a foreground and background
 * layer, so that light appears to fall on the face.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class PersonGraphic extends CompositePhetGraphic implements SimpleObserver
{ 
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Relative location of the head graphic.
  private static final int HEAD_X_OFFSET = 0;
  private static final int HEAD_Y_OFFSET = 120;
  // Relative location of the thought bubble graphic.
  private static final int THOUGHT_BUBBLE_X_OFFSET = 170;
  private static final int THOUGHT_BUBBLE_Y_OFFSET = 5;
   
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  private Person _personModel;
  private PhetImageGraphic _headBackgroundGraphic, _headForegroundGraphic;
  private ThoughtBubbleGraphic _thoughtBubbleGraphic;
    
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor. Note that this graphic adds its components to the 
   * apparatus, so that it can set up foreground and background elements.
   * 
   * @param apparatusPanel the apparatus panel
   * @param backgroundLayer layer for background elements
   * @param foregroundLayer layer for foreground elements
   * @param personModel the model of the person
   */
  public PersonGraphic( 
    ApparatusPanel apparatusPanel,
    double backgroundLayer,
    double foregroundLayer,
    Person personModel )
  {
    super( apparatusPanel );
    
    // Save a reference to the model.
    _personModel = personModel;
 
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

	//----------------------------------------------------------------------------
	// SimpleObserver implementation
  //----------------------------------------------------------------------------

  /**
   * Synchronizes the view with the model.
   * This method is called whenever the model changes.
   */
  public void update()
  { 
    _headBackgroundGraphic.setPosition( (int)(_personModel.getX() + HEAD_X_OFFSET),
                                        (int)(_personModel.getY() + HEAD_Y_OFFSET) );
    _headForegroundGraphic.setPosition( (int)(_personModel.getX() + HEAD_X_OFFSET),
                                        (int)(_personModel.getY() + HEAD_Y_OFFSET) );
    _thoughtBubbleGraphic.setLocation( (int)(_personModel.getX() + THOUGHT_BUBBLE_X_OFFSET), 
                                      (int)(_personModel.getY() + THOUGHT_BUBBLE_Y_OFFSET) );
    _thoughtBubbleGraphic.setFill( _personModel.getColor() );
    
    super.repaint();
  }

}

/* end of file */