package edu.colorado.phet.ehockey;

//Creates Barrier complex for Maze Game or similar.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class BarrierMaker extends JFrame {
    static int del = 10 / 2;  			//grid size
    static int hN = 60 * 2;				//height in grid squares
    static int wN = 70 * 2;				//width in grid squares
    static int fullWidth = wN * del; 	//dimensions in pixels
    static int fullHeight = hN * del;
    static int radius = 8;


    edu.colorado.phet.ehockey.PaintPanel paintPnl;			//

    public BarrierMaker() {
        super( "Make Barriers" );
        setSize( BarrierMaker.fullWidth + 20, BarrierMaker.fullHeight + 50 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        paintPnl = new edu.colorado.phet.ehockey.PaintPanel();
        Container myPane = getContentPane();
        myPane.add( paintPnl, BorderLayout.CENTER );
        setContentPane( myPane );
        setVisible( true );
    }//end of constructor


    public static void main( String[] args ) {
        BarrierMaker barriers1 = new BarrierMaker();
    }
}//end of public class


class PaintPanel extends JPanel
        implements ActionListener, MouseListener, MouseMotionListener {
    static int count = 0;		//number of rectang's
    int del = BarrierMaker.del;	//grid spacing
    int startX;
    int startY;		//corner one of rectang
    int endX;
    int endY;			//corner two of rectang
    Point p;
    JButton finishBtn = new JButton( "Finished" );
    boolean finished;		//job done, write to file
    private Rectangle goal;

    Vector myRectVect = new Vector();
    edu.colorado.phet.ehockey.MyRectangle[] rectArray;
    edu.colorado.phet.ehockey.MyRectangle[] colliderArray;

    public PaintPanel() {
        finished = false;
        setBackground( Color.white );
        setSize( BarrierMaker.fullWidth, BarrierMaker.fullHeight );
        goal = new Rectangle( 9 * BarrierMaker.fullWidth / 10, BarrierMaker.fullHeight / 2 - 25, 5, 50 );
        finishBtn.addActionListener( this );
        add( finishBtn );
        addMouseListener( this );
        addMouseMotionListener( this );
    }


    public void paintComponent( Graphics g ) {
        super.paintComponent( g );


        //draw goal and positivePuckImage
        g.setColor( Color.black );
        g.fillRect( goal.x, goal.y, goal.width, goal.height );
        g.fillOval( BarrierMaker.fullWidth / 5 - BarrierMaker.radius, BarrierMaker.fullHeight / 2 - BarrierMaker.radius, 2 * BarrierMaker.radius, 2 * BarrierMaker.radius );

        g.setColor( Color.red );

        for( int i = 0; i < count; i++ ) {
            edu.colorado.phet.ehockey.MyRectangle myRect = (edu.colorado.phet.ehockey.MyRectangle)( myRectVect.elementAt( i ) );
            myRect.paint( g );
        }

        int width = Math.abs( startX - endX );
        int height = Math.abs( startY - endY );
        int cornerX = Math.min( startX, endX );
        int cornerY = Math.min( startY, endY );
        g.fillRect( cornerX, cornerY, width, height );

        if( finished ) {

        }
    }//end of paint method

    public void printRectList() {
        //Make Rectangle array from Vector List
        rectArray = new edu.colorado.phet.ehockey.MyRectangle[count];
        for( int i = 0; i < count; i++ ) {
            edu.colorado.phet.ehockey.MyRectangle rect1 = (edu.colorado.phet.ehockey.MyRectangle)( myRectVect.elementAt( i ) );
            rectArray[i] = rect1;
        }

        for( int i = 0; i < count; i++ ) {
            int x1 = rectArray[i].minX;
            int y1 = rectArray[i].minY;
            int w1 = rectArray[i].width2;
            int h1 = rectArray[i].height2;
            System.out.println( "new Rectangle(" + x1 +
                                ", " + y1 + ", " + w1 + ", " + h1 + ")," );
        }

        System.out.print( "\n" );

        for( int i = 0; i < count; i++ ) {
            int r = BarrierMaker.radius;
            edu.colorado.phet.ehockey.MyRectangle rect2 = rectArray[i];
            rect2.grow( r, r );
            int x2 = rect2.x;
            int y2 = rect2.y;
            int w2 = rect2.width;
            int h2 = rect2.height;
            //int x2 = rectArray[i].minX;	int y2 = rectArray[i].minY;
            //int w2 = rectArray[i].w; int h2 = rectArray[i].h;

            System.out.println( "new Rectangle(" + x2 +
                                ", " + y2 + ", " + w2 + ", " + h2 + ")," );
        }


    }

    public void mouseClicked( MouseEvent mevt ) {
        System.out.println( "Mouse clicked" );
        int x = mevt.getX();
        int y = mevt.getY();
        Point myPoint = new Point( x, y );
        for( int i = 0; i < myRectVect.size(); i++ ) {
            edu.colorado.phet.ehockey.MyRectangle myRect = (edu.colorado.phet.ehockey.MyRectangle)( myRectVect.elementAt( i ) );
            if( myRect.contains( myPoint ) ) {
                System.out.println( "Killing rectang " + i );
                myRectVect.removeElementAt( i );
                ;
                count--;
            }
            repaint();
        }
    }

    public void mouseEntered( MouseEvent mevt ) {
    }

    public void mouseExited( MouseEvent mevt ) {
    }

    public void mousePressed( MouseEvent mevt ) {
        System.out.println( "Mouse pressed" );
        startX = del * ( mevt.getX() / del );
        startY = del * ( mevt.getY() / del );
    }

    public void mouseReleased( MouseEvent mevt ) {

        endX = del * ( mevt.getX() / del );
        endY = del * ( mevt.getY() / del );

        if( ( startX != endX ) && ( startY != endY ) ) {
            myRectVect.addElement( new edu.colorado.phet.ehockey.MyRectangle( startX, startY, endX, endY ) );
            count++;
        }
        System.out.println( "Count = " + count );

    }

    public void mouseMoved( MouseEvent mevt ) {
    }

    public void mouseDragged( MouseEvent mevt ) {
        endX = del * ( mevt.getX() / del );
        endY = del * ( mevt.getY() / del );
        repaint();
    }

    public void actionPerformed( ActionEvent aevt ) {
        if( aevt.getSource() == finishBtn ) {
            finished = true;
            System.out.println( "Finished" );
            printRectList();
        }
    }
}//end of class edu.colorado.phet.ehockey.PaintPanel


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
}//end of class edu.colorado.phet.ehockey.MyRectangle
