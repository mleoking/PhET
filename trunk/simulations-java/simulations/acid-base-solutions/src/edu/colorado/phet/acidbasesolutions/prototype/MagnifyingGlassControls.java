/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlassControls extends JPanel {
    
    private final MagnifyingGlass magnifyingGlass;
    private final LinearValueControl diameterControl;
    private final JRadioButton dotsRadioButton, imagesRadioButton;
    private final JCheckBox showH2OCheckBox;
    
    public MagnifyingGlassControls( final MagnifyingGlass magnifyingGlass ) {
        setBorder( new TitledBorder( "Magnifying glass" ) );
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        IntegerRange diameterRange = ProtoConstants.MAGNIFYING_GLASS_DIAMETER_RANGE;
        diameterControl = new LinearValueControl( diameterRange.getMin(), diameterRange.getMax(), "diameter:", "##0", "", new HorizontalLayoutStrategy() );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                magnifyingGlass.setDiameter( (int)diameterControl.getValue() );
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
        representationPanel.add( new JLabel( "show molecules as:" ) );
        representationPanel.add( dotsRadioButton );
        representationPanel.add( imagesRadioButton );
        
        showH2OCheckBox = new JCheckBox( "<html>show H<sub>2</sub>O molecules</html>" );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( diameterControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( representationPanel, row, column++ );
        row++;
        column = 0;
        layout.addComponent( showH2OCheckBox, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        diameterControl.setValue( magnifyingGlass.getDiameter() );
        dotsRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imagesRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
        showH2OCheckBox.setSelected( magnifyingGlass.getShowH2O() );
    }
}
