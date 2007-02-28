import java.applet.*;
import java.awt.*;
import JSci.maths.chaos.*;

/**
* Plot of the Koch snowflake.
* @author Mark Hale
* @version 1.0
*/
public final class KochSnowflakePlot extends Applet {
        private final int N=5;
        private Image img;
        private KochCurveGraphic curve;
        private int width, height;
        public void init() {
                width=getSize().width;
                height=getSize().height;
                img=createImage(width, height);
                curve=new KochCurveGraphic(img.getGraphics());
                final int len=width/2;
                final int h_2=(int)Math.round(len*Math.sqrt(3.0)/4.0);
                curve.draw((width-len)/2, height/2-h_2, width/2, height/2+h_2, N);
                curve.draw(width/2, height/2+h_2, (width+len)/2, height/2-h_2, N);
                curve.draw((width+len)/2, height/2-h_2, (width-len)/2, height/2-h_2, N);
        }
        public void paint(Graphics g) {
                g.drawImage(img, 0, 0, this);
        }
        class KochCurveGraphic extends KochCurve {
                private final Graphics g;
                public KochCurveGraphic(Graphics grafixs) {
                        g=grafixs;
                }
                public void draw(int startX, int startY, int endX, int endY, int n) {
                        g.setColor(Color.black);
                        g.drawLine(startX, height-startY, endX, height-endY);
                        recurse(startX, startY, endX, endY, n);
                }
                protected void drawLine(double startX, double startY, double endX, double endY) {
                        g.drawLine((int)Math.round(startX), height-(int)Math.round(startY), (int)Math.round(endX), height-(int)Math.round(endY));
                }
                protected void eraseLine(double startX, double startY, double endX, double endY) {
                        g.setColor(getBackground());
                        drawLine(startX, startY, endX, endY);
                        g.setColor(Color.black);
                }
        }
}

