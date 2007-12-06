/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersResources;


public class ViewControlPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color( 82, 126, 90 ); // green
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color CONTROL_COLOR = Color.WHITE;

    private JCheckBox _equilibriumLineCheckBox;
    private JCheckBox _iceFlowCheckBox;
    private JCheckBox _snowfallCheckBox;
    private JCheckBox _coordinatesCheckBox;
    private JCheckBox _ageOfIceCheckBox;
    
    public ViewControlPanel( Font titleFont, Font controlFont ) {
        super();
        
        _equilibriumLineCheckBox = new JCheckBox( GlaciersResources.getString( "view.checkBox.equilibriumLine" ) );
        _equilibriumLineCheckBox.setFont( controlFont );
        _equilibriumLineCheckBox.setForeground( CONTROL_COLOR );
        
        _iceFlowCheckBox = new JCheckBox( GlaciersResources.getString( "view.checkBox.iceFlow" ) );
        _iceFlowCheckBox.setFont( controlFont );
        _iceFlowCheckBox.setForeground( CONTROL_COLOR );
        
        _snowfallCheckBox = new JCheckBox( GlaciersResources.getString( "view.checkBox.snowfall" ) );
        _snowfallCheckBox.setFont( controlFont );
        _snowfallCheckBox.setForeground( CONTROL_COLOR );
        
        _coordinatesCheckBox = new JCheckBox( GlaciersResources.getString( "view.checkBox.coordinates" ) );
        _coordinatesCheckBox.setFont( controlFont );
        _coordinatesCheckBox.setForeground( CONTROL_COLOR );
        
        _ageOfIceCheckBox = new JCheckBox( GlaciersResources.getString( "view.checkBox.ageOfIce" ) );
        _ageOfIceCheckBox.setFont( controlFont );
        _ageOfIceCheckBox.setForeground( CONTROL_COLOR );
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersResources.getString( "view.title" ) );
        titledBorder.setTitleFont( titleFont );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _equilibriumLineCheckBox, row++, column );
        layout.addComponent( _iceFlowCheckBox, row++, column );
        layout.addComponent( _snowfallCheckBox, row++, column );
        layout.addComponent( _coordinatesCheckBox, row++, column );
        layout.addComponent( _ageOfIceCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* excludedClasses */, false /* processContentsOfExcludedContainers */ );
    }
}
