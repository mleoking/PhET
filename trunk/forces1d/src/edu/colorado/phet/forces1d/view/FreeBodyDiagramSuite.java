/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.ApparatusPanel3;
import edu.colorado.phet.forces1d.common.JButton3D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */
public class FreeBodyDiagramSuite {
    private FreeBodyDiagramPanel diagramPanel;
    private JCheckBox checkBox;
    private JButton3D windowButton;
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

//        windowButton = new JButton3D( "Windowize FBD" );
        windowButton = new JButton3D( "Windowize FBD" );
        windowButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setWindowed();
                MouseEvent me = new MouseEvent( dialogContentPane, 0, 0, 0, 0, 0, 0, false );
                windowButton.getButton3D().fireMouseExited( me );
            }
        } );
//        windowButton.setBorder( BorderFactory.createRaisedBevelBorder() );

//                windowButton = new JButton( "FBD Window" );
//        windowButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setWindowed();
//            }
//        } );
//        windowButton.setBorder( BorderFactory.createRaisedBevelBorder() );
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
        dialogContentPane.add( diagramPanel.getFBDPanel() );
        diagramPanel.getFBDPanel().setLocation( insetX, insetY );
        dialog.setVisible( true );
    }

    private void createDialog() {
        dialog = new JDialog( module.getPhetFrame(), "Free Body Diagram" );
        dialog.setResizable( false );
        dialogContentPane = new JPanel( null );

        insetX = 15;
        insetY = 15;

        ApparatusPanel3 windowAP = diagramPanel.getFBDPanel();
        Dimension preferredSize = new Dimension( windowAP.getWidth() + insetX * 2, windowAP.getHeight() + insetY * 2 );
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( windowAP );
        windowAP.setLocation( insetX, insetY );

        dialog.setContentPane( dialogContentPane );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                windowButton.setVisible( true );
                diagramPanel.getFBDPanel().setLocation( 0, 0 );
                controlPanel.add( diagramPanel.getFBDPanel() );
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
        controlPanel.add( diagramPanel.getFBDPanel() );
    }

    public void reset() {
        diagramPanel.reset();
    }
}
