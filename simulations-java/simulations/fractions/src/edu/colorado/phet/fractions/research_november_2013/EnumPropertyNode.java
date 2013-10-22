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
public class EnumPropertyNode<T> extends PNode {
    private ArrayList<Pair<T, Long>> timestamps = new ArrayList<Pair<T, Long>>();

    public EnumPropertyNode( ObservableProperty<T> property, final Function1<T, Paint> paint, final double y, final Property<Function.LinearFunction> timeToX, VoidFunction1<VoidFunction0> addTickListener, final Function0<Long> time ) {
        final VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                removeAllChildren();
                for ( Pair<T, Long> timestamp : timestamps ) {
                    Shape shape = new Line2D.Double( (float) timeToX.get().evaluate( timestamp._2 ), y, (float) timeToX.get().evaluate( time.apply() ), y );
                    PhetPPath path = new PhetPPath( shape, new BasicStroke( 10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), paint.apply( timestamp._1 ) );
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
                update.apply();
            }
        } );
    }
}
