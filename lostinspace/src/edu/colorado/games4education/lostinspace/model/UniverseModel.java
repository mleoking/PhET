/**
 * Class: UniverseModel
 * Class: edu.colorado.games4education.lostinspace.model
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:05:19 AM
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;

public class UniverseModel extends BaseModel {

    private StarField starField;

    public UniverseModel( StarField starField, AbstractClock clock ) {
        super( clock );
        this.starField = starField;
    }

    public StarField getStarField() {
        return starField;
    }

}
