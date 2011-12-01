// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Oct 26, 2005
 * Time: 2:02:12 PM
 */

public class EnergySkateParkPieChartNode extends PNode {
    private final PieChartNode pieChartNode;
    private final AbstractEnergySkateParkModule module;
    private final SkaterNode skaterNode;
    private final double dy = 25;
    private boolean ignoreThermal;
    private final Body body;

    public EnergySkateParkPieChartNode( AbstractEnergySkateParkModule module, SkaterNode skaterNode ) {
        this.module = module;
        this.skaterNode = skaterNode;
        this.pieChartNode = new PieChartNode( createPieValues(), createPieRect() );
        this.body = skaterNode.getBody();
        addChild( pieChartNode );
        setPickable( false );
        setChildrenPickable( false );
        body.addListener( new Body.ListenerAdapter() {
            public void positionAngleChanged() {
                update();
            }

            public void energyChanged() {
                update();
            }
        } );
    }

    private Rectangle createPieRect() {
        if ( getModel().containsBody( body ) ) {
            PBounds gfb = skaterNode.getGlobalFullBounds();
            Point2D pt = new Point2D.Double( gfb.getCenterX(), gfb.getY() - dy );
            globalToLocal( pt );

            double totalEnergy = getTotalEnergy();
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

    private double getTotalEnergy() {
        if ( ignoreThermal ) {
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
        if ( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = module.getEnergySkateParkModel().getBody( 0 );
            double ke = body.getKineticEnergy();
            double pe = Math.abs( body.getPotentialEnergy() );
            double therm = body.getThermalEnergy();

            //Not clear how to handle negative values (such as negative potential energy).  For now, show absolute value in black
            PieChartNode.PieValue[] values = new PieChartNode.PieValue[] {
                    new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                    pe > 0 ? new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ) : new PieChartNode.PieValue( Math.abs( pe ), Color.black ),

                    //TODO: Sometimes thermal energy is coming up as negative too.  The root cause should be solved, but until then use this workaround of showing absolute value as black
                    therm > 0 ? new PieChartNode.PieValue( therm, getLookAndFeel().getThermalEnergyColor() ) : new PieChartNode.PieValue( Math.abs( therm ), Color.black )
            };
            if ( ignoreThermal ) {
                values = new PieChartNode.PieValue[] {
                        new PieChartNode.PieValue( ke, getLookAndFeel().getKEColor() ),
                        ( body.getPotentialEnergy() > 0 ) ? new PieChartNode.PieValue( pe, getLookAndFeel().getPEColor() ) : new PieChartNode.PieValue( Math.abs( pe ), Color.black ),
                };
            }
            return values;
        }
        else {
            return new PieChartNode.PieValue[0];
        }
    }

    private EnergyLookAndFeel getLookAndFeel() {
        return module.getEnergyLookAndFeel();
    }

    private void update() {
        pieChartNode.setArea( createPieRect() );
        pieChartNode.setPieValues( createPieValues() );

        setVisible( skaterNode.getBody().getPotentialEnergy() >= 0 );
    }

    public void setIgnoreThermal( boolean ignoreThermal ) {
        this.ignoreThermal = ignoreThermal;
        update();
    }
}
