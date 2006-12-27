package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 3, 2003
 * Time: 12:18:31 AM
 * To change this template use Options | File Templates.
 */
public class GridLineGraphic implements Graphic {
    private BoxedPlot target;
    private Stroke stroke;
    private Color color;
    private int nx;
    private int ny;
    private Color backgroundColor;
    private String title;
    Font lineFont = new Font("Lucida Sans", 0, 18);
    DecimalFormat xformat = new DecimalFormat("#0.##");
    DecimalFormat yformat = new DecimalFormat("#0.##");
    private boolean visible = true;
    private double[] ylines;
    private Stroke thickStroke = new BasicStroke(2);

    public GridLineGraphic(BoxedPlot target, Stroke stroke, Color color, int nx, int ny, Color backgroundColor, String title) {
        this.target = target;
        this.stroke = stroke;
        this.color = color;
        this.nx = nx;
        this.ny = ny;
        this.backgroundColor = backgroundColor;
        this.title = title;
    }

    public void paintGridLines(Graphics2D graphics2D) {
        graphics2D.setColor(backgroundColor);
        Rectangle2D.Double output = target.getOutputBox();
        graphics2D.fillRect((int) output.x, (int) output.y, (int) output.width, (int) output.height);

        double xspacing = output.width / (nx);
        graphics2D.setColor(color);
        graphics2D.setStroke(stroke);
        graphics2D.setFont(lineFont);
        for (int i = 0; i < nx; i++) {
            int x = (int) (output.x + i * xspacing);
            graphics2D.drawLine(x, (int) output.y, x, (int) (output.y + output.height));
        }
        if (ylines == null) {
            double yspacing = output.height / (ny - 1);
            for (int i = 0; i < ny; i++) {
                int y = (int) (output.y + i * yspacing);
                graphics2D.drawLine((int) output.x, y, (int) (output.x + output.width), y);
            }
        } else {
            for (int i = 0; i < ylines.length; i++) {
                int y = (int) target.getTransform().transform(new Point2D.Double(0, ylines[i])).y;
                if (i == ylines.length / 2) {
                    graphics2D.setColor(Color.darkGray);
                    graphics2D.setStroke(thickStroke);
                } else {
                    graphics2D.setColor(color);
                    graphics2D.setStroke(stroke);
                }
                graphics2D.drawLine((int) output.x, y, (int) (output.x + output.width), y);
            }
        }
    }

    public void setPaintYLines(double[] ylines) {
        this.ylines = ylines;
    }

    public AffineTransform getImageTransform(Rectangle2D rect, double angle, double x, double y) {
        AffineTransform at = new AffineTransform();
        at.translate(x - rect.getWidth() / 2.0, y - rect.getHeight() / 2.0);
        at.rotate(angle, rect.getWidth() / 2.0, rect.getHeight() / 2.0);
        return at;
    }

    public void paintTextLabels(Graphics2D graphics2D) {
        Rectangle2D.Double output = target.getOutputBox();
        Rectangle2D.Double input = target.getInputBounds();
        double xspacing = output.width / (nx);
        graphics2D.setFont(lineFont);
        graphics2D.setColor(Color.black);
        double modelXSpacing = input.width / (nx);
        double modelYSpacing = input.height / (ny - 1);
        for (int i = 1; i < nx; i++) {
            int x = (int) (output.x + i * xspacing);
            //Label the line's value.
            double modelx = input.x + i * modelXSpacing;
            String text = xformat.format(modelx);
            if (i == 1)
                text += " sec";
            Rectangle2D bounds = lineFont.getStringBounds(text, graphics2D.getFontRenderContext());
//            graphics2D.drawString(text, x-(int)bounds.getWidth(), (int) (output.y + output.height) - 2);
            graphics2D.drawString(text, x + 2, (int) (output.y + output.height / 2 + bounds.getHeight()) - 2);
        }
//        double yspacing = output.height / (ny - 1);
//        for (int i = 0; i < ny+1; i++) {
//            int y = (int) (output.y + i * yspacing);
//            double modely=input.y+i*modelYSpacing;
//            String text=yformat.format(modely);
//            graphics2D.drawString(text,(int)output.x,y-2);
//        }
        if (ylines == null) {

            for (int i = 1; i < ny - 1; i++) {
                double modely = input.y + (i) * modelYSpacing;
//            O.d("i="+i+", Modely="+modely);
                int y = (int) target.getTransform().transform(new Point2D.Double(0, modely)).y;
//            int y = (int) (output.y + i * yspacing);
                String text = yformat.format(modely);
//            graphics2D.setColor(Color.green);
//            graphics2D.fillRect((int)output.x,y,7,7);
//            graphics2D.setColor(Color.black);
                Rectangle2D bounds = lineFont.getStringBounds(text, graphics2D.getFontRenderContext());
                graphics2D.drawString(text, (int) output.x - (int) bounds.getWidth(), y);
//            graphics2D.drawLine((int) output.x, y, (int) (output.x + output.width), y);
            }
        } else {
            for (int i = 0; i < ylines.length; i++) {
                double modely = ylines[i];
                int y = (int) target.getTransform().transform(new Point2D.Double(0, modely)).y;
                String text = yformat.format(modely);
                Rectangle2D bounds = lineFont.getStringBounds(text, graphics2D.getFontRenderContext());

                Point displayPoint = new Point((int) output.x - (int) bounds.getWidth(), y + (int) (bounds.getHeight() / 2.0));
//                Point ctrPt=new Point((int)output.x-10,y+5);
//                AffineTransform at = getImageTransform(bounds, Math.PI / 2, displayPoint.x, displayPoint.y);
//                AffineTransform at = getImageTransform(bounds, Math.PI / 2, ctrPt.x,ctrPt.y);
//                AffineTransform oldTransform = graphics2D.getTransform();
//                graphics2D.setTransform(at);
                graphics2D.drawString(text, displayPoint.x, displayPoint.y);
//                graphics2D.drawString(text, 0, 0);
//                graphics2D.setTransform(oldTransform);
//                graphics2D.drawString(text, (int) output.x, y);
            }
        }

        graphics2D.setStroke(new BasicStroke(2.0f));
        graphics2D.setColor(Color.black);
        graphics2D.drawRect((int) output.x, (int) output.y, (int) output.width, (int) output.height);
    }

    public void paint(Graphics2D graphics2D) {
        if (!visible)
            return;
        paintTitle(graphics2D);
        paintGridLines(graphics2D);
        paintTextLabels(graphics2D);
        paintTimeLabel(graphics2D);
    }

    private void paintTimeLabel(Graphics2D graphics2D) {
        double modely = ylines[ylines.length / 2];
        int y = (int) target.getTransform().transform(new Point2D.Double(0, modely)).y;
        int x = (int) (target.getOutputBox().getWidth() + target.getOutputBox().getX());
        graphics2D.setFont(lineFont);
        String timeStr = "Time";
        Rectangle2D text = lineFont.getStringBounds(timeStr, graphics2D.getFontRenderContext());
        int outx = (int) (x - text.getWidth() * 1.3);
        int outy = (int) (y + text.getHeight() * 1.1);
//        Point pt=new Point(outx,outy);
        graphics2D.drawString(timeStr, outx, outy);
    }

    private void paintTitle(Graphics2D graphics2D) {
        double modely = ylines[ylines.length / 2];
        int y = (int) target.getTransform().transform(new Point2D.Double(0, modely)).y;
//        String text = yformat.format(modely);
        Rectangle2D bounds = lineFont.getStringBounds(title, graphics2D.getFontRenderContext());
        Rectangle2D.Double output = target.getOutputBox();
//        Point displayPoint = new Point((int) output.x - (int) bounds.getWidth(), y + (int) (bounds.getHeight() / 2.0));
        Point ctrPt = new Point((int) output.x - 20, y);
//                AffineTransform at = getImageTransform(bounds, Math.PI / 2, displayPoint.x, displayPoint.y);
        AffineTransform at = getImageTransform(bounds, -Math.PI / 2, ctrPt.x, ctrPt.y);
        AffineTransform oldTransform = graphics2D.getTransform();
        graphics2D.setTransform(at);
//        graphics2D.drawString(title, displayPoint.x, displayPoint.y);
        graphics2D.drawString(title, 0, 0);
        graphics2D.setTransform(oldTransform);
//                graphics2D.drawString(text, (int) output.x, y);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
