/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.HistoryPoint;
import edu.colorado.phet.piccolo.nodes.HTMLGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
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

    public HistoryPointGraphic( final HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        double scale = 1.0 / 50.0;
        final PPath path = new PPath( new Ellipse2D.Double( -5 * scale, -5 * scale, 10 * scale, 10 * scale ) );
        addChild( path );
        path.setStroke( new BasicStroke( (float)( 1.0f * scale ) ) );
        path.setPaint( Color.yellow );
        update();
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                path.removeAllChildren();
                HTMLGraphic htmlGraphic = new HTMLGraphic( "<html>" +
                                                           "Kinetic Energy=" + format( historyPoint.getKE() ) + " J<br>" +
                                                           "Potential Energy=" + format( historyPoint.getPe() ) + " J<br>" +
                                                           "Total Energy=" + format( historyPoint.getTotalEnergy() ) + " J<br>" +
                                                           "</html>" );
                path.addChild( htmlGraphic );
            }

            public void mouseReleased( PInputEvent event ) {
                path.removeAllChildren();
            }
        } );
    }

    private String format( double pe ) {
        return formatter.format( pe );
    }

    private void update() {
        setOffset( historyPoint.getX(), historyPoint.getY() );
    }

    public void setHistoryPoint( HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        update();
    }
}
