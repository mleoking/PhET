/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.view.*;

/**
 * DeveloperControlPanel contains developer controls.
 * This panel is for developers only, and it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends JPanel {

    private static final String SHOW_STRING = "Developer Controls >>";
    private static final String HIDE_STRING = "Developer Controls <<";
    
    private JButton _showHideButton;
    private Box _panel;
    private BeadDeveloperPanel _beadPanel;
    private VectorsDeveloperPanel _vectorsPanel;
    
    public DeveloperControlPanel( Font titleFont, Font controlFont, Frame parentFrame,
            OTClock clock,
            Bead bead, 
            Bead invisibleBead,
            Laser laser,
            DNAStrand dnaStrandBead,
            DNAStrandNode dnaStrandBeadNode,
            DNAStrand dnaStrandFree,
            DNAStrandNode dnaStrandFreeNode,
            TrapForceNode trapForceNode,
            FluidDragForceNode fluidDragForceNode,
            DNAForceNode dnaForceNode,
            ElectricFieldNode electricFieldNode,
            ChargeDistributionNode chargeDistributionNode,
            boolean showVacuumControls ) {
        super();
        
        _showHideButton = new JButton();
        _showHideButton.setFont( titleFont );
        _showHideButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowHideButton();
            }
        } );
        
        _panel = new Box( BoxLayout.Y_AXIS );
        
        _vectorsPanel = new VectorsDeveloperPanel( titleFont, controlFont, trapForceNode, fluidDragForceNode, dnaForceNode, electricFieldNode );
        _panel.add( _vectorsPanel );
        
        JPanel laserPanel = new LaserDeveloperPanel( titleFont, controlFont, laser );
        _panel.add( laserPanel );
        
        _beadPanel = new BeadDeveloperPanel( "Bead", titleFont, controlFont, clock, bead, laser, chargeDistributionNode, showVacuumControls );
        _panel.add( _beadPanel );
        
        if ( invisibleBead != null ) {
            JPanel invisibleBeadPanel = new BeadDeveloperPanel( "Bead (invisible, DNA free end)", titleFont, controlFont, clock, 
                    invisibleBead, null /* laser */, null /* chargeDistributionNode */, showVacuumControls );
            _panel.add( invisibleBeadPanel );
        }
        
        if ( dnaStrandBead != null ) {
            JPanel dnaStrandPanel1 = new DNAStrandDeveloperPanel( "DNA strand (bead attached)", titleFont, controlFont, dnaStrandBead, dnaStrandBeadNode );
            _panel.add( dnaStrandPanel1 );
        }
        
        if ( dnaStrandFree != null ) {
            JPanel dnaStrandPanel2 = new DNAStrandDeveloperPanel( "DNA strand (free end)", titleFont, controlFont, dnaStrandFree, dnaStrandFreeNode );
            _panel.add( dnaStrandPanel2 );
        }
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        layout.setMinimumWidth( 0, 0 );
        int row = 0;
        layout.addComponent( _showHideButton, row++, 1 );
        layout.addComponent( _panel, row++, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _showHideButton.setText( SHOW_STRING );
        _panel.setVisible( false );
    }
    
    public void setAdvancedVisible( boolean b ) {
        if ( b ^ _panel.isVisible() ) {
            handleShowHideButton();
        }
    }
    
    public boolean isAdvancedVisible() {
        return _showHideButton.isVisible();
    }
    
    public VectorsDeveloperPanel getVectorsPanel() {
        return _vectorsPanel;
    }
    
    public BeadDeveloperPanel getBeadPanel() {
        return _beadPanel;
    }
    
    private void handleShowHideButton() {
        _panel.setVisible( !_panel.isVisible() );
        if ( _panel.isVisible() ) {
            _showHideButton.setText( HIDE_STRING );
        }
        else {
            _showHideButton.setText( SHOW_STRING );
        }
    }
}
