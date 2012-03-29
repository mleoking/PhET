// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Specialization of line graph that adds the ability to directly manipulate a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InteractiveLineGraphNode extends LineGraphNode {

    private SlopeInterceptLineNode interactiveLineNode;
    private PNode interactiveLineParentNode;

    public InteractiveLineGraphNode( final LineGraph graph, final ModelViewTransform mvt, final Property<SlopeInterceptLine> interactiveLine, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt, yEqualsXLine, yEqualsNegativeXLine );

        // Interactive line
        interactiveLineParentNode = new PComposite();
        interactiveLineParentNode.setVisible( linesVisible.get() );
        addChild( interactiveLineParentNode );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineParentNode.removeChild( interactiveLineNode );
                }
                interactiveLineNode = new SlopeInterceptLineNode( interactiveLine.get(), graph, mvt, LGColors.INTERACTIVE_LINE );
                interactiveLineParentNode.addChild( interactiveLineNode );
            }
        } );
    }

    @Override protected void updateLinesVisibility() {
        super.updateLinesVisibility();
        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() );
        }
    }
}
