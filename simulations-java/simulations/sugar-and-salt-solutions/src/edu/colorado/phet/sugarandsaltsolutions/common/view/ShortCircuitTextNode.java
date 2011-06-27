// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeAdapter;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ConductivityTester;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SHORT_CIRCUIT;
import static java.awt.Color.yellow;

/**
 * A text label above the light bulb that indicates if the conductivity tester is short circuit (by having a bulb, wire or battery submerged)
 * For the short circuit message, use bold yellow text with no box and say "Short circuit!"
 * <p/>
 * One of the complications in this class is that it is supposed to show above (in z-ordering) the salt-shaker.
 * This means it cannot be a child of the conductivity tester node, and instead we convert coordinates
 *
 * @author Sam Reid
 */
public class ShortCircuitTextNode extends PNode {
    public ShortCircuitTextNode( final ConductivityTester conductivityTester, final PNode lightBulbNode ) {
        addChild( new PText( SHORT_CIRCUIT ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( yellow );

            //Make the "short circuit!" text visible if the circuit has shorted out
            conductivityTester.shortCircuited.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean shortCircuited ) {
                    setVisible( shortCircuited );
                }
            } );

            //Center above the light bulb
            final ConductivityTesterChangeAdapter listener = new ConductivityTesterChangeAdapter() {
                public void locationChanged() {
                    Point2D center2D = new Point2D.Double( lightBulbNode.getGlobalFullBounds().getCenterX(), lightBulbNode.getGlobalFullBounds().getMinY() );
                    center2D = globalToLocal( center2D );
                    center2D = localToParent( center2D );
                    setOffset( center2D.getX() - getFullBounds().getWidth() / 2, center2D.getY() - getFullBounds().getHeight() );
                }
            };
            conductivityTester.addConductivityTesterChangeListener( listener );

            //Initialize correctly on startup
            listener.locationChanged();
        }} );
    }
}