/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.Switch;
import edu.colorado.phet.cck3.common.ShadowTextGraphic;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 3:17:14 PM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class ReadoutGraphic implements Graphic {
    ShadowTextGraphic textGraphic;
    Branch branch;
    private ModelViewTransform2D transform;
    private ApparatusPanel panel;
//    DecimalFormat formatter = new DecimalFormat( "#0.00000" );//for debugging.
    DecimalFormat formatter = new DecimalFormat( "#0.0#" );
    static Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
//    static Font font = new Font( "Dialog", Font.BOLD, 18 );

    public ReadoutGraphic( Branch branch, ModelViewTransform2D transform, ApparatusPanel panel ) {
        this.branch = branch;
        this.transform = transform;
        this.panel = panel;

        recompute();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                recompute();
            }
        } );
        branch.addObserver( new SimpleObserver() {
            public void update() {
                recompute();
            }
        } );
    }

    public void recompute() {
        Rectangle2D r2 = null;
        if( textGraphic != null ) {
            r2 = textGraphic.getBounds();
        }

        String text = getText();
        //For debugging,
        //text+=" V=" + vol;
        AbstractVector2D vec = new Vector2D.Double( branch.getStartJunction().getPosition(),
                                                    branch.getEndJunction().getPosition() );
        vec = vec.getScaledInstance( .5 );
        Point2D pt = vec.getDestination( branch.getStartJunction().getPosition() );
        double angle = branch.getAngle();
        while( angle < 0 ) {
            angle += Math.PI * 2;
        }
        while( angle > Math.PI * 2 ) {
            angle -= Math.PI * 2;
        }
//        System.out.println( "angle = " + angle );
        boolean up = angle > Math.PI / 4 && angle < 3.0 / 4.0 * Math.PI;
        boolean down = angle > 5.0 / 4.0 * Math.PI && angle < 7.0 / 4.0 * Math.PI;
        if( up || down ) {
            pt = new Point2D.Double( pt.getX() + CCK3Module.BATTERY_DIMENSION.getHeight() * .5, pt.getY() );
        }
        else {
            pt = new Point2D.Double( pt.getX(), pt.getY() + CCK3Module.BATTERY_DIMENSION.getHeight() * .5 );
        }

        Point out = transform.modelToView( pt );

        if( textGraphic == null ) {
            textGraphic = new ShadowTextGraphic( font, text, 1, 1, out.x, out.y, Color.black, Color.yellow );
        }
        else {
            textGraphic.setState( font, text, 1, 1, out.x, out.y, Color.black, Color.yellow );
        }
        if( r2 != null ) {
            Rectangle2D r3 = textGraphic.getBounds();
            if( r2 != null && r3 != null ) {
                GraphicsUtil.fastRepaint( panel, r2.getBounds(), r3.getBounds() );
            }
        }
    }

    protected String getText() {
        double r = branch.getResistance();
        if( branch instanceof Switch ) {
            Switch swit = (Switch)branch;
            if( !swit.isClosed() ) {
                r = Double.POSITIVE_INFINITY;
            }
        }
        String res = formatter.format( r );
        String cur = formatter.format( Math.abs( branch.getCurrent() ) );
        String vol = formatter.format( Math.abs( branch.getVoltageDrop() ) );
        res = abs( res );
        cur = abs( cur );
        vol = abs( vol );

//        String text = "R=" + res + " I=" + cur;
        String text = res + " Ohms, " + cur + " Amps";
        return text;
    }

    private String abs( String vol ) {
        StringTokenizer st = new StringTokenizer( vol, ".0" );
        if( st.hasMoreTokens() && st.nextToken().equals( "-" ) ) {
            return vol.substring( 1 );
        }
        else {
            return vol;
        }
    }

    public void paint( Graphics2D g ) {
//        Rectangle2D bounds = textGraphic.getBounds();
//        if( bounds != null ) {
//            Color fill = new Color( 200, 200, 200, 128 );
//            g.setColor( fill );
//            g.fill( bounds );
//        }
        textGraphic.paint( g );
    }

    public static class BatteryReadout extends ReadoutGraphic {
        public BatteryReadout( Branch branch, ModelViewTransform2D transform, ApparatusPanel panel ) {
            super( branch, transform, panel );
        }

        protected String getText() {
            double volts = Math.abs( branch.getVoltageDrop() );
            String vol = formatter.format( volts );
            return "" + vol + " Volts";
        }
    }
}
