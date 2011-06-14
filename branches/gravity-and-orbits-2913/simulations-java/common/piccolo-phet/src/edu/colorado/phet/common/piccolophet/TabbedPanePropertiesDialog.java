// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Developer controls for the properties of PhetTabbedPane.
 * These are developer controls and are therefore not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TabbedPanePropertiesDialog extends PaintImmediateDialog {

    public TabbedPanePropertiesDialog( final Frame parent, PhetTabbedPane tabbedPane ) {
        super( parent, "Tabbed Pane properties" );
        setResizable( false );
        setModal( false );

        JPanel inputPanel = createInputPanel( parent, tabbedPane );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel( Frame parent, final PhetTabbedPane tabbedPane ) {

        // background color
        Color backgroundColor = tabbedPane.getBackground();
        final ColorControl backgroundColorControl = new ColorControl( parent, "background color: ", backgroundColor );
        backgroundColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setBackground( backgroundColorControl.getColor() );
            }
        } );

        // selected tab color
        Color selectedTabColor = tabbedPane.getSelectedTabColor();
        final ColorControl selectedTabColorControl = new ColorControl( parent, "tab color (selected): ", selectedTabColor );
        selectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setSelectedTabColor( selectedTabColorControl.getColor() );
            }
        } );

        // unselected tab color
        Color unselectedTabColor = tabbedPane.getUnselectedTabColor();
        final ColorControl unselectedTabColorControl = new ColorControl( parent, "tab color (unselected): ", unselectedTabColor );
        unselectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setUnselectedTabColor( unselectedTabColorControl.getColor() );
            }
        } );

        // selected text color
        Color selectedTextColor = tabbedPane.getSelectedTextColor();
        final ColorControl selectedTextColorControl = new ColorControl( parent, "text color (selected): ", selectedTextColor );
        selectedTextColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setSelectedTextColor( selectedTextColorControl.getColor() );
            }
        } );

        // unselected text color
        Color unselectedTextColor = tabbedPane.getUnselectedTextColor();
        final ColorControl unselectedTextColorControl = new ColorControl( parent, "text color (unselected): ", unselectedTextColor );
        unselectedTextColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setUnselectedTextColor( unselectedTextColorControl.getColor() );
            }
        } );

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( backgroundColorControl, row++, column );
        layout.addComponent( selectedTabColorControl, row++, column );
        layout.addComponent( unselectedTabColorControl, row++, column );
        layout.addComponent( selectedTextColorControl, row++, column );
        layout.addComponent( unselectedTextColorControl, row++, column );

        return panel;
    }
}
