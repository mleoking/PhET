package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Designed to create particles in a packed, hexagonal arrangement.
 */
public class PackedHexagonalParticleCreationStrategy extends AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    private static final double SQRT_3 = Math.sqrt(3.0);

    private final Shape shape;
    private double startXEven;
    private double endY;
    private double endX;
    private double deltaY;
    private double deltaX;
    private double y;
    private double x;
    private double radius;
    private double mass;
    private double startXOdd;
    private int row;

    public PackedHexagonalParticleCreationStrategy(Shape shape, double mass, double radius, double cushion, double distFromBottom) {
        this.shape  = shape;
        this.radius = radius;
        this.mass   = mass;
        this.row    = 0;

        Rectangle2D bounds = shape.getBounds2D();

        double distBetween = 2 * radius + cushion;

        double startY;

        startY     = bounds.getMaxY() - distFromBottom;
        startXEven = bounds.getMinX() + radius;
        startXOdd  = startXEven - distBetween / 2.0;

        endY = bounds.getMinY() + radius;
        endX = bounds.getMaxX() - radius;

        deltaY = distBetween * SQRT_3 / 2.0;
        deltaX = distBetween;

        y = startY;
        x = startXEven;
    }

    public StatesOfMatterAtom createParticle() {
        while (true) {
            if (y < endY) {
                return null;
            }

            if (x > endX) {
                y -= deltaY;

                if (++row % 2 == 0) {
                    x = startXEven;
                }
                else {
                    x = startXOdd;
                }
            }

            try {
                Rectangle2D.Double bounds = new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);

                if (shape.contains(bounds)) {
                    return new StatesOfMatterAtom(x, y, radius, mass);
                }
            }
            finally {
                x += deltaX;
            }
        }
    }
}
