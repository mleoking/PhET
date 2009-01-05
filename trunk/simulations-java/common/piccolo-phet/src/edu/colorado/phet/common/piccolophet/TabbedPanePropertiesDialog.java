/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.piccolophet;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TabPropertiesDialog is a dialog that contains developer controls for 
 * the properties of PhetTabbedPane.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TabbedPanePropertiesDialog extends JDialog {

    public TabbedPanePropertiesDialog( Frame owner, PhetTabbedPane tabbedPane ) {
        super( owner, "Tabbed Pane properties" );
        setResizable( false );
        setModal( false );
        
        JPanel inputPanel = createInputPanel( tabbedPane );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel( final PhetTabbedPane tabbedPane ) {

        Frame parentFrame = PhetApplication.instance().getPhetFrame();

        // background color
        Color backgroundColor = tabbedPane.getBackground();
        final ColorControl backgroundColorControl = new ColorControl( parentFrame, "background color: ", backgroundColor );
        backgroundColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setBackground( backgroundColorControl.getColor() );
            }
        } );

        // selected tab color
        Color selectedTabColor = tabbedPane.getSelectedTabColor();
        final ColorControl selectedTabColorControl = new ColorControl( parentFrame, "tab color (selected): ", selectedTabColor );
        selectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setSelectedTabColor( selectedTabColorControl.getColor() );
            }
        } );
        
        // unselected tab color
        Color unselectedTabColor = tabbedPane.getUnselectedTabColor();
        final ColorControl unselectedTabColorControl = new ColorControl( parentFrame, "tab color (unselected): ", unselectedTabColor );
        unselectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setUnselectedTabColor( unselectedTabColorControl.getColor() );
            }
        } );

        // selected text color
        Color selectedTextColor = tabbedPane.getSelectedTextColor();
        final ColorControl selectedTextColorControl = new ColorControl( parentFrame, "text color (selected): ", selectedTextColor );
        selectedTextColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                tabbedPane.setSelectedTextColor( selectedTextColorControl.getColor() );
            }
        } );
        
        // unselected text color
        Color unselectedTextColor = tabbedPane.getUnselectedTextColor();
        final ColorControl unselectedTextColorControl = new ColorControl( parentFrame, "text color (unselected): ", unselectedTextColor );
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
