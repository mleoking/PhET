/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.propagators.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 13, 2006
 * Time: 10:46:25 AM
 * Copyright (c) Feb 13, 2006 by Sam Reid
 */

public class PropagatorPanel extends VerticalLayoutPanel {
    private QWIModel QWIModel;
    private ClassicalWavePropagator classicalPropagator2ndOrder;

    public PropagatorPanel( QWIModel QWIModel ) {
        this.QWIModel = QWIModel;

        VerticalLayoutPanel propagatorPanel = this;
        propagatorPanel.setBorder( BorderFactory.createTitledBorder( "Propagator" ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton richardson = createPropagatorButton( buttonGroup, "Richardson", new RichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getPotential(), 1, 1 ) );
        propagatorPanel.add( richardson );

        JRadioButton modified = createPropagatorButton( buttonGroup, "Modified Richardson", new ModifiedRichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getPotential(), 1, 1 ) );
        propagatorPanel.add( modified );

        JRadioButton crank = createPropagatorButton( buttonGroup, "Crank-Nicholson?", new CrankNicholsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        propagatorPanel.add( crank );

        JRadioButton light = createPropagatorButton( buttonGroup, "Avg", new AveragePropagator( getDiscreteModel().getPotential() ) );
        propagatorPanel.add( light );

        classicalPropagator2ndOrder = new ClassicalWavePropagator( getDiscreteModel().getPotential() );
        JRadioButton lap = createPropagatorButton( buttonGroup, "finite difference", classicalPropagator2ndOrder );

        JRadioButton som = createPropagatorButton( buttonGroup, "Split Operator", new SplitOperatorPropagator( getDiscreteModel(), getDiscreteModel().getPotential() ) );
        propagatorPanel.add( som );

        propagatorPanel.add( lap );
        lap.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                initClassicalWave( classicalPropagator2ndOrder );
            }
        } );
    }

    private QWIModel getDiscreteModel() {
        return QWIModel;
    }

    private JRadioButton createPropagatorButton( ButtonGroup buttonGroup, String s, final Propagator propagator ) {
        final JRadioButton radioButton = new JRadioButton( s );
        buttonGroup.add( radioButton );
        if( getDiscreteModel().getPropagator().getClass().equals( propagator.getClass() ) ) {
            radioButton.setSelected( true );
        }
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setPropagator( propagator );
            }
        } );
        radioButton.setSelected( getDiscreteModel().getPropagator().getClass().isAssignableFrom( propagator.getClass() ) );
        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void propagatorChanged() {
                radioButton.setSelected( getDiscreteModel().getPropagator().getClass().equals( propagator.getClass() ) );
            }
        } );
        return radioButton;
    }

    private void initClassicalWave( ClassicalWavePropagator propagator2ndOrder ) {
//        propagator2ndOrder.
//        initialConditionPanel.initClassicalWave( propagator2ndOrder );
    }

}
