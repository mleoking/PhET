/**
 * Class: UniverseModel
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:05:19 AM
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;

public class UniverseModel extends BaseModel {

    private StarField starField;
    private Starship starShip;

    public UniverseModel( StarField starField, AbstractClock clock ) {
        super( clock );
        this.starField = starField;
        this.starShip = new Starship();
    }

    public StarField getStarField() {
        return starField;
    }

    public Starship getStarShip() {
        return starShip;
    }

    public void setStarField( StarField starField ) {
        UniverseModel.this.starField = starField;
        // Using invokeLater() here prevents ConcurrentModification
        // exceptions
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                updateObservers();
                UniverseModel.this.starField.updateObservers();
            }
        } );
    }
}
