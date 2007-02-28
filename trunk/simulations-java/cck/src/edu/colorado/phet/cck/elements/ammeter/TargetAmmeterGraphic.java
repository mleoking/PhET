/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.ammeter;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.common.DifferentialDragHandler;
import edu.colorado.phet.cck.common.TargetReadoutTool;
import edu.colorado.phet.cck.elements.branch.AbstractBranchGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitGraphic;
import edu.colorado.phet.cck.elements.circuit.CircuitObserver;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:57:13 AM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class TargetAmmeterGraphic implements BoundedGraphic {
    TargetAmmeter ammeter;
    ModelViewTransform2D transform;
    CCK2Module module;
    private CircuitGraphic ccg;
    TargetReadoutTool graphic = new TargetReadoutTool();
    Point location;
    ArrayList data = new ArrayList();
    private DifferentialDragHandler ddh;

    public TargetAmmeterGraphic( TargetAmmeter ammeter, ModelViewTransform2D transform,
                                 CCK2Module module, CircuitGraphic ccg ) {
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
        AbstractBranchGraphic elm = ccg.getOverlappingElement( location );
        String currentString = "???";
        if( elm != null ) {
            double current = Math.abs( elm.getBranch().getCurrent() );
            String curStr = df.format( current );
            currentString = curStr + " Amps";
        }
        TargetReadoutTool.Datum datum = new TargetReadoutTool.Datum( "Current", currentString );
        data.add( datum );
    }

    public void paint( Graphics2D g ) {
        graphic.paint( g, location, (TargetReadoutTool.Datum[])data.toArray( new TargetReadoutTool.Datum[0] ) );
    }

    public boolean contains( int x, int y ) {
        return graphic.contains( new Point( x, y ) );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void translate( double x, double y ) {
        ammeter.translate( x, y );
    }

    public Shape getViewBounds() {
        return graphic.getBounds();
    }

    public Rectangle getVisibleRect() {
        Rectangle r = graphic.getBounds().getBounds();
        int inset = 14;
        return new Rectangle( r.x - inset, r.y - inset, r.width + inset * 2, r.height + inset * 2 );
    }
}
