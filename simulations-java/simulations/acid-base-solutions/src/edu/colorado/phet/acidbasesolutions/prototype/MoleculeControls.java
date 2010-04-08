/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * General controls for molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MoleculeControls extends JPanel {
    
    private final MagnifyingGlass magnifyingGlass;
    private final JRadioButton dotsRadioButton, imagesRadioButton;
    
    public MoleculeControls( final MagnifyingGlass magnifyingGlass ) {
        setBorder( new TitledBorder( "Molecules" ) );
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        dotsRadioButton = new JRadioButton( "dots" );
        dotsRadioButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( dotsRadioButton.isSelected() ) {
                    magnifyingGlass.setMoleculeRepresentation( MoleculeRepresentation.DOTS );
                }
            }
        });
        imagesRadioButton = new JRadioButton( "images" );
        imagesRadioButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( imagesRadioButton.isSelected() ) {
                    magnifyingGlass.setMoleculeRepresentation( MoleculeRepresentation.IMAGES );
                }
            }
        });
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( dotsRadioButton );
        buttonGroup.add( imagesRadioButton );
        
        JPanel representationPanel = new JPanel();
        representationPanel.add( new JLabel( "representation:" ) );
        representationPanel.add( dotsRadioButton );
        representationPanel.add( imagesRadioButton );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( representationPanel, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        dotsRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imagesRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
    }
}
