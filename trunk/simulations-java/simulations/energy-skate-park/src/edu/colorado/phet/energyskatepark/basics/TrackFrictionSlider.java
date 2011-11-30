// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author Sam Reid
 */
public class TrackFrictionSlider extends JSlider {

    //TODO lots of 100 literals inlined in this class, can they all be converted to one constant?

    public TrackFrictionSlider( final EnergySkateParkBasicsModule module ) {

        setPreferredSize( new Dimension( 150, getPreferredSize().height ) );
        setLabelTable( new Hashtable<Object, Object>() {{
            put( 0, new JLabel( "none" ) );
            put( 100, new JLabel( "lots" ) );//0.01
        }} );
        setMajorTickSpacing( 100 );
        setPaintTicks( true );
        setPaintLabels( true );
        final ChangeListener frictionChanged = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setCoefficientOfFriction( module.frictionEnabled.get() ? getValue() / 100.0 / 100.0 : 0.0 );
            }
        };
        addChangeListener( frictionChanged );
        module.frictionEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean frictionEnabled ) {
                setEnabled( frictionEnabled );
                frictionChanged.stateChanged( null );
            }
        } );

        //The model has many bodies, each with their own coefficient of friction, so it is difficult to observe that value in the model.
        //Instead, just center the slider on reset
        final int initValue = ( getMaximum() - getMinimum() ) / 2;
        final VoidFunction0 init = new VoidFunction0() {
            public void apply() {
                setValue( initValue );
            }
        };
        module.addResetListener( init );
        init.apply();
    }
}
