package edu.colorado.phet.statesofmatter.view;

import junit.framework.TestCase;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

public class ZParticleContainerNodeTester extends TestCase {
    private volatile ParticleContainerNode node;

    public void setUp() throws Exception {
        node = new ParticleContainerNode(new PhetPCanvas(), new MultipleParticleModel(new ConstantDtClock(100, 10)));
    }

    public void testThatBoundsAreNotEmpty() {
        PiccoloTestingUtils.testBoundsAreNonZero(node);
    }

}
