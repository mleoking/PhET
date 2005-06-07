/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.Uranium238;
import edu.colorado.phet.coreadditions.ModelSlider;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class ControlledChainReactionControlPanel extends JPanel {

    //
    // Static fields and methods
    //
    private static Random random = new Random();
    private static final int U235 = 1;
    private static final int U238 = 2;

    //
    // Instance fields and methods
    //
    private ChainReactionModule module;
    private JSpinner numU235Spinner;
    private JSpinner numU238Spinner;
    private JTextField percentDecayTF;
    private int startNumU235;

    public ControlledChainReactionControlPanel( final ControlledFissionModule module ) {
        super();
        this.module = module;

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "MultipleNucleusFissionControlPanel.ControlBorder" ) );
        this.setBorder( titledBorder );

        //-----------------------------------
        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                SwingUtilities.getWindowAncestor( ControlledChainReactionControlPanel.this ).validate();
            }
        } );

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                int modelNum = module.getU235Nuclei().size();
                int viewNum = ( (Integer)numU235Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU235Spinner.setEnabled( false );
                    numU235Spinner.setValue( new Integer( module.getU235Nuclei().size() ) );
                    numU235Spinner.setEnabled( true );
                }

                // Compute and display the number of U235 nuclei that have fissioned
                if( startNumU235 != 0 ) {
                    percentDecayTF.setText( Integer.toString( ( startNumU235 - modelNum ) * 100 / startNumU235 ) );
                }

                modelNum = module.getU238Nuclei().size();
                viewNum = ( (Integer)numU238Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU238Spinner.setEnabled( false );
                    numU238Spinner.setValue( new Integer( module.getU238Nuclei().size() ) );
                    numU238Spinner.setEnabled( true );
                }
            }
        } );

        // Create the controls
        JButton fireNeutronBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.FireButton" ) );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
                percentDecayTF.setText( "0" );
                percentDecayTF.setEditable( false );
                percentDecayTF.setBackground( Color.white );
                startNumU235 = ( (Integer)numU235Spinner.getValue() ).intValue();
            }
        } );

        Font spinnerFont = new Font( "SansSerif", Font.BOLD, 40 );
        numU235Spinner = new JSpinner( new SpinnerNumberModel( 1, 0, 200, 1 ) );
        numU235Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int netNuclei = setNumU235Nuclei( ( (Integer)numU235Spinner.getValue() ).intValue() );
                percentDecayTF.setText( "0" );
            }
        } );
        numU235Spinner.setPreferredSize( new Dimension( 80, 30 ) );
        numU235Spinner.setFont( spinnerFont );

        numU238Spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 200, 1 ) );
        numU238Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU238Nuclei( ( (Integer)numU238Spinner.getValue() ).intValue() );
                percentDecayTF.setText( "0" );
            }
        } );
        numU238Spinner.setPreferredSize( new Dimension( 80, 30 ) );
        numU238Spinner.setFont( spinnerFont );

        // The reset button
        JButton resetBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                startNumU235 = 0;
                percentDecayTF.setText( "0" );
                numU235Spinner.setValue( new Integer( 1 ) );
                numU238Spinner.setValue( new Integer( 0 ) );
            }
        } );

        percentDecayTF = new JTextField( 4 );
        percentDecayTF.setHorizontalAlignment( JTextField.RIGHT );
        percentDecayTF.setText( "0" );
        percentDecayTF.setEditable( false );
        percentDecayTF.setBackground( Color.white );


        // Layout the panel
        setLayout( new GridBagLayout() );
        GridBagConstraints gbcLeft = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcRight = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcCenter = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                               2, 1, 1, 1, GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 5, 5, 5, 5 ), 5, 5 );
//        gbcLeft.gridy = 1;
//        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.235ULabel" ) ), gbcLeft );
//        gbcRight.gridy = 1;
//        add( numU235Spinner, gbcRight );
//        gbcLeft.gridy = 2;
//        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.238ULabel" ) ), gbcLeft );
//        gbcRight.gridy = 2;
//        add( numU238Spinner, gbcRight );
//        gbcLeft.gridy = 3;
//        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.FissionPercentLabel" ) ), gbcLeft );
//        gbcRight.gridy = 3;
//        add( percentDecayTF, gbcRight );
//        gbcCenter.gridy = 4;
        add( fireNeutronBtn, gbcCenter );
//        gbcCenter.gridy = 5;
        add( resetBtn, gbcCenter );

        // Spinner for the number of control rods
        JPanel jp = new JPanel( new GridBagLayout());
        GridBagConstraints jpGbc = new GridBagConstraints( 0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                                           new Insets( 0,0,0,0),0,0 );
        final JSpinner controlRodSpinner = new JSpinner( new SpinnerNumberModel( module.getNumControlRods(),
                                                                           0, 10, 1 ) );
        controlRodSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setNumControlRods( ((Integer)controlRodSpinner.getValue()).intValue() );
            }
        } );
        jp.add( new JLabel( "<html>Number of<br>Control Rods</html"), jpGbc );
        jpGbc.gridx = 1;
        jp.add( controlRodSpinner, jpGbc );

        final JSpinner numNeutronsSpinner = new JSpinner( new SpinnerNumberModel( module.getNumNeutronsFired(),
                                                                                  1, 10, 1) );
        numNeutronsSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setNumNeutronsFired( ((Integer)numNeutronsSpinner.getValue()).intValue() );
            }
        } );
        jpGbc.gridy = 1;
        jpGbc.gridx = 0;
        jp.add( new JLabel( "Number Neutrons"), jpGbc );
        jpGbc.gridx = 1;
        jp.add( numNeutronsSpinner, jpGbc );
        add( jp, gbcCenter);

        // Slider for absorption probability
        final ModelSlider absorptionSlider = new ModelSlider( "Absorption Probability", 0, 1, 1 );
        absorptionSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setAbsorptionProbability( absorptionSlider.getModelValue() );
            }
        } );
        absorptionSlider.setMajorTickSpacing( 0.25 );
        absorptionSlider.setPaintLabels( true );
        absorptionSlider.setPaintTicks( true );
        add( absorptionSlider, gbcCenter );

        // Control for the spacing between nuclei

        final ModelSlider nucleusSpacingSlider = new ModelSlider( "<html>Spacing Between<br>Nuclei (nuc. diam.)</html>",
                                                                  1, 3, 2.5 );
        nucleusSpacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setInterNucleusSpacing( nucleusSpacingSlider.getModelValue());
            }
        } );
        nucleusSpacingSlider.setMajorTickSpacing( 1 );
        nucleusSpacingSlider.setPaintTicks( true );
        nucleusSpacingSlider.setPaintLabels( true );
        add( nucleusSpacingSlider, gbcCenter );
    }

    private synchronized int setNumU235Nuclei( int num ) {
        int netNuclei = 0;
        int delta = num - module.getU235Nuclei().size();
        for( int i = 0; i < delta; i++ ) {
            Nucleus nucleus = module.addU235Nucleus();
            if( nucleus != null ) {
                netNuclei++;
            }
        }
        for( int i = 0; i < -delta; i++ ) {
            int numNuclei = module.getU235Nuclei().size();
            Uranium235 nucleus = (Uranium235)module.getU235Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU235Nucleus( nucleus );
            netNuclei--;
        }
        return netNuclei;
    }

    private void setNumU238Nuclei( int num ) {
        int delta = num - module.getU238Nuclei().size();
        for( int i = 0; i < delta; i++ ) {
            module.addU238Nucleus();
        }
        for( int i = 0; i < -delta; i++ ) {
            int numNuclei = module.getU238Nuclei().size();
            Uranium238 nucleus = (Uranium238)module.getU238Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU238Nucleus( nucleus );
        }
    }
}
