/**
 * Class: MeasurementDialog
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 23, 2004
 */
package edu.colorado.phet.idealgas.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class MeasurementDialog extends JDialog {

    private static ResourceBundle localizedStrings;
    static {
        localizedStrings = ResourceBundle.getBundle( "localization/MeasurementControlPanel" );
    }

    private MeasurementModule module;

    public MeasurementDialog( Frame owner, MeasurementModule module ) {
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

        gbc = new GridBagConstraints( 1, 0, 1, 1, 1, 1,
                                      GridBagConstraints.WEST,
                                      GridBagConstraints.NONE,
                                      insets, 0, 0 );
        panel.add( new RulerControlPanel(), gbc );

        gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.NONE,
                                      insets, 0, 0 );
        panel.add( new HistogramControlPanel(), gbc );

        this.pack();
    }

    class PressureSliceControl extends JPanel {
        PressureSliceControl() {
            final JCheckBox pressureSliceCB = new JCheckBox( localizedStrings.getString( "<html>Measure_pressure<br>in_layer</html>") );
            pressureSliceCB.setPreferredSize( new Dimension( 140, 30 ) );
            this.add( pressureSliceCB );
            pressureSliceCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( pressureSliceCB.isSelected() );
                }
            } );
        }
    }

    class RulerControlPanel extends JPanel {
        RulerControlPanel() {
            final JCheckBox rulerCB = new JCheckBox( localizedStrings.getString( "Display_ruler" ));
            rulerCB.setPreferredSize( new Dimension( 140, 15 ) );
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
            final JCheckBox histogramCB = new JCheckBox( localizedStrings.getString( "Display_energy_histograms" ));
            this.add( histogramCB );
            histogramCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setHistogramDlgEnabled( histogramCB.isSelected() );
                }
            } );
        }
    }

}
