/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.view.ElectricFieldVectorNode;
import edu.colorado.phet.opticaltweezers.view.LaserNode;

/**
 * LaserDisplayControlPanel controls the display of the laser's beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserDisplayControlPanel extends JPanel {

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

        // Beam
        JPanel beamPanel = null;
        {
            _beamRadioButton = new JRadioButton( OTResources.getString( "choice.beam" ) );
            _beamRadioButton.setFont( controlFont );
            _beamRadioButton.addActionListener( actionListener );

            Icon beamIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_BEAM_ICON ) );
            JLabel beamLabel = new JLabel( beamIcon );
            beamLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setDisplaySelection( true /* beam */, false /*efield */);
                }
            } );

            beamPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            beamPanel.add( _beamRadioButton );
            beamPanel.add( beamLabel );
        }

        // Electric Field
        JPanel efieldPanel = null;
        {
            _electricFieldRadioButton = new JRadioButton( OTResources.getString( "choice.electricField" ) );
            _electricFieldRadioButton.setFont( controlFont );
            _electricFieldRadioButton.addActionListener( actionListener );
            
            Icon electricFieldIcon = ElectricFieldVectorNode.createIcon();
            JLabel eletricFieldLabel = new JLabel( electricFieldIcon );
            eletricFieldLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setDisplaySelection( false /* beam */, true /*efield */ );
                }
            } );
            
            efieldPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            efieldPanel.add( _electricFieldRadioButton );
            efieldPanel.add( eletricFieldLabel );
        }

        // Both
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
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( beamPanel, row++, 0 );
        layout.addComponent( efieldPanel, row++, 0 );
        layout.addComponent( _beamAndElectricFieldRadioButton, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _beamRadioButton.setSelected( true );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setDisplaySelection( boolean beamVisible, boolean electricFieldVisible ) {
        _beamRadioButton.setSelected( beamVisible && !electricFieldVisible );
        _electricFieldRadioButton.setSelected( !beamVisible && electricFieldVisible );
        _beamAndElectricFieldRadioButton.setSelected( beamVisible && electricFieldVisible );
        handleDisplayChoice();
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
     * Sets the laser display to match the controls.
     */
    private void handleDisplayChoice() {
        _laserNode.setBeamVisible( _beamRadioButton.isSelected() || _beamAndElectricFieldRadioButton.isSelected() );
        _laserNode.setElectricFieldVisible( _electricFieldRadioButton.isSelected() || _beamAndElectricFieldRadioButton.isSelected() );
    }
}
