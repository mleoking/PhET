/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.ec2.elements.scene.BoundGraphic;
import edu.colorado.phet.ec2.elements.scene.RectangularBound;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 3, 2003
 * Time: 12:45:55 AM
 * Copyright (c) Aug 3, 2003 by Sam Reid
 */
public class BoundsSetup extends ModelElement {
    private double floorHeight;
    private RectangularBound floor;
    private RectangularBound westWall;
    private RectangularBound eastWall;
//    private EC2Module module;

    public BoundsSetup( EC2Module module ) {
//        this.module = module;

        double floorx = 0;
        double floory = 0;
        double floorWidth = module.getModelWidth();
        floorHeight = module.getModelHeight() / 20.0;

        floor = new RectangularBound( new Rectangle2D.Double( floorx, floory, floorWidth, floorHeight ), module, new PhetVector( 0, 1 ) );
        BoundGraphic bg = new BoundGraphic( module, floor );
//        module.getModel().addModelElement(floor);
        module.addBoundGraphic( bg );
//        module.getApparatusPanel().addGraphic(bg, 0);

        double wallwidth = module.getModelWidth() / 20.0;
        double westx = 0;
        double westy = 0;
        double westwidth = wallwidth;
        double westheight = module.getModelHeight();
        westWall = new RectangularBound( new Rectangle2D.Double( westx, westy, westwidth, westheight ), module, new PhetVector( 1, 0 ) );
        BoundGraphic westGraphic = new BoundGraphic( module, westWall );
        module.addBoundGraphic( westGraphic );
//        module.getApparatusPanel().addGraphic(westGraphic, 0);
//        module.add(westWall, westGraphic, 0);

        double eastx = module.getModelHeight() - wallwidth;
        double easty = 0;
        double eastwidth = wallwidth;
        double eastheight = module.getModelHeight();
        eastWall = new RectangularBound( new Rectangle2D.Double( eastx, easty, eastwidth, eastheight ), module, new PhetVector( -1, 0 ) );
//        module.add(eastWall, new BoundGraphic(module, eastWall), 0);
//        module.getApparatusPanel().addGraphic(, 0);
        module.addBoundGraphic( new BoundGraphic( module, eastWall ) );

    }

    public double getFloorHeight() {
        return floorHeight;
    }

    public void stepInTime( double dt ) {
        floor.stepInTime( dt );
        westWall.stepInTime( dt );
        eastWall.stepInTime( dt );
    }
}
