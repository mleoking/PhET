package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.StatesOfMatterParticle;
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
}
