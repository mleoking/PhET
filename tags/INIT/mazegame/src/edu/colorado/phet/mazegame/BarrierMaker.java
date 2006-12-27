package edu.colorado.phet.mazegame;

//Creates Barrier complex for Maze Game or similar.

import java.awt.*;

class MyRectangle extends Rectangle {
    int startX, startY;
    int endX, endY;
    int minX, minY;
    int width2, height2;  //instance variables width,height of parent Rectangle class preserved

    public MyRectangle( int startX, int startY, int endX, int endY ) {
        super( Math.min( startX, endX ), Math.min( startY, endY ), Math.abs( startX - endX ), Math.abs( startY - endY ) );
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.minX = Math.min( startX, endX );
        this.minY = Math.min( startY, endY );
        this.width2 = Math.abs( startX - endX );
        this.height2 = Math.abs( startY - endY );
    }

    public void paint( Graphics g ) {
        g.fillRect( minX, minY, width2, height2 );
    }
}//end of class edu.colorado.phet.mazegame.MyRectangle