/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */

public class FreeBodyDiagramSuite {
    private FreeBodyDiagramPanel diagramPanel;
    private JCheckBox checkBox;
    private JButton windowButton;
    private Force1DModule module;
    private JDialog dialog;
    private JPanel dialogContentPane;
    private ControlPanel controlPanel;
    private int insetX;
    private int insetY;

    public FreeBodyDiagramSuite( final Force1DModule module ) {
        this.module = module;
        diagramPanel = new FreeBodyDiagramPanel( module );
        checkBox = new JCheckBox( "Free Body Diagram", true );
        checkBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                boolean showFBD = checkBox.isSelected();
                diagramPanel.setVisible( showFBD );
            }
        } );

        windowButton = new JButton( "FBD Window" );
        windowButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setWindowed();
            }
        } );
    }

    private void setWindowed() {

        if( dialog == null ) {
            createDialog();
        }
        windowButton.setVisible( false );

        dialog.pack();
        int w = dialog.getWidth();
        Point togo = checkBox.getLocationOnScreen();
        togo.x -= w;
        dialog.setLocation( togo );
        dialogContentPane.add( diagramPanel.getApparatusPanel() );
        diagramPanel.getApparatusPanel().setLocation( insetX, insetY );
        dialog.setVisible( true );
    }

    private void createDialog() {
        dialog = new JDialog( module.getPhetFrame(), "Free Body Diagram" );
        dialogContentPane = new JPanel( null );

        insetX = 15;
        insetY = 15;

        ApparatusPanel2 windowAP = diagramPanel.getApparatusPanel();
        Dimension preferredSize = new Dimension( windowAP.getWidth() + insetX * 2, windowAP.getHeight() + insetY * 2 );
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( windowAP );
        windowAP.setLocation( insetX, insetY );

        dialog.setContentPane( dialogContentPane );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                windowButton.setVisible( true );
                diagramPanel.getApparatusPanel().setLocation( 0, 0 );
                controlPanel.add( diagramPanel.getApparatusPanel() );
                dialog.setVisible( false );
            }
        } );
    }

    public Component getCheckBox() {
        return checkBox;
    }

    public void updateGraphics() {
        diagramPanel.updateGraphics();
    }

    public Component getWindowBox() {
        return windowButton;
    }

    public void addTo( ControlPanel controlPanel ) {
        this.controlPanel = controlPanel;
        controlPanel.add( checkBox );
        controlPanel.add( windowButton );
        controlPanel.add( diagramPanel.getApparatusPanel() );
    }
}
