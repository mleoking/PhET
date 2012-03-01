// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.WaterGlassNodeFactory;
import edu.umd.cs.piccolo.PNode;

import static fj.Ord.doubleOrd;

/**
 * Node for showing draggable water glasses
 *
 * @author Sam Reid
 */
public class WaterGlassSetNode extends PieSetNode {
    public WaterGlassSetNode( SettableProperty<PieSet> model, PNode rootNode ) {
        super( model, rootNode, new WaterGlassNodeFactory(), createEmptyCellsNode );
    }

    public static final F<PieSet, PNode> createEmptyCellsNode = new F<PieSet, PNode>() {
        @Override public PNode f( final PieSet state ) {
            PNode node = new PNode();
            //Show the beakers
            for ( final Pie pie : state.pies ) {
                final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                    @Override public Double f( Slice s ) {
                        return s.shape().getBounds2D().getMinY();
                    }
                } );

                //TODO: Could read from cache like WaterGlassNodeFactory instead of creating new each time to improve performance
                node.addChild( new WaterGlassNode( state.countFilledCells( pie ), state.denominator ) {{
                    setOffset( pie.cells.index( 0 ).shape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                }} );
            }
            return node;
        }
    };
}