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

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.H2ORepresentation;
import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
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
    private final JRadioButton dotsRadioButtonH2O, imagesRadioButtonH2O, solidColorRadioButtonH2O;
    
    public MagnifyingGlassControls( final MagnifyingGlass magnifyingGlass ) {
        setBorder( new TitledBorder( "Magnifying glass" ) );
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        diameterControl = new LinearValueControl( 10, 500, "diameter:", "##0", "", new HorizontalLayoutStrategy() );
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
        
        dotsRadioButtonH2O = new JRadioButton( "dots" );
        dotsRadioButtonH2O.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( dotsRadioButtonH2O.isSelected() ) {
                    magnifyingGlass.setH2ORepresentation( H2ORepresentation.DOTS );
                }
            }
        });
        imagesRadioButtonH2O = new JRadioButton( "images" );
        imagesRadioButtonH2O.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( imagesRadioButtonH2O.isSelected() ) {
                    magnifyingGlass.setH2ORepresentation( H2ORepresentation.IMAGES );
                }
            }
        });
        solidColorRadioButtonH2O = new JRadioButton( "solid color" );
        solidColorRadioButtonH2O.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( solidColorRadioButtonH2O.isSelected() ) {
                    magnifyingGlass.setH2ORepresentation( H2ORepresentation.SOLID_COLOR );
                }
            }
        });
        ButtonGroup buttonGroupH2O = new ButtonGroup();
        buttonGroupH2O.add( dotsRadioButtonH2O );
        buttonGroupH2O.add( imagesRadioButtonH2O );
        buttonGroupH2O.add( solidColorRadioButtonH2O );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( diameterControl, row, column++, 5, 1 );
        row++;
        column = 0;
        layout.addAnchoredComponent( new JLabel( "molecules:" ), row, column++, GridBagConstraints.EAST );
        layout.addComponent( dotsRadioButton, row, column++ );
        layout.addComponent( imagesRadioButton, row, column++ );
        row++;
        column = 0;
        layout.addAnchoredComponent( new JLabel( "<html>H<sub>2</sub>O:</html>"), row, column++, GridBagConstraints.EAST );
        layout.addComponent( dotsRadioButtonH2O, row, column++ );
        layout.addComponent( imagesRadioButtonH2O, row, column++ );
        layout.addComponent( solidColorRadioButtonH2O, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        diameterControl.setValue( magnifyingGlass.getDiameter() );
        dotsRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imagesRadioButton.setSelected( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
        dotsRadioButtonH2O.setSelected( magnifyingGlass.getH2ORepresentation() == H2ORepresentation.DOTS );
        imagesRadioButtonH2O.setSelected( magnifyingGlass.getH2ORepresentation() == H2ORepresentation.IMAGES );
        solidColorRadioButtonH2O.setSelected( magnifyingGlass.getH2ORepresentation() == H2ORepresentation.SOLID_COLOR );
    }
}
