package edu.colorado.phet.common.piccolophet.nodes;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.app.beans.SVGIcon;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Represents an SVG image that is rendered into a buffered image as necessary.
 */
public class BufferedSVGNode extends PhetPNode {
    private static volatile int counter;

    private final String name;
    private final PhetPCanvas canvas;
    private final URI uri;

    private BufferedImage bufferedImage;


    static {
        if (System.getProperty("org.xml.sax.driver") == null) {
            System.setProperty("org.xml.sax.driver", "com.bluecast.xml.Piccolo");
        }
    }

    /**
     * Creates a new SVGNode with the specified width and height.
     *
     * @param canvas         The canvas, from which we get the transform
     *                       using to draw rescaled node.
     * @param svgInputStream The input stream for the SVG image.
     * @param width          The initial width of the image.
     * @param height         The initial height of the image.
     *
     * @throws java.io.IOException If an error occurs while loading the image.
     */
    public BufferedSVGNode(final PhetPCanvas canvas, final InputStream svgInputStream, double width, double height) throws IOException {
        super();

        this.canvas = canvas;
        this.name   = generateNextName();
        this.uri    = SVGCache.getSVGUniverse().loadSVG(svgInputStream, name);

        setWidth(width);
        setHeight(height);
    }

    /**
     * Creates a new SVGNode with width and height equal to 1.
     *
     * @param canvas         The canvas, from which we get the transform
     *                       using to draw rescaled node.
     * @param svgInputStream The input stream for the SVG image.
     *
     * @throws java.io.IOException If an error occurs while loading the image.
     */
    public BufferedSVGNode(final PhetPCanvas canvas, final InputStream svgInputStream) throws IOException {
        this(canvas, svgInputStream, 1.0, 1.0);
    }

    private void convertSVGToImage(int pixelWidth, int pixelHeight) throws IOException {
        SVGIcon icon = new SVGIcon();

        icon.setSvgURI(uri);
        icon.setScaleToFit(true);
        icon.setAntiAlias(true);
        icon.setPreferredSize(new Dimension(pixelWidth, pixelHeight));

        bufferedImage = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB );

        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
    }

    protected void paint(PPaintContext paintContext) {
        Rectangle2D bounds = getGlobalBounds();

        canvas.getPhetRootNode().globalToScreen(bounds);

        //account for the canvas view transform
        //assume this node is viewed through exactly 1 camera
        bounds=canvas.getCamera().getViewTransform().createTransformedShape( bounds ).getBounds2D();

        int desiredWidth  = (int)Math.round(bounds.getWidth());
        int desiredHeight = (int)Math.round(bounds.getHeight());

        if (desiredWidth > 0 && desiredHeight > 0) {
            if (bufferedImage == null || desiredWidth != bufferedImage.getWidth() || desiredHeight != bufferedImage.getHeight()) {
                try {
                    System.out.println("SVGNode.paint: rebuffering SVG at " + desiredWidth + "x" + desiredHeight);

                    convertSVGToImage(desiredWidth, desiredHeight);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bufferedImage != null) {
            Graphics2D g2 = paintContext.getGraphics();

            AffineTransform originalTransform = g2.getTransform();

            g2.setTransform(canvas.getTransform());

            g2.drawRenderedImage(bufferedImage, AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));

            g2.setTransform(originalTransform);
        }
    }

    public String toString() {
        return name;
    }

    private static synchronized String generateNextName() {
        return "SVGNode" + counter++;
    }
}
