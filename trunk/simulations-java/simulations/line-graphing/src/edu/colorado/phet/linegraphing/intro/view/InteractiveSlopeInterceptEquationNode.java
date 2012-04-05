// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.intro.view.DoubleSpinnerNode.InterceptSpinnerNode;
import edu.colorado.phet.linegraphing.intro.view.DoubleSpinnerNode.SlopeSpinnerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a source-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class InteractiveSlopeInterceptEquationNode extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, intercept;

    public InteractiveSlopeInterceptEquationNode( final Property<SlopeInterceptLine> interactiveLine,
                                                  IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange, PhetFont font ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.intercept = new Property<Double>( interactiveLine.get().intercept );

        // determine the max width of the rise and run spinners
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new SlopeSpinnerNode( UserComponents.riseSpinner, new Property<Double>( (double) riseRange.getMax() ), riseRange, font, FORMAT );
            PNode minRiseNode = new SlopeSpinnerNode( UserComponents.riseSpinner, new Property<Double>( (double) riseRange.getMin() ), riseRange, font, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new SlopeSpinnerNode( UserComponents.riseSpinner, new Property<Double>( (double) runRange.getMax() ), runRange, font, FORMAT );
            PNode minRunNode = new SlopeSpinnerNode( UserComponents.riseSpinner, new Property<Double>( (double) runRange.getMin() ), runRange, font, FORMAT );
            PNode runNode = new SlopeSpinnerNode( UserComponents.runSpinner, this.run, runRange, font, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // y = mx + b
        PText yNode = new PhetPText( "y", font );
        PText equalsNode = new PhetPText( "=", font );
        PNode riseNode = new SlopeSpinnerNode( UserComponents.riseSpinner, this.rise, riseRange, font, FORMAT );
        PNode runNode = new SlopeSpinnerNode( UserComponents.runSpinner, this.run, runRange, font, FORMAT );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ) ) {{
            setStroke( new BasicStroke( 2f ) );
        }};
        PText xNode = new PhetPText( "x", font );
        final PText interceptSignNode = new PhetPText( "+", font );
        PNode interceptNode = new InterceptSpinnerNode( UserComponents.interceptSpinner, this.intercept, interceptRange, font, FORMAT );

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            addChild( interceptSignNode );
            addChild( interceptNode );
        }

        // layout
        {
            final double xSpacing = 6;
            final double ySpacing = 4;
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                equalsNode.getFullBoundsReference().getCenterY() );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         xNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     xNode.getYOffset() );
        }

        // sync the model with the controls
        rise.addObserver( new VoidFunction1<Double>() {
            public void apply( Double rise ) {
                interactiveLine.set( new SlopeInterceptLine( rise, interactiveLine.get().run, interactiveLine.get().intercept ) );
            }
        } );
        run.addObserver( new VoidFunction1<Double>() {
            public void apply( Double run ) {
                interactiveLine.set( new SlopeInterceptLine( interactiveLine.get().rise, run, interactiveLine.get().intercept ) );
            }
        } );
        intercept.addObserver( new VoidFunction1<Double>() {
            public void apply( Double intercept ) {
                interactiveLine.set( new SlopeInterceptLine( interactiveLine.get().rise, interactiveLine.get().run, intercept ) );
                interceptSignNode.setText( intercept > 0 ? "+" : "-" );
            }
        } );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                intercept.set( line.intercept );
            }
        } );
    }
}
