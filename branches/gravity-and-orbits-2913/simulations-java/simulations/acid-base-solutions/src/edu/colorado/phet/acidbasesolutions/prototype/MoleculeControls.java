// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * General controls for molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MoleculeControls extends JPanel {
    
    private final MagnifyingGlassNode magnifyingGlassNode;
    
    private final MagnifyingGlass magnifyingGlass;
    private final JRadioButton imagesRadioButton, dotsRadioButton;
    private final JCheckBox showH2OCheckBox;
    
    public MoleculeControls( final MagnifyingGlass magnifyingGlass, final MagnifyingGlassNode magnifyingGlassNode ) {
        setBorder( new TitledBorder( "Molecules" ) );
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        this.magnifyingGlassNode = magnifyingGlassNode;
        ChangeListener magnifyingGlassNodeChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        };
        magnifyingGlassNode.getImagesNode().addChangeListener( magnifyingGlassNodeChangeListener );
        magnifyingGlassNode.getDotsNode().addChangeListener( magnifyingGlassNodeChangeListener );
        
        imagesRadioButton = new JRadioButton( "images" );
        imagesRadioButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( imagesRadioButton.isSelected() ) {
                    magnifyingGlass.setMoleculeRepresentation( MoleculeRepresentation.IMAGES );
                }
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
       
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( imagesRadioButton );
        buttonGroup.add( dotsRadioButton );
        
        JPanel representationPanel = new JPanel();
        representationPanel.add( new JLabel( "representation:" ) );
        representationPanel.add( imagesRadioButton );
        representationPanel.add( dotsRadioButton );
        
        // show H2O checkbox
        String label = HTMLUtils.toHTMLString( "show " + MGPConstants.H2O_FRAGMENT + " molecules" );
        showH2OCheckBox = new JCheckBox( label );
        showH2OCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                magnifyingGlassNode.getImagesNode().setH2OVisible( showH2OCheckBox.isSelected() );
                magnifyingGlassNode.getDotsNode().setH2OVisible( showH2OCheckBox.isSelected() );
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( representationPanel, row++, column );
        layout.addComponent( showH2OCheckBox, row, column );

        // default state
        updateControls();
    }
    
    private void updateControls() {
        imagesRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
        dotsRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        showH2OCheckBox.setSelected( magnifyingGlassNode.getImagesNode().isH2OVisible() );
    }
}
