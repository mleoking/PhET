/* BulbControlPanel.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.colorvision3.SingleBulbModule;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BulbControl is the user interface component that is used to control the type
 * of bulb. The bulb can be either "white" or "monochromatic".
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class BulbControl extends JPanel implements ActionListener
{
  private SingleBulbModule _module;
  private JRadioButton _whiteRadioButton;
  private JRadioButton _monochromaticRadioButton;

  /**
   * Sole constructor.
   * 
   * @param module the simulation module that this control is associated with
   */
  public BulbControl( final SingleBulbModule module )
  {
    _module = module;

    this.setBorder( new TitledBorder( SimStrings.get( "BulbControl.title" ) ) );
    this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

    // Radio buttons
    _whiteRadioButton = new JRadioButton( SimStrings.get( "BulbControl.whiteRadioButton.label" ) );
    _monochromaticRadioButton = new JRadioButton( SimStrings.get( "BulbControl.monochromaticRadioButton.label" ) );
    this.add( _whiteRadioButton );
    this.add( _monochromaticRadioButton );

    //  Group the radio buttons for mutually-exclusive selection
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( _whiteRadioButton );
    buttonGroup.add( _monochromaticRadioButton );

    // Add a listener to the radio buttons.
    _whiteRadioButton.addActionListener( this );
    _monochromaticRadioButton.addActionListener( this );

    //  Set the initial state.
    _whiteRadioButton.setSelected( true );
    _module.setBulbType( SingleBulbModule.BULB_TYPE_WHITE );
  }

  /**
   * Handler for radio button actions. Tells the module to adjust the bulb type.
   * 
   * @param event the event
   */
  public void actionPerformed( ActionEvent event )
  {
    if ( _whiteRadioButton.isSelected() )
    {
      _module.setBulbType( SingleBulbModule.BULB_TYPE_WHITE );
    }
    else if ( _monochromaticRadioButton.isSelected() )
    {
      _module.setBulbType( SingleBulbModule.BULB_TYPE_MONOCHROMATIC );
    }
  }

}

/* end of file */