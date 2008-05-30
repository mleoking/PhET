package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.engine.Calculator;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class LennardJonesWallForceCalculator implements Calculator {
    private final LennardJonesForce ljf;
    private final Line2D.Double wall;
    private final double[] args = new double[2];

    public LennardJonesWallForceCalculator(LennardJonesForce ljf, Line2D.Double wall) {
        this.ljf  = ljf;
        this.wall = wall;
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        Vector2D lineToPoint = MathUtil.getVectorFromLineToPoint(wall, p.getPosition());

        double dist = lineToPoint.getMagnitude();

        args[0] = lineToPoint.getX();
        args[1] = lineToPoint.getY();

        Point2D point1 = wall.getP1();
        Point2D point2 = wall.getP2();

        double lx = point2.getX() - point1.getX();
        double ly = point2.getY() - point1.getY();

        double crossProductZ = -ly * lineToPoint.getX() + lx * lineToPoint.getY();

        if (crossProductZ < 0) {
            // Behind wall:
            args[0] = -args[0] / dist * ljf.getRMin() * 0.01;
            args[1] = -args[1] / dist * ljf.getRMin() * 0.01;
        }
        else if (dist > ljf.getRMin() ) {
            forces[0] = 0.0;
            forces[1] = 0.0;

            return;
        }

        ljf.evaluateInPlace(args, forces);
    }
}
