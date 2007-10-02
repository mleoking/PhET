package edu.colorado.phet.statesofmatter.model.container;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import junit.framework.TestCase;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class ZRectangularParticleContainerTester extends TestCase {
    private volatile ParticleContainer container;

    public void setUp() {
        container = new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS);
    }

    public void testThatShapeIsRectangular() {
        assertEquals(StatesOfMatterConfig.CONTAINER_BOUNDS, container.getShape());
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
}
