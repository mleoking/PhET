/*, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor_semi.common.TransformGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:48:13 AM
 */
public class ElectricFieldSectionGraphic extends TransformGraphic {
    private ElectricFieldGraphic bfieldG;
    private ElectricFieldGraphic ifieldG;

    public ElectricFieldSectionGraphic( ElectricFieldSection field, ModelViewTransform2D transform ) {
        super( transform );

        bfieldG = new ElectricFieldGraphic( SimStrings.get( "ElectricFieldSectionGraphic.BatteryForceLabel" ), field.getBatteryField(), transform );
        ifieldG = new ElectricFieldGraphic( SimStrings.get( "ElectricFieldSectionGraphic.InternalForceLabel" ), field.getInternalField(), transform );
    }

    public void paint( Graphics2D g ) {
        bfieldG.paint( g );
        ifieldG.paint( g );
    }

    public void update() {
    }
}
