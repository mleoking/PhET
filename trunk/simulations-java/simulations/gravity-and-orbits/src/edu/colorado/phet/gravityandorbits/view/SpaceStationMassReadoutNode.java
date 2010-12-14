package edu.colorado.phet.gravityandorbits.view;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class SpaceStationMassReadoutNode extends MassReadoutNode {

    public SpaceStationMassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        super( bodyNode, visible );
    }

    @Override
    String createText() {
        double massKG = bodyNode.getBody().getMass();
        double spaceStationMasses = massKG / GravityAndOrbitsModule.SPACE_STATION_MASS;
        String text;
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        if ( spaceStationMasses > 1E18 ) {
            text = new DecimalFormat( "0" ).format( spaceStationMasses / 1E18 ) + " billion billion space station masses";
        }
        else if ( Math.abs( spaceStationMasses - 1 ) < 1E-2 ) {
            text = 1 + " space station mass";
        }
        else if ( spaceStationMasses < 1 ) {
            text = new DecimalFormat( "0.000" ).format( spaceStationMasses ) + " space station masses";
        }
        else {
            text = decimalFormat.format( spaceStationMasses ) + " space station masses";
        }
        return text;
    }
}
