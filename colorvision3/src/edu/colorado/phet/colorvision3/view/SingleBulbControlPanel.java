/* SingleBulbControlPanel.java, Copyright 2004 University of Colorado */

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
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  /** Bulb type of "White" */
  public static final int WHITE_BULB = 0;
  /** Bulb type of "Monochrome" */
  public static final int MONOCHROMATIC_BULB = 1;
  /** Beam type of "Solid" */
  public static final int SOLID_BEAM = 2;
  /** Beam type of "Photons" */
  public static final int PHOTON_BEAM = 3;
  
	//----------------------------------------------------------------------------
	// Instance data
  //---------------------------------------------------------------------------
  
  // UI components
  private JRadioButton _whiteRadioButton;
  private JRadioButton _monochromaticRadioButton;
  private JRadioButton _solidRadioButton;
  private JRadioButton _photonsRadioButton;
  private JCheckBox _filterCheckBox;
  
  // Event listeners
  private EventListenerList _listenerList;
  
	//----------------------------------------------------------------------------
	// Constructors
  //---------------------------------------------------------------------------

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
      bulbPanel.setBorder( new TitledBorder( SimStrings.get( "bulbType.title" ) ) );
      bulbPanel.setLayout( new BoxLayout( bulbPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _whiteRadioButton = new JRadioButton( SimStrings.get( "bulbType.white" ) );
      _monochromaticRadioButton = new JRadioButton( SimStrings.get( "bulbType.monochromatic" ) );
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
      beamPanel.setBorder( new TitledBorder( SimStrings.get( "beamType.title" ) ) );
      beamPanel.setLayout( new BoxLayout( beamPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _solidRadioButton = new JRadioButton( SimStrings.get( "beamType.solid" ) );
      _photonsRadioButton = new JRadioButton( SimStrings.get( "beamType.photons" ) );
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
      filterPanel.setBorder( new TitledBorder( SimStrings.get( "filter.title" ) ) );
      filterPanel.setLayout( new BoxLayout( filterPanel, BoxLayout.Y_AXIS ) );

      // Radio buttons
      _filterCheckBox = new JCheckBox( SimStrings.get( "filter.checkBox" ) );
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

	//----------------------------------------------------------------------------
	// Accessors
  //---------------------------------------------------------------------------

  /**
   * Gets the bulb type that is currently selected.
   * 
   * @return WHITE_BULB or MONOCHROMATIC_BULB
   */
  public int getBulbType()
  {
    int bulbType = MONOCHROMATIC_BULB;
    if ( _whiteRadioButton.isSelected() )
    {
      bulbType = WHITE_BULB;
    }
    return bulbType;
  }
  
  /**
   * Sets the bulb type.
   * Registered listeners receive a ChangeEvent.
   * 
   * @param bulbType the bulb type, WHITE_BULB or MONOCHROMATIC_BULB
   * @throws IllegalArgumentException if bulbType is invalid
   */
  public void setBulbType( int bulbType )
  {
    if ( bulbType == WHITE_BULB )
    {
      _whiteRadioButton.setSelected( true );
    }
    else if ( bulbType == MONOCHROMATIC_BULB )
    {
      _monochromaticRadioButton.setSelected( true );
    }
    else
    {
      throw new IllegalArgumentException( "invalid bulb type: " + bulbType );
    }
    fireChangeEvent( new ChangeEvent(this) );
  }
  
  /**
   * Gets the beam type that is currently selected.
   * 
   * @return SOLID_BEAM or PHOTON_BEAM
   */
  public int getBeamType()
  {
    int beamType = SOLID_BEAM;
    if ( _photonsRadioButton.isSelected() )
    {
      beamType= PHOTON_BEAM;
    }
    return beamType;
  }
  
  /**
   * Sets the beam type.
   * Registered listeners receive a ChangeEvent.
   * 
   * @param beamType the beam type, SOLID_BEAM or PHOTON_BEAM
   * @throws IllegalArgumentException if beamType is invalid
   */
  public void setBeamType( int beamType )
  {
    if ( beamType == SOLID_BEAM )
    {
      _solidRadioButton.setSelected( true );
    }
    else if ( beamType == PHOTON_BEAM )
    {
      _photonsRadioButton.setSelected( true );
    }
    else
    {
      throw new IllegalArgumentException( "invalid beam type: " + beamType );
    }
    fireChangeEvent( new ChangeEvent(this) );
  }
  
  /**
   * Gets the state of the filter checkbox.
   * 
   * @return true or false
   */
  public boolean getFilterEnabled()
  {
    return _filterCheckBox.isSelected();
  }
  
  /**
   * Sets the state of the filter checkbox.
   * Registered listeners receive a ChangeEvent.
   * 
   * @param enabled true or false
   */
  public void setFilterEnabled( boolean enabled )
  {
    _filterCheckBox.setSelected( enabled );
    fireChangeEvent( new ChangeEvent(this) );
  }
  
	//----------------------------------------------------------------------------
	// Event handling
  //---------------------------------------------------------------------------

  /**
   * Handles a control panel button event by propogating it as a ChangeEvent.
   * 
   * @param event the event
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