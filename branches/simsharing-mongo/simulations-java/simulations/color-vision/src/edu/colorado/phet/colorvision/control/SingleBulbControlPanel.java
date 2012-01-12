// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision.ColorVisionConstants;
import edu.colorado.phet.colorvision.ColorVisionStrings;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * SingleBulbControlPanel is the control panel for the "Single Bulb" simulation module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SingleBulbControlPanel extends ControlPanel implements ActionListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /** Bulb type of "White" */
    public static final int WHITE_BULB = 0;
    /** Bulb type of "Monochrome" */
    public static final int MONOCHROMATIC_BULB = 1;
    /** Beam type of "Photons" */
    public static final int PHOTON_BEAM = 2;
    /** Beam type of "Solid" */
    public static final int SOLID_BEAM = 3;

    //----------------------------------------------------------------------------
    // Instance data
    //---------------------------------------------------------------------------

    // UI components
    private JRadioButton _whiteRadioButton;
    private JRadioButton _monochromaticRadioButton;
    private JRadioButton _photonsRadioButton;
    private JRadioButton _solidRadioButton;

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
    public SingleBulbControlPanel() {

        super();

        _listenerList = new EventListenerList();

        // Filler, to constrain panel width.
        JPanel fillerPanel = new JPanel();
        fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
        fillerPanel.add( Box.createHorizontalStrut( ColorVisionConstants.CONTROL_PANEL_MIN_WIDTH ) );

        // Bulb Control panel
        JPanel bulbPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( ColorVisionStrings.BULB_TYPE_TITLE );
            Font font = new PhetFont( PhetFont.getDefaultFontSize() + 4 );
            border.setTitleFont( font );

            bulbPanel.setBorder( border );
            bulbPanel.setLayout( new BoxLayout( bulbPanel, BoxLayout.Y_AXIS ) );

            // Radio buttons
            _whiteRadioButton = new JRadioButton( ColorVisionStrings.BULB_TYPE_WHITE );
            _monochromaticRadioButton = new JRadioButton( ColorVisionStrings.BULB_TYPE_MONOCHROMATIC );
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
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( ColorVisionStrings.BEAM_TYPE_TITLE );
            Font font = new PhetFont( PhetFont.getDefaultFontSize() + 4 );
            border.setTitleFont( font );

            beamPanel.setBorder( border );
            beamPanel.setLayout( new BoxLayout( beamPanel, BoxLayout.Y_AXIS ) );

            // Radio buttons
            _photonsRadioButton = new JRadioButton( ColorVisionStrings.BEAM_TYPE_PHOTONS );
            _solidRadioButton = new JRadioButton( ColorVisionStrings.BEAM_TYPE_SOLID );
            beamPanel.add( _photonsRadioButton );
            beamPanel.add( _solidRadioButton );

            // Group the radio buttons for mutually-exclusive selection
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _photonsRadioButton );
            buttonGroup.add( _solidRadioButton );
        }

        // Layout so that control groups fill horizontal space.
        JPanel panel = new JPanel();
        {
            BorderLayout layout = new BorderLayout();
            layout.setVgap( 20 ); // vertical space between control groups
            panel.setLayout( layout );
            panel.add( fillerPanel, BorderLayout.NORTH );
            panel.add( bulbPanel, BorderLayout.CENTER );
            panel.add( beamPanel, BorderLayout.SOUTH );
        }

        // Add a listener to the radio buttons.
        _whiteRadioButton.addActionListener( this );
        _monochromaticRadioButton.addActionListener( this );
        _solidRadioButton.addActionListener( this );
        _photonsRadioButton.addActionListener( this );

        //  Set the initial state.
        _whiteRadioButton.setSelected( true );
        _solidRadioButton.setSelected( true );

        super.addControlFullWidth( panel );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //---------------------------------------------------------------------------

    /**
     * Gets the bulb type that is currently selected.
     * 
     * @return WHITE_BULB or MONOCHROMATIC_BULB
     */
    public int getBulbType() {

        int bulbType = MONOCHROMATIC_BULB;
        if( _whiteRadioButton.isSelected() ) {
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
    public void setBulbType( int bulbType ) {

        if( bulbType == WHITE_BULB ) {
            _whiteRadioButton.setSelected( true );
        }
        else if( bulbType == MONOCHROMATIC_BULB ) {
            _monochromaticRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "invalid bulb type: " + bulbType );
        }
        fireChangeEvent( new ChangeEvent( this ) );
    }

    /**
     * Gets the beam type that is currently selected.
     * 
     * @return SOLID_BEAM or PHOTON_BEAM
     */
    public int getBeamType() {

        int beamType = SOLID_BEAM;
        if( _photonsRadioButton.isSelected() ) {
            beamType = PHOTON_BEAM;
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
    public void setBeamType( int beamType ) {

        if( beamType == SOLID_BEAM ) {
            _solidRadioButton.setSelected( true );
        }
        else if( beamType == PHOTON_BEAM ) {
            _photonsRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "invalid beam type: " + beamType );
        }
        fireChangeEvent( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------------------
    // Event handling
    //---------------------------------------------------------------------------

    /**
     * Handles a control panel button event by propogating it as a ChangeEvent.
     * 
     * @param event the event
     */
    public void actionPerformed( ActionEvent event ) {

        fireChangeEvent( new ChangeEvent( this ) );
    }

    /**
     * Adds a ChangeListener.
     * 
     * @param listener the listener to add
     */
    public void addChangeListener( ChangeListener listener ) {

        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     * 
     * @param listener the listener to remove
     */
    public void removeChangeListener( ChangeListener listener ) {

        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent whenever a control is changed.
     * 
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {

        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }

}