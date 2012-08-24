// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerStandNode;
import edu.colorado.phet.energyformsandchanges.energysystems.model.TeaPot;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo node that represents a teapot with a burner beneath it in the view.
 *
 * @author John Blanco
 */
public class TeaPotNode extends PositionableFadableModelElementNode {

    public TeaPotNode( TeaPot teaPot, final ModelViewTransform mvt ) {
        super( teaPot, mvt );

        // Create the burner.
        // TODO: i18n
        PNode heaterNode = new HeaterCoolerNode( teaPot.heatCoolAmount, "Heat", "Cool" ) {{
            setScale( 0.5 );
        }};

        // Create the tea pot.
        final PNode teaPotImageNode = new ModelElementImageNode( TeaPot.TEAPOT_IMAGE, mvt );

        // Create the steam node.  There are some tweak factors in the position.
        PNode steamNode = new SteamBackgroundNode( new Vector2D( teaPotImageNode.getFullBoundsReference().getMaxX() - 3,
                                                                 teaPotImageNode.getFullBoundsReference().getMinY() + 12 ),
                                                   teaPot.getEnergyProductionRate(),
                                                   TeaPot.MAX_ENERGY_PRODUCTION_RATE );

        // Create the burner stand.
        double burnerStandWidth = teaPotImageNode.getFullBoundsReference().getWidth() * 0.9;
        double burnerStandHeight = burnerStandWidth * 0.7;
        Rectangle2D burnerStandRect = new Rectangle2D.Double( teaPotImageNode.getFullBoundsReference().getCenterX() - burnerStandWidth / 2,
                                                              teaPotImageNode.getFullBoundsReference().getMaxY() - 35,
                                                              burnerStandWidth,
                                                              burnerStandHeight );
        PNode burnerStandNode = new BurnerStandNode( burnerStandRect, burnerStandWidth * 0.2 );

        // Do the layout.
        heaterNode.setOffset( burnerStandRect.getCenterX() - heaterNode.getFullBoundsReference().width / 2,
                              burnerStandRect.getMaxY() - heaterNode.getFullBoundsReference().getHeight() + 15 );

        // Add the nodes in the appropriate order for the desired layering affect.
        addChild( heaterNode );
        addChild( burnerStandNode );
        addChild( steamNode );
        addChild( teaPotImageNode );
    }

    private static class SteamBackgroundNode extends PNode {

        private static double MAX_HEIGHT_AND_WIDTH = 200;
        private static boolean SHOW_BOUNDS = false; // For debug.

        private SteamBackgroundNode( final Vector2D origin, ObservableProperty<Double> energyOutput, final double maxEnergyOutput ) {
            final PPath cloud = new PhetPPath( Color.LIGHT_GRAY );
            addChild( cloud );
            final PPath overallBounds = new PhetPPath( new BasicStroke( 2 ), Color.RED );
            if ( SHOW_BOUNDS ) {
                addChild( overallBounds );
            }
            energyOutput.addObserver( new VoidFunction1<Double>() {
                public void apply( Double energyOutput ) {
                    double proportion = energyOutput / maxEnergyOutput;
                    final double heightAndWidth = proportion * MAX_HEIGHT_AND_WIDTH;
                    List<Vector2D> cloudShapePoints = new ArrayList<Vector2D>() {{
                        Vector2D startingPoint = new Vector2D( 0, heightAndWidth );
                        add( startingPoint );
                        double stemWidth = Math.PI / 4;
                        add( startingPoint.plus( new Vector2D( heightAndWidth / 2, -heightAndWidth / 2 ).getRotatedInstance( -stemWidth / 2 ) ) );
                        add( startingPoint.plus( new Vector2D( heightAndWidth / 2, -heightAndWidth / 2 ).getRotatedInstance( stemWidth / 2 ) ) );
                    }};
                    double cloudHeightAndWidth = heightAndWidth * 0.9;
                    Shape cloudBodyPath = new Ellipse2D.Double( heightAndWidth - cloudHeightAndWidth,
                                                                0,
                                                                cloudHeightAndWidth,
                                                                cloudHeightAndWidth );
                    Area overallShape = new Area( ShapeUtils.createShapeFromPoints( cloudShapePoints ) );
                    overallShape.add( new Area( cloudBodyPath ) );
                    cloud.setPathTo( overallShape );
                    overallBounds.setPathTo( new Rectangle2D.Double( 0, 0, heightAndWidth, heightAndWidth ) );
                    cloud.setTransparency( (float) proportion / 2 );
                    // Move so that the lower left corner is at the origin.
                    cloud.setOffset( origin.getX(), origin.getY() - heightAndWidth );
                    overallBounds.setOffset( origin.getX(), origin.getY() - heightAndWidth );
                }
            } );

        }
    }
}