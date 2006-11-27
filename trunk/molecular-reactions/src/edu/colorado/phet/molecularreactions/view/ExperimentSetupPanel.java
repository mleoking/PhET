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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.RangeLimitedIntegerTextField;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

/**
 * ExperimentSetupPanel
 * <p>
 * Contains the controls for setting up and running an experiment
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExperimentSetupPanel extends JPanel {
    private JTextField numATF;
    private JTextField numBCTF;
    private JTextField numABTF;
    private JTextField numCTF;
    private MoleculeParamGenerator moleculeParamGenerator;
    private ComplexModule module;
    private MoleculeCounter moleculeACounter;
    private MoleculeCounter moleculeBCCounter;
    private MoleculeCounter moleculeABCounter;
    private MoleculeCounter moleculeCCounter;

    private boolean hasBeenReset = true;
    private JButton resetBtn;

    /**
     *
     * @param module
     */
    public ExperimentSetupPanel( ComplexModule module ) {
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
        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds,
                                                                   5,
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2 );

        // Create the controls
        JLabel topLineLbl = new JLabel( SimStrings.get( "ExperimentSetup.topLine" ) );
        JLabel numALbl = new JLabel( SimStrings.get( "ExperimentSetup.numA" ) );
        JLabel numBCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numBC" ) );
        JLabel numABLbl = new JLabel( SimStrings.get( "ExperimentSetup.numAB" ) );
        JLabel numCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numC" ) );

        int maxMolecules = MRConfig.MAX_MOLECULE_CNT;
        numATF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numBCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numABTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );

        // Must create reset button before the stop/go button, because the
        // stop/go button references the reset button
        resetBtn = new JButton( SimStrings.get( "ExperimentSet.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        JButton goBtn = new GoStopBtn( module );

        // Add a border
        setBorder( ControlBorderFactory.createPrimaryBorder( "Experimental Controls" ) );

        // Lay out the controls
        GridBagConstraints labelGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 2, 3, 3, 3 ),
                                                              0, 0 );
        GridBagConstraints textFieldGbc = new GridBagConstraints( 1, 1,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 2, 3, 3, 3 ),
                                                                  0, 0 );
        labelGbc.gridwidth = 2;
        add( topLineLbl, labelGbc );
        labelGbc.gridwidth = 1;
        labelGbc.anchor = GridBagConstraints.EAST;
        add( numALbl, labelGbc );
        add( numBCLbl, labelGbc );
        add( numABLbl, labelGbc );
        add( numCLbl, labelGbc );

        add( numATF, textFieldGbc );
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCTF, textFieldGbc );
        add( numABTF, textFieldGbc );
        add( numCTF, textFieldGbc );

        labelGbc.gridwidth = 2;
        labelGbc.anchor = GridBagConstraints.CENTER;
        add( new ReactionChooserComboBox( module ), labelGbc );
        add( goBtn, labelGbc );
        add( resetBtn, labelGbc );
    }

    /**
     * Adds molecules to the model as specified in the controls
     */
    private void startExperiment() {

        int dA = Integer.parseInt( numATF.getText() ) - moleculeACounter.getCnt();
        int dBC = Integer.parseInt( numBCTF.getText() ) - moleculeBCCounter.getCnt();
        int dAB = Integer.parseInt( numABTF.getText() ) - moleculeABCounter.getCnt();
        int dC = Integer.parseInt( numCTF.getText() ) - moleculeCCounter.getCnt();

        generateMolecules( MoleculeA.class, dA );
        generateMolecules( MoleculeBC.class, dBC );
        generateMolecules( MoleculeAB.class, dAB );
        generateMolecules( MoleculeC.class, dC );
//        generateMolecules( MoleculeA.class, Integer.parseInt( numATF.getText() ));
//        generateMolecules( MoleculeBC.class, Integer.parseInt( numBCTF.getText() ));
//        generateMolecules( MoleculeAB.class, Integer.parseInt( numABTF.getText() ));
//        generateMolecules( MoleculeC.class, Integer.parseInt( numCTF.getText() ));

        module.setStripChartVisible( true );
        if( hasBeenReset ) {
            module.rescaleStripChart();
            hasBeenReset = false;
        }
    }

    private void generateMolecules( Class moleculeClass, int numMolecules ) {
        MRModel model = module.getMRModel();

        // Adding molecules?
        if( numMolecules > 0 ) {
            for( int i = 0; i < numMolecules; i++ ) {
                AbstractMolecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                     moleculeParamGenerator );
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

    private void reset() {

        numATF.setText( "0" );
        numBCTF.setText( "0" );
        numABTF.setText( "0" );
        numCTF.setText( "0" );

        generateMolecules( MoleculeA.class, -moleculeACounter.getCnt() );
        generateMolecules( MoleculeBC.class, -moleculeBCCounter.getCnt() );
        generateMolecules( MoleculeAB.class, -moleculeABCounter.getCnt() );
        generateMolecules( MoleculeC.class, -moleculeCCounter.getCnt() );


        hasBeenReset = true;
    }


    /**
     * Three state button for controlling the experiment
     */
    private class GoStopBtn extends JButton {
        private Object go = new Object();
        private Object stop = new Object();
        private Object setup = new Object();
        private Object state = setup;
        private String goString = SimStrings.get( "ExperimentSetup.go" );
        private String stopString = SimStrings.get( "ExperimentSetup.stop" );
        private String setupString = SimStrings.get( "ExperimentSetup.setup" );
        private Color goColor = Color.green;
        private Color stopColor = Color.red;
        private Color setupColor = Color.yellow;
        private IClock clock;
        private MRModule module;

        public GoStopBtn( MRModule module ) {
            this.clock = module.getClock();
            this.module = module;
            setState( stop );
            addActionListener( new ActionHandler() );
        }

        private void setState( Object state ) {
            this.state = state;
            if( state == go ) {
                clock.start();
                resetBtn.setEnabled( false );
                startExperiment();
                setText( stopString );
                setBackground( stopColor );
            }
            if( state == stop ) {
                clock.pause();
                resetBtn.setEnabled( true );
                setText( goString );
                setBackground( goColor );
            }
        }


        private class ActionHandler implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                if( state == go ) {
                    setState( stop );
                }
                else if( state == stop ) {
                    setState( go );
                }
                else if( state == setup ) {
                    module.reset();
                    clock.pause();
                    state = go;
                    setText( goString );
                    setBackground( goColor );
                }
            }
        }
    }
}
