/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.theramp.common.BarGraphic;
import edu.colorado.phet.theramp.model.RampModel;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 10:42:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphSet extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private BarGraphic keGraphic;
    private BarGraphic peGraphic;
    private int dx = 10;
    private int dy = -10;
    private BarGraphic workGraphic;

    public BarGraphSet( RampPanel rampPanel, final RampModel rampModel ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        int y = 800;
        int width = 20;

        ModelViewTransform1D transform1D = new ModelViewTransform1D( 0, 75, 0, 10 );
        keGraphic = new BarGraphic( getComponent(), "Kinetic Energy", transform1D,
                                    rampModel.getBlock().getKineticEnergy(), 10, width, y, dx, dy );
        addGraphic( keGraphic );
        rampModel.addKEObserver( new SimpleObserver() {
            public void update() {
                keGraphic.setValue( rampModel.getBlock().getKineticEnergy() );
            }
        } );

        peGraphic = new BarGraphic( getComponent(), "Potential Energy", transform1D,
                                    rampModel.getPotentialEnergy(), 10 + width + 10, width, y, dx, dy );
        addGraphic( peGraphic );
        rampModel.addPEObserver( new SimpleObserver() {
            public void update() {
                peGraphic.setValue( rampModel.getPotentialEnergy() );
            }
        } );

        workGraphic = new BarGraphic( getComponent(), "Total Energy", transform1D,
                                      0, 60 + width, width, y, dx, dy );
        addGraphic( workGraphic );
        SimpleObserver obs = new SimpleObserver() {
            public void update() {
                workGraphic.setValue( rampModel.getPotentialEnergy() + rampModel.getBlock().getKineticEnergy() );
            }
        };
        rampModel.addPEObserver( obs );
        rampModel.addKEObserver( obs );

        setIgnoreMouse( true );
    }
}
