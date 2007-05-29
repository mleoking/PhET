/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.view.node.LaserNode;

/**
 * LaserDisplayControlPanel controls the display of the laser's beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserDisplayControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String CHOICE_BEAM = "beam";
    public static final String CHOICE_ELECTRIC_FIELD = "electricField";
    public static final String CHOICE_BEAM_AND_ELECTRIC_FIELD = "beamAndElectricField";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private LaserNode _laserNode;
    
    private JRadioButton _beamRadioButton;
    private JRadioButton _electricFieldRadioButton;
    private JRadioButton _beamAndElectricFieldRadioButton;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     */
    public LaserDisplayControlPanel( Font titleFont, Font controlFont, LaserNode laserNode ) {
        super();
        
        _laserNode = laserNode;

        JLabel titleLabel = new JLabel( OTResources.getString( "title.laserDisplayControlPanel" ) );
        titleLabel.setFont( titleFont );

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDisplayChoice();
            }
        };

        // "no charts" choice
        _beamRadioButton = new JRadioButton( OTResources.getString( "choice.beam" ) );
        _beamRadioButton.setFont( controlFont );
        _beamRadioButton.addActionListener( actionListener );

        // Position Histogram
        _electricFieldRadioButton = new JRadioButton( OTResources.getString( "choice.electricField" ) );
        _electricFieldRadioButton.setFont( controlFont );
        _electricFieldRadioButton.addActionListener( actionListener );

        // Potential Energy chart
        _beamAndElectricFieldRadioButton = new JRadioButton( OTResources.getString( "choice.beamAndElectricField" ) );
        _beamAndElectricFieldRadioButton.setFont( controlFont );
        _beamAndElectricFieldRadioButton.addActionListener( actionListener );

        ButtonGroup bg = new ButtonGroup();
        bg.add( _beamRadioButton );
        bg.add( _electricFieldRadioButton );
        bg.add( _beamAndElectricFieldRadioButton );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _beamRadioButton, row++, 0 );
        layout.addComponent( _electricFieldRadioButton, row++, 0 );
        layout.addComponent( _beamAndElectricFieldRadioButton, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _beamRadioButton.setSelected( true );

        //XXX not implemented
        _electricFieldRadioButton.setForeground( Color.RED );
        _beamAndElectricFieldRadioButton.setForeground( Color.RED );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setChoice( String choice ) {
        if ( choice.equals( CHOICE_BEAM ) ) {
            _beamRadioButton.setSelected( true );
        }
        else if ( choice.equals( CHOICE_ELECTRIC_FIELD ) ) {
            _electricFieldRadioButton.setSelected( true );
        }
        else if ( choice.equals( CHOICE_BEAM_AND_ELECTRIC_FIELD ) ) {
            _beamAndElectricFieldRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "unsupported choice: " + choice );
        }
        handleDisplayChoice();
    }
    
    public String getChoice() {
        String choice = null;
        if ( _beamRadioButton.isSelected() ) {
            choice = CHOICE_BEAM;
        }
        else if ( _electricFieldRadioButton.isSelected() ) {
            choice = CHOICE_ELECTRIC_FIELD;
        }
        else if ( _beamAndElectricFieldRadioButton.isSelected() ) {
            choice = CHOICE_BEAM_AND_ELECTRIC_FIELD;
        }
        assert( choice != null );
        return choice;
    }
    
    public boolean isBeamSelected() {
        return _beamRadioButton.isSelected();
    }

    public boolean isElectricFieldSelected() {
        return _electricFieldRadioButton.isSelected();
    }

    public boolean isBeamAndElectricFieldSelected() {
        return _beamAndElectricFieldRadioButton.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    /*
     * Sets the view of laser to match the controls.
     */
    private void handleDisplayChoice() {
        //XXX
    }
}
