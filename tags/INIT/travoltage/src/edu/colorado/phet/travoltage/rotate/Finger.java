package edu.colorado.phet.travoltage.rotate;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Finger// implements AngleListener, Painter
{
//      Component paintMe;
//      RotatingImage source;
//      int relX;
//      int relY;
//      Spark sparky;
//      Spark.SparkRunner sr;
//      public Finger(Component paintMe,RotatingImage source,int relX,int relY,Spark sparky)
//      {
//  	this.sparky=sparky;
//  	this.relX=relX;
//  	this.relY=relY;
//  	this.source=source;
//  	this.paintMe=paintMe;
//  	this.sr=sparky.newSparkRunner(paintMe,100000,20);
//  	new Thread(sr).start();
//      }
    public static Point getFingerLocation( RotatingImage source ) {
        return getFingerLocation( source, 115, 6 );
    }

    public static Point getFingerLocation( RotatingImage source, int relX, int relY ) {
        AffineTransform at = source.getTransform();
        Point p = new Point( relX, relY );
        Point2D dst = new Point();
        dst = at.transform( p, dst );
        //DoublePoint dp=getPosition();
        int x = (int)dst.getX();
        int y = (int)dst.getY();
        return new Point( x, y );
    }
//      public void paint(Graphics2D g)
//      {
//  	g.setColor(Color.red);
//  //  	AffineTransform at=source.getTransform();
//  //  	Point p=new Point(relX,relY);
//  //  	Point2D dst=new Point();
//  //  	dst=at.transform(p,dst);
//  //  	//DoublePoint dp=getPosition();
//  //  	int x=(int)dst.getX();
//  //  	int y=(int)dst.getY();
//  	Point sink=getFingerLocation(source,relX,relY);
//  	sparky.setSink(sink);//new Point(x,y));
//  	sr.paint(g);
//  	//edu.colorado.phet.common.util.Debug.traceln(new Point(x,y));
//  //  	edu.colorado.phet.common.util.Debug.traceln("Got finger position: "+dp+", current angle="+currentAngle);
//  //  	int x=(int)dp.getX();
//  //  	int y=(int)dp.getY();
//  	g.fillOval(sink.x,sink.y,10,10);
//      }
//      public void angleChanged(double ang)
//      {
//  	this.paintMe.repaint();
//      }
}
