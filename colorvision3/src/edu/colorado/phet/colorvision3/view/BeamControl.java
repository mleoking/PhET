/* BeamControlPanel.java */

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
 * BeamControl is the user interface component that is used to control the type
 * of beam that is animated. The beam can be animated as either "solid" or
 * "photons".
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class BeamControl extends JPanel implements ActionListener
{
  private SingleBulbModule _module;
  private JRadioButton _solidRadioButton;
  private JRadioButton _photonsRadioButton;

  /**
   * Sole constructor.
   * 
   * @param module the simulation module that this control is associated with
   */
  public BeamControl( final SingleBulbModule module )
  {
    _module = module;

    this.setBorder( new TitledBorder( SimStrings.get( "BeamControl.title" ) ) );
    this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

    // Radio buttons
    _solidRadioButton = new JRadioButton( SimStrings.get( "BeamControl.solidRadioButton.label" ) );
    _photonsRadioButton = new JRadioButton( SimStrings.get( "BeamControl.photonsRadioButton.label" ) );
    this.add( _solidRadioButton );
    this.add( _photonsRadioButton );

    // Group the radio buttons for mutually-exclusive selection
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( _solidRadioButton );
    buttonGroup.add( _photonsRadioButton );

    // Add a listener to the radio buttons
    _solidRadioButton.addActionListener( this );
    _photonsRadioButton.addActionListener( this );

    // Set the initial state.
    _solidRadioButton.setSelected( true );
    _module.setBeamType( SingleBulbModule.BEAM_TYPE_SOLID );
  }

  /**
   * Handler for radio button actions. Tells the module to adjust the beam type.
   * 
   * @param event the event
   */
  public void actionPerformed( ActionEvent event )
  {
    if ( _solidRadioButton.isSelected() )
    {
      _module.setBeamType( SingleBulbModule.BEAM_TYPE_SOLID );
    }
    else if ( _photonsRadioButton.isSelected() )
    {
      _module.setBeamType( SingleBulbModule.BEAM_TYPE_PHOTONS );
    }
  }

}

/* end of file */