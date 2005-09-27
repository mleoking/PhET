/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.FloorGraphic;
import edu.colorado.phet.piccolo.PiccoloModule;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Module extends PiccoloModule {
    private EnergyConservationModel energyModel;
    private EC3PhetPCanvas energyCanvas;

    /**
     * @param name
     * @param clock
     */
    public EC3Module( String name, AbstractClock clock ) {
        super( name, clock );
        energyModel = new EnergyConservationModel();
        Body body = new Body(new Rectangle( 100,100) );
        energyModel.addBody( body );
        Floor floor = new Floor( getEnergyConservationModel() );
        energyModel.addFloor( floor );
        setModel( new BaseModel() );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                energyModel.stepInTime( dt );
            }
        } );
        energyCanvas = new EC3PhetPCanvas( this );
        setPhetPCanvas( energyCanvas );
        energyCanvas.addWorldChild( new FloorGraphic( floor ) );
    }


    public EnergyConservationModel getEnergyConservationModel() {
        return energyModel;
    }
}
