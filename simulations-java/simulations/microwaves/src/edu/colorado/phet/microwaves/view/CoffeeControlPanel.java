/**
 * Class: MicrowaveControlPanel
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.CoffeeModule;
import edu.colorado.phet.microwaves.MicrowaveModule;
import edu.colorado.phet.microwaves.model.MicrowaveModel;

public class CoffeeControlPanel extends JPanel {
    static private double DEFAULT_FREQUENCY = 0.002;
    ;
    static private double DEFAULT_AMPLITUDE = 0.33;

    MicrowaveModel model;
    MicrowaveModule module;
    PhetApplication application;
    private JRadioButton noFieldViewRB;
    private JRadioButton fullViewRB;
    private JRadioButton singleViewRB;
    private ButtonGroup fieldViewBtnGrp;
    private JRadioButton splineViewRB;
    private JRadioButton pct100RB;
    private JRadioButton pct75RB;
    private JRadioButton pct50RB;
    private JRadioButton pct25RB;
    private ButtonGroup powerBtnGrp;

    public CoffeeControlPanel( MicrowaveModule module, MicrowaveModel model ) {
        this.module = module;
        this.model = model;
        layoutPanel();
    }

    private void layoutPanel() {

        // Create the controls

        module.setMicrowaveFrequency( DEFAULT_FREQUENCY );
        module.setMicrowaveAmplitude( DEFAULT_AMPLITUDE );

        // Button to toggle the microwave
        JButton onOffBtn = new JButton( SimStrings.get( "CoffeeControlPanel.StartStopButton" ) );
        onOffBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.toggleMicrowave();
            }
        } );

        powerBtnGrp = new ButtonGroup();
        pct100RB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.FullPowerRadioButton" ) );
        pct100RB.addActionListener( powerBtnActionListener );
        powerBtnGrp.add( pct100RB );
        pct75RB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.ThreeQuarterPowerRadioButton" ) );
        pct75RB.addActionListener( powerBtnActionListener );
        powerBtnGrp.add( pct75RB );
        pct50RB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.HalfPowerRadioButton" ) );
        pct50RB.addActionListener( powerBtnActionListener );
        powerBtnGrp.add( pct50RB );
        pct25RB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.QuarterPowerRadioButton" ) );
        pct25RB.addActionListener( powerBtnActionListener );
        powerBtnGrp.add( pct25RB );
        JPanel powerBtnPane = new JPanel( new GridBagLayout() );
        int rowIdx3 = 0;
        try {
            SwingUtils.addGridBagComponent( powerBtnPane, pct100RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( powerBtnPane, pct75RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( powerBtnPane, pct50RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( powerBtnPane, pct25RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        powerBtnPane.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "CoffeeControlPanel.PowerBorderTitle" ) ) );

        // Button to set field display type
        noFieldViewRB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.NoneRadioButton" ) );
        noFieldViewRB.addActionListener( fieldViewActionListener );
        fullViewRB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.FullFieldRadioButton" ) );
        fullViewRB.addActionListener( fieldViewActionListener );
        singleViewRB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.SingleLineRadioButton" ) );
        singleViewRB.addActionListener( fieldViewActionListener );
        splineViewRB = new JRadioButton( SimStrings.get( "CoffeeControlPanel.CurveRadioButton" ) );
        splineViewRB.addActionListener( fieldViewActionListener );
        fieldViewBtnGrp = new ButtonGroup();
        fieldViewBtnGrp.add( fullViewRB );
        fieldViewBtnGrp.add( singleViewRB );
        fieldViewBtnGrp.add( splineViewRB );
        fieldViewBtnGrp.add( noFieldViewRB );
        fieldViewActionListener.actionPerformed( null );


        JPanel fieldViewRBPane = new JPanel( new GridBagLayout() );
        int rowIdx2 = 0;
        try {
            SwingUtils.addGridBagComponent( fieldViewRBPane, splineViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, singleViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, fullViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, noFieldViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        fieldViewRBPane.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "CoffeeControlPanel.FieldViewBorderTitle" ) ) );

        // A Reset onOffBtn
        JButton resetBtn = new JButton( SimStrings.get( "CoffeeControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Lay out the controls
        this.setPreferredSize( new Dimension( 150, 400 ) );

        EmptyBorder emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder();
        this.setBorder( emptyBorder );
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            SwingUtils.addGridBagComponent( this, onOffBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, powerBtnPane,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, fieldViewRBPane,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, resetBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

        // Set initial conditions
        fullViewRB.setSelected( true );
        setFieldView();
        pct100RB.setSelected( true );
        setPowerLevel();
    }

    private ActionListener fieldViewActionListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            setFieldView();
        }
    };

    private void setFieldView() {
        JRadioButton selection = SwingUtils.getSelection( fieldViewBtnGrp );
        if( selection == noFieldViewRB ) {
            module.setFieldViewOff();
        }
        if( selection == fullViewRB ) {
            module.setFieldViewFull();
        }
        if( selection == singleViewRB ) {
            module.setFieldViewSingle();
        }
        if( selection == splineViewRB ) {
            module.setFieldViewSingle();
            module.setFiledViewSpline();
        }
    }

    private ActionListener powerBtnActionListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            setPowerLevel();
        }
    };

    private void setPowerLevel() {

        JRadioButton selection = SwingUtils.getSelection( powerBtnGrp );
        if( selection == pct100RB ) {
            ( (CoffeeModule)module ).setPowerLevel( 1.0 );
        }
        if( selection == pct75RB ) {
            ( (CoffeeModule)module ).setPowerLevel( 0.75 );
        }
        if( selection == pct50RB ) {
            ( (CoffeeModule)module ).setPowerLevel( 0.5 );
        }
        if( selection == pct25RB ) {
            ( (CoffeeModule)module ).setPowerLevel( 0.25 );
        }
    }

    public void reset() {
        fullViewRB.setSelected( true );
        setFieldView();
        pct100RB.setSelected( true );
        setPowerLevel();
    }
}
