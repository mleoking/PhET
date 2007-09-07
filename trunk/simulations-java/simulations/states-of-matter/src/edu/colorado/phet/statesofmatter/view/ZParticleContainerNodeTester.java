package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import junit.framework.TestCase;

public class ZParticleContainerNodeTester extends TestCase {
    private volatile ParticleContainerNode node;

    public void setUp() {
        node = new ParticleContainerNode(new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS));
    }

    public void testThatBoundsAreNotEmpty() {
        PiccoloTestingUtils.testBoundsAreNonZero(node);
    }
}
