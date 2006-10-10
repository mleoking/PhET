/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.HistoryPoint;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
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
    private HTMLNode htmlGraphic;
    private String html = "";

    public HistoryPointGraphic( final HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        final double scale = 1.0 / 50.0;
        final PPath path = new PPath( new Ellipse2D.Double( -5 * scale, -5 * scale, 10 * scale, 10 * scale ) );
        addChild( path );
        path.setStroke( new BasicStroke( (float)( 1.0f * scale ) ) );
        path.setPaint( Color.yellow );

        htmlGraphic = new HTMLNode();

        htmlGraphic.scale( scale );
        htmlGraphic.transformBy( AffineTransform.getScaleInstance( 1, -1 ) );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                toggleVisible();
            }
        } );
        update();
    }

    private void toggleVisible() {
        if( getChildrenReference().contains( htmlGraphic ) ) {
            removeChild( htmlGraphic );
        }
        else {
            updateHTMLText();
            addChild( htmlGraphic );
        }
    }

    private String format( double pe ) {
        return formatter.format( pe );
    }

    private void update() {
        setOffset( historyPoint.getX(), historyPoint.getY() );
        html = ( "<html>" +
                 "Kinetic Energy=" + format( historyPoint.getKE() ) + " J<br>" +
                 "Potential Energy=" + format( historyPoint.getPe() ) + " J<br>" +
                 "Total Energy=" + format( historyPoint.getTotalEnergy() ) + " J<br>" +
                 "</html>" );
        if( isHTMLVisible() ) {
            updateHTMLText();
        }
    }

    private void updateHTMLText() {
        htmlGraphic.setHTML( html );
    }

    private boolean isHTMLVisible() {
        return getChildrenReference().contains( htmlGraphic );
    }

    public void setHistoryPoint( HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        update();
    }
}
