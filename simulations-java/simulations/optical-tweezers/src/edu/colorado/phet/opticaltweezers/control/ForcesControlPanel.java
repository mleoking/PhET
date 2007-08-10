/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.umd.cs.piccolo.PNode;

/**
 * ForcesControlPanel controls the view of forces.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ForcesControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private PNode _trapForceNode;
    private PNode _dragForceNode;
    private PNode _dnaForceNode;
    
    private JCheckBox _trapForceCheckBox;
    private JCheckBox _dragForceCheckBox;
    private JCheckBox _brownianMotionCheckBox;
    private JCheckBox _dnaForceCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param bead
     * @param fluid
     * @param trapForceNode
     * @param dragForceNode
     * @param dnaForceNode optional
     */
    public ForcesControlPanel( Font titleFont, Font controlFont, Bead bead, Fluid fluid, PNode trapForceNode, PNode dragForceNode, PNode dnaForceNode ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _trapForceNode = trapForceNode;
        _dragForceNode = dragForceNode;
        _dnaForceNode = dnaForceNode;
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.forcesOnBead" ) );
        titleLabel.setFont( titleFont );
        
        _trapForceCheckBox = new JCheckBox( OTResources.getString( "label.showTrapForce" ) );
        _trapForceCheckBox.setFont( controlFont );
        _trapForceCheckBox.addActionListener( new ActionListener() {
           public void actionPerformed( ActionEvent event ) {
               handleTrapForceCheckBox();
           }
        });

        _dragForceCheckBox = new JCheckBox( OTResources.getString( "label.showDragForce" ) );
        _dragForceCheckBox.setFont( controlFont );
        _dragForceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidDragCheckBox();
            }
         });
        
        if ( _dnaForceNode != null ) {
            _dnaForceCheckBox = new JCheckBox( OTResources.getString( "label.showDNAForce" ) );
            _dnaForceCheckBox.setFont( controlFont );
            _dnaForceCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleDNAForceCheckBox();
                }
            } );
        }
        
        _brownianMotionCheckBox = new JCheckBox( OTResources.getString( "label.enableBrownianMotion" ) );
        _brownianMotionCheckBox.setFont( controlFont );
        _brownianMotionCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleBrownianMotionCheckBox();
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
        layout.addComponent( _dragForceCheckBox, row++, 0, 2, 1 );
        if ( _dnaForceCheckBox != null ) {
            layout.addComponent( _dnaForceCheckBox, row++, 0, 2, 1 );
        }
        layout.addComponent( _brownianMotionCheckBox, row++, 0, 2, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _trapForceCheckBox.setSelected( false );
        _dragForceCheckBox.setSelected( false );
        _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
        _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
        _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( false );
        }
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
        _fluid.deleteObserver( this );
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
    
    public void setDragForceSelected( boolean b ) {
        _dragForceCheckBox.setSelected( b );
        handleFluidDragCheckBox();
    }
    
    public boolean isDragForceSelected() {
        return _dragForceCheckBox.isSelected();
    }
   
    public void setBrownianMotionSelected( boolean b ) {
        _brownianMotionCheckBox.setSelected( b );
        handleBrownianMotionCheckBox();
    }
    
    public boolean isBrownianMotionSelected() {
        return _brownianMotionCheckBox.isSelected();
    }
    
    public void setDNAForceSelected( boolean b ) {
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( b );
            handleDNAForceCheckBox();
        }
    }
    
    public boolean isDNAForceSelecte() { 
        boolean selected = false;
        if ( _dnaForceCheckBox != null ) {
            selected = _dnaForceCheckBox.isSelected();
        }
        return selected;
    }
    
    /**
     * Sets visibility of the checkbox that controls Brownian motion.
     * This feature is not visible in for some control panels.
     * 
     * @param visible
     */
    public void setBrownianMotionCheckBoxVisible( boolean visible ) {
        _brownianMotionCheckBox.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceCheckBox() {
        final boolean selected = _trapForceCheckBox.isSelected();
        _trapForceNode.setVisible( selected );
    }
    
    private void handleFluidDragCheckBox() {
        final boolean selected = _dragForceCheckBox.isSelected();
        _dragForceNode.setVisible( selected );
    }
    
    private void handleBrownianMotionCheckBox() {
        final boolean selected = _brownianMotionCheckBox.isSelected();
        _bead.setBrownianMotionEnabled( selected );
    }
    
    private void handleDNAForceCheckBox() {
        final boolean selected = _dnaForceCheckBox.isSelected();
        _dnaForceNode.setVisible( selected );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_ENABLED ) {
                _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceNode.setVisible( _fluid.isEnabled() && _dragForceCheckBox.isSelected() );
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_BROWNIAN_MOTION_ENABLED ) {
                _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
            }
        }
    }
}
