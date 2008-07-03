package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

public class ZPackedHexagonalParticleCreationStrategyTester extends ZNonOverlappingParticleCreationStrategyTester {
    private static final Rectangle2D.Double B = StatesOfMatterConstants.CONTAINER_BOUNDS;
    private static final Shape ICE_CUBE = new Rectangle2D.Double(B.getX() + 1, B.getY() + 1, B.getWidth() - 1, B.getHeight() - 1);
    private static final double DIST_FROM_BOTTOM = 1.1 * PARTICLE_RADIUS;
    private static final double MARGIN = PARTICLE_RADIUS * 4.0;

    public void setUp() {
        particles = new ArrayList();

        createNewStrategy();

        strategy.createParticles(particles, NUM_PARTICLES_TO_TEST * 400);
    }

    private void createNewStrategy() {
        cushion = 0.01;

        strategy = new PackedHexagonalParticleCreationStrategy(ICE_CUBE, PARTICLE_MASS, PARTICLE_RADIUS, cushion, DIST_FROM_BOTTOM);
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
                StatesOfMatterAtom p1, p2;

                p1 = (StatesOfMatterAtom)o1;
                p2 = (StatesOfMatterAtom)o2;

                return (int)Math.round(1000 * dist(p1.getX(), p1.getY(), x, y)) -
                       (int)Math.round(1000 * dist(p2.getX(), p2.getY(), x, y));
            }
        });

        return list;
    }

    public void testPackingIsHexagonal() {
        Rectangle2D b = ICE_CUBE.getBounds2D();

        for (int i = 0; i < 100; i++) {
            double x = Math.random() * (b.getWidth()  - 2 * MARGIN) + b.getMinX() + MARGIN;
            double y = Math.random() * (b.getHeight() - 2 * MARGIN) + b.getMinY() + MARGIN;

            StatesOfMatterAtom closest = (StatesOfMatterAtom)getClosest(x, y).iterator().next();

            List list = getClosest(closest.getX(), closest.getY());

            assertSame(closest, list.get(0));

            list.remove(0);

            StatesOfMatterAtom p = (StatesOfMatterAtom)list.get(0);

            double dist = dist(closest.getX(), closest.getY(), p.getX(), p.getY());

            for (int j = 1; j < 6; j++) {
                p = (StatesOfMatterAtom)list.get(j);

                double curDist = dist(closest.getX(), closest.getY(), p.getX(), p.getY());

                assertEquals("Particle " + p + " is one of the six closest to particle " + closest + ", but is not the same distance away from it as at least one of the others.", dist, curDist, 0.000001);
            }
        }
    }

    public void testPackingFollowsDistanceConstraint() {
        Rectangle2D b = ICE_CUBE.getBounds2D();

        double distBetweenParticles = 2.0 * PARTICLE_RADIUS + cushion;

        for (int i = 0; i < 100; i++) {
            double x = Math.random() * (b.getWidth()  - 2 * MARGIN) + b.getMinX() + MARGIN;
            double y = Math.random() * (b.getHeight() - 2 * MARGIN) + b.getMinY() + MARGIN;

            StatesOfMatterAtom closest = (StatesOfMatterAtom)getClosest(x, y).iterator().next();

            List list = getClosest(closest.getX(), closest.getY());

            list.remove(0);

            for (int j = 1; j < 6; j++) {
                StatesOfMatterAtom p = (StatesOfMatterAtom)list.get(j);

                double curDist = dist(closest.getX(), closest.getY(), p.getX(), p.getY());

                assertEquals("Particle is wrong distance away from closest neighbor", distBetweenParticles, curDist, 0.000001);
            }
        }
    }

    public void testIsSpecifiedDistanceAwayFromBottom() {
        double maxY = ICE_CUBE.getBounds2D().getMaxY();

        List list = getClosest(ICE_CUBE.getBounds2D().getMinX(), maxY);

        StatesOfMatterAtom p = (StatesOfMatterAtom)list.get(0);

        assertEquals(DIST_FROM_BOTTOM, maxY - p.getY(), 0.000001);
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
