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

import edu.colorado.phet.solublesalts.model.IonInitializer;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.Drain;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * SaltSpinnerPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SaltSpinnerPanel extends JPanel implements SolubleSaltsModel.ChangeListener,
                                                        Ion.ChangeListener {

    //----------------------------------------------------------------
    // Class fields
    //----------------------------------------------------------------

    static private HashMap ionClassToName = new HashMap();

    static {
        ionClassToName.put( ConfigurableCation.class, SimStrings.get("Ion.cation" ));
        ionClassToName.put( Sodium.class, SimStrings.get("Ion.sodium" ));
        ionClassToName.put( Lead.class, SimStrings.get("Ion.lead" ));
        ionClassToName.put( Chromium.class, SimStrings.get("Ion.chromium" ));
        ionClassToName.put( Copper.class, SimStrings.get("Ion.copper" ));
        ionClassToName.put( Silver.class, SimStrings.get("Ion.silver" ));
        ionClassToName.put( Thallium.class, SimStrings.get("Ion.thallium" ));
        ionClassToName.put( Strontium.class, SimStrings.get("Ion.strontium" ));
        ionClassToName.put( Mercury.class, SimStrings.get("Ion.mercury" ));

        ionClassToName.put( ConfigurableAnion.class, SimStrings.get("Ion.anion" ));
        ionClassToName.put( Phosphate.class, SimStrings.get("Ion.phosphate" ));
        ionClassToName.put( Bromine.class, SimStrings.get("Ion.bromide" ));
        ionClassToName.put( Arsenate.class, SimStrings.get("Ion.aresenate" ));
        ionClassToName.put( Sulfur.class, SimStrings.get("Ion.sulfide" ));
        ionClassToName.put( Chlorine.class, SimStrings.get("Ion.chloride" ));
        ionClassToName.put( Iodide.class, SimStrings.get("Ion.iodide" ));
        ionClassToName.put( Hydroxide.class, SimStrings.get("Ion.hydroxide" ));
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

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
    public SaltSpinnerPanel.IonSpinnerChangeListener anionSpinnerListener;
    public SaltSpinnerPanel.IonSpinnerChangeListener cationSpinnerListener;
    private JTextField numFreeAnionTF;
    private JTextField numFreeCationTF;
    private final int maxIons = SolubleSaltsConfig.MAX_ION_SPINNER_NUM;
    private JTextField numBoundAnionTF;
    private JTextField numBoundCationTF;
    private JLabel ionsLabel;
    private JLabel totalNumLabel;
    private JLabel freeNumLabel;
    private JLabel boundNumLabel;
    private Random random = new Random();

    /**
     * @param model
     */
    public SaltSpinnerPanel( final SolubleSaltsModel model ) {
        super( new GridBagLayout() );
        this.model = model;

        model.addChangeListener( this );

        // Make labels with the names of the ions and icons that corresponds to the ions graphics
        cationClass = model.getCurrentSalt().getCationClass();
        String cationName = getIonName( cationClass );
        cationLabel = new JLabel( cationName, new ImageIcon( IonGraphicManager.getIonImage( cationClass ) ), JLabel.RIGHT );
        cationLabel.setPreferredSize( new Dimension( 85, 20 ) );
        cationLabel.setHorizontalAlignment( JLabel.CENTER );

        anionClass = model.getCurrentSalt().getAnionClass();
        String anionName = getIonName( anionClass );
        anionLabel = new JLabel( anionName, new ImageIcon( IonGraphicManager.getIonImage( anionClass ) ), JLabel.RIGHT );
        anionLabel.setPreferredSize( new Dimension( 85, 20 ) );
        anionLabel.setHorizontalAlignment( JLabel.CENTER );

        // Make the spinners
        cationSpinner = new IonSpinner();
        anionSpinner = new IonSpinner();

        // Make readouts for the number of free ions
        int textFieldWidth = 4;
        numFreeAnionTF = new JTextField( "", textFieldWidth );
        numFreeAnionTF.setEditable( false );
        numFreeAnionTF.setHorizontalAlignment( JTextField.RIGHT );
        numFreeCationTF = new JTextField( "", textFieldWidth );
        numFreeCationTF.setEditable( false );
        numFreeCationTF.setHorizontalAlignment( JTextField.RIGHT );

        // Make readouts for the number of bound ions
        numBoundAnionTF = new JTextField( "", textFieldWidth );
        numBoundAnionTF.setEditable( false );
        numBoundAnionTF.setHorizontalAlignment( JTextField.RIGHT );
        numBoundCationTF = new JTextField( "", textFieldWidth );
        numBoundCationTF.setEditable( false );
        numBoundCationTF.setHorizontalAlignment( JTextField.RIGHT );

        // Add an ionListener to the model that will manage the states of the controls
        model.addIonListener( new IonListener() {
            public void ionAdded( IonEvent event ) {
                syncSpinnersWithModel();
                event.getIon().addChangeListener( SaltSpinnerPanel.this );
            }

            public void ionRemoved( IonEvent event ) {
                syncSpinnersWithModel();
                event.getIon().removeChangeListener( SaltSpinnerPanel.this );
            }
        } );

        // Add a listener to the vessel that will disable the spinners when the tank is empty
        model.getVessel().addChangeListener( new Vessel.ChangeListener() {
            public void stateChanged( Vessel.ChangeEvent event ) {
                Vessel vessel = event.getVessel();
                double drainLevel = vessel.getLocation().getY() + vessel.getDepth() -model.getDrain().getPosition().getY();
                boolean b = vessel.getWaterLevel() > drainLevel;
                anionSpinner.setEnabled( b );
                cationSpinner.setEnabled( b );
            }
        } );

        // Make labels for the columns
        ionsLabel = new JLabel( SimStrings.get( "ControlLabels.Ions" ));
        totalNumLabel = new JLabel( SimStrings.get("ControlLabels.Total" ));
        freeNumLabel = new JLabel( SimStrings.get("ControlLabels.Free") );
        boundNumLabel = new JLabel( SimStrings.get("ControlLabels.Bound") );

        layoutPanel();
//        layoutPanel2();
    }

    private void layoutPanel2() {
        // Layout the panel
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.insets = new Insets( 5, 0, 0, 5 );
        gbc.weightx = 0.5;

        // Ion labels on left
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( anionLabel, gbc );
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        add( cationLabel, gbc );

        // Ion labels on left
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( anionLabel, gbc );
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        add( cationLabel, gbc );

        // Labels over counters
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.insets = new Insets( 15, 0, 0, 5 );
        add( ionsLabel, gbc );
        add( totalNumLabel, gbc );
        add( freeNumLabel, gbc );
        add( boundNumLabel, gbc );

        // Counters
        gbc.insets = new Insets( 5, 0, 0, 5 );
        gbc.gridx = 1;
        gbc.gridy++;
        add( anionSpinner, gbc );
        gbc.gridy++;
        add( cationSpinner, gbc );

        gbc.gridx = 2;
        gbc.gridy = 1;
        add( numFreeAnionTF, gbc );
        gbc.gridy = 2;
        add( numFreeCationTF, gbc );

        gbc.gridx = 3;
        gbc.gridy = 1;
        add( numBoundAnionTF, gbc );
        gbc.gridy = 2;
        add( numBoundCationTF, gbc );
    }

    private void layoutPanel() {

        // Layout the panel
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.insets = new Insets( 5, 0, 0, 5 );
        gbc.weightx = 0.5;

        // Top row
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add( ionsLabel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( cationLabel, gbc );
        add( anionLabel, gbc );

        // Dissolved row
        gbc.gridy++;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.EAST;
        add( freeNumLabel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( numFreeCationTF, gbc );
        add( numFreeAnionTF, gbc );

        // Bound row
        gbc.gridy++;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.EAST;
        add( boundNumLabel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( numBoundCationTF, gbc );
        add( numBoundAnionTF, gbc );

        // Totals row
        gbc.gridy++;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.EAST;
        add( totalNumLabel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( cationSpinner, gbc );
        add( anionSpinner, gbc );

    }

    /**
     *
     */
    private void syncSpinnersWithModel() {
        anionSpinner.setSyncWithDependentIonspinner( false );
        anionSpinner.setValue( new Integer( model.getNumIonsOfType( anionClass ) ) );
        anionSpinner.setSyncWithDependentIonspinner( true );
        cationSpinner.setSyncWithDependentIonspinner( false );
        cationSpinner.setValue( new Integer( model.getNumIonsOfType( cationClass ) ) );
        cationSpinner.setSyncWithDependentIonspinner( true );
        numFreeAnionTF.setText( new Integer( model.getNumFreeIonsOfType( anionClass ) ).toString() );
        numFreeCationTF.setText( new Integer( model.getNumFreeIonsOfType( cationClass ) ).toString() );
        numBoundAnionTF.setText( new Integer( model.getNumBoundIonsOfType( anionClass ) ).toString() );
        numBoundCationTF.setText( new Integer( model.getNumBoundIonsOfType( cationClass ) ).toString() );
    }

    /**
     * @param salt
     */
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
        anionSpinner.setModel( new SpinnerNumberModel( 0, 0, maxIons, anionRatio ) );
        anionSpinner.removeChangeListener( anionSpinnerListener );
        anionSpinnerListener = new SaltSpinnerPanel.IonSpinnerChangeListener( anionSpinner,
                                                                              cationSpinner,
                                                                              anionClass,
                                                                              anionRatio,
                                                                              cationRatio );
        anionSpinner.addChangeListener( anionSpinnerListener );

        cationSpinner.setModel( new SpinnerNumberModel( 0, 0, maxIons, cationRatio ) );
        cationSpinner.removeChangeListener( cationSpinnerListener );
        cationSpinnerListener = new SaltSpinnerPanel.IonSpinnerChangeListener( cationSpinner,
                                                                               anionSpinner,
                                                                               cationClass,
                                                                               cationRatio,
                                                                               anionRatio );
        cationSpinner.addChangeListener( cationSpinnerListener );
    }

    //----------------------------------------------------------------
    // Ion.ChangeListener implementation
    //----------------------------------------------------------------

    public void stateChanged( Ion.ChangeEvent event ) {
        syncSpinnersWithModel();
    }

    //----------------------------------------------------------------
    // SolubleSaltsModel.ChangeListener implementation
    //----------------------------------------------------------------

    public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
        if( event.isSaltChanged() ) {
            setSalt( event.getModel().getCurrentSalt() );
        }
        if( event.isModelReset() ) {
            syncSpinnersWithModel();
        }
    }

    public void reset( SolubleSaltsModel.ChangeEvent event ) {
        // noop
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
                        // Remove a free ion if one is avaliable
                        java.util.List ions = model.getIonsOfType( ionClass );
                        boolean found = false;
                        for( int j = 0; j < ions.size() && !found; j++ ) {
                            Ion ion = (Ion)ions.get( j );
                            if( !ion.isBound() ) {
                                model.removeModelElement( ion );
                                found = true;
                            }
                        }
                        // If a free ion wasn't available, remove one from a crystal
                        if( !found ) {
                            java.util.List crystals = model.getCrystals();
                            Crystal crystal = (Crystal)crystals.get( random.nextInt( crystals.size() ));
                            crystal.releaseIon( SolubleSaltsConfig.DT );
                        }
                    }
                }
                dependentSpinner.setValue( new Integer( model.getNumIonsOfType( ionClass ) * dependentIonRatio / ionRatio ) );
            }
        }
    }

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
