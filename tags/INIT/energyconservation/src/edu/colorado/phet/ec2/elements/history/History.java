/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.history;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.common.GraphicControllerAdapter;
import edu.colorado.phet.ec2.elements.car.Car;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 19, 2003
 * Time: 12:17:12 AM
 * Copyright (c) Sep 19, 2003 by Sam Reid
 */
public class History extends ModelElement {
    public static boolean historyActive;
    double totalTime;
    double dotTime;
    double energyDotDT;
    EC2Module module;
    ArrayList energyDots = new ArrayList();
    ArrayList dotControls = new ArrayList();

    public History( double energyDotDT, EC2Module module ) {
//        this.totalTime = totalTime;
//        this.dotTime = dotTime;
        this.energyDotDT = energyDotDT;
        this.module = module;
    }

    public void clear() {

        for( int i = 0; i < dotControls.size(); i++ ) {
            InteractiveGraphic interactiveGraphic = (InteractiveGraphic)dotControls.get( i );
            module.getApparatusPanel().removeGraphic( interactiveGraphic );
        }
        dotControls.clear();

        for( int i = 0; i < energyDots.size(); i++ ) {
            EnergyDotGraphic edg = (EnergyDotGraphic)energyDots.get( i );
            module.getApparatusPanel().removeGraphic( edg );
            module.getBuffer().removeGraphic( edg );
        }
        module.getBuffer().updateBuffer();
        energyDots = new ArrayList();
        module.getEC2ControlPanel().numHistoryDotsChanged();
    }

    public void stepInTime( double dt ) {
//                O.d("time="+totalTime);
        if( historyActive ) {
            if( totalTime >= dotTime ) {
//                    if (isDotTime(totalTime)){
                dotTime = totalTime + energyDotDT;
                Car car = module.getCar();
                double speed = car.getSpeed();
                EnergyDot dot = new EnergyDot( car.getX(), car.getY(), totalTime, car.getKineticEnergy(), car.getPotentialEnergy(), car.getHeightAboveGround(), speed );
                EnergyDotGraphic dotGraphic = new EnergyDotGraphic( module, dot );
                EnergyDotGraphic last = null;
                if( energyDots.size() > 0 ) {
                    last = (EnergyDotGraphic)energyDots.get( energyDots.size() - 1 );
                }
                energyDots.add( dotGraphic );
//                        buffer.addGraphic(dotGraphic,EN);
                module.addDotGraphic( dotGraphic );

                if( last != null ) {
                    module.getApparatusPanel().removeGraphic( last );
                    module.addToBuffer( last );

                    InteractiveGraphic dotControl = new GraphicControllerAdapter( last );
                    module.addDotControl( dotControl );

                    module.getApparatusPanel().addGraphic( dotControl, 100 );
//                    buffer.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    last.paint( module.getBuffer().getGraphics() );
                    module.getEC2ControlPanel().numHistoryDotsChanged();
//                            buffer.updateBuffer();
                    //move the dot to the background.
//                            getApparatusPanel().addGraphic(last, ENERGY_DOT_BOTTOM_LAYER);
                }
            }
//               O.d("Total time="+totalTime);
        }
//                O.d("TotalTime="+totalTime);
        totalTime += dt;//timeScale;
    }

    public void setActive( boolean historyActive ) {
        this.historyActive = historyActive;
    }

    public void setEnergyDotDT( double energyDotDT ) {
        this.energyDotDT = energyDotDT;
    }

    public int numHistoryDots() {
        return energyDots.size();
    }

    public boolean isActive() {
        return historyActive;
    }

}
