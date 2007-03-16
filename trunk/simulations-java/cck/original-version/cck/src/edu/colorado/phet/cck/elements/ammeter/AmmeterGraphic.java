/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.ammeter;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.common.DifferentialDragHandler;
import edu.colorado.phet.cck.elements.branch.AbstractBranchGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitGraphic;
import edu.colorado.phet.cck.elements.circuit.CircuitObserver;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:57:13 AM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class AmmeterGraphic implements InteractiveGraphic {
    Ammeter ammeter;
    ModelViewTransform2D transform;
    CCK2Module module;
    private CircuitGraphic ccg;
    MeasuringGraphicNew graphic = new MeasuringGraphicNew();
    Point location;
    ArrayList data = new ArrayList();
    private DifferentialDragHandler ddh;

    public AmmeterGraphic( Ammeter ammeter, ModelViewTransform2D transform, CCK2Module module, CircuitGraphic ccg ) {
        this.ammeter = ammeter;
        this.transform = transform;
        this.module = module;
        this.ccg = ccg;
        ammeter.addObserver( new SimpleObserver() {
            public void update() {
                doUpdate();
            }
        } );
        doUpdate();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
                doUpdate();
            }
        } );
        ccg.getCircuit().addCircuitObserver( new CircuitObserver() {
            public void branchAdded( Circuit circuit2, Branch branch ) {
                doUpdate();
            }

            public void branchRemoved( Circuit circuit2, Branch branch ) {
                doUpdate();
            }

            public void connectivityChanged( Circuit circuit2 ) {
                doUpdate();
            }
        } );
    }

    DecimalFormat df = new DecimalFormat( "#0.0#" );

    public void doUpdate() {
        data.clear();
        location = transform.modelToView( ammeter.getLocation() );
//        double width = .2;
//        Shape myShape = new Rectangle2D.Double(location.getX() - width / 2, location.getY() - width / 2, width, width);
        AbstractBranchGraphic elm = ccg.getOverlappingElement( location );
        String currentString = "???";
        if( elm != null ) {
            double current = Math.abs( elm.getBranch().getCurrent() );
            String curStr = df.format( current );
            currentString = curStr + " Amps";
        }
        MeasuringGraphicNew.Datum datum = new MeasuringGraphicNew.Datum( "Current", currentString );
        data.add( datum );
    }

    public void paint( Graphics2D g ) {
        graphic.paint( g, location, data );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return graphic.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        ddh = new DifferentialDragHandler( event.getPoint() );
    }

    public void mouseDragged( MouseEvent event ) {
        Point pt = ddh.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double model = transform.viewToModelDifferential( pt );
        ammeter.translate( model.x, model.y );
        module.getApparatusPanel().repaint();
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public boolean contains( int x, int y ) {
        return graphic.contains( new Point( x, y ) );
    }
}
