// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.model.ConcentrationMeter;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Concentration meter, with probe.
 * <p/>
 * The probe needs to register the concentration when of all possible fluids that it may contact, including:
 * <ul>
 * <li>solution in the beaker
 * <li>output of the solvent faucet
 * <li>output of the drain faucet
 * <li>output of the dropper
 * </ul>
 * <p/>
 * Rather than trying to model the shapes of all of these fluids, we handle "probe is in fluid"
 * herein via intersection of view shapes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationMeterNode extends PhetPNode {

    public static final String VALUE_PATTERN = "0.000";

    private final ConcentrationMeter meter;
    private final Solution solution;
    private final SolutionNode solutionNode;
    private final Faucet solventFaucet, drainFaucet;
    private final OutputFluidNode solventFluidNode, drainFluidNode;
    private final Dropper dropper;
    private final StockSolutionNode stockSolutionNode;

    private final ProbeNode probeNode;

    public ConcentrationMeterNode( ConcentrationMeter meter,
                                   Solution solution, SolutionNode solutionNode,
                                   Faucet solventFaucet, OutputFluidNode solventFluidNode,
                                   Faucet drainFaucet, OutputFluidNode drainFluidNode,
                                   Dropper dropper, StockSolutionNode stockSolutionNode ) {

        this.meter = meter;
        this.solutionNode = solutionNode;
        this.solution = solution;
        this.solventFaucet = solventFaucet;
        this.solventFluidNode = solventFluidNode;
        this.drainFaucet = drainFaucet;
        this.drainFluidNode = drainFluidNode;
        this.dropper = dropper;
        this.stockSolutionNode = stockSolutionNode;

        // nodes
        BodyNode bodyNode = new BodyNode( meter );
        probeNode = new ProbeNode( meter, solutionNode, solventFluidNode, drainFluidNode, stockSolutionNode );
        WireNode wireNode = new WireNode( meter, new ImmutableVector2D( bodyNode.getFullBoundsReference().getWidth() / 2, bodyNode.getFullBoundsReference().getHeight() / 2 ) );

        // rendering order
        addChild( wireNode );
        addChild( bodyNode );
        addChild( probeNode );

        //NOTE: layout is handled by child nodes observing model elements.

        // Update the meter value
        RichSimpleObserver valueUpdater = new RichSimpleObserver() {
            public void update() {
                updateValue();
            }
        };
        valueUpdater.observe( meter.probe.location, solution.solute, solution.volume, solventFaucet.flowRate, drainFaucet.flowRate, dropper.location );
        solution.addConcentrationObserver( valueUpdater );
        dropper.addFlowRateObserver( valueUpdater );
    }

    private void updateValue() {
        if ( probeNode.isInSolution() ) {
            meter.setValue( solution.getConcentration() );
        }
        else if ( probeNode.isInSolvent() ) {
            meter.setValue( 0d );
        }
        else if ( probeNode.isInDrain() ) {
            meter.setValue( solution.getConcentration() );
        }
        else if ( probeNode.isInStockSolution() ) {
            meter.setValue( dropper.solute.get().stockSolutionConcentration );
        }
        else {
            meter.setValue( null );
        }
    }

    // Meter body, origin at upper left.
    private static class BodyNode extends PNode {

        private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( VALUE_PATTERN );
        private static final String NO_VALUE = "-";

        // image-specific locations and dimensions
        private static final double TITLE_Y_OFFSET = 12;
        private static final double X_MARGIN = 30;  // specific to image files
        private static final double VALUE_Y_OFFSET = 67; // specific to image files

        public BodyNode( final ConcentrationMeter meter ) {

            // text nodes
            PText titleNode = new PText( Strings.CONCENTRATION ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, 18 ) );
            }};
            PText unitsNode = new PText( MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_MOLES_PER_LITER ) ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, 16 ) );
            }};
            final PText valueNode = new PText( VALUE_PATTERN ) {{
                setFont( new PhetFont( 24 ) );
            }};

            // create a background that fits the text
            final double maxTextWidth = Math.max( titleNode.getFullBoundsReference().getWidth(), Math.max( unitsNode.getFullBoundsReference().getWidth(), valueNode.getFullBoundsReference().getWidth() ) );
            final double bodyWidth = ( 2 * X_MARGIN ) + maxTextWidth;
            final PImage imageNode = new TiledBackgroundNode( bodyWidth, Images.CONCENTRATION_METER_BODY_LEFT, Images.CONCENTRATION_METER_BODY_CENTER, Images.CONCENTRATION_METER_BODY_RIGHT );

            // rendering order
            addChild( imageNode );
            addChild( titleNode );
            addChild( unitsNode );
            addChild( valueNode );

            // layout
            titleNode.setOffset( ( imageNode.getFullBoundsReference().getWidth() - titleNode.getFullBoundsReference().getWidth() ) / 2, TITLE_Y_OFFSET );
            unitsNode.setOffset( ( imageNode.getFullBoundsReference().getWidth() - unitsNode.getFullBoundsReference().getWidth() ) / 2,
                                 titleNode.getFullBoundsReference().getMaxY() + 3 );
            valueNode.setOffset( 0, VALUE_Y_OFFSET ); //NOTE: x offset will be adjusted when value is set, to maintain right justification

            // body location
            meter.body.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            // displayed value
            meter.addValueObserver( new SimpleObserver() {
                public void update() {
                    Double value = meter.getValue();
                    if ( value == null ) {
                        valueNode.setText( NO_VALUE );
                        // centered
                        valueNode.setOffset( imageNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ),
                                             valueNode.getYOffset() );
                    }
                    else {
                        // eg, "0.23400 M"
                        valueNode.setText( VALUE_FORMAT.format( value ) );
                        // right justified
                        valueNode.setOffset( imageNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth() - X_MARGIN,
                                             valueNode.getYOffset() );
                    }
                }
            } );
        }
    }

    // Meter probe, origin at geometric center.
    private static class ProbeNode extends PNode {

        private final ConcentrationMeter meter;
        private final SolutionNode solutionNode;
        private final OutputFluidNode solventFluidNode;
        private final OutputFluidNode drainFluidNode;
        private final StockSolutionNode stockSolutionNode;

        public ProbeNode( final ConcentrationMeter meter, SolutionNode solutionNode, OutputFluidNode solventFluidNode, OutputFluidNode drainFluidNode, StockSolutionNode stockSolutionNode ) {

            this.meter = meter;
            this.solutionNode = solutionNode;
            this.solventFluidNode = solventFluidNode;
            this.drainFluidNode = drainFluidNode;
            this.stockSolutionNode = stockSolutionNode;

            PImage imageNode = new PImage( Images.CONCENTRATION_METER_PROBE ) {{
                scale( 0.5 );
            }};
            addChild( imageNode );
            imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );

            // body location
            meter.probe.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( UserComponents.concentrationMeterProbe, meter.probe, this ) {
                @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).add( Parameters.isInSolution, isInSolution() );
                }
            } );
        }

        private boolean isInSolution() {
            return isInNode( solutionNode );
        }

        private boolean isInSolvent() {
            return isInNode( solventFluidNode );
        }

        private boolean isInDrain() {
            return isInNode( drainFluidNode );
        }

        private boolean isInStockSolution() {
            return isInNode( stockSolutionNode );
        }

        private boolean isInNode( PNode node ) {
            //TODO ...or should this return true if any part of ProbeNode intersects node?
            return node.getFullBoundsReference().contains( meter.probe.location.get().toPoint2D() );
        }
    }

    // Wire that connects the probe to the body of the meter.
    private static class WireNode extends PPath {

        public WireNode( final ConcentrationMeter meter, final ImmutableVector2D bodyCenterOffset ) {
            setStrokePaint( new Color( 0, 133, 65 ).darker() ); // green, a little darker than the meter body
            setStroke( new BasicStroke( 6f ) );

            SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    // straight line from the center of the probe to the center of the body.
                    setPathTo( new Line2D.Double( meter.probe.getX(), meter.probe.getY(),
                                                  meter.body.getX() + bodyCenterOffset.getX(), meter.body.getY() + bodyCenterOffset.getY() ) );
                }
            };
            meter.body.location.addObserver( observer );
            meter.probe.location.addObserver( observer );
        }
    }
}
