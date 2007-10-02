package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.engine.Calculator;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.awt.geom.Line2D;

public class LennardJonesWallForceCalculator implements Calculator {
    private final LennardJonesForce ljf;
    private final Line2D.Double wall;
    private final double[] args = new double[2];

    public LennardJonesWallForceCalculator(LennardJonesForce ljf, Line2D.Double wall) {
        this.ljf  = ljf;
        this.wall = wall;
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        Vector2D lineToPoint = MathUtil.getClockwiseVectorFromLineToPoint(wall, p.getPosition());

        args[0] = -lineToPoint.getX();
        args[1] = -lineToPoint.getY();

        ljf.evaluateInPlace(args, forces);
    }
}
