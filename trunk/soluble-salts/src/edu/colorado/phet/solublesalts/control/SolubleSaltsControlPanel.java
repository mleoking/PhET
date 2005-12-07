/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.salt.*;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsControlPanel extends ControlPanel {
    private ModelSlider vesselIonStickSlider;
    private ModelSlider vesselIonReleaseSlider;
    private ModelSlider dissociationSlider;
    private AnionPanel anionPanel;
    private JPanel cationPanel;

    public SolubleSaltsControlPanel( final SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        anionPanel = new AnionPanel( model );
        cationPanel = new CationPanel( model );
        addControl( makeSaltSelectionPanel( model ) );
        addControl( anionPanel );
        addControl( cationPanel );

        // Sliders for affinity adjustment
        vesselIonStickSlider = new ModelSlider( "Lattice stick likelihood",
                                                "",
                                                0,
                                                1,
                                                0,
                                                new DecimalFormat( "0.00" ) );
        vesselIonStickSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setIonStickAffinity( new RandomAffinity( vesselIonStickSlider.getValue() ) );
            }
        } );
        vesselIonStickSlider.setValue( 0.9 );
        vesselIonStickSlider.setNumMajorTicks( 5 );

        dissociationSlider = new ModelSlider( "Lattice dissociation likelihood",
                                              "",
                                              0,
                                              1,
                                              0,
                                              new DecimalFormat( "0.000" ) );
        dissociationSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Crystal.setDissociationLikelihood( dissociationSlider.getValue() );
            }
        } );
        dissociationSlider.setValue( 0.01 );
        dissociationSlider.setNumMajorTicks( 5 );

        addControl( vesselIonStickSlider );
        addControl( dissociationSlider );
        addControl( makeConcentrationPanel( model ) );
        addControl( makeWaterLevelPanel( model ) );

        // Zoom button
        final JToggleButton zoomButton = new JToggleButton( "Zoom" );
        final ZoomDlg zoomDlg = new ZoomDlg();
        zoomButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setZoomEnabled( zoomButton.isSelected() );
                zoomDlg.setVisible( zoomButton.isSelected() );
            }
        } );
        addControl( zoomButton );

        // Reset button
        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent arg0 ) {
                model.reset();
            }
        } );
        addControl( resetBtn );
    }

    /**
     *
     */
    private JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {
        String sodiumChlorideString = "Sodium Chloride";
        String leadChlorideString = "Lead Chloride";
        String silverIodideString = "Silver Iodide";
        String copperHydroxideString = "Copper Hydroxide";
        final HashMap saltMap = new HashMap();
        saltMap.put( sodiumChlorideString, new SodiumChloride() );
        saltMap.put( leadChlorideString, new LeadChloride() );
        saltMap.put( silverIodideString, new SilverIodide() );
        saltMap.put( copperHydroxideString, new CopperHydroxide() );

        final JComboBox comboBox = new JComboBox( saltMap.keySet().toArray() );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
                model.setCurrentSalt( saltClass );
                model.reset();
            }
        } );
        comboBox.setSelectedIndex( 0 );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( new JLabel( "Salt:" ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( comboBox, gbc );
        return panel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeLatticePanel( final SolubleSaltsModel model ) {
        String plainCubicStr = "Plain cubic";
        String twoToOne = "Tow-to-one";
        String[] options = new String[]{
            plainCubicStr,
            twoToOne
        };
        final HashMap latticeMap = new HashMap();
        latticeMap.put( plainCubicStr, SolubleSaltsConfig.oneToOneLattice );
        latticeMap.put( twoToOne, SolubleSaltsConfig.twoToOneLattice );

        final JComboBox latticeCBox = new JComboBox( options );
        latticeCBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Lattice lattice = (Lattice)latticeMap.get( latticeCBox.getSelectedItem() );
                SolubleSaltsConfig.LATTICE = lattice;
                model.reset();
            }
        } );
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( new JLabel( "Lattice:" ), gbc );
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add( latticeCBox );
        return panel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeConcentrationPanel( final SolubleSaltsModel model ) {
        final ModelSlider kspSlider = new ModelSlider( "Ksp", "", 0, 3E-4, 0 );
        kspSlider.setSliderLabelFormat( new DecimalFormat( "0E00" ) );
        kspSlider.setTextFieldFormat( new DecimalFormat( "0E00" ) );
        kspSlider.setNumMajorTicks( 3 );
        kspSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( kspSlider.getValue() );
            }
        } );
        model.setKsp( kspSlider.getValue() );

        final JTextField concentrationTF = new JTextField( 8 );
        model.addModelElement( new ModelElement() {
            DecimalFormat format = new DecimalFormat( "0E00" );

            public void stepInTime( double dt ) {
                double concentration = model.getConcentration();
                concentrationTF.setText( format.format( concentration ) );
            }
        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createTitledBorder( "Concentration" ) );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.gridwidth = 2;
        panel.add( kspSlider, gbc );
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( new JLabel( "Concentration:" ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( concentrationTF, gbc );

        return panel;
    }

    /**
     * @param model
     * @return
     */
    private Component makeHeatControl( final SolubleSaltsModel model ) {
        JPanel heatControlPanel = new JPanel();
        final ModelSlider heatSlider = new ModelSlider( "Heat Control", "", -20, 20, 0 );
        heatSlider.setSliderLabelFormat( new DecimalFormat( "#0" ) );
        heatSlider.setTextFieldFormat( new DecimalFormat( "#0" ) );
        heatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getHeatSource().setHeatChangePerClockTick( heatSlider.getValue() );
            }
        } );
        heatSlider.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                heatSlider.setValue( 0 );
            }
        } );
        heatControlPanel.add( heatSlider );
        return heatControlPanel;
    }

    private JPanel makeWaterLevelPanel( final SolubleSaltsModel model ) {
        JPanel panel = new JPanel();
        final ModelSlider slider = new ModelSlider( "Water level", "",
                                                    0,
                                                    model.getVessel().getDepth(),
                                                    model.getVessel().getWaterLevel() );
        slider.setTextFieldFormat( new DecimalFormat( "#" ) );
        slider.setNumMajorTicks( 6 );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setWaterLevel( slider.getValue() );
            }
        } );
        panel.add( slider );
        return panel;
    }

    private class IonCountSyncAgent implements IonListener {
        private SolubleSaltsModel model;
        private Class ionClass;
        private JSpinner spinner;

        public IonCountSyncAgent( SolubleSaltsModel model, Class ionClass, JSpinner spinner ) {
            this.model = model;
            this.ionClass = ionClass;
            this.spinner = spinner;
        }

        public void ionAdded( IonEvent event ) {
            syncSpinner();
        }

        public void ionRemoved( IonEvent event ) {
            syncSpinner();
        }

        private void syncSpinner() {
            if( model.getIonsOfType( ionClass ) != null
                && model.getIonsOfType( ionClass ).size() != ( (Integer)spinner.getValue() ).intValue() ) {
                spinner.setValue( new Integer( model.getNumIonsOfType( ionClass ) ) );
            }
        }
    }

    private class FreeIonCountSyncAgent implements IonListener, Ion.ChangeListener {
        private SolubleSaltsModel model;
        private Class ionClass;
        private JTextField testField;

        public FreeIonCountSyncAgent( SolubleSaltsModel model, Class ionClass, JTextField testField ) {
            this.model = model;
            this.ionClass = ionClass;
            this.testField = testField;
        }

        public void ionAdded( IonEvent event ) {
            if( event.getIon().getClass() == ionClass ) {
                event.getIon().addChangeListener( this );
                syncSpinner();
            }
        }

        public void ionRemoved( IonEvent event ) {
            if( event.getIon().getClass() == ionClass ) {
                event.getIon().removeChangeListener( this );
                syncSpinner();
            }
        }

        public void stateChanged( Ion.ChangeEvent event ) {
            if( event.getIon().getClass() == ionClass ) {
                syncSpinner();
            }
        }

        private void syncSpinner() {
            testField.setText( Integer.toString( model.getNumFreeIonsOfType( ionClass ) ) );
        }
    }

    /**
     * A modelss dialog that explains how to zoom. This is a temporary thing
     */
    class ZoomDlg extends JDialog {
        public ZoomDlg() throws HeadlessException {
            super( PhetApplication.instance().getPhetFrame(), false );
            String text = "To Zoom:"
                          + "\t\n\t1) Move the mouse to the spot you would like to zoom to."
                          + "\t\n\t2) Press the left mouse button."
                          + "\t\n\t3) Move the mouse to the right to zoom in,"
                          + "\t\n\t4) Move the mouse to the left to zoom out."
                    ;
            JTextArea textArea = new JTextArea( text );
            textArea.setBackground( new Color( 255, 255, 160 ) );
            getContentPane().add( textArea );
            pack();
//            setLocation( 100, 30);
            setLocationRelativeTo( PhetApplication.instance().getPhetFrame() );
        }
    }

    //----------------------------------------------------------------
    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------
    //----------------------------------------------------------------

    private class AnionPanel extends IonPanel {

        public AnionPanel( final SolubleSaltsModel model ) {
            super( model );
        }

        public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
            trackIonClass( event.getModel().getCurrentSalt().getAnionClass() );
        }
    }

    private class CationPanel extends IonPanel {

        public CationPanel( final SolubleSaltsModel model ) {
            super( model );
        }

        public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
            trackIonClass( event.getModel().getCurrentSalt().getCationClass() );
        }
    }

    private abstract class IonPanel extends JPanel implements SolubleSaltsModel.ChangeListener {
        private FreeIonCountSyncAgent freeIonCountSyncAgent;
        private SolubleSaltsModel model;
        private JTextField ionCountTF;
        private JSpinner spinner;
        private IonCountSyncAgent ionCountSyncAgent;
        private Class ionClass;
        private JLabel ionLabel;

        public IonPanel( final SolubleSaltsModel model ) {
            super( new GridBagLayout() );
            this.model = model;
            model.addChangeListener( this );
            GridBagConstraints gbc = new DefaultGridBagConstraints();

            // Get name of the ion
            ionClass = model.getCurrentSalt().getCationClass();
            String ionName = getIonName( ionClass );

            ionLabel = new JLabel( ionName, new ImageIcon( IonGraphicManager.getIonImage( ionClass ) ), JLabel.LEADING );
            gbc.anchor = GridBagConstraints.EAST;
            add( ionLabel, gbc );

            // Spinners for the number of ions
            spinner = new JSpinner( new SpinnerNumberModel( model.getNumIonsOfType( ionClass ),
                                                            0,
                                                            100,
                                                            1 ) );
            ionCountSyncAgent = new IonCountSyncAgent( model, ionClass, spinner );
            model.addIonListener( ionCountSyncAgent );
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            add( spinner, gbc );

            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dIons = ( (Integer)spinner.getValue() ).intValue()
                                - model.getNumIonsOfType( ionClass );
                    if( dIons > 0 ) {
                        IonFactory ionFactory = new IonFactory();
                        for( int i = 0; i < dIons; i++ ) {
                            Ion ion = ionFactory.create( ionClass );
                            IonInitializer.initialize( ion, model );
                            model.addModelElement( ion );
                        }
                    }
                    if( dIons < 0 ) {
                        for( int i = dIons; i < 0; i++ ) {
                            List ions = model.getIonsOfType( ionClass );
                            if( ions != null ) {
                                Ion ion = (Ion)ions.get( 0 );
                                model.removeModelElement( ion );
                            }
                        }
                    }
                }
            } );

            ionCountTF = new JTextField( 4 );
            freeIonCountSyncAgent = new FreeIonCountSyncAgent( model, ionClass, ionCountTF );
            model.addIonListener( freeIonCountSyncAgent );
            gbc.gridx++;
            add( ionCountTF, gbc );
        }

        private String getIonName( Class ionClass ) {
            String ionName = null;
            if( ionClass == Sodium.class ) {
                ionName = "Sodium";
            }
            if( ionClass == Lead.class ) {
                ionName = "Lead";
            }
            if( ionClass == Chloride.class ) {
                ionName = "Chlorine";
            }
            return ionName;
        }

        void trackIonClass( Class ionClass ) {
            if( ionClass != this.ionClass ) {
                this.ionClass = ionClass;
                model.removeIonListener( freeIonCountSyncAgent );
                freeIonCountSyncAgent = new FreeIonCountSyncAgent( model, ionClass, ionCountTF );
                model.addIonListener( freeIonCountSyncAgent );

                model.removeIonListener( ionCountSyncAgent );
                ionCountSyncAgent = new IonCountSyncAgent( model, ionClass, spinner );
                model.addIonListener( ionCountSyncAgent );

                // Update the counter label
                String ionName = getIonName( ionClass );
                ionLabel.setText( ionName );
                ionLabel.setIcon( new ImageIcon( IonGraphicManager.getIonImage( ionClass ) ) );
            }
        }
    }
}
