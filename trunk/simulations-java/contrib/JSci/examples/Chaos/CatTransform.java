import java.applet.*;
import java.awt.*;
import JSci.maths.chaos.*;

/**
* Cat map transforms.
* @author Mark Hale
* @version 1.2
*/
public final class CatTransform extends Applet implements Runnable {
        private CatMap cm;
        private double p1[],p2[],p3[],p4[];
        private Thread thr;
        private int xScale,yScale;
        private int p1x,p1y,p2x,p2y,p3x,p3y,p4x,p4y;
        private int iteration;
        public void init() {
                cm=new CatMap();
                p1=new double[2];
                p2=new double[2];
                p3=new double[2];
                p4=new double[2];
                p1[0]=0.1;p1[1]=0.1;
                p2[0]=0.9;p2[1]=0.1;
                p3[0]=0.9;p3[1]=0.9;
                p4[0]=0.1;p4[1]=0.9;
                xScale=getSize().width;
                yScale=getSize().height;
                iteration=0;
        }
        public void start() {
                if(thr==null)
                        thr=new Thread(this);
                thr.start();
        }
        public void stop() {
                thr=null;
        }
        private int dataToScreenX(double x) {
                return (int)Math.round(x*xScale);
        }
        private int dataToScreenY(double y) {
                return (int)Math.round(y*yScale);
        }
        public void run() {
                while(thr==Thread.currentThread()) {
                        p1x=dataToScreenX(p1[0]);
                        p1y=dataToScreenY(p1[1]);
                        p2x=dataToScreenX(p2[0]);
                        p2y=dataToScreenY(p2[1]);
                        p3x=dataToScreenX(p3[0]);
                        p3y=dataToScreenY(p3[1]);
                        p4x=dataToScreenX(p4[0]);
                        p4y=dataToScreenY(p4[1]);
                        repaint();
                        try {
                                Thread.sleep(200);
                        } catch(InterruptedException e) {}
                        p1=cm.map(p1);
                        p2=cm.map(p2);
                        p3=cm.map(p3);
                        p4=cm.map(p4);
                        iteration++;
                }
        }
        public void paint(Graphics g) {
                showStatus("Iteration = "+iteration);
                g.drawLine(p1x,p1y,p2x,p2y);
                g.drawLine(p2x,p2y,p3x,p3y);
                g.drawLine(p3x,p3y,p4x,p4y);
                g.drawLine(p4x,p4y,p1x,p1y);
        }
}

