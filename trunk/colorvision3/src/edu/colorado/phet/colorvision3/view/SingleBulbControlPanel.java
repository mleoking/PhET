/* SingleBulbControlPanel.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.SingleBulbModule;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * SingleBulbControlPanel is the control panel for the "Single Bulb" simulation module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SingleBulbControlPanel extends PhetControlPanel implements ActionListener
{
  public static final int WHITE_BULB = 0;
  public static final int MONOCHROMATIC_BULB = 1;
  public static final int SOLID_BEAM = 2;
  public static final int PHOTONS_BEAM = 3;
  
  private JRadioButton _whiteRadioButton;
  private JRadioButton _monochromaticRadioButton;
  private JRadioButton _solidRadioButton;
  private JRadioButton _photonsRadioButton;
  private JCheckBox _filterCheckBox;
  private EventListenerList _listenerList;
  
  /**
   * Sole constructor.
   * 
   * @param module the module that this control panel is associated with.
   */
  public SingleBulbControlPanel( SingleBulbModule module )
  {
    super( module );
    
    _listenerList = new EventListenerList();
    
    // Bulb Control panel
    JPanel bulbPanel = new JPanel();
    {
      bulbPanel.setBorder( new TitledBorder( SimStrings.get( "BulbControl.title" ) ) );
      bulbPanel.setLayout( new BoxLayout( bulbPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _whiteRadioButton = new JRadioButton( SimStrings.get( "BulbControl.whiteRadioButton.label" ) );
      _monochromaticRadioButton = new JRadioButton( SimStrings.get( "BulbControl.monochromaticRadioButton.label" ) );
      bulbPanel.add( _whiteRadioButton );
      bulbPanel.add( _monochromaticRadioButton );

      //  roup the radio buttons for mutually-exclusive selection
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add( _whiteRadioButton );
      buttonGroup.add( _monochromaticRadioButton );
    }
    
    // Beam Control panel
    JPanel beamPanel = new JPanel();
    {
      beamPanel.setBorder( new TitledBorder( SimStrings.get( "BeamControl.title" ) ) );
      beamPanel.setLayout( new BoxLayout( beamPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _solidRadioButton = new JRadioButton( SimStrings.get( "BeamControl.solidRadioButton.label" ) );
      _photonsRadioButton = new JRadioButton( SimStrings.get( "BeamControl.photonsRadioButton.label" ) );
      beamPanel.add( _solidRadioButton );
      beamPanel.add( _photonsRadioButton );

      // Group the radio buttons for mutually-exclusive selection
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add( _solidRadioButton );
      buttonGroup.add( _photonsRadioButton );
    }
    
    // Filter Control panel
    JPanel filterPanel = new JPanel();
    {
      filterPanel.setBorder( new TitledBorder( SimStrings.get( "FilterControl.title" ) ) );
      filterPanel.setLayout( new BoxLayout( filterPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _filterCheckBox = new JCheckBox( SimStrings.get( "FilterControl.checkBox.label" ) );
      filterPanel.add( _filterCheckBox );
    }
    
    // Layout so that they fill horizontal space.
    JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( bulbPanel, BorderLayout.NORTH );
    panel.add( beamPanel, BorderLayout.CENTER );
    panel.add( filterPanel, BorderLayout.SOUTH );
    
    // Add a listener to the radio buttons.
    _whiteRadioButton.addActionListener( this );
    _monochromaticRadioButton.addActionListener( this );
    _solidRadioButton.addActionListener( this );
    _photonsRadioButton.addActionListener( this );
    _filterCheckBox.addActionListener( this );
    
    //  Set the initial state.
    _whiteRadioButton.setSelected( true );
    _solidRadioButton.setSelected( true );
    _filterCheckBox.setSelected( true );
    
    super.setControlPane( panel );
  }

  public int getBulbType()
  {
    int bulbType = MONOCHROMATIC_BULB;
    if ( _whiteRadioButton.isSelected() )
    {
      bulbType = WHITE_BULB;
    }
    return bulbType;
  }
  
  public void setBulbType( int bulbType )
  {
    if ( bulbType == WHITE_BULB )
    {
      _whiteRadioButton.setSelected( true );
    }
    else
    {
      _monochromaticRadioButton.setSelected( true );
    }
  }
  
  public int getBeamType()
  {
    int beamType = SOLID_BEAM;
    if ( _photonsRadioButton.isSelected() )
    {
      beamType= PHOTONS_BEAM;
    }
    return beamType;
  }
  
  public void setBeamType( int beamType )
  {
    if ( beamType == SOLID_BEAM )
    {
      _solidRadioButton.setSelected( true );
    }
    else
    {
      _photonsRadioButton.setSelected( true );
    }
  }
  
  public boolean getFilterEnabled()
  {
    return _filterCheckBox.isSelected();
  }
  
  public void setFilterEnabled( boolean enabled )
  {
    _filterCheckBox.setSelected( enabled );
  }
  
  /**
   * Handles control panel events.
   * Propogates them as ChangeEvents.
   */
  public void actionPerformed( ActionEvent event )
  {
    fireChangeEvent( new ChangeEvent(this) ); 
  }
  
  /**
   * Adds a ChangeListener.
   * 
   * @param listener the listener to add
   */
  public void addChangeListener( ChangeListener listener )
  {
    _listenerList.add( ChangeListener.class, listener );
  }
  
  /**
   * Removes a ChangeListener.
   * 
   * @param listener the listener to remove
   */
  public void removeChangeListener( ChangeListener listener )
  {
    _listenerList.remove( ChangeListener.class, listener );
  }
  
  /**
   * Fires a ChangeEvent whenever a control is changed.
   * 
   * @param event the event
   */
  private void fireChangeEvent( ChangeEvent event )
  {
    Object[] listeners = _listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i+=2 )
    {
      if ( listeners[i] == ChangeListener.class )
      {
        ((ChangeListener)listeners[i+1]).stateChanged( event );
      }
    }
  }
  
}

/* end of file */