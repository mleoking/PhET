// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.common.view;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.molarity.MolarityResources.Strings;
import edu.colorado.phet.molarity.common.model.Solution;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that the solution is saturated.
 * This consists of "Saturated!" on a translucent white background, so that it's visible
 * on top of all solute colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SaturatedIndicatorNode extends PComposite {

    public SaturatedIndicatorNode( final Solution solution ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // nodes
        PText textNode = new PText( Strings.SATURATED ) {{
            setFont( new PhetFont( 20 ) );
        }};
        final double width = 1.2 * textNode.getFullBoundsReference().getWidth();
        final double height = 1.2 * textNode.getFullBoundsReference().getHeight();

        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, 8, 8 ) ) {{
            setPaint( ColorUtils.createColor( Color.WHITE, 150 ) );
            setStroke( null );
        }};

        // rendering order
        addChild( backgroundNode );
        addChild( textNode );

        // layout, text centered in the background
        textNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ),
                            backgroundNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );

        // make this node visible when the solution is saturated
        solution.addPrecipitateAmountObserver( new SimpleObserver() {
            public void update() {
                setVisible( solution.isSaturated() );
            }
        } );
    }
}
