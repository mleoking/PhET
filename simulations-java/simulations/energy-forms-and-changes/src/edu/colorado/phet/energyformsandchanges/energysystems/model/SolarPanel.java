// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double CONVERSION_EFFICIENCY = 0.3;

    private static final Vector2D SOLAR_PANEL_OFFSET = new Vector2D( 0, 0.044 );
    private static final ModelElementImage SOLAR_PANEL_IMAGE = new ModelElementImage( SOLAR_PANEL, SOLAR_PANEL_OFFSET );
    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( SOLAR_PANEL_BASE, new Vector2D( 0.015, -0.025 ) ) );
        add( SOLAR_PANEL_IMAGE );
        add( new ModelElementImage( WIRE_BLACK_MIDDLE, new Vector2D( 0.075, -0.04 ) ) );
        add( new ModelElementImage( CONNECTOR, new Vector2D( 0.058, -0.04 ) ) );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // State property for tracking whether sunlight can be absorbed.  Only
    // true when the solar panel is active.
    private BooleanProperty ableToAbsorbSunlight = new BooleanProperty( false );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected SolarPanel() {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON, IMAGE_LIST );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {
        double energyProduced = 0;
        if ( isActive() && incomingEnergy.type == EnergyType.SOLAR ) {
            energyProduced = incomingEnergy.amount * CONVERSION_EFFICIENCY;
        }
        return new Energy( EnergyType.ELECTRICAL, energyProduced, 0 );
    }

    @Override public void activate() {
        super.activate();
        ableToAbsorbSunlight.set( true );
    }

    @Override public void deactivate() {
        super.deactivate();
        ableToAbsorbSunlight.set( false );
    }

    public ObservableProperty<Boolean> getAbleToAbsorbSunlight() {
        return ableToAbsorbSunlight;
    }

    /**
     * Get the shape of the region that absorbs sunlight.
     *
     * @return A shape, in model space, of the region of the solar panel that
     *         can absorb sunlight.
     */
    public Shape getAbsorptionShape() {
        Rectangle2D panelBounds = new Rectangle2D.Double( -SOLAR_PANEL_IMAGE.getWidth() / 2,
                                                          -SOLAR_PANEL_IMAGE.getHeight() / 2,
                                                          SOLAR_PANEL_IMAGE.getWidth(),
                                                          SOLAR_PANEL_IMAGE.getHeight() );
        AffineTransform transform = AffineTransform.getTranslateInstance( getPosition().getX() + SOLAR_PANEL_OFFSET.getX(),
                                                                          getPosition().getY() + SOLAR_PANEL_OFFSET.getX() );
        return transform.createTransformedShape( panelBounds );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSolarPanelButton;
    }
}
