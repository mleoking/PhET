// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
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
 * Developer control menu item for miscellaneous colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperColorsMenuItem extends JCheckBoxMenuItem {

    private final BCEGlobalProperties globalProperties;
    private DeveloperColorsDialog dialog;

    public DeveloperColorsMenuItem( final BCEGlobalProperties globalProperties ) {
        super( "Colors..." );
        this.globalProperties = globalProperties;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleColorsDialog();
            }
        });

        globalProperties.getCanvasColorProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( dialog != null ) {
                    dialog.setCanvasColor( globalProperties.getCanvasColorProperty().getValue() );
                }
            }
        } );

        globalProperties.getBoxColorProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( dialog != null ) {
                    dialog.setBoxColor( globalProperties.getBoxColorProperty().getValue() );
                }
            }
        } );
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

        private final ColorControl canvasColorControl, boxColorControl;

        public DeveloperColorsDialog( final BCEGlobalProperties globalProperties ) {
            super( globalProperties.getFrame() );
            super.setTitle( "Colors" );
            super.setModal( false );
            super.setResizable( false );

            // canvas (play area)
            canvasColorControl = new ColorControl( globalProperties.getFrame(), "play area:", globalProperties.getCanvasColorProperty().getValue() );
            canvasColorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    globalProperties.getCanvasColorProperty().setValue( canvasColorControl.getColor() );
                }
            } );

            // boxes
            boxColorControl = new ColorControl( globalProperties.getFrame(), "boxes:", globalProperties.getBoxColorProperty().getValue() );
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

        public void setCanvasColor( Color color ) {
            canvasColorControl.setColor( color );
        }

        public void setBoxColor( Color color ) {
            boxColorControl.setColor( color );
        }
    }
}
