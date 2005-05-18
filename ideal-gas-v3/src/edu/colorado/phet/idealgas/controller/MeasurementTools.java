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
 * MeasurementTools
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MeasurementTools {

    /**
     * These controls are each done as a JPanel, so they can have multiple widgets if necessary
     */

    static public class PressureSliceControl extends JCheckBox {
        PressureSliceControl( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Measure_pressure_in_layer" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( isSelected() );
                }
            } );
        }
    }

    static public class RulerControl extends JCheckBox {
        RulerControl( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Display_ruler" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRulerEnabed( isSelected() );
                }
            } );
        }
    }

    static public class HistogramControlPanel extends JCheckBox {
        HistogramControlPanel( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Display_energy_histograms" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WindowListener windowListener = new WindowAdapter() {
                        public void windowClosing( WindowEvent e ) {
                            setSelected( false );
                        }
                    };
                    JDialog dlg = module.setHistogramDlgEnabled( isSelected() );
                    dlg.addWindowListener( windowListener );
                }
            } );
        }
    }

    static public class CmLinesControl extends JCheckBox {
        CmLinesControl( final IdealGasModule module ) {
            super( SimStrings.get( "IdealGasControlPanel.Show_CM_lines" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    module.setCmLinesOn( isSelected() );
                }
            } );
        }
    }

    static public class SpeciesMonitorControl extends JPanel {
        SpeciesMonitorControl( final IdealGasModule module ) {
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

    static public class StopwatchControl extends JPanel {

        public StopwatchControl( final IdealGasModule module ) {
            final JCheckBox stopwatchCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.Stopwatch" ), false );
            stopwatchCB.addActionListener( new ActionListener() {
                PhetFrame frame = PhetApplication.instance().getPhetFrame();

                public void actionPerformed( ActionEvent e ) {
                    module.stopwatchEnabled( stopwatchCB.isSelected() );
                }
            } );
            this.add( stopwatchCB );
        }
    }
}
