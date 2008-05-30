package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import edu.colorado.phet.statesofmatter.model.engine.gravity.GravityForceCalculator;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesForce;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesForceCalculator;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesWallForceCalculator;

public class ZEngineForceCalculatorTester extends TestCase {

    private volatile EngineForceCalculator c;
    private Collection allWalls;

    public void setUp() {
        Rectangle2D.Double shape = new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0);

        RectangularParticleContainer container = new RectangularParticleContainer(shape);
        
        allWalls = container.getAllWalls();
        c = new EngineForceCalculator(1.0, LennardJonesForce.TEST, new ArrayList(), allWalls);
    }

    public void testThatForceCalculatorIsComposite() {
        assertTrue(c instanceof CompositeCalculator);
    }

    public void testThatEngineForceCalculatorHasCalculatorForEachWall() {
        assertEquals(4, count(LennardJonesWallForceCalculator.class));
    }

    public void testThatEngineForceCalculatorHasGravityCalculator() {
        assertEquals(1, count(GravityForceCalculator.class));
    }

    public void testThatEngineForceCalculatorHasLJFCalculator() {
        assertEquals(1, count(LennardJonesForceCalculator.class));
    }

    private int count(Class theClass) {
        int count = 0;

        for (Iterator iterator = c.getCalculators().iterator(); iterator.hasNext();) {
            Calculator calculator = (Calculator)iterator.next();

            if (theClass.isAssignableFrom(calculator.getClass())) {
                count++;
            }
        }

        return count;
    }
}
