// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.module.RealModeList;

import static edu.colorado.phet.gravityandorbits.GAOStrings.*;

/**
 * Provides a textual readout of a Body's mass in "earth masses"
 *
 * @author Sam Reid
 */
public class EarthMassReadoutNode extends MassReadoutNode {

    public EarthMassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        super( bodyNode, visible );
    }

    @Override
    protected String createText() {
        double massKG = bodyNode.getBody().getMass();
        double earthMasses = massKG / RealModeList.EARTH_MASS;

        //Show the value in terms of earth masses (or thousands of earth masses)
        String value, units;
        if ( earthMasses > 1E3 ) {
            value = new DecimalFormat( "0" ).format( Math.round( earthMasses / 1E3 ) );
            units = THOUSAND_EARTH_MASSES;
        }
        else if ( Math.abs( earthMasses - 1 ) < 1E-2 ) {
            value = "1";
            units = EARTH_MASS;
        }
        else if ( earthMasses < 1 ) {
            value = new DecimalFormat( "0.00" ).format( earthMasses );
            units = EARTH_MASSES;
        }
        else {
            //Handle showing exactly "1 earth mass" instead of "1 earth masses"
            value = new DecimalFormat( "0.00" ).format( earthMasses );
            units = ( earthMasses == 1.0 ) ? EARTH_MASS : EARTH_MASSES;
        }
        return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
    }
}
