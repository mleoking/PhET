/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.Propagator;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.propagators.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 13, 2006
 * Time: 10:46:25 AM
 */

public class PropagatorPanel extends VerticalLayoutPanel {
    private QWIModel QWIModel;
    private ClassicalWavePropagator classicalPropagator2ndOrder;

    public PropagatorPanel( QWIModel QWIModel ) {
        this.QWIModel = QWIModel;

        VerticalLayoutPanel propagatorPanel = this;
        propagatorPanel.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "propagator" ) ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton richardson = createPropagatorButton( buttonGroup, QWIResources.getString( "richardson" ), new RichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getPotential(), 1, 1 ) );
        propagatorPanel.add( richardson );

        JRadioButton modified = createPropagatorButton( buttonGroup, QWIResources.getString( "modified.richardson" ), new ModifiedRichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getPotential(), 1, 1 ) );
        propagatorPanel.add( modified );

        JRadioButton crank = createPropagatorButton( buttonGroup, QWIResources.getString( "crank.nicholson" ), new CrankNicholsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        propagatorPanel.add( crank );

        JRadioButton light = createPropagatorButton( buttonGroup, QWIResources.getString( "avg" ), new AveragePropagator( getDiscreteModel().getPotential() ) );
        propagatorPanel.add( light );

        classicalPropagator2ndOrder = new ClassicalWavePropagator( getDiscreteModel().getPotential() );
        JRadioButton lap = createPropagatorButton( buttonGroup, QWIResources.getString( "finite.difference" ), classicalPropagator2ndOrder );

        JRadioButton som = createPropagatorButton( buttonGroup, QWIResources.getString( "split.operator" ), new SplitOperatorPropagator( getDiscreteModel(), getDiscreteModel().getPotential() ) );
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
