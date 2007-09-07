package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.util.PBounds;
import junit.framework.TestCase;

public class ZParticleNodeTester extends TestCase {
    private ParticleNode node;

    public ZParticleNodeTester() {
    }

    protected void setUp() throws Exception {
        node = new ParticleNode(StatesOfMatterParticle.TEST);
    }

    public void testParticleNodeBoundsExists(){
        PiccoloTestingUtils.testBoundsAreNonZero(node);
    }

    public void testParticleNodeBoundsAreCentered() {
        PBounds bounds = node.getFullBounds();

        assertEquals(0.0, bounds.getCenterX(), 0.000001);
        assertEquals(0.0, bounds.getCenterY(), 0.000001);
    }

    public void testParticleNodeSizeReflectsParticleRadius() {
        PBounds bounds = node.getFullBounds();
        
        assertEquals(StatesOfMatterParticle.TEST.getRadius() * 2, bounds.getWidth(),  0.000001);
        assertEquals(StatesOfMatterParticle.TEST.getRadius() * 2, bounds.getHeight(), 0.000001);
    }
}
