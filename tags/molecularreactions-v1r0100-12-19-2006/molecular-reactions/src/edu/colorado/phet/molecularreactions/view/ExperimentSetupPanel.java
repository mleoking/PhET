/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.RateExperimentsModule;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.RangeLimitedIntegerTextField;
import edu.colorado.phet.molecularreactions.util.Resetable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

/**
 * ExperimentSetupPanel
 * <p/>
 * Contains the controls for setting up and running an experiment
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExperimentSetupPanel extends JPanel implements Resetable {
    private JTextField numATF;
    private JTextField numBCTF;
    private JTextField numABTF;
    private JTextField numCTF;
    private MoleculeParamGenerator moleculeParamGenerator;
    private MoleculeParamGenerator moleculeAParamGenerator;
    private MoleculeParamGenerator moleculeBCParamGenerator;
    private MoleculeParamGenerator moleculeABParamGenerator;
    private MoleculeParamGenerator moleculeCParamGenerator;
    private HashMap moleculeTypeToGenerator = new HashMap( );
    private RateExperimentsModule module;
    private MoleculeCounter moleculeACounter;
    private MoleculeCounter moleculeBCCounter;
    private MoleculeCounter moleculeABCounter;
    private MoleculeCounter moleculeCCounter;

    /**
     * @param module
     */
    public ExperimentSetupPanel( RateExperimentsModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        moleculeACounter = new MoleculeCounter( MoleculeA.class, module.getMRModel() );
        moleculeBCCounter = new MoleculeCounter( MoleculeBC.class, module.getMRModel() );
        moleculeABCounter = new MoleculeCounter( MoleculeAB.class, module.getMRModel() );
        moleculeCCounter = new MoleculeCounter( MoleculeC.class, module.getMRModel() );

        // Create a generator for molecule parameters
        Rectangle2D r = module.getMRModel().getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        moleculeAParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                   module.getMRModel(),
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2,
                                                                   MoleculeA.class );
        moleculeBCParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                   module.getMRModel(),
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2,
                                                                   MoleculeBC.class );
        moleculeABParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                   module.getMRModel(),
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2,
                                                                   MoleculeAB.class );
        moleculeCParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                   module.getMRModel(),
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2,
                                                                   MoleculeC.class );
        moleculeTypeToGenerator.put( MoleculeA.class, moleculeAParamGenerator );
        moleculeTypeToGenerator.put( MoleculeBC.class, moleculeBCParamGenerator );
        moleculeTypeToGenerator.put( MoleculeAB.class, moleculeABParamGenerator );
        moleculeTypeToGenerator.put( MoleculeC.class, moleculeCParamGenerator );

//        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds,
//                                                                   5,
//                                                                   .1,
//                                                                   0,
//                                                                   Math.PI * 2 );

        // Create the controls
        JLabel topLineLbl = new JLabel( SimStrings.get( "ExperimentSetup.topLine" ) );
        JLabel numALbl = new JLabel( SimStrings.get( "ExperimentSetup.numA" ) );
        JLabel numBCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numBC" ) );
        JLabel numABLbl = new JLabel( SimStrings.get( "ExperimentSetup.numAB" ) );
        JLabel numCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numC" ) );

        // Make the text fields for the number of molecules
        int maxMolecules = MRConfig.MAX_MOLECULE_CNT;
        numATF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numBCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numABTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );


        JButton resetBtn = new JButton( SimStrings.get( "ExperimentSet.clear" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );

        // The GO button
        JButton goButton = new JButton( new StartExperimentAction( module, this ) );

        // Add a border
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ExperimentSetup.title" ) ) );

        // Lay out the controls
        GridBagConstraints labelGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
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
        labelGbc.gridx = 0;
        labelGbc.gridwidth = 4;
        labelGbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.get( "Control.selectReaction" ) ), labelGbc );
        labelGbc.anchor = GridBagConstraints.CENTER;
        add( new ReactionChooserComboBox( module ), labelGbc );

        // Header
        labelGbc.gridwidth = 4;
        labelGbc.anchor = GridBagConstraints.WEST;
        add( topLineLbl, labelGbc );

        // Labels
        labelGbc.gridwidth = 1;
        labelGbc.anchor = GridBagConstraints.EAST;
        add( numALbl, labelGbc );
        add( numABLbl, labelGbc );
        labelGbc.gridy = 0;
        labelGbc.gridx = 2;
        labelGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCLbl, labelGbc );
        add( numCLbl, labelGbc );

        // Text fields
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numATF, textFieldGbc );
        add( numABTF, textFieldGbc );
        textFieldGbc.gridy = 1;
        textFieldGbc.gridx = 3;
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCTF, textFieldGbc );
        add( numCTF, textFieldGbc );

        // Buttons
        labelGbc.gridx = 0;
        labelGbc.gridwidth = 4;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.anchor = GridBagConstraints.CENTER;
        add( goButton, labelGbc );
        add( resetBtn, labelGbc );
    }

    /**
     *
     * @param moleculeClass
     * @param numMolecules
     */
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

    /**
     *
     * @param editable
     */
    private void setInitialConditionsEditable( boolean editable ) {
        numATF.setEditable( editable );
        numBCTF.setEditable( editable );
        numABTF.setEditable( editable );
        numCTF.setEditable( editable );
    }

    /**
     * Resets everything
     */
    public void reset() {

        setInitialConditionsEditable( true );
        numATF.setText( "0" );
        numBCTF.setText( "0" );
        numABTF.setText( "0" );
        numCTF.setText( "0" );

        generateMolecules( MoleculeA.class, -moleculeACounter.getCnt() );
        generateMolecules( MoleculeBC.class, -moleculeBCCounter.getCnt() );
        generateMolecules( MoleculeAB.class, -moleculeABCounter.getCnt() );
        generateMolecules( MoleculeC.class, -moleculeCCounter.getCnt() );

        module.setExperimentRunning( false );
        module.resetStripChart();
//        module.setStripChartRecording( true );
        module.getClock().start();
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Action for starting an experiment
     */
    private static class StartExperimentAction extends AbstractAction {
        private RateExperimentsModule module;
        private ExperimentSetupPanel panel;


        public StartExperimentAction( RateExperimentsModule module, ExperimentSetupPanel panel ) {
            super( SimStrings.get( "ExperimentSetup.go" ) );
            this.module = module;
            this.panel = panel;
        }

        public void actionPerformed( ActionEvent e ) {
            module.getMRModel().removeAllMolecules();
            panel.generateMolecules( MoleculeA.class, Integer.parseInt( panel.numATF.getText() ) );
            panel.generateMolecules( MoleculeBC.class, Integer.parseInt( panel.numBCTF.getText() ) );
            panel.generateMolecules( MoleculeAB.class, Integer.parseInt( panel.numABTF.getText() ) );
            panel.generateMolecules( MoleculeC.class, Integer.parseInt( panel.numCTF.getText() ) );

            module.resetStripChart();
            module.setExperimentRunning( true );
            panel.setInitialConditionsEditable( false );
        }
    }
}
