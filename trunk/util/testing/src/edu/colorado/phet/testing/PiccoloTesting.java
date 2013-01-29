// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.testing;

import edu.umd.cs.piccolo.PNode;

/**
 * Some Piccolo tests for various operations, so that the prototype JS scene-graph can duplicate this as much as possible
 *
 * @author Jonathan Olson
 */
public class PiccoloTesting {
    public static void main( String[] args ) {
        {
            PNode node = new PNode();
            System.out.println( node.getTransform() );

            node.scale( 2 );
            System.out.println( node.getTransform() );

            node.translate( 1, 3 );
            System.out.println( node.getTransform() );

            node.rotate( Math.PI / 2 );
            System.out.println( node.getTransform() );

            node.translate( -31, 21 );
            System.out.println( node.getTransform() );
            System.out.println( node.getOffset().getX() );
            System.out.println( node.getOffset().getY() );
            System.out.println( node.getRotation() );

            node.setOffset( -5, 7 );
            System.out.println( node.getTransform() );

            node.setRotation( 1.2 );
            System.out.println( node.getTransform() );

            node.setRotation( -0.7 );
            System.out.println( node.getTransform() );
        }
    }
}
