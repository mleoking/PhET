/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:40:30 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGPlotFrame extends JDialog {
    private DGPlotPanel dgPlotPanel;

    public DGPlotFrame( Frame owner, DGModule dgModule ) {
        super( owner, "Intensity Plot", false );
        dgPlotPanel = new DGPlotPanel( dgModule );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( dgPlotPanel, BorderLayout.CENTER );
        JPanel controlPanel = new DGPlotControlPanel( this );
        contentPane.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( contentPane );
        pack();
    }

    static class DGPlotControlPanel extends HorizontalLayoutPanel {
        private SaveDGPanel saveDGPanel = new SaveDGPanel();

        public DGPlotControlPanel( final DGPlotFrame dgPlotFrame ) {
            JButton saveButton = new JButton( "Save Snapshot" );
            saveButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    saveDGPanel.savePanel( dgPlotFrame.getDgPlotPanel() );
                }
            } );
            setFill( GridBagConstraints.NONE );
            add( saveButton );
        }
    }

    public DGPlotPanel getDgPlotPanel() {
        return dgPlotPanel;
    }
}
