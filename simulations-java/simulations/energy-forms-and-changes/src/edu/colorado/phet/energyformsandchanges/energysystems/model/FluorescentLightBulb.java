// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * @author John Blanco
 */
public class FluorescentLightBulb extends EnergyUser {

    public static final ModelElementImage BULB_BASE = new ModelElementImage( ELEMENT_BASE,
                                                                             ELEMENT_BASE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                             new Vector2D( 0, -0.022 ) );
    public static final ModelElementImage NON_ENERGIZED_BULB = new ModelElementImage( FLUORESCENT,
                                                                                      FLUORESCENT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                      new Vector2D( 0, 0.02 ) );
    public static final ModelElementImage ENERGIZED_BULB = new ModelElementImage( FLUORESCENT_ON,
                                                                                  FLUORESCENT_ON.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                  new Vector2D( 0, 0.02 ) );

    private static final double ENERGY_TO_FULLY_LIGHT = 20; // In joules/sec, a.k.a. watts.

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( BULB_BASE );
        add( NON_ENERGIZED_BULB );
        add( ENERGIZED_BULB );
    }};

    public final Property<Double> litProportion = new Property<Double>( 0.0 );

    protected FluorescentLightBulb() {
        super( EnergyFormsAndChangesResources.Images.FLUORESCENT_ICON, IMAGE_LIST );
    }

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {
        if ( incomingEnergy.type == Energy.Type.ELECTRICAL ) {
            litProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / ENERGY_TO_FULLY_LIGHT, 1 ) );
            System.out.println( "litProportion = " + litProportion.get() );
        }
        else {
            litProportion.set( 0.0 );
        }
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectFluorescentLightBulbButton;
    }
}
