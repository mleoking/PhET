import java.applet.*;
import java.awt.*;
import JSci.maths.chaos.*;

/**
* Plot of Cantor dust.
* @author Mark Hale
* @version 1.0
*/
public final class CantorDustPlot extends Applet {
        private final int N=5;
        private Image img;
        private CantorDustGraphic dust;
        private int width, height;
        public void init() {
                width=getSize().width;
                height=getSize().height;
                img=createImage(width, height);
                dust=new CantorDustGraphic(img.getGraphics());
                dust.draw(0, width, N);
        }
        public void paint(Graphics g) {
                g.drawImage(img, 0, 0, this);
        }
        class CantorDustGraphic extends CantorDust {
                private final Graphics g;
                public CantorDustGraphic(Graphics grafixs) {
                        g=grafixs;
                }
                public void draw(int start, int end, int n) {
                        g.setColor(Color.black);
                        g.drawLine(start, height/2, end, height/2);
                        g.setColor(getBackground());
                        recurse(start, end, n);
                }
                protected void eraseLine(double start, double end) {
                        g.drawLine((int)Math.round(start), height/2, (int)Math.round(end), height/2);
                }
        }
}

