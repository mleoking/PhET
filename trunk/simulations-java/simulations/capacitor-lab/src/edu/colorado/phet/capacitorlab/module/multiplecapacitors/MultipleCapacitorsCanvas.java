// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Canvas for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCanvas extends CLCanvas {

    private final MultipleCapacitorsModel model;
    private final CLModelViewTransform3D mvt;

    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode plateChargeMeterNode;
    private final StoredEnergyMeterNode storedEnergyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;

    public MultipleCapacitorsCanvas( MultipleCapacitorsModel model, CLModelViewTransform3D mvt, CLGlobalProperties globalProperties ) {

        this.model = model;
        this.mvt = mvt;

        // maximums
        final double eFieldVectorReferenceMagnitude = 1; //XXX

        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldVectorReferenceMagnitude, globalProperties.dev );

        // rendering order
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetector.getBodyNode() );
        addChild( eFieldDetector.getWireNode() );
        addChild( eFieldDetector.getProbeNode() );
        addChild( voltmeter.getBodyNode() );
        addChild( voltmeter.getPositiveProbeNode() );
        addChild( voltmeter.getPositiveWireNode() );
        addChild( voltmeter.getNegativeProbeNode() );
        addChild( voltmeter.getNegativeWireNode() );
    }

    public void reset() {
        //TODO anything to reset?
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // adjust the model bounds
        Point3D p = mvt.viewToModelDelta( worldSize.getWidth(), worldSize.getHeight() );
        model.getWorldBounds().setBounds( 0, 0, p.getX(), p.getY() );
    }
}
