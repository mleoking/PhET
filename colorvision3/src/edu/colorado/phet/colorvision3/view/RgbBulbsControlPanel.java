/* RgbBulbsControlPanel.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

import javax.swing.JPanel;

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
    JPanel panel = new JPanel();
    this.setControlPane( panel );
  }

}

/* end of file */