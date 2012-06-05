package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.util.PUtil;

import static edu.colorado.phet.fractionsintro.buildafraction_functional.view.BuildAFractionCanvas.controlPanelStroke;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class ScoreBoxNode extends PNode {
    public final Fraction fraction;
    public final PhetPPath path;

    public ScoreBoxNode( final int numerator, final int denominator, final Property<List<Fraction>> matches ) {
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 140, 150, 30, 30 ), controlPanelStroke, Color.darkGray ) {{

            setStrokePaint( Color.darkGray );
            setStroke( controlPanelStroke );

            matches.addObserver( new VoidFunction1<List<Fraction>>() {

                //Light up if the user matched
                public void apply( final List<Fraction> fractions ) {
                    if ( fractions.find( new F<Fraction, Boolean>() {
                        @Override public Boolean f( final Fraction f ) {
                            return f.approxEquals( fraction );
                        }
                    } ).isSome() ) {
                        setStrokePaint( Color.red );
                        final PInterpolatingActivity ta = new PInterpolatingActivity( 2000, PUtil.DEFAULT_ACTIVITY_STEP_RATE ) {
                            public void setRelativeTargetValue( final float zeroToOne ) {
                                setStroke( new BasicStroke( (float) ( Math.abs( Math.sin( zeroToOne * 4 * 2 ) ) * 4 ), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f ) );
                            }
                        };
                        ta.setSlowInSlowOut( false );
                        addActivity( ta );
                    }
                }
            } );
        }};
        this.fraction = new Fraction( numerator, denominator );
        addChild( this.path );
    }

    public void completed() {
        path.setStrokePaint( Color.darkGray );
    }
}