// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.modules.RateExperimentsModule;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.util.Resetable;

/**
 * ExperimentSetupPanel
 * <p/>
 * Contains the controls for setting up and running an experiment
 *
 * @author Ron LeMaster
 */
public class ExperimentSetupPanel extends JPanel implements Resetable {
    private static final int BEGIN_EXPERIMENT_DELAY_MS = 1000;

    private IntegerRangeSpinner spinnerA, spinnerBC, spinnerAB, spinnerC;
    private HashMap moleculeTypeToGenerator = new HashMap();
    private RateExperimentsModule module;
    private InitialTemperaturePanel initialTemperaturePanel;
    private JButton goButton;

    public ExperimentSetupPanel( final RateExperimentsModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        new MoleculeCounter( MoleculeA.class, module.getMRModel() );
        new MoleculeCounter( MoleculeBC.class, module.getMRModel() );
        new MoleculeCounter( MoleculeAB.class, module.getMRModel() );
        new MoleculeCounter( MoleculeC.class, module.getMRModel() );

        // Create a generator for molecule parameters
        Rectangle2D r = module.getMRModel().getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        MoleculeParamGenerator moleculeAParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                                                        module.getMRModel(),
                                                                                                        .1,
                                                                                                        0,
                                                                                                        Math.PI * 2,
                                                                                                        MoleculeA.class );
        MoleculeParamGenerator moleculeBCParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                                                         module.getMRModel(),
                                                                                                         .1,
                                                                                                         0,
                                                                                                         Math.PI * 2,
                                                                                                         MoleculeBC.class );
        MoleculeParamGenerator moleculeABParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                                                         module.getMRModel(),
                                                                                                         .1,
                                                                                                         0,
                                                                                                         Math.PI * 2,
                                                                                                         MoleculeAB.class );
        MoleculeParamGenerator moleculeCParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                                                        module.getMRModel(),
                                                                                                        .1,
                                                                                                        0,
                                                                                                        Math.PI * 2,
                                                                                                        MoleculeC.class );
        moleculeTypeToGenerator.put( MoleculeA.class, moleculeAParamGenerator );
        moleculeTypeToGenerator.put( MoleculeBC.class, moleculeBCParamGenerator );
        moleculeTypeToGenerator.put( MoleculeAB.class, moleculeABParamGenerator );
        moleculeTypeToGenerator.put( MoleculeC.class, moleculeCParamGenerator );

        // Create the controls
        JLabel topLineLbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.topLine" ) );
        JLabel numALbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.numA" ) );
        JLabel numBCLbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.numBC" ) );
        JLabel numABLbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.numAB" ) );
        JLabel numCLbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.numC" ) );

        // Make the text fields for the number of molecules
        spinnerA = new IntegerRangeSpinner( 0, MRConfig.MAX_INITIAL_MOLECULES );
        spinnerBC = new IntegerRangeSpinner( 0, MRConfig.MAX_INITIAL_MOLECULES );
        spinnerAB = new IntegerRangeSpinner( 0, MRConfig.MAX_INITIAL_MOLECULES );
        spinnerC = new IntegerRangeSpinner( 0, MRConfig.MAX_INITIAL_MOLECULES );

        // The GO button
        goButton = new JButton( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.go" ) );
        goButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( module.isExperimentRunning() ) {
                    endExperiment();
                }
                else {
                    beginExperiment();
                }
            }
        });

        // Add a border
        setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.title" ) ) );

        // Lay out the controls
        GridBagConstraints c = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                       1, 1, 1, 1,
                                                       GridBagConstraints.WEST,
                                                       GridBagConstraints.NONE,
                                                       new Insets( 2, 3, 3, 3 ),
                                                       0, 0 );

        GridBagConstraints textFieldGbc = new GridBagConstraints( 1, 1,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 2, 3, 3, 3 ),
                                                                  0, 0 );
        c.gridx = 0;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.WEST;
        add( new JLabel( MRConfig.RESOURCES.getLocalizedString( "Control.selectReaction" ) ), c );
        c.anchor = GridBagConstraints.CENTER;
        add( new ReactionChooserComboBox( module ), c );

        // Header
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.WEST;
        add( topLineLbl, c );

        // Labels
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        add( numALbl, c );
        add( numABLbl, c );
        c.gridy = 0;
        c.gridx = 2;
        c.gridy = GridBagConstraints.RELATIVE;
        add( numBCLbl, c );
        add( numCLbl, c );

        // Text fields
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( spinnerA, textFieldGbc );
        add( spinnerAB, textFieldGbc );
        textFieldGbc.gridy = 1;
        textFieldGbc.gridx = 3;
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( spinnerBC, textFieldGbc );
        add( spinnerC, textFieldGbc );

        // Initial temperature slider:
        initialTemperaturePanel = new InitialTemperaturePanel( module );

        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add( initialTemperaturePanel, c );

        // Buttons on button panel
        JPanel buttonPanel = new JPanel( new GridBagLayout() );

        c.anchor = GridBagConstraints.CENTER;
        buttonPanel.add( goButton );

        // Button panel
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;

        add( buttonPanel, c );
    }

    private void generateMolecules( Class moleculeClass, int numMolecules ) {
        MRModel model = module.getMRModel();

        // Adding molecules?
        if( numMolecules > 0 ) {
            for( int i = 0; i < numMolecules; i++ ) {
                MoleculeParamGenerator generator = (MoleculeParamGenerator)moleculeTypeToGenerator.get( moleculeClass );
                AbstractMolecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                     generator );
                if( m instanceof CompositeMolecule ) {
                    CompositeMolecule cm = (CompositeMolecule)m;
                    for( int j = 0; j < cm.getComponentMolecules().length; j++ ) {
                        model.addModelElement( cm.getComponentMolecules()[j] );
                    }
                }
                model.addModelElement( m );
            }
        }
        // Removing molecules?
        else {
            for( int i = numMolecules; i < 0; i++ ) {
                List modelElements = model.getModelElements();
                boolean moleculeRemoved = false;
                for( int j = 0; j < modelElements.size() && !moleculeRemoved; j++ ) {
                    Object o = modelElements.get( j );
                    if( moleculeClass.isInstance( o ) ) {
                        if( o instanceof CompositeMolecule ) {
                            CompositeMolecule cm = (CompositeMolecule)o;
                            for( int k = 0; k < cm.getComponentMolecules().length; k++ ) {
                                model.removeModelElement( cm.getComponentMolecules()[k] );
                            }
                        }
                        model.removeModelElement( (ModelElement)o );
                    }
                }
            }
        }
    }

    private void setInitialMoleculeCountsEnabled( boolean editable ) {
        spinnerA.setEnabled( editable );
        spinnerBC.setEnabled( editable );
        spinnerAB.setEnabled( editable );
        spinnerC.setEnabled( editable );
    }

    /**
     * Resets everything
     */
    public void reset() {
        endExperiment();

        spinnerA.setIntValue( 0 );
        spinnerBC.setIntValue( 0 );
        spinnerAB.setIntValue( 0 );
        spinnerC.setIntValue( 0 );

        if( module.isActive() ) {
            module.getClock().start();
        }
        module.resetStripChart();
        initialTemperaturePanel.reset();
    }

    public void endExperiment() {

        goButton.setText( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.go" ) );
        
        // enable the initial molecule counts
        setInitialMoleculeCountsEnabled( true );

        module.getClock().pause();

        module.setExperimentRunning( false );
    }

    private void beginExperiment() {

        // update button immediately so that user gets immediate feedback
        goButton.setText( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.stop" ) );
        
        // disable button until experiment begins, so that user can't click it again
        goButton.setEnabled( false );  
        
        // disable the initial molecule counts
        setInitialMoleculeCountsEnabled( false );
        
        // clear existing molecules
        module.getMRModel().removeAllMolecules();
        
        module.resetStripChart(); // pauses the strip chart recording

        // wait a bit with the box cleared, then start the experiment
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                generateMolecules( MoleculeA.class, spinnerA.getIntValue() );
                generateMolecules( MoleculeBC.class, spinnerBC.getIntValue() );
                generateMolecules( MoleculeAB.class, spinnerAB.getIntValue() );
                generateMolecules( MoleculeC.class, spinnerC.getIntValue() );

                module.setExperimentRunning( true ); // restart the recording
                module.getClock().start();

                goButton.setEnabled( true ); // enable button
            }
        };
        Timer timer = new Timer( BEGIN_EXPERIMENT_DELAY_MS, actionListener );
        timer.setRepeats( false );
        timer.start();
    }

    public boolean isTemperatureBeingAdjusted() {
        return initialTemperaturePanel.isTemperatureBeingAdjusted();
    }
}
