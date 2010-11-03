package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class ModelState {
    ArrayList<VelocityVerlet.BodyState> bodyStates;
    ArrayList<VelocityVerlet.PotentialField> fields;

    public ModelState( ArrayList<VelocityVerlet.BodyState> bodyStates, ArrayList<VelocityVerlet.PotentialField> fields ) {
        this.bodyStates = bodyStates;
        this.fields = fields;
    }
}
