/* FilterControl.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.colorvision3.SingleBulbModule;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * FilterControl is the user interface component that turns the filter 
 * on and off.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class FilterControl extends JPanel implements ActionListener
{
  private SingleBulbModule _module;
  private JCheckBox _checkBox;

  /**
   * Sole constructor.
   * 
   * @param module the simulation module that this control is associated with
   */
  public FilterControl( final SingleBulbModule module )
  {
    _module = module;

    this.setBorder( new TitledBorder( SimStrings.get( "FilterControl.title" ) ) );
    this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

    // Radio buttons
    _checkBox = new JCheckBox( SimStrings.get( "FilterControl.checkBox.label" ) );
    this.add( _checkBox );

    // Add a listener to the radio buttons.
    _checkBox.addActionListener( this );

    //  Set the initial state.
    _checkBox.setSelected( true );
    _module.setFilterEnabled( true );
  }

  /**
   * Handler for radio button actions. Tells the module to turn the filter
   * on/off.
   * 
   * @param event the event
   */
  public void actionPerformed( ActionEvent event )
  {
    _module.setFilterEnabled( _checkBox.isSelected() );
  }
}

/* end of file */