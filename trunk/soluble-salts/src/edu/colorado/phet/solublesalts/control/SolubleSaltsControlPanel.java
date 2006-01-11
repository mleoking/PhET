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
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.solublesalts.model.IonInitializer;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.solublesalts.model.salt.*;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
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

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private HashMap saltMap;

    static {
        saltMap = new HashMap();
//        saltMap.put( "Sodium Chloride", new SodiumChloride() );
//        saltMap.put( "Lead Chloride", new LeadChloride() );
        saltMap.put( "Silver Iodide", new SilverIodide() );
        saltMap.put( "Copper Hydroxide", new CopperHydroxide() );
        saltMap.put( "Chromium Hydroxide", new ChromiumHydroxide() );
        saltMap.put( "Strontium Phosphate", new StrontiumPhosphate() );
        saltMap.put( "Mercury Bromide", new MercuryBromide() );
    }

    static private String defaultSalt = "Mercury Bromide";

    static private HashMap ionClassToName = new HashMap();

    static {
        ionClassToName.put( Sodium.class, "Sodium" );
        ionClassToName.put( Lead.class, "Lead" );
        ionClassToName.put( Chromium.class, "Chromium" );
        ionClassToName.put( Copper.class, "Copper" );
        ionClassToName.put( Silver.class, "Silver" );
        ionClassToName.put( Chlorine.class, "Chlorine" );
        ionClassToName.put( Iodine.class, "Iodine" );
        ionClassToName.put( Hydroxide.class, "Hydroxide" );
        ionClassToName.put( Strontium.class, "Strontium" );
        ionClassToName.put( Phosphate.class, "Phosphate" );
        ionClassToName.put( Mercury.class, "Mercury" );
        ionClassToName.put( Bromine.class, "Bromine" );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private ModelSlider vesselIonStickSlider;
    private ModelSlider dissociationSlider;

    public SolubleSaltsControlPanel( final SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        JPanel concentrationPanel = makeConcentrationPanel( model );
        JPanel saltPanel = new JPanel( new GridLayout( 3, 1 ) );
        saltPanel.setBorder( new EtchedBorder() );
        saltPanel.add( makeSaltSelectionPanel( model ) );
        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        saltPanel.add( saltSPinnerPanel );
        addControlFullWidth( saltPanel );
        addControl( concentrationPanel );

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
        vesselIonStickSlider.setValue( SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD );
        model.getVessel().setIonStickAffinity( new RandomAffinity( vesselIonStickSlider.getValue() ) );
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
        dissociationSlider.setValue( SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD );
        Crystal.setDissociationLikelihood( dissociationSlider.getValue() );
        dissociationSlider.setNumMajorTicks( 5 );

        addControl( vesselIonStickSlider );
        addControl( dissociationSlider );
        addControl( makeWaterLevelPanel( model ) );

        // Zoom button
//        final JToggleButton zoomButton = new JToggleButton( "Zoom" );
//        final ZoomDlg zoomDlg = new ZoomDlg();
//        zoomButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setZoomEnabled( zoomButton.isSelected() );
//                zoomDlg.setVisible( zoomButton.isSelected() );
//            }
//        } );
//        addControl( zoomButton );

        // Reset button
        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent arg0 ) {
                model.reset();
            }
        } );
        addControl( resetBtn );

        //-----------------------------------------------------------------
        // DEBUG
        //-----------------------------------------------------------------
        JButton releaseButton = new JButton( "Release ion" );
        releaseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                List crystals = model.crystalTracker.getCrystals();
                for( int i = 0; i < crystals.size(); i++ ) {
                    Crystal crystal = (Crystal)crystals.get( i );
                    crystal.releaseIonDebug( module.getClock().getSimulationTimeChange() );
                }
            }
        } );
        addControl( releaseButton );
    }

    /**
     *
     */
    private JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {

        final JComboBox comboBox = new JComboBox( saltMap.keySet().toArray() );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                model.reset();
                Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
                model.setCurrentSalt( saltClass );
                model.reset();
            }
        } );
        comboBox.setSelectedItem( defaultSalt );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( new JLabel( "Salt: " ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( comboBox, gbc );
        return panel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeConcentrationPanel( final SolubleSaltsModel model ) {
        final ModelSlider kspSlider = new ModelSlider( "Ksp", "", 0, 3E-16, 0 );
        kspSlider.setSliderLabelFormat( new DecimalFormat( "0E00" ) );
        kspSlider.setTextFieldFormat( new DecimalFormat( "0E00" ) );
        kspSlider.setNumMajorTicks( 3 );
        kspSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( kspSlider.getValue() );
            }
        } );
        model.setKsp( kspSlider.getValue() );

        // Add a listener that will change the Ksp when the current salt changes
        model.addChangeListener( new SolubleSaltsModel.ChangeListener() {
            public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
                double ksp = model.getCurrentSalt().getKsp();
                kspSlider.setValue( ksp );
            }
        } );

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


    //----------------------------------------------------------------
    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------
    //----------------------------------------------------------------


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
            setLocationRelativeTo( PhetApplication.instance().getPhetFrame() );
        }
    }

    /**
     * Panel with spinners to add and remove ions from the model
     */
    private class SaltSpinnerPanel extends JPanel implements SolubleSaltsModel.ChangeListener {
        Salt salt;
        IonSpinner anionSpinner;
        IonSpinner cationSpinner;
        public int anionRatio;
        public int cationRatio;
        public Class anionClass;
        public Class cationClass;
        private SolubleSaltsModel model;
        public JLabel anionLabel;
        public JLabel cationLabel;
        public IonSpinnerChangeListener anionSpinnerListener;
        public IonSpinnerChangeListener cationSpinnerListener;

        public SaltSpinnerPanel( final SolubleSaltsModel model ) {
            super( new GridBagLayout() );
            this.model = model;
            model.addChangeListener( this );

            GridBagConstraints gbc = new DefaultGridBagConstraints();
            gbc.insets = new Insets( 5, 0, 0, 0 );
            gbc.gridwidth = 1;

            // Make labels with the names of the ions and icons that corresponds to the ions graphics
            anionClass = model.getCurrentSalt().getCationClass();
            String anionName = getIonName( anionClass );
            anionLabel = new JLabel( anionName, new ImageIcon( IonGraphicManager.getIonImage( anionClass ) ), JLabel.LEADING );
            anionLabel.setPreferredSize( new Dimension( 100, 20 ) );
            gbc.anchor = GridBagConstraints.WEST;
            add( anionLabel, gbc );

            cationClass = model.getCurrentSalt().getCationClass();
            String cationName = getIonName( anionClass );
            cationLabel = new JLabel( cationName, new ImageIcon( IonGraphicManager.getIonImage( anionClass ) ), JLabel.LEADING );
            anionLabel.setPreferredSize( new Dimension( 100, 20 ) );
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.WEST;
            add( cationLabel, gbc );

            // Make the spinners
            anionSpinner = new IonSpinner();
            cationSpinner = new IonSpinner();
            gbc.gridx = 1;
            gbc.gridy = 0;
            add( anionSpinner, gbc );
            gbc.gridy++;
            add( cationSpinner, gbc );

            setSalt( model.getCurrentSalt() );

            model.addIonListener( new IonListener() {
                public void ionAdded( IonEvent event ) {
                    syncSpinnersWithModel();
                }

                public void ionRemoved( IonEvent event ) {
                    syncSpinnersWithModel();
                }
            } );
        }

        private void syncSpinnersWithModel() {
            anionSpinner.setSyncWithDependentIonspinner( false );
            anionSpinner.setValue( new Integer( model.getNumIonsOfType( anionClass ) ) );
            anionSpinner.setSyncWithDependentIonspinner( true );
            cationSpinner.setSyncWithDependentIonspinner( false );
            cationSpinner.setValue( new Integer( model.getNumIonsOfType( cationClass ) ) );
            cationSpinner.setSyncWithDependentIonspinner( true );
        }

        public void setSalt( Salt salt ) {
            if( salt.getAnionClass() != this.anionClass ) {
                this.anionClass = salt.getAnionClass();

                // Update the counter label
                String anionName = getIonName( anionClass );
                anionLabel.setText( anionName );
                anionLabel.setIcon( new ImageIcon( IonGraphicManager.getIonImage( anionClass ) ) );
            }

            if( salt.getCationClass() != this.cationClass ) {
                this.cationClass = salt.getCationClass();

                // Update the counter label
                String cationName = getIonName( cationClass );
                cationLabel.setText( cationName );
                cationLabel.setIcon( new ImageIcon( IonGraphicManager.getIonImage( cationClass ) ) );
            }

            this.salt = salt;
            anionRatio = 0;
            cationRatio = 0;
            anionClass = salt.getAnionClass();
            cationClass = salt.getCationClass();
            Salt.Component[] components = salt.getComponents();
            for( int i = 0; i < components.length; i++ ) {
                Salt.Component component = components[i];
                if( component.getIonClass() == anionClass ) {
                    anionRatio = component.getLatticeUnitFraction().intValue();
                }
                if( component.getIonClass() == cationClass ) {
                    cationRatio = component.getLatticeUnitFraction().intValue();
                }
            }
            anionSpinner.setModel( new SpinnerNumberModel( 0, 0, 100, anionRatio ) );
            anionSpinner.removeChangeListener( anionSpinnerListener );
            anionSpinnerListener = new IonSpinnerChangeListener( anionSpinner,
                                                                 cationSpinner,
                                                                 anionClass,
                                                                 anionRatio,
                                                                 cationRatio );
            anionSpinner.addChangeListener( anionSpinnerListener );

            cationSpinner.setModel( new SpinnerNumberModel( 0, 0, 100, cationRatio ) );
            cationSpinner.removeChangeListener( cationSpinnerListener );
            cationSpinnerListener = new IonSpinnerChangeListener( cationSpinner,
                                                                  anionSpinner,
                                                                  cationClass,
                                                                  cationRatio,
                                                                  anionRatio );
            cationSpinner.addChangeListener( cationSpinnerListener );
        }

        public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
            if( event.saltChanged ) {
                setSalt( event.getModel().getCurrentSalt() );
            }
            if( event.modelReset ) {
                syncSpinnersWithModel();
            }
        }

        private String getIonName( Class ionClass ) {
            String ionName = (String)ionClassToName.get( ionClass );
            return ionName;
        }

        /**
         * Creates and removes ions of the appropriate class, and coordinates the spinner
         * for the other component of the salt
         */
        private class IonSpinnerChangeListener implements ChangeListener {
            private IonSpinner spinner;
            private IonSpinner dependentSpinner;
            private Class ionClass;
            private int ionRatio;
            private int dependentIonRatio;

            IonSpinnerChangeListener( IonSpinner spinner,
                                      IonSpinner dependentSpinner,
                                      Class ionClass,
                                      int ionRatio,
                                      int dependentIonRatio ) {

                this.spinner = spinner;
                this.dependentSpinner = dependentSpinner;
                this.ionClass = ionClass;
                this.ionRatio = ionRatio;
                this.dependentIonRatio = dependentIonRatio;
            }

            public void stateChanged( ChangeEvent e ) {
                if( spinner.isSyncWithDependentIonspinner() ) {
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
                    dependentSpinner.setValue( new Integer( model.getNumIonsOfType( ionClass ) * dependentIonRatio / ionRatio ) );
                }
            }
        }
    }

    /**
     * Subclass of JSpinner that knows if it is to be synchronized with the another
     * IonSpinner
     * <p/>
     * todo: move some of the code from other classes above into this.
     */
    private class IonSpinner extends JSpinner {
        private boolean syncWithDependentIonspinner = true;

        public boolean isSyncWithDependentIonspinner() {
            return syncWithDependentIonspinner;
        }

        public void setSyncWithDependentIonspinner( boolean syncWithDependentIonspinner ) {
            this.syncWithDependentIonspinner = syncWithDependentIonspinner;
        }
    }
}
