// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.colorado.phet.molarity.MolarityResources.Strings;
import edu.colorado.phet.molarity.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker.
 * 3D perspective is provided by an image (see BeakerImageNode).
 * Other elements (ticks, label, ...) are added programmatically.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerNode extends PComposite {

    // Shows the solution concentration on the beaker label.
    private static final boolean CONCENTRATION_FEATURE_ENABLED = false;

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( BeakerNode.class.getCanonicalName() );

    // label properties
    private static final DecimalFormat CONCENTRATION_FORMAT = new DefaultDecimalFormat( "0.00" );

    // tick mark properties
    private static final Color TICK_COLOR = Color.GRAY;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

    // tick label properties
    private static final String[] MAJOR_TICK_LABELS = { "\u00bd", "1" }; // 1/2L, 1L
    private static final Font TICK_LABEL_FONT = new PhetFont( 20 );
    private static final Color TICK_LABEL_COLOR = Color.DARK_GRAY;
    private static final double TICK_LABEL_X_SPACING = 8;

    private final BeakerImageNode beakerImageNode;
    private final LabelNode labelNode;
    private final ArrayList<PText> tickLabelNodes;

    public BeakerNode( IUserComponent userComponent,
                       Solution solution, double maxVolume,
                       String volumeUnits, String concentrationUnits,
                       Font formulaFont, Font concentrationFont,
                       PDimension labelSize,
                       final double imageScaleX, final double imageScaleY,
                       Property<Boolean> valuesVisible ) {

        // the glass beaker
        beakerImageNode = new BeakerImageNode() {{
            getTransformReference( true ).scale( imageScaleX, imageScaleY );
        }};
        final PDimension cylinderSize = beakerImageNode.getCylinderSize();
        final Point2D cylinderOffset = beakerImageNode.getCylinderOffset();
        final double cylinderEndHeight = beakerImageNode.getCylinderEndHeight();
        beakerImageNode.setOffset( -cylinderOffset.getX(), -cylinderOffset.getY() );

        // inside bottom line
        PPath bottomNode = new PPath() {{
            setPathTo( new Arc2D.Double( 0, cylinderSize.getHeight() - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight,
                                         5, 170, Arc2D.OPEN ) );
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( new Color( 150, 150, 150, 100 ) );
        }};

        addChild( bottomNode );
        addChild( beakerImageNode );

        // tick marks, arcs that wrap around the edge of the beaker's cylinder
        tickLabelNodes = new ArrayList<PText>();
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double bottomY = cylinderSize.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = cylinderSize.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY ) - ( cylinderEndHeight / 2 );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 30, Arc2D.OPEN ) ) {{
                    setStroke( MAJOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );

                // major tick label
                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length ) {
                    String label = MAJOR_TICK_LABELS[labelIndex] + volumeUnits;
                    PText textNode = new PText( label ) {{
                        setFont( TICK_LABEL_FONT );
                        setTextPaint( TICK_LABEL_COLOR );
                    }};
                    ticksNode.addChild( textNode );
                    textNode.setOffset( tickNode.getFullBounds().getMaxX() + TICK_LABEL_X_SPACING,
                                        tickNode.getFullBounds().getMaxY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );
                    tickLabelNodes.add( textNode );
                }
            }
            else {
                // minor tick, no label
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 15, Arc2D.OPEN ) ) {{
                    setStroke( MINOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
        }

        // label on the beaker
        labelNode = new LabelNode( solution, labelSize, formulaFont, concentrationFont, concentrationUnits, valuesVisible );
        addChild( labelNode );
        labelNode.setOffset( ( cylinderSize.getWidth() / 2 ), ( 0.15 * cylinderSize.getHeight() ) );

        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setValuesVisible( visible );
            }
        } );

        addInputEventListener( new NonInteractiveEventHandler( userComponent ) );
    }

    // Controls visibility of tick mark values
    public void setValuesVisible( boolean visible ) {
        for ( PNode node : tickLabelNodes ) {
            node.setVisible( visible );
        }
    }

    // Sets the label text on the beaker
    public void setLabelText( String text ) {
        labelNode.setText( text );
    }

    public PDimension getCylinderSize() {
        return beakerImageNode.getCylinderSize();
    }

    public double getCylinderEndHeight() {
        return beakerImageNode.getCylinderEndHeight();
    }

    public void setConcentration( double concentration ) {
        labelNode.setConcentration( concentration );
    }

    /*
     * Label that appears on the beaker in a frosty, translucent frame.
     * Origin at top center.
     * REVIEW: I recommend moving this class to top-level instead of nested.  It is big enough and different enough.
     */
    private static class LabelNode extends PComposite {

        private final String concentrationUnits;
        private final HTMLNode formulaNode;
        private final PText concentrationNode;
        private final PNode textParentNode; // parent of formula and concentration value
        private final PPath backgroundNode;

        public LabelNode( final Solution solution, final PDimension labelSize,
                          final Font formulaFont, final Font concentrationFont, String concentrationUnits,
                          Property<Boolean> valuesVisible ) {

            this.concentrationUnits = concentrationUnits;

            // nodes
            formulaNode = new HTMLNode( "?" ) {{
                setFont( formulaFont );
            }};
            concentrationNode = new PText( "?" ) {{
                setFont( concentrationFont );
            }};
            textParentNode = new PNode();
            backgroundNode = new PPath() {{
                setPaint( ColorUtils.createColor( Color.WHITE, 150 ) );
                setStrokePaint( Color.LIGHT_GRAY );
                setPathTo( new RoundRectangle2D.Double( -labelSize.getWidth() / 2, 0, labelSize.getWidth(), labelSize.getHeight(), 10, 10 ) );
            }};

            // rendering order
            addChild( backgroundNode );
            textParentNode.addChild( formulaNode );
            textParentNode.addChild( concentrationNode );
            addChild( textParentNode );

            // update formula on label
            solution.solute.addObserver( new SimpleObserver() {
                public void update() {
                    setText( solution.solute.get().formula );
                }
            } );

            // update concentration on label
            solution.concentration.addObserver( new VoidFunction1<Double>() {
                public void apply( Double concentration ) {
                    setConcentration( concentration );
                }
            });

            valuesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    concentrationNode.setVisible( visible && CONCENTRATION_FEATURE_ENABLED );
                    updateLayout();
                }
            } );
        }

        private void setText( String text ) {
            formulaNode.setHTML( text );
            updateLayout();
        }

        private void setConcentration( double concentration ) {
            String concentrationString = CONCENTRATION_FORMAT.format( concentration );
            String valueUnitsString = MessageFormat.format( Strings.PATTERN__0VALUE__1UNITS, concentrationString, concentrationUnits );
            concentrationNode.setText( MessageFormat.format( Strings.PATTERN__PARENTHESES__0TEXT, valueUnitsString ) );
            updateLayout();
        }

        private void updateLayout() {

            if ( concentrationNode.getVisible() && CONCENTRATION_FEATURE_ENABLED ) {
                // center concentration under formula
                concentrationNode.setOffset( formulaNode.getFullBoundsReference().getCenterX() - ( concentrationNode.getFullBoundsReference().getWidth() / 2 ),
                                             formulaNode.getFullBoundsReference().getMaxY() + 2 );
            }
            else {
                concentrationNode.setOffset( formulaNode.getOffset() );
            }

            // scale to fit the background with some margin
            final double margin = 2;
            final double scaleX = ( backgroundNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / textParentNode.getFullBoundsReference().getWidth();
            final double scaleY = ( backgroundNode.getFullBoundsReference().getHeight() - ( 2 * margin ) ) / textParentNode.getFullBoundsReference().getHeight();
            if ( scaleX < 1 || scaleY < 1 ) {
                double scale = Math.min( scaleX, scaleY );
                LOGGER.info( "scaling beaker label by " + scale );
                textParentNode.setScale( scale );
            }

            // center in the background
            textParentNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( textParentNode.getFullBoundsReference().getWidth() / 2 ),
                                      backgroundNode.getFullBoundsReference().getCenterY() - ( textParentNode.getFullBoundsReference().getHeight() / 2 ) );
        }
    }
}
