/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.energyskatepark.model.HistoryPoint;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 4:53:12 PM
 */

public class HistoryPointNode extends PNode {
    private HistoryPoint historyPoint;
    private EnergySkateParkRootNode rootNode;
    private DecimalFormat formatter = new DecimalFormat( "0.00" );
    private ShadowHTMLNode htmlNode;
    private Paint paint = new Color( 220, 175, 250 );

    public HistoryPointNode( HistoryPoint historyPoint, EnergySkateParkRootNode rootNode ) {
        this.historyPoint = historyPoint;
        this.rootNode = rootNode;
        final double scale = 1.5;
        final PPath path = new PPath( new Ellipse2D.Double( -5 * scale, -5 * scale, 10 * scale, 10 * scale ) );
        addChild( path );
        path.setStroke( new BasicStroke( (float)( 1.0f * scale ) ) );
        path.setPaint( paint );

        htmlNode = new ShadowHTMLNode( "" );
        htmlNode.addInputEventListener( new CursorHandler() );
        htmlNode.setShadowOffset( 1, 1 );
        htmlNode.setShadowColor( Color.white );
        htmlNode.setColor( Color.black );

        htmlNode.scale( scale );
        addInputEventListener( new CursorHandler() );
        PBasicInputEventHandler eventHandler = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                HistoryPointNode.this.historyPoint.setReadoutVisible( !HistoryPointNode.this.historyPoint.isReadoutVisible() );
                update();//todo: listener pattern
            }
        };
        rootNode.addWorldTransformListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        addInputEventListener( eventHandler );
        htmlNode.addInputEventListener( eventHandler );
        update();
    }

    private String format( double pe ) {
        return formatter.format( pe );
    }

    private void update() {
        Point2D.Double pt = new Point2D.Double( historyPoint.getX(), historyPoint.getY() );
        rootNode.worldToScreen( pt );
        setOffset( pt );

        String heatString = historyPoint.getThermalEnergy() != 0 ? "Thermal Energy=" + format( historyPoint.getThermalEnergy() ) + " J<br>" : "";
//        String heightSpeedReadout="";
        String heightSpeedReadout = "<br>Height=" + format( historyPoint.getHeightAboveZero() ) + " m<br>" +
                                    "Speed=" + format( historyPoint.getSpeed() ) + " m/s";
        String html = "<html>" +
                      "Kinetic Energy=" + format( historyPoint.getKE() ) + " J<br>" +
                      "Potential Energy=" + format( historyPoint.getPe() ) + " J<br>" +
                      heatString +
                      "Total Energy=" + format( historyPoint.getTotalEnergy() ) + " J<br>" +
                      heightSpeedReadout + "<br>" +
                      "</html>";
        if( historyPoint.isReadoutVisible() ) {
            htmlNode.setHtml( html );
        }
        getReadoutGraphic().setVisible( historyPoint.isReadoutVisible() );
        getReadoutGraphic().setPickable( historyPoint.isReadoutVisible() );
        getReadoutGraphic().setChildrenPickable( historyPoint.isReadoutVisible() );
        getReadoutGraphic().setOffset( getOffset() );
    }

    public void setHistoryPoint( HistoryPoint historyPoint ) {
        this.historyPoint = historyPoint;
        update();
    }

    public PNode getReadoutGraphic() {
        return htmlNode;
    }
}
