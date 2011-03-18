// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * @author Sam Reid
 */
public class MoreToolsModel extends IntroModel {
    public final VelocitySensor velocitySensor = new VelocitySensor();
    public final Function1<ImmutableVector2D, Option<Double>> waveValueGetter = new Function1<ImmutableVector2D, Option<Double>>() {
        public Option<Double> apply( ImmutableVector2D position ) {
            return getWaveValue( position );
        }
    };
    public final WaveSensor waveSensor = new WaveSensor( getClock(), waveValueGetter, waveValueGetter );

    public MoreToolsModel() {
        final VoidFunction0 updateReading = new VoidFunction0() {
            public void apply() {
                velocitySensor.value.setValue( getVelocity( velocitySensor.position.getValue() ) );
            }
        };
        addModelUpdateListener( updateReading );
        new RichSimpleObserver() {
            @Override
            public void update() {
                updateReading.apply();
            }
        }.observe( velocitySensor.position, waveSensor.probe1.position, waveSensor.probe2.position );
    }

    protected double getN1() {
        return environmentDispersion.getIndexOfRefraction( laser.color.getValue().getWavelength(), topMedium.getValue().getIndexOfRefraction() );
    }

    protected double getN2() {
        return prismDispersion.getIndexOfRefraction( laser.color.getValue().getWavelength(), bottomMedium.getValue().getIndexOfRefraction() );
    }
}