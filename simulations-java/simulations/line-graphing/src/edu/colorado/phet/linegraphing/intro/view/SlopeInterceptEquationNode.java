// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.piccolophet.PhetPNode;
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

        //TODO placeholder for layout
        addChild( new PPath( new Rectangle2D.Double( 0, 0, 350, 200 ) ) );

        //TODO test for changing line
        {
            final IntegerSpinner interceptSpinner = new IntegerSpinner( new UserComponent( "interceptSpinner" ), new IntegerRange( -10, 10, interactiveLine.get().intercept ) );
            interceptSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    SlopeInterceptLine line = interactiveLine.get();
                    interactiveLine.set( new SlopeInterceptLine( line.rise, line.run, interceptSpinner.getIntValue() ) );
                }
            } );
            JLabel label = new JLabel( "y = mx + " );
            JPanel panel = new JPanel();
            panel.add( label );
            panel.add( interceptSpinner );
            PSwing pswing = new PSwing( panel );
            pswing.setOffset( 30, 30 );
            pswing.scale( 2 );
            addChild( pswing );
        }
    }
}
