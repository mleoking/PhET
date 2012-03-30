// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Interface for manipulating the source-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationNode extends PhetPNode {

    public SlopeInterceptEquationNode( final Property<SlopeInterceptLine> interactiveLine, IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange ) {

        // TODO quick-and-dirty implementation using JSpinners
        {
            // Swing controls
            final IntegerSpinner riseSpinner = new IntegerSpinner( UserComponents.riseSpinner,
                                                                   new IntegerRange( riseRange.getMin(), riseRange.getMax(), (int) interactiveLine.get().rise ) ) {{
                setTextBackground( LGColors.SLOPE_COLOR );
            }};
            final IntegerSpinner runSpinner = new IntegerSpinner( UserComponents.runSpinner,
                                                                  new IntegerRange( runRange.getMin(), runRange.getMax(), (int) interactiveLine.get().run ) ) {{
                setTextBackground( LGColors.SLOPE_COLOR );
            }};
            final IntegerSpinner interceptSpinner = new IntegerSpinner( UserComponents.interceptSpinner,
                                                                        new IntegerRange( interceptRange.getMin(), interceptRange.getMax(), (int) interactiveLine.get().intercept ) ) {{
                setTextBackground( LGColors.INTERCEPT_COLOR );
            }};

            // Update line when the spinners change
            final ChangeListener changeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    interactiveLine.set( new SlopeInterceptLine( riseSpinner.getIntValue(), runSpinner.getIntValue(), interceptSpinner.getIntValue() ) );
                }
            };
            riseSpinner.addChangeListener( changeListener );
            runSpinner.addChangeListener( changeListener );
            interceptSpinner.addChangeListener( changeListener );

            // update spinners when line changes
            interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
                public void apply( SlopeInterceptLine line ) {
                    riseSpinner.setIntValue( (int) line.rise );
                    runSpinner.setIntValue( (int) line.run );
                    interceptSpinner.setIntValue( (int) line.intercept );
                }
            } );

            // nodes
            PText yEquals = new PText( "y = " ) {{ setFont( new PhetFont( Font.BOLD, 14 ) ); }};
            PSwing pswingRise = new PSwing( riseSpinner );
            PSwing pswingRun = new PSwing( runSpinner );
            PPath lineNode = new PPath( new Line2D.Double( 0, 0, pswingRise.getFullBoundsReference().getWidth(), 0 ) );
            PText xPlus = new PText( "x +" ) {{ setFont( new PhetFont( Font.BOLD, 14 ) ); }};
            PSwing pswingIntercept = new PSwing( interceptSpinner );

            // rendering order
            PNode parentNode = new PNode();
            parentNode.addChild( yEquals );
            parentNode.addChild( pswingRise );
            parentNode.addChild( lineNode );
            parentNode.addChild( pswingRun );
            parentNode.addChild( xPlus );
            parentNode.addChild( pswingIntercept );

            // layout, in y=mx+b format
            pswingRise.setOffset( yEquals.getFullBoundsReference().getMaxX() + 2,
                                  yEquals.getFullBoundsReference().getMaxY() - pswingRise.getFullBoundsReference().getHeight() );
            lineNode.setOffset( pswingRise.getXOffset(),
                                pswingRise.getFullBoundsReference().getMaxY() + 2 );
            pswingRun.setOffset( pswingRise.getXOffset(),
                                 lineNode.getFullBoundsReference().getMaxY() + 2 );
            xPlus.setOffset( pswingRise.getFullBoundsReference().getMaxX() + 4,
                             yEquals.getYOffset() );
            pswingIntercept.setOffset( xPlus.getFullBoundsReference().getMaxX() + 2,
                                       xPlus.getFullBoundsReference().getCenterY() - ( pswingIntercept.getFullBoundsReference().getHeight() / 2 ) );

            // make the equation larger
            parentNode.scale( 1.8 ); //TODO scaling up is bad
            addChild( parentNode );
        }
    }
}
