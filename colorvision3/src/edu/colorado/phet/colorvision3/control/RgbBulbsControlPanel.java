/* RgbBulbsControlPanel.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetControlPanel;

/**
 * RgbBulbsControlPanel is the control panel for the "RGB Bulbs" simulation module.
 * This control panel currently has no controls, but does contain the default PhET logo
 * graphic and Help buttons.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class RgbBulbsControlPanel extends PhetControlPanel
{
  /**
   * Sole constructor.
   * 
   * @param module the module that this control panel is associated with.
   */
  public RgbBulbsControlPanel( Module module )
  {
    super( module );
    
    // WORKAROUND: PhetControlPanel doesn't display anything unless we give it a dummy control pane.
    JPanel fillerPanel = new JPanel();
    fillerPanel.setLayout( new BoxLayout(fillerPanel, BoxLayout.X_AXIS) );
    fillerPanel.add( Box.createHorizontalStrut(ColorVisionConfig.CONTROL_PANEL_MIN_WIDTH) );
    
    this.setControlPane( fillerPanel );
  }

}

/* end of file */