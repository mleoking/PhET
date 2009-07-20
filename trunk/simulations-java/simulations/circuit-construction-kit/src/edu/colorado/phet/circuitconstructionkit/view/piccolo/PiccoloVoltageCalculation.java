package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.Connection;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Oct 7, 2004
 * Time: 4:57:32 PM
 */
public class PiccoloVoltageCalculation {
    private Circuit circuit;

    public PiccoloVoltageCalculation(Circuit circuit) {
        this.circuit = circuit;
    }

    public double getVoltage(Shape leftTip, Shape rightTip) {
        Area tipIntersection = new Area(leftTip);
        tipIntersection.intersect(new Area(rightTip));
        if (!tipIntersection.isEmpty()) {
            return 0;
        } else {
            Connection red = circuit.getConnection(leftTip);
            Connection black = circuit.getConnection(rightTip);

            if (red == null || black == null) {
                return Double.NaN;
            } else {
                return circuit.getVoltage(red, black);//dfs from one branch to the other, counting the voltage drop.
            }
        }
    }

}
