/**
 * Class: MeasurementDialog
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 23, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MeasurementDialog extends JDialog {

    private IdealGasModule module;

    public MeasurementDialog( Frame owner, IdealGasModule module ) {
        super( owner, "Measurement Tools", false );
        this.module = module;
        JPanel panel = new JPanel();
        setContentPane( panel );
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = null;

        int rowIdx = 0;
        Insets insets = new Insets( 0, 10, 0, 0 );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.WEST,
                                      GridBagConstraints.NONE,
                                      insets, 0, 0 );
        panel.add( new PressureSliceControl(), gbc );
        gbc.gridy = 2;
        panel.add( new RulerControl(), gbc );
        gbc.gridy = 3;
        panel.add( new HistogramControlPanel(), gbc );
        gbc.gridy = 4;
        panel.add( new CmLinesControl(), gbc );



        this.pack();
    }

    class PressureSliceControl extends JPanel {
        PressureSliceControl() {
            final JCheckBox pressureSliceCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Measure_pressure_in_layer" ) );
            this.add( pressureSliceCB );
            pressureSliceCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( pressureSliceCB.isSelected() );
                }
            } );
        }
    }

    class RulerControl extends JPanel {
        RulerControl() {
            final JCheckBox rulerCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Display_ruler" ) );
            this.add( rulerCB );
            rulerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRulerEnabed( rulerCB.isSelected() );
                }
            } );
        }
    }

    class HistogramControlPanel extends JPanel {
        public HistogramControlPanel() {
            final JCheckBox histogramCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Display_energy_histograms" ) );
            this.add( histogramCB );
            histogramCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setHistogramDlgEnabled( histogramCB.isSelected() );
                }
            } );
        }
    }

    class CmLinesControl extends JPanel {
        public CmLinesControl() {
            final JCheckBox cmLinesOnCB = new JCheckBox( SimStrings.get( "IdealGasControlPanel.Show_CM_lines" ) );
            this.add( cmLinesOnCB );
            cmLinesOnCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    module.setCmLinesOn( cmLinesOnCB.isSelected() );
                }
            } );
        }
    }
}
