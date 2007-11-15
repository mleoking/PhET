/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
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
    private JCheckBox _iceVelocitiesCheckBox;
    private JCheckBox _debrisCheckBox;
    private JCheckBox _snowFallCheckBox;
    private JCheckBox _coordinatesCheckBox;
    private JCheckBox _viewFollowsTerminusCheckBox;
    private JCheckBox _massBalanceCheckBox;
    private JCheckBox _terminusTrackerCheckBox;
    private JCheckBox _rulerCheckBox;
    private JCheckBox _boreholdDrillCheckBox;
    
    public static class CheckBoxIcon extends JLabel {
        public CheckBoxIcon( Icon icon, final JCheckBox associatedCheckBox ) {
            super( icon );
            addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent e ) {
                    associatedCheckBox.setSelected( !associatedCheckBox.isSelected() );
                }
            });
        }
    }
    
    public ViewControlPanel( Font titleFont, Font controlFont ) {
        super();
        
        _equilibriumLineCheckBox = new JCheckBox( GlaciersResources.getString( "label.equilibriumLine" ) );
        _equilibriumLineCheckBox.setFont( controlFont );
        _equilibriumLineCheckBox.setForeground( CONTROL_COLOR );
        
        _iceVelocitiesCheckBox = new JCheckBox( GlaciersResources.getString( "label.iceVelocities" ) );
        _iceVelocitiesCheckBox.setFont( controlFont );
        _iceVelocitiesCheckBox.setForeground( CONTROL_COLOR );
        
        _debrisCheckBox = new JCheckBox( GlaciersResources.getString( "label.debris" ) );
        _debrisCheckBox.setFont( controlFont );
        _debrisCheckBox.setForeground( CONTROL_COLOR );
        
        _snowFallCheckBox = new JCheckBox( GlaciersResources.getString( "label.snowfall" ) );
        _snowFallCheckBox.setFont( controlFont );
        _snowFallCheckBox.setForeground( CONTROL_COLOR );
        
        _coordinatesCheckBox = new JCheckBox( GlaciersResources.getString( "label.coordinates" ) );
        _coordinatesCheckBox.setFont( controlFont );
        _coordinatesCheckBox.setForeground( CONTROL_COLOR );
        
        _viewFollowsTerminusCheckBox = new JCheckBox( GlaciersResources.getString( "label.viewFollowsTerminus" ) );
        _viewFollowsTerminusCheckBox.setFont( controlFont );
        _viewFollowsTerminusCheckBox.setForeground( CONTROL_COLOR );
        
        _massBalanceCheckBox = new JCheckBox( GlaciersResources.getString( "label.massBalance" ) );
        _massBalanceCheckBox.setFont( controlFont );
        _massBalanceCheckBox.setForeground( CONTROL_COLOR );
        
        _terminusTrackerCheckBox = new JCheckBox( GlaciersResources.getString( "label.terminusTracker" ) );
        _terminusTrackerCheckBox.setFont( controlFont );
        _terminusTrackerCheckBox.setForeground( CONTROL_COLOR );
        
        _rulerCheckBox = new JCheckBox( GlaciersResources.getString( "label.ruler" ) );
        _rulerCheckBox.setFont( controlFont );
        _rulerCheckBox.setForeground( CONTROL_COLOR );
        
        _boreholdDrillCheckBox = new JCheckBox( GlaciersResources.getString( "label.boreholeDrill" ) );
        _boreholdDrillCheckBox.setFont( controlFont );
        _boreholdDrillCheckBox.setForeground( CONTROL_COLOR );
        
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 1);
        TitledBorder titledBorder = new TitledBorder( GlaciersResources.getString( "title.view" ) );
        titledBorder.setTitleFont( titleFont );
        titledBorder.setTitleColor( TITLE_COLOR );
        Border compoundBorder = BorderFactory.createCompoundBorder( lineBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( _equilibriumLineCheckBox, row++, column );
        layout.addComponent( _iceVelocitiesCheckBox, row++, column );
        layout.addComponent( _debrisCheckBox, row++, column );
        layout.addComponent( _snowFallCheckBox, row++, column );
        layout.addComponent( _coordinatesCheckBox, row++, column );
        row = 0;
        column++;
        JSeparator separator = new JSeparator( SwingConstants.VERTICAL );
        separator.setForeground( CONTROL_COLOR );
        layout.addFilledComponent( separator, row++, column, 1, 5, GridBagConstraints.VERTICAL );
        row = 0;
        column++;
        layout.addComponent( _viewFollowsTerminusCheckBox, row++, column );
        layout.addComponent( _massBalanceCheckBox, row++, column );
        layout.addComponent( _terminusTrackerCheckBox, row++, column );
        layout.addComponent( _rulerCheckBox, row++, column );
        layout.addComponent( _boreholdDrillCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* excludedClasses */, false /* processContentsOfExcludedContainers */ );
    }
}
