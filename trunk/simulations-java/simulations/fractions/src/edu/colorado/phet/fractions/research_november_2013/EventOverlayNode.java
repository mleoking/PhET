// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * For single events like clicks
 */
public class EventOverlayNode<T extends Number> extends PNode {
    private ArrayList<Pair<T, Long>> timestamps = new ArrayList<Pair<T, Long>>();

    public EventOverlayNode( ObservableProperty<T> property, final double minY, final double maxY, final Property<Function.LinearFunction> timeToX, VoidFunction1<VoidFunction0> addTickListener, final Function0<Long> time, final Function0<Long> endTime ) {
        final VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                removeAllChildren();
                for ( int i = 0; i < timestamps.size(); i++ ) {
                    Pair<T, Long> timestamp = timestamps.get( i );
                    Shape shape = new Line2D.Double( (float) timeToX.get().evaluate( timestamp._2 ), minY,
                                                     (float) timeToX.get().evaluate( timestamp._2 ), maxY );
                    PhetPPath path = new PhetPPath( shape, new BasicStroke( 1 ), new Color( 0, 0, 0, 64 ) );
                    addChild( path );
                }
            }
        };
        addTickListener.apply( update );
        timeToX.addObserver( new VoidFunction1<Function.LinearFunction>() {
            public void apply( Function.LinearFunction linearFunction ) {
                update.apply();
            }
        } );

        property.addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                timestamps.add( new Pair<T, Long>( t, time.apply() ) );
                removeAllChildren();
                update.apply();
            }
        } );
    }
}
