// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.module.RealModeList;

/**
 * Shows the mass of a Body in terms of space station masses.
 *
 * @author Sam Reid
 */
public class SpaceStationMassReadoutNode extends MassReadoutNode {

    public SpaceStationMassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        super( bodyNode, visible );
    }

    @Override
    protected String createText() {
        double massKG = bodyNode.getBody().getMass();
        double spaceStationMasses = massKG / RealModeList.SPACE_STATION_MASS;

        //Show the readout in terms of space station masses (or billions of billions of space station masses)
        String value, units;
        if ( spaceStationMasses > 1E18 ) {
            value = new DecimalFormat( "0" ).format( spaceStationMasses / 1E18 );
            units = GAOStrings.BILLION_BILLION_SPACE_STATION_MASSES;
        }
        else if ( Math.abs( spaceStationMasses - 1 ) < 1E-2 ) {
            value = "1";
            units = GAOStrings.SPACE_STATION_MASS;
        }
        else if ( spaceStationMasses < 1 ) {
            value = new DecimalFormat( "0.000" ).format( spaceStationMasses );
            units = GAOStrings.SPACE_STATION_MASSES;
        }
        else {
            value = new DecimalFormat( "0.00" ).format( spaceStationMasses );
            units = GAOStrings.SPACE_STATION_MASSES;
        }
        return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
    }
}
