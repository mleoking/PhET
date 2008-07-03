package edu.colorado.phet.statesofmatter.view;

import junit.framework.TestCase;
import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.util.PBounds;

public class ZParticleNodeTester extends TestCase {
    private volatile ParticleNode node;
    private volatile StatesOfMatterAtom modelObject;

    public ZParticleNodeTester() {
    }

    protected void setUp() throws Exception {
        this.modelObject = new StatesOfMatterAtom(0.0, 0.0, 1.0, 1.0);
        this.node        = new ParticleNode(modelObject, new ModelViewTransform());
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

        assertEquals(modelObject.getRadius() * 2, bounds.getWidth(),  0.000001);
        assertEquals(modelObject.getRadius() * 2, bounds.getHeight(), 0.000001);
    }

    public void testParticleNodeImplementsUpdatable() {
        assertTrue(Updatable.class.isAssignableFrom(node.getClass()));
    }

    public void testParticleNodesLocationIsSameAsModel() {
        modelObject = new StatesOfMatterAtom(3.0, 2.0, 1.0, 1.0);
        node        = new ParticleNode(modelObject, new ModelViewTransform());

        assertEquals(modelObject.getX(), node.getFullBounds().getCenterX(), 0);
        assertEquals(modelObject.getY(), node.getFullBounds().getCenterY(), 0);
    }

    public void testParticleNodeUpdateSynchronizesViewWithModel() {
        PiccoloTestingUtils.testViewManuallySyncsWithModel(modelObject, node);
    }
}
