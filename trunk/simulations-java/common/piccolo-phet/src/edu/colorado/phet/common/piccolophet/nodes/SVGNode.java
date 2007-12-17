package edu.colorado.phet.common.piccolophet.nodes;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.app.beans.SVGIcon;
import edu.colorado.phet.common.phetcommon.resources.IResourceLoader;
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

public class SVGNode extends PhetPNode {
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
     * Constructor.
     *
     * @param canvas         The canvas, from which we get the transform
     *                       using to draw rescaled node.
     * @param svgInputStream The input stream for the SVG image.
     * @param width          The initial width of the image, in pixels.
     * @param height         The initial height of the image, in pixels.
     *
     * @throws java.io.IOException If an error occurs while loading the image.
     */
    public SVGNode(final PhetPCanvas canvas, final InputStream svgInputStream, double width, double height) throws IOException {
        super();

        this.canvas = canvas;
        this.name   = generateNextName();
        this.uri    = SVGCache.getSVGUniverse().loadSVG(svgInputStream, name);

        setWidth(width);
        setHeight(height);
    }

    public SVGNode(final PhetPCanvas canvas, IResourceLoader resourceLoader, String resourceName, int width, int height) throws IOException {
        this(canvas, resourceLoader.getResourceAsStream(resourceName), width, height);
    }

    private void convertSVGToPImage(int pixelWidth, int pixelHeight) throws IOException {
        SVGIcon icon = new SVGIcon();

        icon.setSvgURI(uri);
        icon.setScaleToFit(true);
        icon.setAntiAlias(true);
        icon.setPreferredSize(new Dimension(pixelWidth, pixelHeight));

        bufferedImage = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);

        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
    }

    protected void paint(PPaintContext paintContext) {
        Rectangle2D bounds = getGlobalBounds();

        canvas.getPhetRootNode().globalToScreen(bounds);

        int desiredWidth = (int)Math.round(bounds.getWidth());
        int desiredHeight = (int)Math.round(bounds.getHeight());

        if (desiredWidth > 0 && desiredHeight > 0) {
            if (bufferedImage == null || desiredWidth != bufferedImage.getWidth() || desiredHeight != bufferedImage.getHeight()) {
                try {
                    System.out.println("Rerendering SVG: desiredWidth = " + desiredWidth + ", desiredHeight = " + desiredHeight);

                    convertSVGToPImage(desiredWidth, desiredHeight);
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
