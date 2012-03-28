// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
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

    private static final String PATTERN = "{0} = {1}{2} + {3}"; // eg, y = mx + b

    // y = mx + b
    private static String EQUATION = MessageFormat.format( PATTERN,
                                                         Strings.SYMBOL_VERTICAL_AXIS,
                                                         Strings.SYMBOL_SLOPE,
                                                         Strings.SYMBOL_HORIZONTAL_AXIS,
                                                         Strings.SYMBOL_INTERCEPT );

    public SlopeInterceptEquationNode( final Property<SlopeInterceptLine> interactiveLine ) {

        // TODO quick-and-dirty implementation using JSpinners
        {
            // Swing controls
            final IntegerSpinner riseSpinner = new IntegerSpinner( new UserComponent( "riseSpinner" ), new IntegerRange( 0, 10, interactiveLine.get().rise ) );
            final IntegerSpinner runSpinner = new IntegerSpinner( new UserComponent( "runSpinner" ), new IntegerRange( -10, 10, interactiveLine.get().run ) );
            final IntegerSpinner interceptSpinner = new IntegerSpinner( new UserComponent( "interceptSpinner" ), new IntegerRange( -10, 10, interactiveLine.get().intercept ) );
            final VoidFunction0 updateLine = new VoidFunction0() {
                public void apply() {
                    interactiveLine.set( new SlopeInterceptLine( riseSpinner.getIntValue(), runSpinner.getIntValue(), interceptSpinner.getIntValue() ) );
                }
            };
            final ChangeListener changeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateLine.apply();
                }
            };
            riseSpinner.addChangeListener( changeListener );
            runSpinner.addChangeListener( changeListener );
            interceptSpinner.addChangeListener( changeListener );

            // nodes
            PText yEquals = new PText( "y = " ) {{ setFont( new PhetFont( Font.BOLD, 14 )); }};
            PSwing pswingRise = new PSwing( riseSpinner );
            PSwing pswingRun = new PSwing( runSpinner );
            PPath lineNode = new PPath( new Line2D.Double( 0, 0, pswingRise.getFullBoundsReference().getWidth(), 0 ) );
            PText xPlus = new PText( "x +" ) {{ setFont( new PhetFont( Font.BOLD, 14 )); }};
            PSwing pswingIntercept = new PSwing( interceptSpinner );

            // rendering order
            PNode parentNode = new PNode();
            parentNode.addChild( yEquals );
            parentNode.addChild( pswingRise );
            parentNode.addChild( lineNode );
            parentNode.addChild( pswingRun );
            parentNode.addChild( xPlus );
            parentNode.addChild( pswingIntercept );

            // layout
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

            parentNode.scale( 1.8 );
            addChild( new ControlPanelNode( parentNode ) );
        }
    }
}
