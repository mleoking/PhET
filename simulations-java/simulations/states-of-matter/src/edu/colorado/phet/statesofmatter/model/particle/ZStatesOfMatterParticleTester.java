package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.TestingUtils;
import junit.framework.TestCase;

public class ZStatesOfMatterParticleTester extends TestCase {
    private static final StatesOfMatterParticle P              = new StatesOfMatterParticle(1, 2, 3);
    private static final StatesOfMatterParticle EQUAL_TO_P     = new StatesOfMatterParticle(1, 2, 3);
    private static final StatesOfMatterParticle NOT_EQUAL_TO_P = new StatesOfMatterParticle(1, 2, 4);

    public void setUp() {
    }

    public void testEquality() {
        TestingUtils.testEquality(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }

    public void testHashCode() {
        TestingUtils.testHashCode(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }

    public void testToString() {
        TestingUtils.testToString(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }
}
