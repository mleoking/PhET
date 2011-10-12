// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Beaker used in the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {

    private final PDimension beakerSize;
    private final Solution solution;
    private final LinearFunction volumeFunction, concentrationFunction;

    private final PNode glassNode;
    private final PPath solutionNode;
    private final HTMLNode labelNode;
    private final PText saturatedNode;

    public BeakerNode( PDimension beakerSize, Solution solution, DoubleRange volumeRange, DoubleRange concentrationRange ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        this.beakerSize = beakerSize;
        this.solution = solution;
        this.volumeFunction = new LinearFunction( volumeRange.getMin(), volumeRange.getMax(),
                                                  ( volumeRange.getMin() / volumeRange.getMax() ) * beakerSize.getHeight(), beakerSize.getHeight() );
        this.concentrationFunction = new LinearFunction( concentrationRange.getMin(), concentrationRange.getMax(), 0, 1 );

        // nodes
        glassNode = new PPath( new Rectangle2D.Double( 0, 0, beakerSize.getWidth(), beakerSize.getHeight() ) );
        solutionNode = new PPath() {{
            setStroke( null );
        }};
        labelNode = new HTMLNode() {{
            setFont( new PhetFont( Font.BOLD, 24 ) );
        }};
        saturatedNode = new PText( Strings.SATURATED ) {{
            setFont( new PhetFont( 20 ) );
        }};

        // rendering order
        {
            addChild( solutionNode );
            addChild( glassNode );
            addChild( labelNode );
            addChild( saturatedNode );
        }

        // layout
        saturatedNode.setOffset( ( beakerSize.getWidth() / 2 ) - ( saturatedNode.getFullBoundsReference().getWidth() / 2 ),
                                 ( 0.90 * beakerSize.getHeight() ) - saturatedNode.getFullBoundsReference().getHeight() );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        solution.addConcentrationObserver( observer );
        solution.addPrecipitateAmountObserver( observer );
        solution.solute.addObserver( observer );
        solution.volume.addObserver( observer );
    }

    private void updateNode() {

        // update solute label
        labelNode.setHTML( solution.solute.get().formula );
        labelNode.setOffset( glassNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                             glassNode.getFullBoundsReference().getMinY() + ( 0.35 * glassNode.getFullBoundsReference().getHeight() ) - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );

        // update the color of the solution, based on solute and concentration
        double colorScale = concentrationFunction.evaluate( solution.getConcentration() );
        solutionNode.setPaint( ColorUtils.interpolateRBGA( DilutionsColors.WATER_COLOR, solution.solute.get().solutionColor, colorScale ) );

        // update amount of stuff in the beaker, based on solution volume
        double height = volumeFunction.evaluate( solution.volume.get() );
        solutionNode.setPathTo( new Rectangle2D.Double( 0, beakerSize.getHeight() - height, beakerSize.getWidth(), height ) );

        // show "Saturated!" if the solution is saturated
        saturatedNode.setVisible( solution.getPrecipitateAmount() != 0 );
    }
}
