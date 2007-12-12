package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZPackedHexagonalParticleCreationStrategyTester extends ZNonOverlappingParticleCreationStrategyTester {
    private static final Rectangle2D.Double B = StatesOfMatterConfig.CONTAINER_BOUNDS;
    private static final Shape ICE_CUBE = new Rectangle2D.Double(B.getX() + 1, B.getY() + 1, B.getWidth() - 1, B.getHeight() - 1);

    public void setUp() {
        particles = new ArrayList();

        createNewStrategy();

        strategy.createParticles(particles, NUM_PARTICLES_TO_TEST * 400);
    }

    private void createNewStrategy() {
        cushion = 0.01;

        strategy = new PackedHexagonalParticleCreationStrategy(ICE_CUBE, PARTICLE_MASS, PARTICLE_RADIUS, cushion);
    }

    private double dist(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy);
    }

    private List getClosest(final double x, final double y) {
        List list = new ArrayList(particles);

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                StatesOfMatterParticle p1, p2;

                p1 = (StatesOfMatterParticle)o1;
                p2 = (StatesOfMatterParticle)o2;

                return (int)Math.round(1000 * dist(p1.getX(), p1.getY(), x, y)) -
                       (int)Math.round(1000 * dist(p2.getX(), p2.getY(), x, y));
            }
        });

        return list;
    }

    public void testPackingIsHexagonal() {
        double cushion = PARTICLE_RADIUS * 4.0;

        Rectangle2D b = ICE_CUBE.getBounds2D();

        for (int i = 0; i < 100; i++) {
            double x = Math.random() * (b.getWidth()  - 2 * cushion) + b.getMinX() + cushion;
            double y = Math.random() * (b.getHeight() - 2 * cushion) + b.getMinY() + cushion;

            StatesOfMatterParticle closest = (StatesOfMatterParticle)getClosest(x, y).iterator().next();

            List list = getClosest(closest.getX(), closest.getY());

            assertSame(closest, list.get(0));

            list.remove(0);

            StatesOfMatterParticle p = (StatesOfMatterParticle)list.get(0);

            double dist = dist(closest.getX(), closest.getY(), p.getX(), p.getY());

            for (int j = 1; j < 6; j++) {
                p = (StatesOfMatterParticle)list.get(j);

                double curDist = dist(closest.getX(), closest.getY(), p.getX(), p.getY());

                assertEquals("Particle " + p + " is one of the six closest to particle " + closest + ", but is not the same distance away from it as at least one of the others.", dist, curDist, 0.000001);
            }
        }
    }

    public void testCanCreateList() {
        createNewStrategy();

        super.testCanCreateList();
    }

    public void testListCreationAddsToExistingList() {
        createNewStrategy();

        super.testListCreationAddsToExistingList();
    }

    public void testListCreationReturnsAppropriateParticleCount() {
        createNewStrategy();
        
        super.testListCreationReturnsAppropriateParticleCount();
    }
}
