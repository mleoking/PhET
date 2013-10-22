// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import edu.colorado.phet.common.phetcommon.math.*;
import edu.colorado.phet.common.phetcommon.model.property.*;
import edu.colorado.phet.common.phetcommon.util.*;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.common.piccolophet.nodes.*;
import edu.umd.cs.piccolo.*;

/**
 * Created by Sam on 10/18/13.
 */
public class NumericPropertyNode<T extends Number> extends PNode {
    private ArrayList<Pair<T, Long>> timestamps = new ArrayList<Pair<T, Long>>();

    public NumericPropertyNode( ObservableProperty<T> property, final Function.LinearFunction valueToY, final Property<Function.LinearFunction> timeToX, VoidFunction1<VoidFunction0> addTickListener, final Function0<Long> time, final Function0<Long> endTime ) {
        final VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                removeAllChildren();
                for ( int i = 0; i < timestamps.size(); i++ ) {
                    Pair<T, Long> timestamp = timestamps.get( i );
                    long nextTime = i == timestamps.size() - 1 ? endTime.apply() : timestamps.get( i + 1 )._2;
                    Shape shape = new Line2D.Double( (float) timeToX.get().evaluate( timestamp._2 ), valueToY.evaluate( timestamp._1.doubleValue() ),
                                                     (float) timeToX.get().evaluate( nextTime ), valueToY.evaluate( timestamp._1.doubleValue() ) );
                    PhetPPath path = new PhetPPath( shape, new BasicStroke( 2 ), Color.black );
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
