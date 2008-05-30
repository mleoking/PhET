package edu.colorado.phet.statesofmatter.model.container;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

public class ZRectangularParticleContainerTester extends TestCase {
    private volatile ParticleContainer container;

    public void setUp() {
        container = new RectangularParticleContainer(StatesOfMatterConstants.CONTAINER_BOUNDS);
    }

    public void testThatShapeIsRectangular() {
        assertEquals(StatesOfMatterConstants.CONTAINER_BOUNDS, container.getShape());
    }

    public void testThatNorthWallIsNorthOfSouthWall() {
        assertTrue(north().getBounds2D().getCenterY() < south().getBounds2D().getCenterY());
    }

    public void testThatWestWallIsWestOfEastWall() {
        assertTrue(west().getBounds2D().getCenterX() < east().getBounds2D().getCenterX());
    }

    public void testThatShapeBoundsContainsNorthWallBounds() {
        PiccoloTestingUtils.contains(bounds(), north().getBounds());
    }

    public void testThatShapeBoundsContainsSouthWallBounds() {
        PiccoloTestingUtils.contains(bounds(), south().getBounds());
    }

    public void testThatShapeBoundsContainsWestWallBounds() {
        PiccoloTestingUtils.contains(bounds(), north().getBounds());
    }

    public void testThatShapeBoundsContainsEastWallBounds() {
        PiccoloTestingUtils.contains(bounds(), south().getBounds());
    }

    public void testThatNorthWallEnclosesShapeClockwise() {
        assertTrue(north().getX2() > north().getX1());
    }

    public void testThatEastWallEnclosesShapeClockwise() {
        assertTrue(east().getY2() > east().getY1());
    }

    public void testThatSouthWallEnclosesShapeClockwise() {
        assertTrue(south().getX1() > south().getX2());
    }

    public void testThatWestWallEnclosesShapeClockwise() {
        assertTrue(west().getY1() > west().getY2());
    }

    public void testThatGetWallsReturnsAllFourWalls() {
        Collection allWalls = container.getAllWalls();

        assertEquals(4, allWalls.size());

        assertTrue(allWalls.contains(north()));
        assertTrue(allWalls.contains(south()));
        assertTrue(allWalls.contains(east()));
        assertTrue(allWalls.contains(west()));
    }

    public void testIdentityOfNorthWallDoesNotChange() {
        assertSame(north(), north());
    }

    public void testIdentityOfSouthWallDoesNotChange() {
        assertSame(south(), south());
    }

    public void testIdentityOfWestWallDoesNotChange() {
        assertSame(west(), west());
    }

    public void testIdentityOfEastWallDoesNotChange() {
        assertSame(east(), east());
    }

    public void testCanGetCenterOfRotation() {
        assertNotNull(container.getCenterOfRotation());
    }

    public void testInitialCenterOfRotationIsBottomMidPoint() {
        assertEquals(new Point2D.Double(container.getSouthWall().getBounds().getCenterX(), container.getSouthWall().getY1()), container.getCenterOfRotation());
    }

    public void testCanSetCenterOfRotation() {
        Point2D.Double r = new Point2D.Double(Math.random(), Math.random());

        container.setCenterOfRotation(r);

        assertEquals(r, container.getCenterOfRotation());
    }

    public void testCanGetRotation() {
        container.setRotation(Math.PI / 2);

        assertEquals(Math.PI / 2, container.getRotation(), 1.0E-10);
    }

    public void testCanRotateContainer() {
        container.setRotation(-Math.PI / 2);

        assertTrue(boundsOf(north()).getCenterX() < boundsOf(south()).getCenterX());
        assertTrue(boundsOf(east()).getCenterY()  < boundsOf(west()).getCenterY());
    }

    public void testThatRotationIsNotCommulative() {
        container.setRotation(-Math.PI / 2);

        testCanRotateContainer();
    }

    private Rectangle2D bounds() {
        return (Rectangle2D)shape().getBounds2D().clone();
    }

    private Shape shape() {
        return container.getShape();
    }

    private ParticleContainerWall north() {
        return container.getNorthWall();
    }

    private ParticleContainerWall south() {
        return container.getSouthWall();
    }

    private ParticleContainerWall east() {
        return container.getEastWall();
    }

    private ParticleContainerWall west() {
        return container.getWestWall();
    }

    private Rectangle2D boundsOf(ParticleContainerWall wall) {
        return wall.getBounds2D();
    }
}
