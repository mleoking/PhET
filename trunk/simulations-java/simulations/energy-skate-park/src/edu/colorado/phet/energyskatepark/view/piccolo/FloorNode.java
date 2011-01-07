// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.energyskatepark.model.Planet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 12:59:47 PM
 */

public class FloorNode extends PNode {
    private EnergySkateParkModule module;
    private EnergySkateParkModel energySkateParkModel;
    private Floor floor;
    private PPath groundPPath;
    private PPath groundLinePPath;

    public FloorNode( EnergySkateParkModule module, EnergySkateParkModel energySkateParkModel, Floor floor ) {
        this.module = module;
        this.energySkateParkModel = energySkateParkModel;
        energySkateParkModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {

            public void gravityChanged() {
                update();
            }

            public void floorChanged() {
                update();
            }
        } );
        this.floor = floor;
        double y = floor.getY();
//        float offsetY = 3 * AbstractSpline.SPLINE_THICKNESS / 2;
        float offsetY = 0;
        double xMin = -1000;
        double xMax = 1000;
        double height = 1000;
        groundPPath = new PPath( new Rectangle2D.Double( xMin, y - height, xMax - xMin, height + offsetY ) );
        groundPPath.setPaint( new Color( 100, 170, 100 ) );
        groundPPath.setStroke( null );
        addChild( groundPPath );

        Line2D.Double line = new Line2D.Double( xMin, y + offsetY, xMax, y + offsetY );
        groundLinePPath = new PPath( line );
        groundLinePPath.setStroke( new BasicStroke( 0.03f ) );
        groundLinePPath.setStrokePaint( new Color( 0, 130, 0 ) );

        addChild( groundLinePPath );

        update();
    }

    private void update() {
        Planet[] planets = module.getPlanets();

        boolean madeVisible = false;
        for( int i = 0; i < planets.length; i++ ) {
            Planet planet = planets[i];
            if( planet.getGravity() == energySkateParkModel.getGravity() ) {
                setPlanetVisible( planet );
                madeVisible = true;
                break;
            }
        }
        if( !madeVisible ) {
            setPlanetVisible( new Planet.Earth() );//default state
        }
        setVisible( energySkateParkModel.getFloor() != null );
    }

    private void setPlanetVisible( Planet planet ) {
        groundPPath.setPaint( planet.getGroundPaint() );
        groundLinePPath.setStrokePaint( planet.getGroundLinePaint() );
        setVisible( planet.isGroundVisible() );
    }
}
