// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.NodeFactory;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolIconNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.TITLE_FONT;

/**
 * The toolbox node that the conductivity tester gets dragged out of and back into.
 *
 * @author Sam Reid
 */
public class ConductivityTesterToolboxNode extends WhiteControlPanelNode {
    public ConductivityTesterToolboxNode( final SugarAndSaltSolutionModel model, final SugarAndSaltSolutionsCanvas canvas ) {
        super( new VBox() {{

            //Function for determining whether the conductivity node should get dropped back in the toolbox.
            //This is used by both the drag handler from the toolbox and by the node itself (after being dropped once, it gets a new drag handler)
            final Function0<Rectangle2D> getToolboxBounds = new Function0<Rectangle2D>() {
                public Rectangle2D apply() {
                    return getParent().getGlobalFullBounds();
                }
            };
            //Add title and a spacer below it
            addChild( new PText( "Conductivity" ) {{setFont( TITLE_FONT );}} );

            //Factory that creates the ConductivityTesterToolNode and positions it where the mouse is
            NodeFactory conductivityNodeMaker = new NodeFactory() {
                public ToolNode createNode( final ModelViewTransform transform, Property<Boolean> visible, final Point2D location ) {
                    //Create and return the tool node, which reuses the same conductivityTesterNode
                    return new ConductivityTesterToolNode( new SugarAndSaltSolutionsConductivityTesterNode( model.conductivityTester, transform, canvas.getRootNode(), getToolboxBounds, location ) );
                }
            };

            //Create a thumbnail to be shown in the toolbox
            Image thumbnail = new SugarAndSaltSolutionsConductivityTesterNode( model.conductivityTester, canvas.getModelViewTransform(), canvas.getRootNode(), getToolboxBounds, new Point2D.Double( 0, 0 ) ).createImage();

            //Add the tool icon node, which can be dragged out of the toolbox to create the full-sized conductivity tester node
            addChild( new ToolIconNode<SugarAndSaltSolutionsCanvas>(
                    multiScaleToWidth( toBufferedImage( thumbnail ), 130 ), model.conductivityTester.visible, canvas.getModelViewTransform(), canvas,
                    conductivityNodeMaker, model, getToolboxBounds ) {

                //Override addChild so that the created node will go behind the salt shaker, since the salt shaker should always be in front
                @Override protected void addChild( SugarAndSaltSolutionsCanvas canvas, ToolNode node ) {
                    canvas.submergedInWaterNode.addChild( node );

                    //Make sure the conductivity node shows as submerged in the water, but still goes behind the shaker
                    node.moveToBack();
                }

                //Remove created tools from their parent node
                @Override protected void removeChild( SugarAndSaltSolutionsCanvas canvas, ToolNode node ) {
                    canvas.submergedInWaterNode.removeChild( node );
                }
            } );
        }} );
    }
}