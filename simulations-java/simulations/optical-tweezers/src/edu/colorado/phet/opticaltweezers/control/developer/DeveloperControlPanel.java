/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.view.DNAStrandNode;

/**
 * DeveloperControlPanel contains developer controls.
 * This panel is for developers only, and it not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends JPanel {

    private static final String SHOW_STRING = "Developer Controls >>";
    private static final String HIDE_STRING = "Developer Controls <<";
    
    private JButton _showHideButton;
    private Box _panel;
    private VectorsDeveloperPanel _vectorsPanel;
    
    public DeveloperControlPanel( Font titleFont, Font controlFont, Frame parentFrame, Bead bead, DNAStrand dnaStrand, DNAStrandNode dnaStrandNode, List forceVectorNodes ) {
        super();
        
        _showHideButton = new JButton();
        _showHideButton.setFont( titleFont );
        _showHideButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowHideButton();
            }
        } );
        
        _panel = new Box( BoxLayout.Y_AXIS );
        
        _vectorsPanel = new VectorsDeveloperPanel( titleFont, controlFont, forceVectorNodes );
        _panel.add( _vectorsPanel );
        
        JPanel beadMotionPanel = new BeadMotionDeveloperPanel( titleFont, controlFont, bead );
        _panel.add( beadMotionPanel );
        
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
