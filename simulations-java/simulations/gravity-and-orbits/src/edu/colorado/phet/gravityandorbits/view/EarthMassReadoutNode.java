package edu.colorado.phet.gravityandorbits.view;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class EarthMassReadoutNode extends MassReadoutNode {

    public EarthMassReadoutNode( final BodyNode bodyNode, final Property<Boolean> visible ) {
        super( bodyNode, visible );
    }

    @Override
    String createText() {
        double massKG = bodyNode.getBody().getMass();
        double earthMasses = massKG / GravityAndOrbitsModule.EARTH_MASS;
        String text;
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        if ( earthMasses > 1E3 ) {
            text = new DecimalFormat( "0" ).format( Math.round( earthMasses / 1E3 ) ) + " thousand Earth masses";
        }
        else if ( Math.abs( earthMasses - 1 ) < 1E-2 ) {
            text = 1 + " Earth mass";
        }
        else if ( earthMasses < 1 ) {
            text = new DecimalFormat( "0.00" ).format( earthMasses ) + " Earth masses";
        }
        else {
            text = decimalFormat.format( earthMasses ) + " Earth masses";
        }
        return text;
    }
}
