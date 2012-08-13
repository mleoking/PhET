package edu.colorado.phet.functionalscenegraph;

import fj.data.Array;
import fj.data.List;

import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * Take a difference of two SNode trees to see what regions need to be repainted.
 *
 * @author Sam Reid
 */
public class CompareTrees {

    //Assume similar structures--could give false positives (may paint some regions that don't need it) but will be easier and guarantees no false negatives (never leaves a dirty region)
    public static List<ImmutableRectangle2D> compare( SNode a, SNode b ) {

        //If the trees are equal, nothing has changed and nothing needs to be repainted
        if ( a.equals( b ) ) {
            return List.nil();
        }
        else {
            if ( a instanceof SList && b instanceof SList ) {
                SList x = (SList) a;
                SList y = (SList) b;
                int xLength = x.length();
                int yLength = y.length();
                Array<SNode> x1 = x.children.toArray();
                Array<SNode> y1 = y.children.toArray();
                List<ImmutableRectangle2D> result = List.nil();
                for ( int i = 0; i < Math.min( xLength, yLength ); i++ ) {
                    List<ImmutableRectangle2D> r = compare( x1.get( i ), y1.get( i ) );//Index call is slow here
                    result = result.append( r );
                }
                return result;
            }
            else {
//                if ( a.getClass() != b.getClass() ) {
                return List.list( a.getBounds(), b.getBounds() );
//                return List.single( a.getBounds().union( b.getBounds() ) );
//                }
            }
        }
    }

    public static void main( String[] args ) {
        SNode a = createNode( 1 );
        SNode b = createNode( 2 );
        System.out.println( "compare( a,b ) = " + compare( a, b ) );
    }

    private static SNode createNode( double x ) {
//        final SNode text = DrawText.textNode( "Hello", new PhetFont( 30, true ), Color.blue ).translate( 100, 100 );
//        ImmutableRectangle2D bounds = text.getBounds();
//        //        System.out.println( "bounds = " + bounds );
//
//        DrawShape shape = new DrawShape( bounds );
//        SNode fillShape = new FillShape( bounds ).withPaint( Color.white );
//
        final SNode ellipse = new FillShape( new Ellipse2D.Double( 0, 0, 100, 100 ) ).translate( x, 0 );


//        //        int additional = 4;
//        int numCircles = 0;
//        //        final int max = numCircles + additional;
//        //        ArrayList<SNode> nodes = new ArrayList<SNode>( max );
//        List<SNode> list = List.nil();
//        for ( int i = 0; i < numCircles; i++ ) {
//            list = list.cons( new FillShape( new Ellipse2D.Double( 0, 0, 10, 10 ) ).withPaint( new Color( i % 255, 0, 0 ) ).translate( i / 10.0, i / 10.0 ) );
//        }

        //        fj.data.List<SNode> elements = fj.data.List.range( 0, 10000 ).map( new F<Integer, SNode>() {
        //            @Override public SNode f( final Integer i ) {
        //                return new FillShape( new Ellipse2D.Double( 0, 0, 10, 10 ) ).withPaint( new Color( i % 255, 0, 0 ) ).translate( i/10, i/10 );
        //            }
        //        } );
        //        list = list.cons( text );
        //        list = list.cons( fillShape );
        //        list = list.cons( shape );
//        list = list.cons( ellipse );
        //        nodes.addAll( Arrays.asList( fillShape, shape, text, ellipse ) );
        return new SList( ellipse );
    }
}