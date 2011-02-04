// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Developer control menu item for miscellaneous colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperColorsMenuItem extends JCheckBoxMenuItem {

    private final BCEGlobalProperties globalProperties;
    private JDialog dialog;

    public DeveloperColorsMenuItem( BCEGlobalProperties globalProperties ) {
        super( "Colors..." );
        this.globalProperties = globalProperties;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleColorsDialog();
            }
        });
    }

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

    private static class DeveloperColorsDialog extends PaintImmediateDialog {

        public DeveloperColorsDialog( final BCEGlobalProperties globalProperties ) {
            super( globalProperties.getFrame() );
            super.setTitle( "Colors" );
            super.setModal( false );
            super.setResizable( false );

            // canvas (play area)
            final ColorControl canvasColorControl = new ColorControl( globalProperties.getFrame(), "play area:", globalProperties.getCanvasColorProperty().getValue() );
            canvasColorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    globalProperties.getCanvasColorProperty().setValue( canvasColorControl.getColor() );
                }
            } );

            // boxes
            final ColorControl boxColorControl = new ColorControl( globalProperties.getFrame(), "boxes:", globalProperties.getBoxColorProperty().getValue() );
            boxColorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    globalProperties.getBoxColorProperty().setValue( boxColorControl.getColor() );
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
    }
}
