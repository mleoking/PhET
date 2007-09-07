package edu.colorado.phet.statesofmatter.model.container;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import junit.framework.TestCase;

public class ZRectangularParticleContainerTester extends TestCase {
    private volatile AbstractParticleContainer container;

    public void setUp() {
        container = new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS);
    }

    public void testThatShapeIsRectangular() {
        assertEquals(StatesOfMatterConfig.CONTAINER_BOUNDS, container.getShape());
    }
}
