/**
 * Class: WiggleMeGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class WiggleMeGraphic extends PhetGraphic {

    private Component component;
    private BaseModel model;
    Point2D.Double current = new Point2D.Double();
    String family = "Sans Serif";
    int style = Font.BOLD;
    int size = 16;
    Font font = new Font( family, style, size );
    private Point2D.Double startLocation;
    private Color color = IdealGasConfig.HELP_COLOR;
    private Rectangle redrawArea;
    private ModelElement wiggleMeModelElement;

    public WiggleMeGraphic( final Component component, final Point2D.Double startLocation, BaseModel model ) {
        super( component );
        this.component = component;
        this.model = model;
        this.startLocation = startLocation;
        current.setLocation( startLocation );
        redrawArea = new Rectangle( (int)startLocation.getX(), (int)startLocation.getY() - 50,
                                    110, 60 );
        wiggleMeModelElement = new ModelElement() {
            double cnt = 0;

            public void stepInTime( double dt ) {
                cnt += 0.1;
                current.setLocation( startLocation.getX() + 30 * Math.cos( cnt ),
                                     startLocation.getY() + 15 * Math.sin( cnt ) );
                component.invalidate();
                component.repaint( (int)redrawArea.getX(), (int)redrawArea.getY(), redrawArea.width, redrawArea.height );
            }
        };
    }

    public void start() {
        model.addModelElement( wiggleMeModelElement );
    }

    protected Rectangle determineBounds() {
        return redrawArea;
    }

    public void kill() {
        model.removeModelElement( wiggleMeModelElement );
    }

//    public void run() {
//        double cnt = 0;
//        while( loop == this ) {
//            try {
//                Thread.sleep( 50 );
//            }
//            catch( InterruptedException e ) {
//                e.printStackTrace();
//            }
//            cnt += 0.2;
//            current.setLocation( startLocation.getX() + 30 * Math.cos( cnt ),
//                                 startLocation.getY() + 15 * Math.sin( cnt ) );
//
////            this.setBoundsDirty();
////            this.repaint();
//            component.invalidate();
//            component.repaint();
////            component.repaint( (int)redrawArea.getX(), (int)redrawArea.getY(), redrawArea.width, redrawArea.height );
//        }
//    }

    public void paint( Graphics2D g ) {
        RenderingHints orgRH = g.getRenderingHints();
        GraphicsUtil.setAntiAliasingOn( g );
        g.setFont( font );
        g.setColor( color );
        String s1 = SimStrings.get( "WiggleMe.Pump_the" );
        String s2 = SimStrings.get( "WiggleMe.handle!" );
        g.drawString( s1, (int)current.getX(), (int)current.getY() - g.getFontMetrics( font ).getHeight() );
        g.drawString( s2, (int)current.getX(), (int)current.getY() );
        Point2D.Double arrowTail = new Point2D.Double( current.getX() + SwingUtilities.computeStringWidth( g.getFontMetrics( font ), s2 ) + 10,
                                                       (int)current.getY() - g.getFontMetrics( font ).getHeight() / 2 );
        Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );
        Arrow arrow = new Arrow( arrowTail, arrowTip, 6, 6, 2, 100, false );
        g.fill( arrow.getShape() );

        g.setRenderingHints( orgRH );
    }
}
