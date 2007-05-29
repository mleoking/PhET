/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.PNode;


public class ForcesControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final Object CHOICE_WHOLE_BEAD = "wholeBead";
    public static final Object CHOICE_HALF_BEAD = "halfBead";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _trapForceNode;
    private PNode _dragForceNode;
    private PNode _brownianForceNode;
    
    private JCheckBox _trapForceCheckBox;
    private JLabel _horizontalTrapForceLabel;
    private JRadioButton _wholeBeadRadioButton;
    private JRadioButton _halfBeadRadioButton;
    private JCheckBox _dragForceCheckBox;
    private JCheckBox _brownianForceCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param trapForceNode
     * @param dragForceNode
     * @param brownianForceNode
     */
    public ForcesControlPanel( Font titleFont, Font controlFont, PNode trapForceNode, PNode dragForceNode, PNode brownianForceNode ) {
        super();
        
        _trapForceNode = trapForceNode;
        _dragForceNode = dragForceNode;
        _brownianForceNode = brownianForceNode;
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.forcesOnBead" ) );
        titleLabel.setFont( titleFont );
        
        _trapForceCheckBox = new JCheckBox( OTResources.getString( "label.showTrapForce" ) );
        _trapForceCheckBox.setFont( controlFont );
        _trapForceCheckBox.addActionListener( new ActionListener() {
           public void actionPerformed( ActionEvent event ) {
               handleTrapForceCheckBox();
           }
        });

        _horizontalTrapForceLabel = new JLabel( OTResources.getString( "label.horizontalTrapForce" ) );
        _horizontalTrapForceLabel.setFont( controlFont );
        
        _wholeBeadRadioButton = new JRadioButton( OTResources.getString( "choice.wholeBead" ) );
        _wholeBeadRadioButton.setFont( controlFont );
        _wholeBeadRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleWholeBeadRadioButton();
            }
         });
        
        _halfBeadRadioButton = new JRadioButton( OTResources.getString( "choice.halfBead" ) );
        _halfBeadRadioButton.setFont( controlFont );
        _halfBeadRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleHalfBeadRadioButton();
            }
         });
        
        ButtonGroup bg = new ButtonGroup();
        bg.add( _wholeBeadRadioButton );
        bg.add( _halfBeadRadioButton );
        
        _dragForceCheckBox = new JCheckBox( OTResources.getString( "label.showDragForce" ) );
        _dragForceCheckBox.setFont( controlFont );
        _dragForceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidDragCheckBox();
            }
         });
        
        _brownianForceCheckBox = new JCheckBox( OTResources.getString( "label.showBrownianForce" ) );
        _brownianForceCheckBox.setFont( controlFont );
        _brownianForceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleBrownianForceCheckBox();
            }
         });
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0, 2, 1 );
        layout.addComponent( _trapForceCheckBox, row++, 0, 2, 1 );
        layout.addComponent( _horizontalTrapForceLabel, row++, 1 );
        layout.addComponent( _wholeBeadRadioButton, row++, 1 );
        layout.addComponent( _halfBeadRadioButton, row++, 1 );
        layout.addComponent( _dragForceCheckBox, row++, 0, 2, 1 );
        layout.addComponent( _brownianForceCheckBox, row++, 0, 2, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _trapForceCheckBox.setSelected( false );
        _horizontalTrapForceLabel.setEnabled( _trapForceCheckBox.isSelected() );
        _wholeBeadRadioButton.setSelected( true );
        _wholeBeadRadioButton.setEnabled( _trapForceCheckBox.isSelected() );
        _halfBeadRadioButton.setSelected( false );
        _halfBeadRadioButton.setEnabled( _trapForceCheckBox.isSelected() );
        _dragForceCheckBox.setSelected( false );
        _brownianForceCheckBox.setSelected( false );
        
        //XXX not implemented
        _horizontalTrapForceLabel.setForeground( Color.RED );
        _wholeBeadRadioButton.setForeground( Color.RED );
        _halfBeadRadioButton.setForeground( Color.RED );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setTrapForceSelected( boolean b ) {
        _trapForceCheckBox.setSelected( b );
        handleTrapForceCheckBox();
    }
    
    public boolean isTrapForceSelected() {
        return _trapForceCheckBox.isSelected();
    }
    
    public void setHorizontalTrapForceChoice( Object choice ) {
        if ( choice == CHOICE_WHOLE_BEAD ) {
            _wholeBeadRadioButton.setSelected( true );
        }
        else if ( choice == CHOICE_HALF_BEAD ) {
            _halfBeadRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "unsupported choice: " + choice );
        }
    }
    
    public boolean isWholeBeadSelected() {
        return _wholeBeadRadioButton.isSelected();
    }
    
    public boolean isHalfBeadSelected() {
        return _halfBeadRadioButton.isSelected();
    }
   
    public void setDragForceSelected( boolean b ) {
        _dragForceCheckBox.setSelected( b );
        handleFluidDragCheckBox();
    }
    
    public boolean isDragForceSelected() {
        return _dragForceCheckBox.isSelected();
    }
   
    public void setBrownianForceSelected( boolean b ) {
        _brownianForceCheckBox.setSelected( b );
        handleBrownianForceCheckBox();
    }
    
    public boolean isBrownianForceSelected() {
        return _brownianForceCheckBox.isSelected();
    }
    
    public void setHorizontalTrapForceControlsEnabled( boolean enabled ) {
        _horizontalTrapForceLabel.setEnabled( enabled );
        _wholeBeadRadioButton.setEnabled( enabled );
        _halfBeadRadioButton.setEnabled( enabled );
    }
    
    public boolean isHorizontalTrapForceControlsEnabled() {
        return _horizontalTrapForceLabel.isEnabled();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceCheckBox() {
        final boolean selected = _trapForceCheckBox.isSelected();
        // related controls
        _horizontalTrapForceLabel.setEnabled( selected );
        _wholeBeadRadioButton.setEnabled( selected );
        _halfBeadRadioButton.setEnabled( selected );
        // update view
        _trapForceNode.setVisible( selected );
    }
    
    private void handleWholeBeadRadioButton() {
        //XXX
    }
    
    private void handleHalfBeadRadioButton() {
        //XXX
    }
    
    private void handleFluidDragCheckBox() {
        final boolean selected = _dragForceCheckBox.isSelected();
        _dragForceNode.setVisible( selected );
    }
    
    private void handleBrownianForceCheckBox() {
        final boolean selected = _brownianForceCheckBox.isSelected();
        _brownianForceNode.setVisible( selected );
    }
}
