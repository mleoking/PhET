// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that the solution is saturated.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SaturatedIndicatorNode extends PComposite {

    public SaturatedIndicatorNode( final Solution solution ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // text
        PText textNode = new PText( Strings.SATURATED ) {{
            setFont( new PhetFont( 20 ) );
        }};

        // translucent light-gray background, so this shows up on all solution colors
        double width = 1.2 * textNode.getFullBoundsReference().getWidth();
        double height = 1.2 * textNode.getFullBoundsReference().getHeight();
        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, 8, 8 ) ) {{
            setPaint( ColorUtils.createColor( new Color( 240, 240, 240 ), 150 ) );
            setStroke( null );
        }};

        // rendering order
        addChild( backgroundNode );
        addChild( textNode );

        // layout
        textNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ),
                            backgroundNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );

        // make this node visible when the solution is saturated
        solution.addConcentrationObserver( new SimpleObserver() {
            public void update() {
                setVisible( solution.isSaturated() );
            }
        } );
    }
}
