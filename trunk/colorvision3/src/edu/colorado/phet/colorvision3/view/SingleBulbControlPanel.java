/* SingleBulbControlPanel.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import edu.colorado.phet.colorvision3.SingleBulbModule;
import edu.colorado.phet.common.view.PhetControlPanel;

/**
 * SingleBulbControlPanel is the control panel for the "Single Bulb" simulation module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SingleBulbControlPanel extends PhetControlPanel
{
  /**
   * Sole constructor.
   * 
   * @param module the module that this control panel is associated with.
   */
  public SingleBulbControlPanel( SingleBulbModule module )
  {
    super( module );
    
    // Create the controls that are part of this panel.
    BulbControl bulbControl = new BulbControl( module );
    BeamControl beamControl = new BeamControl( module );
    FilterControl filterControl = new FilterControl( module );
    
    // Layout so that they fill horizontal space.
    JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( bulbControl, BorderLayout.NORTH );
    panel.add( beamControl, BorderLayout.CENTER );
    panel.add( filterControl, BorderLayout.SOUTH );
        
    this.setControlPane( panel );
  }
  
}

/* end of file */