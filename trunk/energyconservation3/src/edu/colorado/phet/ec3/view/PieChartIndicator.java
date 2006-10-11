/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EnergyLookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 26, 2005
 * Time: 2:02:12 PM
 * Copyright (c) Oct 26, 2005 by Sam Reid
 */

public class PieChartIndicator extends PNode {
    private PieChartNode pieChartNode;
    private EnergySkateParkModule module;
    private BodyGraphic bodyGraphic;
    private double dy = 25;
    private boolean ignoreThermal;

    public PieChartIndicator( EnergySkateParkModule module, BodyGraphic body ) {
        this.module = module;
        this.bodyGraphic = body;
        PieChartNode.PieValue[] values = createPieValues();
        Rectangle createPieRect = createPieRect();
        pieChartNode = new PieChartNode( values, createPieRect );
        addChild( pieChartNode );
        setPickable( false );
        setChildrenPickable( false );
    }

    private Rectangle createPieRect() {
        if( getModel().containsBody( bodyGraphic.getBody() ) ) {
            Body body = bodyGraphic.getBody();

//            Point2D.Double pt = new Point2D.Double( 0, 0 );
            PBounds gfb = bodyGraphic.getGlobalFullBounds();
            Point2D pt = new Point2D.Double( gfb.getCenterX(), gfb.getY() - dy );
            globalToLocal( pt );
//            Point2D pt=gfb.getOrigin();

            double totalEnergy = getTotalEnergy( body );
            double area = totalEnergy / 10 * 3.5;

            double radius = Math.sqrt( area / Math.PI );
            Rectangle createPieRect = new Rectangle();
            createPieRect.setFrameFromCenter( pt.getX(), pt.getY(), pt.getX() + radius, pt.getY() + radius );
            return createPieRect;
        }
        else {
            return new Rectangle( 10, 10 );
        }
    }

    private double getTotalEnergy( Body body ) {
        if( ignoreThermal ) {
            return getModel().getMechanicalEnergy( body );
        }
        else {
            return getModel().getMechanicalEnergy( body ) + getModel().getThermalEnergy();
        }
    }

    private EnergyConservationModel getModel() {
        return module.getEnergyConservationModel();
    }

    private PieChartNode.PieValue[] createPieValues() {
        if( module.getEnergyConservationModel().numBodies() > 0 ) {
            Body body = module.getEnergyConservationModel().bodyAt( 0 );
            double ke = body.getKineticEnergy();
            double pe = module.getEnergyConservationModel().getPotentialEnergy( body );
            double therm = module.getEnergyConservationModel().getThermalEnergy();

            PieChartNode.PieValue[] values = new PieChartNode.PieValue[]{
                    new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                    new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ),
                    new PieChartNode.PieValue( therm, getLookAndFeel().getThermalEnergyColor() )
            };
            if( ignoreThermal ) {
                values = new PieChartNode.PieValue[]{
                        new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                        new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ),
                };
            }
            // new PieChartNode.PieValue( 20, Color.pink ), new PieChartNode.PieValue( 15, Color.blue )};
            return values;
        }
        else {
            return new PieChartNode.PieValue[0];
        }
    }

    private EnergyLookAndFeel getLookAndFeel() {
        return module.getEnergyLookAndFeel();
    }

    public void update() {
        pieChartNode.setArea( createPieRect() );
        pieChartNode.setPieValues( createPieValues() );
    }

    public void setIgnoreThermal( boolean ignoreThermal ) {
        this.ignoreThermal = ignoreThermal;
        update();
    }
}
