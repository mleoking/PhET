package edu.colorado.phet.electrichockey;

import java.awt.*;

public class BarrierList {
    private ElectricHockeySimulationPanel electricHockeySimulationPanel;
    static int fullWidth = 700;    //edu.colorado.phet.ehockey.BarrierMaker.fullWidth;		//edu.colorado.phet.ehockey.BarrierMaker.fullWidth;
    static int fullHeight = 600;    //edu.colorado.phet.ehockey.BarrierMaker.fullHeight;	//edu.colorado.phet.ehockey.BarrierMaker.fullHeight;

    //Arrays of barrier Rectangles made by edu.colorado.phet.ehockey.BarrierMaker.class

    //Level O play has no barriers
    static Rectangle[] rectArray0 = {new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),};

    static Rectangle[] rectArray1 = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 360, 150, 10, 210 ),
    };
    static Rectangle[] rectArray2 = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 450, 210, 10, 230 ),
            new Rectangle( 280, 70, 10, 210 ),
    };
    static Rectangle[] rectArray3 = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 370, -20, 10, 350 ),
            new Rectangle( 240, 180, 10, 450 ),
            new Rectangle( 500, 180, 10, 430 ),
            new Rectangle( 470, 170, 70, 10 ),
            new Rectangle( 340, 330, 70, 10 ),
            new Rectangle( 210, 170, 70, 10 ),
    };

    static Rectangle[][] currentRectArray = {
            rectArray0, rectArray1, rectArray2, rectArray3};

    //Need two copies of the arrays, but I don't know how to clone
    //arrays, so I simply make them again.

    static Rectangle[] rectArray0b = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
    };

    static Rectangle[] rectArray1b = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 360, 150, 10, 210 ),
    };
    static Rectangle[] rectArray2b = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 450, 210, 10, 230 ),
            new Rectangle( 280, 70, 10, 210 ),
    };
    static Rectangle[] rectArray3b = {
            new Rectangle( 625, 265, 15, 5 ),
            new Rectangle( 625, 330, 10, 5 ),
            new Rectangle( 635, 265, 5, 70 ),
            new Rectangle( 370, -20, 10, 350 ),
            new Rectangle( 240, 180, 10, 450 ),
            new Rectangle( 500, 180, 10, 430 ),
            new Rectangle( 470, 170, 70, 10 ),
            new Rectangle( 340, 330, 70, 10 ),
            new Rectangle( 210, 170, 70, 10 ),
    };


    //Rectangle[] colliderArray1;  //Dont need
    //int[][] barrierArray = new int[fullWidth][fullHeight];  //Dont need
    static int[][] currentCollisionArray = new int[fullWidth][fullHeight];
    static int[][] collisionArray0 = new int[fullWidth][fullHeight];
    static int[][] collisionArray1 = new int[fullWidth][fullHeight];
    static int[][] collisionArray2 = new int[fullWidth][fullHeight];
    static int[][] collisionArray3 = new int[fullWidth][fullHeight];

    static {
        collisionArray0 = makeCollisionArray( rectArray0b );
        collisionArray1 = makeCollisionArray( rectArray1b );
        collisionArray2 = makeCollisionArray( rectArray2b );
        collisionArray3 = makeCollisionArray( rectArray3b );
    }

    public BarrierList( ElectricHockeySimulationPanel electricHockeySimulationPanel ) {
        this.electricHockeySimulationPanel = electricHockeySimulationPanel;
    }

    public static int[][] makeCollisionArray( Rectangle[] rectArray ) {
        //grow wall rectangles to collision rectangles
        for ( int k = 0; k < rectArray.length; k++ ) {
            int r = 8; //edu.colorado.phet.ehockey.BarrierMaker.radius;
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
                }
                else {
                    collisionArray[i][j] = 0;
                }
            }
        }
        return collisionArray;
    }
}
