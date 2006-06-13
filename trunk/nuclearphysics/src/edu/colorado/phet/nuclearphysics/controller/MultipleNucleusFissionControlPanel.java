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
import java.text.DecimalFormat;

public class MultipleNucleusFissionControlPanel extends JPanel {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Random random = new Random();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private MultipleNucleusFissionModule module;
    private JSpinner numU235Spinner;
    private JSpinner numU238Spinner;
    private JTextField percentDecayTF;
    private int startNumU235;
    private JButton fireNeutronBtn;
    private JButton resetBtn;

    /**
     * Constructor
     *
     * @param module
     */
    public MultipleNucleusFissionControlPanel( final MultipleNucleusFissionModule module ) {
        super();
        this.module = module;

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( !(SwingUtilities.getWindowAncestor( MultipleNucleusFissionControlPanel.this )== null )) {
                SwingUtilities.getWindowAncestor( MultipleNucleusFissionControlPanel.this ).validate();
                }
            }
        } );

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                int modelNum = module.getU235Nuclei().size();
                int viewNum = ( (Integer)numU235Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU235Spinner.setValue( new Integer( module.getU235Nuclei().size() ) );
                }

                // Compute and display the number of U235 nuclei that have fissioned
                DecimalFormat percentFormat = new DecimalFormat( "#%" );
                double percent = 0;
                if( startNumU235 != 0 ) {
                    percent = ( (double)( startNumU235 - modelNum ) ) / startNumU235;
                }
                percentDecayTF.setText( percentFormat.format( percent ) );

                modelNum = module.getU238Nuclei().size();
                viewNum = ( (Integer)numU238Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU238Spinner.setValue( new Integer( module.getU238Nuclei().size() ) );
                }
            }
        } );

        // Create the controls
        fireNeutronBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.FireButton" ) );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
                percentDecayTF.setText( "0" );
                percentDecayTF.setEditable( false );
                percentDecayTF.setBackground( Color.white );
                startNumU235 = ( (Integer)numU235Spinner.getValue() ).intValue();
                fireNeutronBtn.setEnabled( false );
                resetBtn.setEnabled( true );
                numU235Spinner.setEnabled( false );
                numU238Spinner.setEnabled( false );
            }
        } );

        //--------------------------------------------------------------------------------------------------
        // Spinners
        //--------------------------------------------------------------------------------------------------

        numU235Spinner = new JSpinner( new SpinnerNumberModel( 1, 0, 200, 1 ) );
        numU235Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU235Nuclei( ( (Integer)numU235Spinner.getValue() ).intValue() );
            }
        } );

        numU238Spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 200, 1 ) );
        numU238Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU238Nuclei( ( (Integer)numU238Spinner.getValue() ).intValue() );
            }
        } );

        //--------------------------------------------------------------------------------------------------
        // Buttons
        //--------------------------------------------------------------------------------------------------

        resetBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                startNumU235 = 0;
                percentDecayTF.setText( "0" );
                numU235Spinner.setValue( new Integer( 1 ) );
                numU238Spinner.setValue( new Integer( 0 ) );
                fireNeutronBtn.setEnabled( true );
                resetBtn.setEnabled( false );
                numU235Spinner.setEnabled( true );
                numU238Spinner.setEnabled( true );
            }
        } );
        resetBtn.setEnabled( false );

        //--------------------------------------------------------------------------------------------------
        // Other controls
        //--------------------------------------------------------------------------------------------------

        percentDecayTF = new JTextField( 4 );
        percentDecayTF.setHorizontalAlignment( JTextField.RIGHT );
        percentDecayTF.setText( "0" );
        percentDecayTF.setEditable( false );
        percentDecayTF.setBackground( Color.white );

        final JCheckBox containmentCB = new JCheckBox( SimStrings.get( "MultipleNucleusFissionControlPanel.ContainmentCheckBox" ) );
//        containmentCB.setForeground( Color.white );
        containmentCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setContainmentEnabled( containmentCB.isSelected() );
            }
        } );


        // Layout the panel
        setLayout( new GridBagLayout() );
        GridBagConstraints gbcLeft = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcRight = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcCenter = new GridBagConstraints( 0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 5, 5, 5, 5 ), 5, 5 );
        add( containmentCB, gbcCenter );
        gbcLeft.gridy = 1;
        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.235ULabel" ) ), gbcLeft );
        gbcRight.gridy = 1;
        add( numU235Spinner, gbcRight );
        gbcLeft.gridy = 2;
        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.238ULabel" ) ), gbcLeft );
        gbcRight.gridy = 2;
        add( numU238Spinner, gbcRight );
        gbcLeft.gridy = 3;
        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.FissionPercentLabel" ) ), gbcLeft );
        gbcRight.gridy = 3;
        add( percentDecayTF, gbcRight );
        gbcCenter.gridy = 4;
        add( fireNeutronBtn, gbcCenter );
        gbcCenter.gridy = 5;
        add( resetBtn, gbcCenter );
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "MultipleNucleusFissionControlPanel.ControlBorder" ) );
        this.setBorder( titledBorder );
    }

    private int setNumU235Nuclei( int num ) {
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