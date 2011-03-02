// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;
import edu.colorado.phet.bendinglight.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;

/**
 * @author Sam Reid
 */
public class MoreToolsModel extends IntroModel {
    public final VelocitySensor velocitySensor = new VelocitySensor();
    public final AmplitudeSensor amplitudeSensor = new AmplitudeSensor();

    public MoreToolsModel() {
        final VoidFunction0 updateReading = new VoidFunction0() {
            public void apply() {
                velocitySensor.value.setValue( getSpeed( velocitySensor.position.getValue() ) );
            }
        };
        addModelUpdateListener( updateReading );
        new RichSimpleObserver() {
            @Override
            public void update() {
                updateReading.apply();
            }
        }.observe( velocitySensor.position, amplitudeSensor.probe1.position, amplitudeSensor.probe2.position );
    }
}