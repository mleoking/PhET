/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:40:30 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGPlotFrame extends JDialog {
    private DGPlotPanel dgPlotPanel;
    private Frame owner;
    private DGModule dgModule;

    public DGPlotFrame( Frame owner, DGModule dgModule ) {
        super( owner, "Intensity Plot", false );
        this.owner = owner;
        this.dgModule = dgModule;
        dgPlotPanel = new DGPlotPanel( dgModule );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( dgPlotPanel, BorderLayout.CENTER );
        JPanel controlPanel = new DGPlotControlPanel( this );
        contentPane.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( contentPane );
        pack();
    }

    public void setVisible( boolean b ) {
        dgPlotPanel.visibilityChanged( b );
        super.setVisible( b );
    }

    static class DGPlotControlPanel extends HorizontalLayoutPanel {
        private SaveDGPanel saveDGPanel = new SaveDGPanel();

        public DGPlotControlPanel( final DGPlotFrame dgPlotFrame ) {
            JButton saveButton = new JButton( "Save Snapshot" );
            saveButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double sliderFraction = dgPlotFrame.dgModule.getDGSchrodingerPanel().getDGGunGraphic().getDgParticle().getSliderFraction();
                    double scale = 2.0 / 3.0;
//                    double val = sliderFraction * scale+ scale;
                    double val = ( sliderFraction + 1 ) * scale;
                    DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
//                    String text = decimalFormat.format( val ) + "*v";
                    double velocity = dgPlotFrame.dgModule.getVelocityRealUnits();
//                    System.out.println( "velocity = " + velocity );
//                    String text = "v=" + new DecimalFormat( "0" ).format( velocity ) + "km/s, D=" + new DecimalFormat( "0.00" ).format( dgPlotFrame.dgModule.getSpacing() ) + "nm";
//                    String text="<html>hello!<br>Line2</html>";
                    String text = "v=" + new DecimalFormat( "0" ).format( velocity ) + " km/s, D=" + new DecimalFormat( "0.0" ).format( dgPlotFrame.dgModule.getSpacing() ) + " nm";
                    saveDGPanel.savePanel( dgPlotFrame.getDgPlotPanel(), dgPlotFrame.getOwnerFrame(), text );
                }
            } );
            setFill( GridBagConstraints.NONE );
            add( saveButton );

            JButton clearButton = new JButton( "Clear Snapshots" );
            clearButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dgPlotFrame.getDgPlotPanel().clearSnapshots();
                }
            } );
            add( clearButton );
        }
    }

    private Frame getOwnerFrame() {
        return owner;
    }

    public DGPlotPanel getDgPlotPanel() {
        return dgPlotPanel;
    }
}
