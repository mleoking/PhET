/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
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
    private SkaterNode skaterNode;
    private double dy = 25;
    private boolean ignoreThermal;

    public PieChartIndicator( EnergySkateParkModule module, SkaterNode skaterNode ) {
        this.module = module;
        this.skaterNode = skaterNode;
        pieChartNode = new PieChartNode( createPieValues(), createPieRect() );
        addChild( pieChartNode );
        setPickable( false );
        setChildrenPickable( false );

//        final Body body = skaterNode.getBody();
//
//        body.addListener(new Body.ListenerAdapter() {
//            public void potentialEnergyChanged() {
//                setVisible(body.getPotentialEnergy() >= 0);
//            }
//        });
    }

    private Rectangle createPieRect() {
        if( getModel().containsBody( skaterNode.getBody() ) ) {
            Body body = skaterNode.getBody();

//            Point2D.Double pt = new Point2D.Double( 0, 0 );
            PBounds gfb = skaterNode.getGlobalFullBounds();
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
            return body.getKineticEnergy() + Math.abs( body.getPotentialEnergy() );
        }
        else {
            return body.getKineticEnergy() + Math.abs( body.getPotentialEnergy() ) + body.getThermalEnergy();
        }
    }

    private EnergySkateParkModel getModel() {
        return module.getEnergySkateParkModel();
    }

    private PieChartNode.PieValue[] createPieValues() {
        if( module.getEnergySkateParkModel().numBodies() > 0 ) {
            Body body = module.getEnergySkateParkModel().bodyAt( 0 );
            double ke = body.getKineticEnergy();
            double pe = Math.abs( body.getPotentialEnergy() );
            double therm = body.getThermalEnergy();

            PieChartNode.PieValue[] values = new PieChartNode.PieValue[]{
                    new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                    ( body.getPotentialEnergy() > 0 ) ? new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ) : new PieChartNode.PieValue( pe, Color.black ),
                    new PieChartNode.PieValue( therm, getLookAndFeel().getThermalEnergyColor() )
            };
            if( ignoreThermal ) {
                values = new PieChartNode.PieValue[]{
                        new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                        ( body.getPotentialEnergy() > 0 ) ? new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ) : new PieChartNode.PieValue( pe, Color.black ),
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

        setVisible( skaterNode.getBody().getPotentialEnergy() >= 0 );
    }

    public void setIgnoreThermal( boolean ignoreThermal ) {
        this.ignoreThermal = ignoreThermal;
        update();
    }
}
