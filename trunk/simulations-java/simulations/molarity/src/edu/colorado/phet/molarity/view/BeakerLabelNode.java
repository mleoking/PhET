// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.molarity.MolarityResources.Strings;
import edu.colorado.phet.molarity.MolaritySymbols;
import edu.colorado.phet.molarity.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Label that appears on the beaker in a frosty, translucent frame.
 * Displays solute formula and (optionally) solution concentration.
 * Origin at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerLabelNode extends PComposite {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( BeakerLabelNode.class.getCanonicalName() );

    // label properties
    private static final DecimalFormat CONCENTRATION_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final boolean CONCENTRATION_FEATURE_ENABLED = false; // Shows the solution concentration on the beaker label.

    private final Solution solution;
    private final String concentrationUnits;
    private final HTMLNode formulaNode;
    private final PText concentrationNode;
    private final PNode textParentNode; // parent of formula and concentration value
    private final PPath backgroundNode;

    public BeakerLabelNode( final Solution solution, final PDimension labelSize,
                            final Font formulaFont, final Font concentrationFont, String concentrationUnits,
                            Property<Boolean> valuesVisible ) {

        this.solution = solution;
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
        RichSimpleObserver formulaUpdater = new RichSimpleObserver() {
            @Override public void update() {
                updateFormula();
            }
        };
        formulaUpdater.observe( solution.concentration, solution.volume, solution.solute );

        // update concentration on label
        solution.concentration.addObserver( new SimpleObserver() {
            public void update() {
                updateConcentration();
            }
        } );

        // toggle between quantitative and qualitative views
        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                concentrationNode.setVisible( visible && CONCENTRATION_FEATURE_ENABLED );
                updateLayout();
            }
        } );
    }

    private void updateFormula() {
        String labelText;
        if ( solution.volume.get() == 0 ) {
            labelText = "";
        }
        else if ( solution.concentration.get() == 0 ) {
            labelText = MolaritySymbols.WATER;
        }
        else {
            labelText = solution.solute.get().formula;
        }
        formulaNode.setHTML( labelText );
        updateLayout();
    }

    private void updateConcentration() {
        String concentrationString = CONCENTRATION_FORMAT.format( solution.concentration.get() );
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
