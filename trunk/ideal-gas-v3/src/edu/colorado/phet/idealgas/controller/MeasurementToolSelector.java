/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.*;

/**
 * MeasurementToolSelector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MeasurementToolSelector extends JPopupMenu {
    private IdealGasModule module;
    private JCheckBoxMenuItem pressureSliceMI;

    public MeasurementToolSelector( IdealGasModule module) {
        this.module = module;
        pressureSliceMI = new JCheckBoxMenuItem( new PressureSliceAction() );
//        add( pressureSliceMI );
//        add( pressureSliceMI );
//        add( pressureSliceMI );
        add( new PressureSliceControlCB() );
        add( new RulerControl() );
        add( new HistogramControlPanel() );
        add( new CmLinesControl() );
        add( new SpeciesMonitorControl() );
        add( new StopwatchControl() );
    }


    /**
     * These controls are each done a s JPanel, so they can have multiple widgets if necessary
     */

    class PressureSliceAction extends AbstractAction {
        public void actionPerformed( ActionEvent e ) {
            module.setPressureSliceEnabled( pressureSliceMI.isSelected() );
        }
    }

    class PressureSliceControlCB extends JCheckBox {
        PressureSliceControlCB() {
            super( SimStrings.get( "MeasurementControlPanel.Measure_pressure_in_layer" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( isSelected() );
                }
            } );
        }

        public String toString() {
            return getText();
        }
    }
    class PressureSliceControl extends JPanel {
        private JCheckBox pressureSliceCB;

        PressureSliceControl() {
            pressureSliceCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Measure_pressure_in_layer" ) );
            this.add( pressureSliceCB );
            pressureSliceCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( pressureSliceCB.isSelected() );
                }
            } );
        }
        public String toString() {
            return pressureSliceCB.getText();
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
        HistogramControlPanel() {
            final JCheckBox histogramCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Display_energy_histograms" ) );
            this.add( histogramCB );
            histogramCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WindowListener windowListener = new WindowAdapter() {
                        public void windowClosing( WindowEvent e ) {
                            histogramCB.setSelected( false );
                        }
                    };
                    JDialog dlg = module.setHistogramDlgEnabled( histogramCB.isSelected() );
                    dlg.addWindowListener( windowListener );
                }
            } );
        }
    }

    class CmLinesControl extends JPanel {
        CmLinesControl() {
            final JCheckBox cmLinesOnCB = new JCheckBox( SimStrings.get( "IdealGasControlPanel.Show_CM_lines" ) );
            this.add( cmLinesOnCB );
            cmLinesOnCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    module.setCmLinesOn( cmLinesOnCB.isSelected() );
                }
            } );
        }
    }

    class SpeciesMonitorControl extends JPanel {
        SpeciesMonitorControl() {
            final JCheckBox speciesMonotorCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Show_species_information" ) );
            this.add( speciesMonotorCB );
            speciesMonotorCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WindowListener windowListener = new WindowAdapter() {
                        public void windowClosing( WindowEvent e ) {
                            speciesMonotorCB.setSelected( false );
                        }
                    };
                    JDialog dlg = module.setSpeciesMonitorDlgEnabled( speciesMonotorCB.isSelected() );
                    dlg.addWindowListener( windowListener );
                }
            } );
        }
    }

    class StopwatchControl extends JPanel {

        public StopwatchControl() {
           final JCheckBox stopwatchCB = new JCheckBox( SimStrings.get("MeasurementControlPanel.Stopwatch"), false );
            stopwatchCB.addActionListener( new ActionListener() {
                PhetFrame frame = PhetApplication.instance().getPhetFrame();
                public void actionPerformed( ActionEvent e ) {
                    module.setStopwatchEnabled( stopwatchCB.isSelected() );
                }
            } );
            this.add( stopwatchCB );
        }
    }

}
