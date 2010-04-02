/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlassControls extends JPanel {
    
    private final LinearValueControl diameterControl;
    private final JRadioButton dotsRadioButton, imagesRadioButton;
    private final JRadioButton dotsRadioButtonH2O, imagesRadioButtonH2O, solidRadioButtonH2O;
    
    public MagnifyingGlassControls() {
        setBorder( new TitledBorder( "Magnifying glass" ) );
        
        diameterControl = new LinearValueControl( 10, 500, "diameter:", "##0", "", new HorizontalLayoutStrategy() );
        
        dotsRadioButton = new JRadioButton( "dots" );
        imagesRadioButton = new JRadioButton( "images" );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( dotsRadioButton );
        buttonGroup.add( imagesRadioButton );
        
        dotsRadioButtonH2O = new JRadioButton( "dots" );
        imagesRadioButtonH2O = new JRadioButton( "images" );
        solidRadioButtonH2O = new JRadioButton( "solid" );
        ButtonGroup buttonGroupH2O = new ButtonGroup();
        buttonGroupH2O.add( dotsRadioButtonH2O );
        buttonGroupH2O.add( imagesRadioButtonH2O );
        buttonGroupH2O.add( solidRadioButtonH2O );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( diameterControl, row, column++, 5, 1 );
        row++;
        column = 0;
        layout.addAnchoredComponent( new JLabel( "particles:" ), row, column++, GridBagConstraints.EAST );
        layout.addComponent( dotsRadioButton, row, column++ );
        layout.addComponent( imagesRadioButton, row, column++ );
        row++;
        column = 0;
        layout.addAnchoredComponent( new JLabel( "<html>H<sub>2</sub>O:</html>"), row, column++, GridBagConstraints.EAST );
        layout.addComponent( dotsRadioButtonH2O, row, column++ );
        layout.addComponent( imagesRadioButtonH2O, row, column++ );
        layout.addComponent( solidRadioButtonH2O, row, column++ );
        
        // default state
        diameterControl.setValue( 100 );//XXX get state from what we're controlling
        dotsRadioButton.setSelected( true ); //XXX get state from what we're controlling
        solidRadioButtonH2O.setSelected( true ); //XXX get state from what we're controlling
    }
}
