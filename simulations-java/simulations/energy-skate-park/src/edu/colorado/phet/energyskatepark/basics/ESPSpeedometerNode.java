// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * Speedometer graphic
 *
 * @author Sam Reid
 */
public class ESPSpeedometerNode extends SpeedometerNode {
    public ESPSpeedometerNode( final ObservableProperty<Option<Double>> speed ) {

        //Using 20 m/s as max makes 1 m/s per tick on the speedometer node since it has 20 ticks.
        super( EnergySkateParkResources.getString( "properties.speed" ), 120, speed, 20 );
    }
}