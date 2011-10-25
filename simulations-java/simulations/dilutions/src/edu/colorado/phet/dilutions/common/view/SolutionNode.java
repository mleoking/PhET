// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.molarity.model.Solution;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Soution shown in the beaker in the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionNode extends PComposite {

    private final PDimension cylinderSize;
    private final double cylinderEndHeight;
    private final Solution solution;
    private final LinearFunction volumeFunction;
    private final PPath cylinderNode, surfaceNode;

    public SolutionNode( PDimension cylinderSize, double cylinderEndHeight, Solution solution, DoubleRange volumeRange ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        this.cylinderSize = cylinderSize;
        this.cylinderEndHeight = cylinderEndHeight;
        this.solution = solution;
        this.volumeFunction = new LinearFunction( volumeRange.getMin(), volumeRange.getMax(),
                                                  ( volumeRange.getMin() / volumeRange.getMax() ) * cylinderSize.getHeight(), cylinderSize.getHeight() );

        // nodes
        cylinderNode = new PPath() {{
            setStroke( null );
        }};
        surfaceNode = new PPath() {{
            setStroke( new BasicStroke( 0.5f ) );
            setStrokePaint( new Color( 0, 0, 0, 85 ) );
        }};

        // rendering order
        addChild( cylinderNode );
        addChild( surfaceNode );

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

        // update the color of the solution, accounting for saturation
        LinearFunction f = new LinearFunction( 0, solution.getSaturatedConcentration(), 0, 1 );
        double colorScale = f.evaluate( solution.getConcentration() );
        Color color = ColorUtils.interpolateRBGA( DilutionsColors.WATER_COLOR, solution.solute.get().solutionColor, colorScale );
        cylinderNode.setPaint( color );
        surfaceNode.setPaint( color );

        // update amount of stuff in the beaker, based on solution volume
        double height = volumeFunction.evaluate( solution.volume.get() );
        cylinderNode.setPathTo( createCylinderShape( height ) );
        surfaceNode.setPathTo( createSurfaceShape( height ) );
    }

    private Shape createCylinderShape( double height ) {
        Area area = new Area( new Rectangle2D.Double( 0, cylinderSize.getHeight() - height, cylinderSize.getWidth(), height ) );
        area.add( new Area( new Ellipse2D.Double( 0, cylinderSize.getHeight() - height - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight ) ) );
        area.add( new Area( new Ellipse2D.Double( 0, cylinderSize.getHeight() - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight ) ) );
        return area;
    }

    private Shape createSurfaceShape( double height ) {
        return new Ellipse2D.Double( 0, cylinderSize.getHeight() - height - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight );
    }
}
