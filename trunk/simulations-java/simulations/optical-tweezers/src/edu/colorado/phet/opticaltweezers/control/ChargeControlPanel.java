/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.PNode;

/**
 * ChargeControlPanel controls the view of charge on a bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String CHOICE_HIDDEN = "hidden";
    public static final String CHOICE_DISTRIBUTION = "distribution";
    public static final String CHOICE_EXCESS = "excess";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _chargeDistributionNode, _chargeExcessNode;
    
    private JRadioButton _hiddenRadioButton;
    private JRadioButton _distributionRadioButton;
    private JRadioButton _excessRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     */
    public ChargeControlPanel( Font titleFont, Font controlFont, PNode chargeDistributionNode, PNode chargeExcessNode ) {
        super();
        
        _chargeDistributionNode = chargeDistributionNode;
        _chargeExcessNode = chargeExcessNode;

        JLabel titleLabel = new JLabel( OTResources.getString( "title.chargeControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleChargeChoice();
            }
        };

        _hiddenRadioButton = new JRadioButton( OTResources.getString( "choice.noCharge" ) );
        _hiddenRadioButton.setFont( controlFont );
        _hiddenRadioButton.addActionListener( actionListener );

        _distributionRadioButton = new JRadioButton( OTResources.getString( "choice.chargeDistribution" ) );
        _distributionRadioButton.setFont( controlFont );
        _distributionRadioButton.addActionListener( actionListener );

        _excessRadioButton = new JRadioButton( OTResources.getString( "choice.chargeExcess" ) );
        _excessRadioButton.setFont( controlFont );
        _excessRadioButton.addActionListener( actionListener );

        ButtonGroup bg = new ButtonGroup();
        bg.add( _hiddenRadioButton );
        bg.add( _distributionRadioButton );
        bg.add( _excessRadioButton );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _hiddenRadioButton, row++, 0 );
        layout.addComponent( _distributionRadioButton, row++, 0 );
        layout.addComponent( _excessRadioButton, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _hiddenRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setChoice( String choice ) {
        if ( choice.equals( CHOICE_HIDDEN ) ) {
            _hiddenRadioButton.setSelected( true );
        }
        else if ( choice.equals( CHOICE_DISTRIBUTION ) ) {
            _distributionRadioButton.setSelected( true );
        }
        else if ( choice.equals( CHOICE_EXCESS ) ) {
            _excessRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "unsupported choice: " + choice );
        }
        handleChargeChoice();
    }
    
    public Object getChoice() {
        String choice = null;
        if ( _hiddenRadioButton.isSelected() ) {
            choice = CHOICE_HIDDEN;
        }
        else if ( _distributionRadioButton.isSelected() ) {
            choice = CHOICE_DISTRIBUTION;
        }
        else if ( _excessRadioButton.isSelected() ) {
            choice = CHOICE_EXCESS;
        }
        assert( choice != null );
        return choice;
    }
    
    public boolean isHiddenSelected() {
        return _hiddenRadioButton.isSelected();
    }
    
    public boolean isDistributionSelected() {
        return _distributionRadioButton.isSelected();
    }
    
    public boolean isExcessSelected() {
        return _excessRadioButton.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Sets the view of bead charge to match the controls.
     */
    private void handleChargeChoice() {
        _chargeDistributionNode.setVisible( isDistributionSelected() );
        _chargeExcessNode.setVisible( isExcessSelected() );
    }
}
