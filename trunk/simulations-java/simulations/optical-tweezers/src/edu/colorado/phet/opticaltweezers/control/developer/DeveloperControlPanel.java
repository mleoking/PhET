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
    private VectorsDeveloperPanel _vectorsPanel;
    
    public DeveloperControlPanel( Font titleFont, Font controlFont, Frame parentFrame,
            OTClock clock,
            Bead bead, 
            Laser laser,
            DNAStrand dnaStrand,
            DNAStrandNode dnaStrandNode,
            TrapForceNode trapForceNode,
            FluidDragForceNode fluidDragForceNode,
            DNAForceNode dnaForceNode,
            LaserElectricFieldNode electricFieldNode ) {
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
        
        JPanel beadPanel = new BeadDeveloperPanel( titleFont, controlFont, clock, bead, laser );
        _panel.add( beadPanel );
        
        if ( dnaStrand != null ) {
            JPanel dnaStrandPanel = new DNAStrandDeveloperPanel( titleFont, controlFont, dnaStrand, dnaStrandNode );
            _panel.add( dnaStrandPanel );
        }
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
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
