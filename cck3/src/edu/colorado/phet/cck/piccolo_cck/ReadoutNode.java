/** Sam Reid*/
package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;

import javax.swing.*;
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
public class ReadoutNode extends PhetPNode {
    private ShadowHTMLGraphic htmlNode;
    protected ICCKModule module;
    protected Branch branch;
    private JComponent panel;
    static Font font = new Font( "Lucida Sans", Font.BOLD, 16 );

    protected DecimalFormat formatter;

    public ReadoutNode( ICCKModule module, Branch branch, JComponent panel, DecimalFormat formatter ) {
        this.module = module;
        this.branch = branch;
        this.panel = panel;
        this.formatter = formatter;

        htmlNode = new ShadowHTMLGraphic( "" );
        htmlNode.setFont( font );
        Color foregroundColor = Color.black;
        Color backgroundColor = Color.yellow;
        htmlNode.setPaint( foregroundColor );
        htmlNode.setShadowColor( backgroundColor );
        htmlNode.setShadowOffset( 1, 1 );
//            panel, text, font, out.x, out.y, foregroundColor, dx, dy, backgroundColor );
        htmlNode.setVisible( false );

        recompute();
        setVisible( false );
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

        htmlNode.setHtml( toHTML( text ) );
//        Rectangle2D bounds = textGraphic.getFullBounds();
        htmlNode.setOffset( branch.getEndPoint() );
//        out = new Point( (int)( out.getX() - bounds.getWidth() ), (int)( out.getY() - bounds.getHeight() ) );
//        textGraphic.setPosition( out.x, out.y );
    }

    private String toHTML( String[] text ) {
        String html = "<html>";
        for( int i = 0; i < text.length; i++ ) {
            html += text[i];
            if( i < text.length - 1 ) {
                html += "<br>";
            }
        }
        return html + "</html>";
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

    public Branch getBranch() {
        return branch;
    }

    public String format( double amplitude ) {
        return abs( formatter.format( amplitude ) );
    }

    public static class BatteryReadoutNode extends ReadoutNode {

        public BatteryReadoutNode( ICCKModule module, Branch branch, JComponent panel, boolean visible, DecimalFormat decimalFormatter ) {
            super( module, branch, panel, decimalFormatter );
        }

        protected String[] getText() {
            boolean internal = module.isInternalResistanceOn();
            double volts = Math.abs( branch.getVoltageDrop() );
            String vol = formatter.format( volts );
            String str = "" + vol + " " + SimStrings.get( "ReadoutGraphic.Volts" );
            ArrayList text = new ArrayList();
            text.add( str );
            if( internal ) {
                String s2 = super.formatter.format( branch.getResistance() ) + " " + SimStrings.get( "ReadoutGraphic.Ohms" );
                text.add( s2 );
            }
            return (String[])text.toArray( new String[0] );
        }
    }
}
