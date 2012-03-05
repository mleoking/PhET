// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.WaterGlassNodeFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode.cachedWaterGlassNode;
import static fj.Ord.doubleOrd;

/**
 * Node for showing draggable water glasses
 *
 * @author Sam Reid
 */
public class WaterGlassSetNode extends PieSetNode {
    public WaterGlassSetNode( SettableProperty<PieSet> model, PNode rootNode, Color color, double width, double height ) {
        super( model, rootNode, new WaterGlassNodeFactory(), createEmptyCellsNode( color, width, height ) );
    }

    //Show the empty beakers
    public static F<PieSet, PNode> createEmptyCellsNode( final Color color, final double width, final double height ) {
        return new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet state ) {
                PNode node = new PNode();

                for ( final Pie pie : state.pies ) {
                    final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                        @Override public Double f( Slice s ) {
                            return s.getShape().getBounds2D().getMinY();
                        }
                    } );

                    //Read from cache like WaterGlassNodeFactory instead of creating new each time to improve performance
                    node.addChild( new PImage( cachedWaterGlassNode( state.countFilledCells( pie ), state.denominator, color, width, height ) ) {{
                        setOffset( pie.cells.index( 0 ).getShape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                    }} );
                }
                return node;
            }
        };
    }
}