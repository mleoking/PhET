/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.energyskatepark.model.HistoryPoint;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkRootNode;
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
    private String html = "";
    private boolean readoutVisible = false;
    private Paint paint = new Color( 220, 175, 250 );

    public HistoryPointNode( final HistoryPoint historyPoint, EnergySkateParkRootNode rootNode ) {
        this.historyPoint = historyPoint;
        this.rootNode = rootNode;
        final double scale = 1.5;
        final PPath path = new PPath( new Ellipse2D.Double( -5 * scale, -5 * scale, 10 * scale, 10 * scale ) );
        addChild( path );
        path.setStroke( new BasicStroke( (float)( 1.0f * scale ) ) );
        path.setPaint( paint );

        htmlNode = new ShadowHTMLNode( "" );
        htmlNode.setShadowOffset( 1, 1 );
        htmlNode.setShadowColor( Color.white );
        htmlNode.setColor( Color.black );

        htmlNode.scale( scale );
        PBasicInputEventHandler eventHandler = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                toggleVisible();
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

    private void toggleVisible() {
        this.readoutVisible = !this.readoutVisible;
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
        html = ( "<html>" +
                 "Kinetic Energy=" + format( historyPoint.getKE() ) + " J<br>" +
                 "Potential Energy=" + format( historyPoint.getPe() ) + " J<br>" +
                 heatString +
                 "Total Energy=" + format( historyPoint.getTotalEnergy() ) + " J<br>" +
                 "</html>" );
        if( readoutVisible ) {
            htmlNode.setHtml( html );
        }
        getReadoutGraphic().setVisible( readoutVisible );
        getReadoutGraphic().setPickable( readoutVisible );
        getReadoutGraphic().setChildrenPickable( readoutVisible );
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
