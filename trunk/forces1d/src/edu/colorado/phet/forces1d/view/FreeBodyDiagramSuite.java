/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */
public class FreeBodyDiagramSuite {
    private FreeBodyDiagramPanel fbdPanel;
    private JCheckBox checkBox;
    private Force1DModule module;
    private JDialog dialog;
    private JPanel dialogContentPane;
    private ControlPanel controlPanel;
    private int dialogInsetX;
    private int dialogInsetY;

    public FreeBodyDiagramSuite( final Force1DModule module ) {
        this.module = module;
        fbdPanel = new FreeBodyDiagramPanel( module );
        checkBox = new JCheckBox( "Free Body Diagram", true );
        checkBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                boolean showFBD = checkBox.isSelected();
                fbdPanel.setVisible( showFBD );
                if( showFBD ) {
                    checkBox.setVisible( false );
                }
            }
        } );

        try {
            final JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 1, 1 ) );

            final ApparatusPanel2 fbdPanel = this.fbdPanel.getFBDPanel();
            fbdPanel.setLayout( null );
            BufferedImage tearImage = ImageLoader.loadBufferedImage( "images/tear-20.png" );
            BufferedImage xImage = ImageLoader.loadBufferedImage( "images/x-20.png" );

            JButton tearButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( buttonPanel, tearImage, 14 ) ) );
            JButton closeButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( buttonPanel, xImage, 14 ) ) );

            buttonPanel.add( tearButton );
            buttonPanel.add( closeButton );

            tearButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( dialog == null || !dialog.isVisible() ) {
                        setWindowed();
                    }
                    else {
                        closeDialog();
                    }
                }
            } );

            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fbdPanel.setVisible( false );
                    checkBox.setVisible( true );
                    checkBox.setSelected( false );
                }
            } );

            fbdPanel.add( buttonPanel );
            Dimension panelDim = buttonPanel.getPreferredSize();
            buttonPanel.reshape( 0, 0, panelDim.width, panelDim.height );
            reshapeTopRight( fbdPanel, buttonPanel, 3, 3 );
            fbdPanel.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    reshapeTopRight( fbdPanel, buttonPanel, 3, 3 );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        checkBox.setVisible( false );

    }

    private void setWindowed() {
        if( dialog == null ) {
            createDialog();
        }

        dialog.pack();
        int w = dialog.getWidth();
        Point togo = controlPanel.getLocationOnScreen();
        togo.x -= w;
        dialog.setLocation( togo );
        dialogContentPane.add( fbdPanel.getFBDPanel() );
        fbdPanel.getFBDPanel().setLocation( dialogInsetX, dialogInsetY );
        dialog.setVisible( true );
        controlPanel.invalidate();
        controlPanel.doLayout();
        controlPanel.validate();
    }

    private void createDialog() {
        dialog = new JDialog( module.getPhetFrame(), "Free Body Diagram" );
        dialog.setResizable( false );
        dialogContentPane = new JPanel( null );

        dialogInsetX = 15;
        dialogInsetY = 15;

        JPanel windowAP = fbdPanel.getFBDPanel();
        Dimension preferredSize = new Dimension( windowAP.getWidth() + dialogInsetX * 2, windowAP.getHeight() + dialogInsetY * 2 );
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( windowAP );
        windowAP.setLocation( dialogInsetX, dialogInsetY );

        dialog.setContentPane( dialogContentPane );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                closeDialog();
            }
        } );
    }

    private void closeDialog() {
        fbdPanel.getFBDPanel().setLocation( 0, 0 );
        controlPanel.add( fbdPanel.getFBDPanel() );
        dialog.setVisible( false );
    }

    public Component getCheckBox() {
        return checkBox;
    }

//    public void updateGraphics() {
//        fbdPanel.getFBDPanel().handleUserInput();
//        fbdPanel.updateGraphics();
//    }

    public void addTo( ControlPanel controlPanel ) {
        this.controlPanel = controlPanel;
        controlPanel.add( checkBox );
        controlPanel.add( fbdPanel.getFBDPanel() );
    }

    public void reshapeTopRight( JComponent container, JComponent movable, int dx, int dy ) {
        int w = container.getWidth();
        int h = container.getHeight();
        Dimension d = movable.getPreferredSize();
        int x = w - d.width - dx;
        int y = 0 + dy;
        movable.reshape( x, y, d.width, d.height );
    }

    public void reset() {
        fbdPanel.reset();
    }

    public void handleUserInput() {
        fbdPanel.getFBDPanel().handleUserInput();
    }

    public void updateGraphics() {
        fbdPanel.updateGraphics();
    }
}
