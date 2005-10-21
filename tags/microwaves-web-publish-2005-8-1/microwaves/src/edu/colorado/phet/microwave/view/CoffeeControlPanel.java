/**
 * Class: MicrowaveControlPanel
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwave.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.microwave.CoffeeModule;
import edu.colorado.phet.microwave.MicrowaveModule;
import edu.colorado.phet.microwave.model.MicrowaveModel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CoffeeControlPanel extends JPanel {

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
//    private PowerManager powerManager;
//    private boolean ovenOn;
//    private boolean powerOn;

    public CoffeeControlPanel( MicrowaveModule module, MicrowaveModel model ) {
        this.module = module;
        this.model = model;
        layoutPanel();
    }

    private void layoutPanel() {

        // Create the controls
        module.setMicrowaveFrequency( 0.002 );
        module.setMicrowaveAmplitude( 0.33 );

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
            GraphicsUtil.addGridBagComponent( powerBtnPane, pct100RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( powerBtnPane, pct75RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( powerBtnPane, pct50RB, 0, rowIdx3++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( powerBtnPane, pct25RB, 0, rowIdx3++, 1, 1,
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
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, splineViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, singleViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, fullViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, noFieldViewRB, 0, rowIdx2++, 1, 1,
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
            GraphicsUtil.addGridBagComponent( this, onOffBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, powerBtnPane,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fieldViewRBPane,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, resetBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

//        this.setPreferredSize( new Dimension( 150, 400 ));
//

        // Set initial conditions
        fullViewRB.setSelected( true );
        setFieldView();
        pct100RB.setSelected( true );
        setPowerLevel();
    }

    private ActionListener fieldViewActionListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            setFieldView();
//            JRadioButton selection = GraphicsUtil.getSelection( fieldViewBtnGrp );
//            if( selection == noFieldViewRB ) {
//                module.setFieldViewOff();
//            }
//            if( selection == fullViewRB ) {
//                module.setFieldViewFull();
//            }
//            if( selection == singleViewRB ) {
//                module.setFieldViewSingle();
//            }
//            if( selection == splineViewRB ) {
//                module.setFieldViewSingle();
//                module.setFiledViewSpline();
//            }
        }
    };

    private void setFieldView() {
        JRadioButton selection = GraphicsUtil.getSelection( fieldViewBtnGrp );
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
//            JRadioButton selection = GraphicsUtil.getSelection( powerBtnGrp );
//            if( selection == pct100RB ) {
//                ( (CoffeeModule)module ).setPowerLevel( 1.0 );
//            }
//            if( selection == pct75RB ) {
//                ( (CoffeeModule)module ).setPowerLevel( 0.75 );
//            }
//            if( selection == pct50RB ) {
//                ( (CoffeeModule)module ).setPowerLevel( 0.5 );
//            }
//            if( selection == pct25RB ) {
//                ( (CoffeeModule)module ).setPowerLevel( 0.25 );
//            }
        }
    };

    private void setPowerLevel() {
        JRadioButton selection = GraphicsUtil.getSelection( powerBtnGrp );
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
}
