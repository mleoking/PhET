/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.HistoryPoint;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 4:53:12 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class HistoryPointGraphic extends PNode {
    private HistoryPoint historyPoint;
    private DecimalFormat formatter = new DecimalFormat( "0.00" );
    private ShadowHTMLGraphic htmlGraphic;
    private String html = "";
    private boolean readoutVisible = false;

    public HistoryPointGraphic( final HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        final double scale = 1.0 / 50.0;
        final PPath path = new PPath( new Ellipse2D.Double( -5 * scale, -5 * scale, 10 * scale, 10 * scale ) );
        addChild( path );
        path.setStroke( new BasicStroke( (float)( 1.0f * scale ) ) );
        path.setPaint( Color.yellow );

        htmlGraphic = new ShadowHTMLGraphic( "" );
        htmlGraphic.setShadowOffset( 1, 1 );
        htmlGraphic.setShadowColor( Color.white );
        htmlGraphic.setColor( Color.black );

        htmlGraphic.scale( scale );
        htmlGraphic.transformBy( AffineTransform.getScaleInstance( 1, -1 ) );
        PBasicInputEventHandler eventHandler = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                toggleVisible();
            }
        };
        addInputEventListener( eventHandler );
        htmlGraphic.addInputEventListener( eventHandler );
        update();
    }

    private void toggleVisible() {
        this.readoutVisible = !this.readoutVisible;
        update();
    }

    private String format( double pe ) {
        return formatter.format( pe );
    }

    private void update() {
        setOffset( historyPoint.getX(), historyPoint.getY() );
        String heatString = historyPoint.getThermalEnergy() != 0 ? "Thermal Energy=" + format( historyPoint.getThermalEnergy() ) + " J<br>" : "";
        html = ( "<html>" +
                 "Kinetic Energy=" + format( historyPoint.getKE() ) + " J<br>" +
                 "Potential Energy=" + format( historyPoint.getPe() ) + " J<br>" +
                 heatString +
                 "Total Energy=" + format( historyPoint.getTotalEnergy() ) + " J<br>" +
                 "</html>" );
        if( readoutVisible ) {
            htmlGraphic.setHtml( html );
        }
        getReadoutGraphic().setVisible( readoutVisible );
        getReadoutGraphic().setPickable( readoutVisible );
        getReadoutGraphic().setChildrenPickable( readoutVisible );
        getReadoutGraphic().setOffset( historyPoint.getX(), historyPoint.getY() );
//        getReadoutGraphic().setTransform( getTransform() );
    }

    public void setHistoryPoint( HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        update();
    }

    public PNode getReadoutGraphic() {
        return htmlGraphic;
    }
}
