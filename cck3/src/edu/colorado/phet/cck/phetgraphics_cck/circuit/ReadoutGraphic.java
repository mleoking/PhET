/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetMultiLineTextGraphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 3:17:14 PM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class ReadoutGraphic implements Graphic {
    PhetMultiLineTextGraphic textGraphic;
    private CCKModule module;
    Branch branch;
    private ModelViewTransform2D transform;
    private ApparatusPanel panel;
    static Font font = new Font( "Lucida Sans", Font.BOLD, 16 );

    private DecimalFormat formatter;
    private SimpleObserver observer;
    private TransformListener transformListener;

    public ReadoutGraphic( CCKModule module, Branch branch, ModelViewTransform2D transform, ApparatusPanel panel, DecimalFormat formatter ) {
        this.module = module;
        this.branch = branch;
        this.transform = transform;
        this.panel = panel;
        this.formatter = formatter;

        recompute();
        TransformListener transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                recompute();
            }
        };
        this.transformListener = transformListener;
        transform.addTransformListener( this.transformListener );
        observer = new SimpleObserver() {
            public void update() {
                recompute();
            }
        };
        this.branch.addObserver( observer );
        setVisible( false );
    }

    public boolean isVisible() {
        return textGraphic.isVisible();
    }

    public void setVisible( boolean visible ) {
        textGraphic.setVisible( visible );
    }

    public void recompute() {
        String[] text = getText();
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
            pt = new Point2D.Double( pt.getX() + CCKModel.BATTERY_DIMENSION.getHeight() * 2.3, pt.getY() );
        }
        else {
            pt = new Point2D.Double( pt.getX(), pt.getY() + CCKModel.BATTERY_DIMENSION.getHeight() * .3 );
        }
        pt = new Point2D.Double( pt.getX() + CCKModel.BATTERY_DIMENSION.getLength() / 2, pt.getY() );
        Point out = transform.modelToView( pt );
        Color foregroundColor = Color.black;
        Color backgroundColor = Color.yellow;

        int dx = 1;
        int dy = 1;
        if( textGraphic == null ) {
            textGraphic = new PhetMultiLineTextGraphic( panel, text, font, out.x, out.y, foregroundColor, dx, dy, backgroundColor );
            textGraphic.setVisible( false );
        }
        else {
            textGraphic.setText( text );
            Rectangle bounds = textGraphic.getBounds();
            out = new Point( (int)( out.getX() - bounds.getWidth() ), (int)( out.getY() - bounds.getHeight() ) );
            textGraphic.setPosition( out.x, out.y );
        }
    }

    public DecimalFormat getFormatter() {
        return formatter;
    }

    protected String[] getText() {
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
        String text = res + " " + SimStrings.get( "ReadoutGraphic.Ohms" );  //, " + cur + " " + SimStrings.get( "ReadoutGraphic.Amps" );
        return new String[]{text};
    }

    protected String abs( String vol ) {
        StringTokenizer st = new StringTokenizer( vol, ".0" );
        if( st.hasMoreTokens() && st.nextToken().equals( "-" ) ) {
            return vol.substring( 1 );
        }
        else {
            return vol;
        }
    }

    public void paint( Graphics2D g ) {
        textGraphic.paint( g );
    }

    public Branch getBranch() {
        return branch;
    }

    public void delete() {
        branch.removeObserver( observer );
        transform.removeTransformListener( transformListener );
    }

    public String format( double amplitude ) {
        return abs( formatter.format( amplitude ) );
    }

    public static class BatteryReadout extends ReadoutGraphic {

        public BatteryReadout( CCKModule module, Branch branch, ModelViewTransform2D transform, ApparatusPanel panel, boolean visible, DecimalFormat decimalFormatter ) {
            super( module, branch, transform, panel, decimalFormatter );
        }

        protected String[] getText() {
            boolean internal = super.module.isInternalResistanceOn();
            double volts = Math.abs( branch.getVoltageDrop() );
            String vol = super.formatter.format( volts );
            String str = "" + vol + " " + SimStrings.get( "ReadoutGraphic.Volts" );
            ArrayList text = new ArrayList();
            text.add( str );
            if( internal ) {
                String s2 = super.formatter.format( branch.getResistance() ) + " " + SimStrings.get( "ReadoutGraphic.Ohms" );
                text.add( s2 );
            }
            String[] out = (String[])text.toArray( new String[0] );
            return out;
        }
    }
}
