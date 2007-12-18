package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import junit.framework.TestCase;

public class ZParticleContainerNodeTester extends TestCase {
    private volatile ParticleContainerNode node;

    public void setUp() throws Exception {
        node = new ParticleContainerNode(new PhetPCanvas(), new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS));
    }

    public void testThatBoundsAreNotEmpty() {
        PiccoloTestingUtils.testBoundsAreNonZero(node);
    }

    public void testCanRetrieveAddedParticleNode() {
        node.addParticleNode(ParticleNode.TEST);

        assertSame(ParticleNode.TEST, node.getParticleNode(0));
    }

    public void testAddedParticleNodeIncreasesParticleNodeListSize() {
        assertEquals(0, node.getParticleNodes().size());

        node.addParticleNode(ParticleNode.TEST);

        assertEquals(1, node.getParticleNodes().size());
    }
}
