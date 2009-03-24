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
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.AbstractEnzyme;
import edu.colorado.phet.opticaltweezers.view.EnzymeANode;
import edu.colorado.phet.opticaltweezers.view.EnzymeBNode;

/**
 * EnzymeControlPanel contains controls related to enzymes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeControlPanel extends JPanel implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ENZYME_ICON_HEIGHT = 30; // pixels

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractEnzyme _enzymeA;
    private AbstractEnzyme _enzymeB;
    
    private JRadioButton _enzymeARadioButton;
    private JRadioButton _enzymeBRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnzymeControlPanel( Font titleFont, Font controlFont, EnzymeANode enzymeANode, EnzymeBNode enzymeBNode ) {
        super();
        
        _enzymeA = enzymeANode.getEnzyme();
        _enzymeA.addObserver( this );
        
        _enzymeB = enzymeBNode.getEnzyme();
        _enzymeB.addObserver( this );
        
        JLabel titleLabel = new JLabel( OTResources.getString( "title.enzymeControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        JPanel enzymePanel = null;
        {
            // Enzyme A
            JPanel enzymeAPanel = null;
            {
                _enzymeARadioButton = new JRadioButton();
                _enzymeARadioButton.setFont( controlFont );
                _enzymeARadioButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        handleEnzymeChoice();
                    }
                } );
                
                Icon enzymeAIcon = enzymeANode.createIcon( ENZYME_ICON_HEIGHT );
                JLabel enzymeALabel = new JLabel( enzymeAIcon );
                enzymeALabel.addMouseListener( new MouseAdapter() {
                    public void mouseReleased( MouseEvent event ) {
                        setEnzymeASelected( true );
                    }
                });
                
                enzymeAPanel = new JPanel(new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                enzymeAPanel.add( _enzymeARadioButton );
                enzymeAPanel.add( enzymeALabel );
            }

            // Enzyme B
            JPanel enzymeBPanel = null;
            {
                _enzymeBRadioButton = new JRadioButton();
                _enzymeBRadioButton.setFont( controlFont );
                _enzymeBRadioButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        handleEnzymeChoice();
                    }
                } );
                
                Icon enzymeBIcon = enzymeBNode.createIcon( ENZYME_ICON_HEIGHT );
                JLabel enzymeBLabel = new JLabel( enzymeBIcon );
                enzymeBLabel.addMouseListener( new MouseAdapter() {
                    public void mouseReleased( MouseEvent event ) {
                        setEnzymeBSelected( true );
                    }
                });
                
                enzymeBPanel = new JPanel(new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                enzymeBPanel.add( _enzymeBRadioButton );
                enzymeBPanel.add( enzymeBLabel );
            }

            ButtonGroup bg = new ButtonGroup();
            bg.add( _enzymeARadioButton );
            bg.add( _enzymeBRadioButton );
            
            enzymePanel = new JPanel(new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            enzymePanel.add( enzymeAPanel );
            enzymePanel.add( Box.createHorizontalStrut( 20 ) );
            enzymePanel.add( enzymeBPanel );
        }
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.NONE );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( enzymePanel, row++, column );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _enzymeARadioButton.setSelected( _enzymeA.isEnabled() );
        _enzymeBRadioButton.setSelected( _enzymeB.isEnabled() );
        handleEnzymeChoice();
    }
    
    public void cleanup() {
        _enzymeA.deleteObserver( this );
        _enzymeB.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEnzymeASelected( boolean b ) {
        _enzymeARadioButton.setSelected( b );
        handleEnzymeChoice();
    }
    
    public boolean isEnzymeASelected() {
        return _enzymeARadioButton.isSelected();
    }
    
    public void setEnzymeBSelected( boolean b ) {
        _enzymeBRadioButton.setSelected( b );
        handleEnzymeChoice();
    }
    
    public boolean isEnzymeBSelected() {
        return _enzymeBRadioButton.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleEnzymeChoice() {
        boolean enzymeASelected = _enzymeARadioButton.isSelected();
        // enable one of the 2 enzyme models
        _enzymeA.deleteObserver( this );
        _enzymeB.deleteObserver( this );
        _enzymeA.setEnabled( enzymeASelected );
        _enzymeB.setEnabled( !enzymeASelected );
        _enzymeA.addObserver( this );
        _enzymeB.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _enzymeA || o == _enzymeB ) {
            if ( arg == AbstractEnzyme.PROPERTY_ENABLED ) {
                // this is a little weird... at least one must be enabled since these are radio buttons
                _enzymeARadioButton.setSelected( _enzymeA.isEnabled() );
                _enzymeBRadioButton.setSelected( _enzymeB.isEnabled() );
            }
        }
    }
}
