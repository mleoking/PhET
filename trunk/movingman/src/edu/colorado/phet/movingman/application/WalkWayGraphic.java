package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.math.transforms.functions.RangeToRange;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 5:34:02 PM
 * To change this template use Options | File Templates.
 */
public class WalkWayGraphic implements Graphic {
    int numTickMarks = 21;
    private double treex;
    private double housex;
    MovingManModule module;
    DecimalFormat format = new DecimalFormat("##");
    Font font = new Font("dialog", 0, 20);
    private BufferedImage tree;
    private BufferedImage house;

//    Color textColor=Color.balc;
    public WalkWayGraphic(MovingManModule module, int numTickMarks) {
        this(module, numTickMarks, -10, 10);
    }

    public WalkWayGraphic(MovingManModule module, int numTickMarks, double treex, double housex) {
        this.module = module;
        this.numTickMarks = numTickMarks;
        this.treex = treex;
        this.housex = housex;
        ImageLoader ilo = new ImageLoader();
        tree = ilo.loadBufferedImage("images/tree.gif");
        house = ilo.loadBufferedImage("images/cottage.gif");
    }

    public void setTreeX(double treex) {
        this.treex = treex;
    }

    public void setHouseX(double housex) {
        this.housex = housex;
    }

    public void paint(Graphics2D graphics2D) {
        RangeToRange transform = module.getManPositionTransform();
        double modelRange = transform.getInputWidth();
        double modelDX = modelRange / (numTickMarks - 1);
        graphics2D.setColor(Color.black);
        graphics2D.setFont(font);
        int height = 134;
//        if (height <= -10)
//        PhetVector vanishingPoint=new PhetVector();

//        Rectangle2D.Double rect=new Rectangle2D.Double(transform.evaluate(-10),transform);
        graphics2D.setColor(module.getPurple());
        graphics2D.fillRect(0, 0, module.getApparatusPanel().getWidth(), height + 30);
        graphics2D.setColor(Color.black);
        for (int i = 0; i < numTickMarks; i++) {
            double modelx = transform.getLowInputPoint() + i * modelDX;
            int viewx = (int) transform.evaluate(modelx);
//            O.d("modelx="+modelx+", viewx="+viewx);

//            graphics2D.drawLine(viewx, 0, viewx, height);

            Point dst = new Point(viewx, height - 20);
            graphics2D.drawLine(viewx, height, dst.x, dst.y);

            String str = format.format(modelx);
            if (str.equals("0"))
                str = "0 meters";
            Rectangle2D bounds = font.getStringBounds(str, graphics2D.getFontRenderContext());
            graphics2D.drawString(str, viewx - (int) (bounds.getWidth() / 2), height + (int) bounds.getHeight());
        }
        //Tree at -10.
        int treex = (int) (transform.evaluate(this.treex) - tree.getWidth() / 2);
        int treey = 10;
        int housex = (int) (transform.evaluate(this.housex) - house.getWidth() / 2);
        int housey = 10;
        graphics2D.drawImage(tree, treex, treey, null);
        graphics2D.drawImage(house, housex, housey, null);
    }
}
