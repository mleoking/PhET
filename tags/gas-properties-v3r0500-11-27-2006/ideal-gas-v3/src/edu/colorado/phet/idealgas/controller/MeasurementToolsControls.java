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
import edu.colorado.phet.idealgas.model.IdealGasModel;

import javax.swing.*;
import java.awt.event.*;

/**
 * MeasurementToolsControls
 * <p/>
 * A set of static Swing component classes for enabling and disabling various
 * measurement tools
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MeasurementToolsControls {

    //----------------------------------------------------------------
    // Check boxes
    //----------------------------------------------------------------

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
                    if( dlg != null ) {
                        dlg.addWindowListener( windowListener );
                    }
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

    static public class SpeciesMonitorControl extends JCheckBox {
        SpeciesMonitorControl( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Show_species_information" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WindowListener windowListener = new WindowAdapter() {
                        public void windowClosing( WindowEvent e ) {
                            setSelected( false );
                        }
                    };
                    JDialog dlg = module.setSpeciesMonitorDlgEnabled( isSelected() );
                    dlg.addWindowListener( windowListener );
                }
            } );
        }
    }

    static public class StopwatchControl extends JCheckBox {
        StopwatchControl( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Stopwatch" ), false );
            addActionListener( new ActionListener() {
                PhetFrame frame = PhetApplication.instance().getPhetFrame();

                public void actionPerformed( ActionEvent e ) {
                    module.setStopwatchEnabled( isSelected() );
                }
            } );
        }
    }


    static public class ParticleInteractionControl extends JCheckBox {
        ParticleInteractionControl( final IdealGasModel model ) {
            super( SimStrings.get( "MeasurementControlPanel.Molecules-interact" ), true );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.enableParticleParticleInteractions( isSelected() );
                }
            } );
        }
    }

    //----------------------------------------------------------------
    // Menu items
    //----------------------------------------------------------------

    static public class PressureSliceControlMI extends JCheckBoxMenuItem {
        PressureSliceControlMI( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Measure_pressure_in_layer" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( isSelected() );
                }
            } );
        }
    }

    static public class RulerControlMI extends JCheckBoxMenuItem {
        RulerControlMI( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Display_ruler" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRulerEnabed( isSelected() );
                }
            } );
        }
    }

    static public class HistogramControlPanelMI extends JCheckBoxMenuItem {
        HistogramControlPanelMI( final IdealGasModule module ) {
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

    static public class CmLinesControlMI extends JCheckBoxMenuItem {
        CmLinesControlMI( final IdealGasModule module ) {
            super( SimStrings.get( "IdealGasControlPanel.Show_CM_lines" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    module.setCmLinesOn( isSelected() );
                }
            } );
        }
    }

    static public class SpeciesMonitorControlMI extends JCheckBoxMenuItem {
        SpeciesMonitorControlMI( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Show_species_information" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WindowListener windowListener = new WindowAdapter() {
                        public void windowClosing( WindowEvent e ) {
                            setSelected( false );
                        }
                    };
                    JDialog dlg = module.setSpeciesMonitorDlgEnabled( isSelected() );
                    dlg.addWindowListener( windowListener );
                }
            } );
        }
    }

    static public class StopwatchControlMI extends JCheckBoxMenuItem {
        StopwatchControlMI( final IdealGasModule module ) {
            super( SimStrings.get( "MeasurementControlPanel.Stopwatch" ), false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStopwatchEnabled( isSelected() );
                }
            } );
        }
    }

    static public class ParticleInteractionMI extends JCheckBoxMenuItem {
        ParticleInteractionMI( final IdealGasModel model ) {
            super( SimStrings.get( "MeasurementControlPanel.Molecules-interact" ), true );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.enableParticleParticleInteractions( isSelected() );
                }
            } );
        }
    }
}
