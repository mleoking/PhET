/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.util.PieChartNode;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * MoleculePopulationsPieChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsPieChart extends PieChartNode {

    private double updateInterval;
    private double timeSinceLastUpdate;
    private PieValue[] values = new PieValue[4];
    private MoleculeCounter counterA;
    private MoleculeCounter counterAB;
    private MoleculeCounter counterBC;
    private MoleculeCounter counterC;
    private Rectangle currentSize = new Rectangle();
    private Point2D pieCenter;

    public MoleculePopulationsPieChart( MRModule module, Rectangle2D bounds, double updateInterval ) {
        super( new Rectangle());

        setBounds( bounds );
        pieCenter = new Point2D.Double( bounds.getCenterX(), bounds.getCenterY() );

        this.updateInterval = updateInterval;

        values[0] = new PieValue( 0, MoleculePaints.getPaint( MoleculeA.class ));
        values[1] = new PieValue( 0, MoleculePaints.getPaint( MoleculeBC.class ));
        values[2] = new PieValue( 0, MoleculePaints.getPaint( MoleculeAB.class ));
        values[3] = new PieValue( 0, MoleculePaints.getPaint( MoleculeC.class ));
        setPieValues( values );

        // Create counters for each of the molecule types
        counterA = new MoleculeCounter( MoleculeA.class, module.getMRModel() );
        counterAB = new MoleculeCounter( MoleculeAB.class, module.getMRModel() );
        counterBC = new MoleculeCounter( MoleculeBC.class, module.getMRModel() );
        counterC = new MoleculeCounter( MoleculeC.class, module.getMRModel() );

        module.getClock().addClockListener( new PieChartUpdater() );
    }

    private void update() {
        int numMolecules = counterA.getCnt() + counterBC.getCnt() + counterAB.getCnt() + counterC.getCnt();
        double diam = Math.max( 4, Math.min( getBounds().getHeight(), numMolecules )) * 2;
        currentSize.setFrameFromCenter( pieCenter.getX(),
                                        pieCenter.getY(),
                                        pieCenter.getX() - diam / 2,
                                        pieCenter.getY() - diam / 2);
        setArea( currentSize );
        setPieValues( values );
    }


    private class PieChartUpdater extends ClockAdapter {

        public void clockTicked( ClockEvent clockEvent ) {
            timeSinceLastUpdate += clockEvent.getSimulationTimeChange();
            if( timeSinceLastUpdate > updateInterval ) {
                timeSinceLastUpdate = 0;
                values[0].setValue( counterA.getCnt() );
                values[1].setValue( counterBC.getCnt() );
                values[2].setValue( counterAB.getCnt() );
                values[3].setValue( counterC.getCnt() );
                update();
            }
        }
    }


}
