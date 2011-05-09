// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.mazegame;

import java.awt.*;

public class BarrierList {
    static int fullWidth = 700;
    static int fullHeight = 250;

    //Arrays of barrier Rectangles made by edu.colorado.phet.mazegame.BarrierMaker.class
    //Level O play has no barriers
    static Rectangle[] rectArray0 = {};

    static Rectangle[] rectArray1 = {
            new Rectangle( 190, 120, 340, 10 ),
            new Rectangle( 520, 120, 10, 40 ),
            new Rectangle( 190, 90, 10, 30 ),
    };
    static Rectangle[] rectArray2 = {
            new Rectangle( 0, 0, 10, 250 ),
            new Rectangle( 690, 0, 10, 250 ),
            new Rectangle( 140, 20, 380, 10 ),
            new Rectangle( 140, 20, 10, 100 ),
            new Rectangle( 220, 80, 370, 10 ),
            new Rectangle( 220, 80, 10, 110 ),
            new Rectangle( 220, 180, 370, 10 ),
    };
    static Rectangle[] rectArray3 = {
            new Rectangle( 140, 20, 380, 10 ),
            new Rectangle( 140, 20, 10, 100 ),
            new Rectangle( 220, 180, 370, 10 ),
            new Rectangle( 20, 110, 130, 10 ),
            new Rectangle( 210, 60, 10, 60 ),
            new Rectangle( 210, 80, 170, 10 ),
            new Rectangle( 370, 80, 10, 70 ),
            new Rectangle( 470, 90, 10, 140 ),
            new Rectangle( 280, 120, 10, 110 ),
            new Rectangle( 20, 110, 10, 70 ),
            new Rectangle( 40, 220, 240, 10 ),
            new Rectangle( 510, 20, 10, 50 ),
            new Rectangle( 540, 110, 150, 10 ),
            new Rectangle( 690, 0, 10, 250 ),
            new Rectangle( 580, 160, 10, 50 ),
    };

    static Rectangle[][] currentRectArray = {
            rectArray0, rectArray1, rectArray2, rectArray3};

    //Need two copies of the arrays, but I don't know how to clone
    //arrays, so I simply make them again.

    static Rectangle[] rectArray1b = {
            new Rectangle( 190, 120, 340, 10 ),
            new Rectangle( 520, 120, 10, 40 ),
            new Rectangle( 190, 90, 10, 30 ),
    };
    static Rectangle[] rectArray2b = {
            new Rectangle( 0, 0, 10, 250 ),
            new Rectangle( 690, 0, 10, 250 ),
            new Rectangle( 140, 20, 380, 10 ),
            new Rectangle( 140, 20, 10, 100 ),
            new Rectangle( 220, 80, 370, 10 ),
            new Rectangle( 220, 80, 10, 110 ),
            new Rectangle( 220, 180, 370, 10 ),
    };
    static Rectangle[] rectArray3b = {
            new Rectangle( 140, 20, 380, 10 ),
            new Rectangle( 140, 20, 10, 100 ),
            new Rectangle( 220, 180, 370, 10 ),
            new Rectangle( 20, 110, 130, 10 ),
            new Rectangle( 210, 60, 10, 60 ),
            new Rectangle( 210, 80, 170, 10 ),
            new Rectangle( 370, 80, 10, 70 ),
            new Rectangle( 470, 90, 10, 140 ),
            new Rectangle( 280, 120, 10, 110 ),
            new Rectangle( 20, 110, 10, 70 ),
            new Rectangle( 40, 220, 240, 10 ),
            new Rectangle( 510, 20, 10, 50 ),
            new Rectangle( 540, 110, 150, 10 ),
            new Rectangle( 690, 0, 10, 250 ),
            new Rectangle( 580, 160, 10, 50 ),
    };

    static int[][] currentCollisionArray = new int[fullWidth][fullHeight];
    static int[][] collisionArray0 = new int[fullWidth][fullHeight];
    static int[][] collisionArray1 = new int[fullWidth][fullHeight];
    static int[][] collisionArray2 = new int[fullWidth][fullHeight];
    static int[][] collisionArray3 = new int[fullWidth][fullHeight];

    static {
        collisionArray0 = makeCollisionArray( rectArray0 );
        collisionArray1 = makeCollisionArray( rectArray1b );
        collisionArray2 = makeCollisionArray( rectArray2b );
        collisionArray3 = makeCollisionArray( rectArray3b );
    }

    public static int[][] makeCollisionArray( Rectangle[] rectArray ) {
        //grow wall rectangles to collision rectangles
        for ( int k = 0; k < rectArray.length; k++ ) {
            int r = 8; //edu.colorado.phet.mazegame.BarrierMaker.radius;
            rectArray[k].grow( r, r );
        }

        int[][] collisionArray = new int[fullWidth][fullHeight];

        for ( int i = 0; i < collisionArray.length; i++ ) {
            for ( int j = 0; j < collisionArray[0].length; j++ ) {
                Point pt = new Point( i, j );
                boolean isInside = false;

                for ( int k = 0; k < rectArray.length; k++ ) {

                    if ( rectArray[k].contains( pt ) ) {
                        isInside = true;
                        break;
                    }
                }
                if ( isInside ) {
                    collisionArray[i][j] = 1;
                    //System.out.print("*");
                }
                else {
                    collisionArray[i][j] = 0;
                    //System.out.print(" ");
                }
            }
        }

        return collisionArray;
    }

}