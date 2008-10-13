/**
 * Class: MonitorPanel
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 20, 2004
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.LasersResources;

public class MonitorPanel extends ApparatusPanel {

    private static Font axisLabelFont;

    static {
        String family = "SansSerif";
        int style = Font.BOLD;
        int size = 12;
        axisLabelFont = new Font( family, style, size );
    }

    private Point2D strLoc = new Point2D.Double();
    private AffineTransform atx = new AffineTransform();
    protected int numGroundLevel;
    protected int numMiddleLevel;
    protected int numHighLevel;
    private String yAxisLabel = LasersResources.getString( "MonitorPanel.YAxislabel" );


    protected MonitorPanel() {
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        GraphicsState gs = new GraphicsState( g2 );

        super.paintComponent( g );

        g2.setFont( axisLabelFont );
        g2.setColor( Color.black );
        FontMetrics fm = g2.getFontMetrics();

        Point2D top1 = new Point2D.Double( fm.getHeight() + 4, 10 );
        Point2D bottom1 = new Point2D.Double( fm.getHeight() + 4, getBounds().getHeight() - 10 );
        Arrow arrow1 = new Arrow( bottom1, top1, 10, 10, 2 );
        g2.fill( arrow1.getShape() );

        strLoc.setLocation( fm.getHeight(), getBounds().getHeight() - 10 );
        AffineTransform strTx = rotateInPlace( atx, -Math.PI / 2, strLoc.getX(), strLoc.getY() );
        g2.transform( strTx );
        GraphicsUtil.setAntiAliasingOn( g2 );
        g2.drawString( yAxisLabel, (int) strLoc.getX(), (int) strLoc.getY() );
        gs.restoreGraphics();

//        super.paintComponent( g );

    }

    private static AffineTransform rotateInPlace( AffineTransform atx, double theta, double x, double y ) {
        atx.setToIdentity();
        atx.translate( x, y );
        atx.rotate( theta );
        atx.translate( -x, -y );
        return atx;
    }
}
