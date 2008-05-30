package edu.colorado.phet.statesofmatter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;
import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;

public class PiccoloTestingUtils {
    private static final double SMALL_SIZE  = 1.0 / 3.0;
    private static final double MEDIUM_SIZE = SMALL_SIZE * 2;
    private static final double LARGE_SIZE  = SMALL_SIZE * 3;
    private static final int TEST_TIMEOUT = 2000;

    public static boolean contains(Rectangle2D container,Rectangle2D child){
        return child.isEmpty()?container.contains(child.getX(),child.getY()) :container.contains(child);
    }

    public static boolean intersects(Rectangle2D r1,Rectangle2D r2){
        if (r2.isEmpty()) {
            return r1.contains(r2.getX(), r2.getY());
        }
        else if (r1.isEmpty()) {
            return r2.contains(r1.getX(), r1.getY());
        }
        else {
            return r1.intersects(r2);
        }
    }

    public static void testBoundsAreFullyVisible(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        PBounds nodeBounds = node.getGlobalFullBounds();

        TestCase.assertTrue("The bounds of the node " + node.getClass() + ": "+nodeBounds+" do not fully fall within the visible portion of the canvas.", contains(visibleBounds, nodeBounds));
    }

    public static void testBoundsAreVisible(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        PBounds nodeBounds = node.getGlobalFullBounds();

        boolean visibleBoundsIntersectsNodeBounds = intersects(visibleBounds, nodeBounds);

        TestCase.assertTrue("The bounds of the node " + node + " do not fall within the visible portion of the canvas.", visibleBoundsIntersectsNodeBounds);
    }

    public static void testBoundsAreNonZero(PNode node) {
        TestCase.assertFalse("The bounds of the node " + node + " are zero.", node.getGlobalFullBounds().isEmpty());
    }

    public static void testIsRoughlyCentered(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        double maxDistance = 1.0 / 5.0 * Math.max(visibleBounds.getWidth(), visibleBounds.getHeight());

        PBounds nodeBounds = node.getGlobalFullBounds();

        Point2D canvasCenter = visibleBounds.getCenter2D();
        Point2D nodeCenter = nodeBounds.getCenter2D();

        double distance = canvasCenter.distance(nodeCenter);

        TestCase.assertTrue("The "+ node.getClass() + " node with global full bounds " + nodeBounds + " does not lie near the center of the visible portion of the canvas.", distance < maxDistance);
    }

    public static void testIsSmallSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, 0, SMALL_SIZE);
    }

    public static void testIsMediumSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, SMALL_SIZE, MEDIUM_SIZE);
    }

    public static void testIsLargeSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, MEDIUM_SIZE, LARGE_SIZE);
    }

    public static boolean isSmallerThan(PNode node, PCanvas canvas, double maxSizeRatio) {
        double limitingSize = Math.min(canvas.getWidth(), canvas.getHeight());
        double nodeDimension = limitingSize == canvas.getWidth() ? node.getGlobalFullBounds().getWidth() : node.getGlobalFullBounds().getHeight();
        double fraction = nodeDimension / limitingSize;
        return fraction < maxSizeRatio;
    }

    /**
     * Tests that the specified node has non-zero bounds and is visible
     * somewhere on the Piccolo canvas. Does not test for obscuring.
     *
     * @param node      The node to test.
     *
     * @param canvas    The canvas.
     */
    public static void testIsVisible(PNode node, PCanvas canvas) {
        PNode originalNode = node;

        if (node.getGlobalFullBounds().isEmpty()) {
            TestCase.fail("The node " + originalNode.getClass() + " is not visible because it has zero global full bounds.");
        }

        testBoundsAreVisible(node, canvas);

        while (node != null) {
            if (node == canvas.getLayer()) {
                return;
            }

            node = node.getParent();
        }

        TestCase.fail("The node " + originalNode.getClass() + " is not visible on the canvas " + canvas + ".");
    }

    /**
     * Tests that the view object automatically synchronizes with the model
     * object. This method assumes the model object contains getX()/getY()/
     * setX()/setY() methods.
     *
     * @param modelObject   The model object.
     *
     * @param viewObject    The view object.
     */
    public static void testViewAutomaticallySyncsWithModel(Object modelObject, PNode viewObject) {
        testViewSyncsWithModel(modelObject, viewObject, false);
    }

    /**
     * Tests that the view object synchronizes with the model object whenever
     * the view object's Updatable.update() method is invoked. This method
     * assumes the model object contains getX()/getY()/setX()/setY() methods.
     *
     * @param modelObject   The model object.
     *
     * @param viewObject    The view object, which must implement Updatable.
     */
    public static void testViewManuallySyncsWithModel(Object modelObject, PNode viewObject) {
        testViewSyncsWithModel(modelObject, viewObject, true);
    }

    private static double getDouble(Method getter, Object object) throws Exception {
        Double d = (Double)getter.invoke(object, new Object[0]);

        return d.doubleValue();
    }

    private static void setDouble(Method setter, Object object, double value) throws Exception {
        setter.invoke(object, new Object[]{new Double(value)});
    }

    private static void assertSizeIsCorrect(PNode node, PCanvas canvas, double lowerBound, double upperBound) {
        TestCase.assertTrue("node global full bounds=" + node.getGlobalFullBounds() + " canvas bounds=" + getVisibleBounds(canvas) + ", lower bound=" + lowerBound + ", upperBound=" + upperBound,
                            !isSmallerThan(node, canvas, lowerBound)
                            && isSmallerThan(node, canvas, upperBound));
    }

    private static PBounds getVisibleBounds(final PCanvas canvas) {
        //Must interact with Swing in the Swing Thread to avoid race conditions, even for getters.
        //see: http://java.sun.com/products/jfc/tsc/articles/threads/threads3.html
        final PBounds[] value = new PBounds[]{null};
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    value[0] = new PBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                }
            });
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return value[0];
    }

    private static void verifyCameraTransformIsIdentity(PCanvas canvas) {
        if (!canvas.getCamera().getTransform().equals(new PAffineTransform())) {
            throw new IllegalArgumentException("Cannot deal with camera transform: " + canvas.getCamera().getTransform());
        }
    }

    private static void testViewSyncsWithModel(Object modelObject, PNode viewObject, boolean forceUpdate) {
        try {
            Method setXMethod = modelObject.getClass().getMethod("setX", new Class[]{double.class});
            Method setYMethod = modelObject.getClass().getMethod("setY", new Class[]{double.class});

            Method getXMethod = modelObject.getClass().getMethod("getX", new Class[0]);
            Method getYMethod = modelObject.getClass().getMethod("getY", new Class[0]);

            // Test synchronization in all 4 directions, on each axis:
            for (double deltaX = -1.0; deltaX <= 1.0; deltaX += 2.0) {
                for (double deltaY = -1.0; deltaY <= 1.0; deltaY += 2.0) {
                    // Find out where the model is starting from:
                    double startModelX = getDouble(getXMethod, modelObject);
                    double startModelY = getDouble(getYMethod, modelObject);

                    double startNodeX = viewObject.getFullBounds().getCenterX();
                    double startNodeY = viewObject.getFullBounds().getCenterY();

                    // Set the model element to the new position:
                    setDouble(setXMethod, modelObject, startModelX + deltaX);
                    setDouble(setYMethod, modelObject, startModelY + deltaY);

                    // Wait for the view to update (or the timeout to elapse):
                    long startTime = System.currentTimeMillis();

                    if (forceUpdate) {
                        if (viewObject instanceof Updatable) {
                            ((Updatable)viewObject).update();
                        }
                        else {
                            TestCase.fail("The view object " + viewObject + " does not implement Updatable, and therefore cannot be forced to update.");
                        }
                    }

                    while (viewObject.getFullBounds().getCenterX() == startNodeX &&
                           viewObject.getFullBounds().getCenterY() == startNodeY) {
                        Thread.yield();

                        if ((System.currentTimeMillis() - startTime) > TEST_TIMEOUT) {
                            TestCase.fail("The view object " + viewObject + " did not sync with the model object " + modelObject + " within the timeout period.");
                        }
                    }

                    // See if the view's position actually changed in the same
                    // direction as the model:
                    double endNodeX = viewObject.getFullBounds().getCenterX();
                    double endNodeY = viewObject.getFullBounds().getCenterY();

                    double signX = (endNodeX - startNodeX) > 0 ? 1.0 : -1.0;
                    double signY = (endNodeY - startNodeY) > 0 ? 1.0 : -1.0;

                    TestCase.assertEquals("The view object " + viewObject + " did not sync with the model object " + modelObject + " for horizontal adjustment.", deltaX, signX, 0.0);
                    TestCase.assertEquals("The view object " + viewObject + " did not sync with the model object " + modelObject + " for vertical adjustment.",   deltaY, signY, 0.0);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("The model object " + modelObject + " must have public getX()/getY() and setX()/setY() methods returning/accepting double values.", e);
        }
    }
}
