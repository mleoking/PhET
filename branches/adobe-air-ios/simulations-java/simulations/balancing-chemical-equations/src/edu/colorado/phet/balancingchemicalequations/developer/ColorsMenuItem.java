// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Developer menu item for miscellaneous colors.
 * This menu item opens a dialog box with color chips for each color that can be modified.
 * No i18n is required.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorsMenuItem extends JCheckBoxMenuItem {

    private final BCEGlobalProperties globalProperties;
    private DeveloperColorsDialog dialog;

    public ColorsMenuItem( final BCEGlobalProperties globalProperties ) {
        super( "Colors..." );
        this.globalProperties = globalProperties;

        // the menu item was changed
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleColorsDialog();
            }
        } );

        // update the dialog if the canvas color changes
        globalProperties.canvasColor.addObserver( new SimpleObserver() {
            public void update() {
                if ( dialog != null ) {
                    dialog.setCanvasColor( globalProperties.canvasColor.get() );
                }
            }
        } );

        // update the dialog if the box color changes
        globalProperties.boxColor.addObserver( new SimpleObserver() {
            public void update() {
                if ( dialog != null ) {
                    dialog.setBoxColor( globalProperties.boxColor.get() );
                }
            }
        } );
    }

    /*
     * Open or close the dialog, depending on whether the menu item is selected.
     */
    private void handleColorsDialog() {
        if ( isSelected() ) {
            dialog = new DeveloperColorsDialog( globalProperties );
            dialog.setVisible( true );
            dialog.addWindowListener( new WindowAdapter() {
                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    setSelected( false );
                    dialog = null;
                }
            } );
        }
        else {
            dialog.dispose();
        }
    }

    /*
     * Dialog with color chips for each color that can be modified.
     * No i18n is required.
     */
    private static class DeveloperColorsDialog extends PaintImmediateDialog {

        private final ColorControl canvasColorControl, boxColorControl;

        public DeveloperColorsDialog( final BCEGlobalProperties globalProperties ) {
            super( globalProperties.frame );
            super.setTitle( "Colors" );
            super.setModal( false );
            super.setResizable( false );

            // canvas (play area)
            canvasColorControl = new ColorControl( globalProperties.frame, "play area:", globalProperties.canvasColor.get() );
            canvasColorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    globalProperties.canvasColor.set( canvasColorControl.getColor() );
                }
            } );

            // boxes
            boxColorControl = new ColorControl( globalProperties.frame, "boxes:", globalProperties.boxColor.get() );
            boxColorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    globalProperties.boxColor.set( boxColorControl.getColor() );
                }
            } );

            GridPanel gridPanel = new GridPanel();
            gridPanel.setAnchor( Anchor.EAST );
            gridPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            gridPanel.setGridX( 0 );
            gridPanel.setGridY( GridBagConstraints.RELATIVE );
            gridPanel.add( canvasColorControl );
            gridPanel.add( boxColorControl );

            JPanel panel = new JPanel();
            panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
            panel.add( gridPanel );

            getContentPane().add( panel );
            pack();
            SwingUtils.centerInParent( this );
        }

        public void setCanvasColor( Color color ) {
            canvasColorControl.setColor( color );
        }

        public void setBoxColor( Color color ) {
            boxColorControl.setColor( color );
        }
    }
}
