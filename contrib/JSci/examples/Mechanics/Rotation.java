import java.awt.*;
import java.awt.event.*;
import JSci.physics.*;

/**
* Angular momentum simulator.
* @author Mark Hale
* @version 1.0
*/
public final class Rotation extends Frame implements Runnable {
        private RigidBody2D body=new RigidBody2D();
        private Display display=new Display();

        public static void main(String arg[]) {
                Frame app=new Rotation();
                app.setSize(250,250);
                app.setVisible(true);
        }
        public Rotation() {
                super("Rotation!");
                add(display,"Center");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                body.setMass(5.0);
                body.setMomentOfInertia(5.0);
                Thread thr=new Thread(this);
                thr.start();
        }
        public void run() {
                double width,height;
                double x,y;
                while(true) {
                        body.move(0.01);
                        width=getSize().width;
                        height=getSize().height;
                        x=body.getXPosition();
                        y=body.getYPosition();
                        if(x>width/2.0)
                                body.setXPosition(x-width);
                        else if(x<-width/2.0)
                                body.setXPosition(x+width);
                        if(y>height/2.0)
                                body.setYPosition(y-height);
                        else if(y<-height/2.0)
                                body.setYPosition(y+height);
                        display.repaint();
                        try {
                                Thread.sleep(100);
                        } catch(InterruptedException e) {}
                }
        }
        private final class Display extends Canvas {
                private Point start,end;
                private boolean firstDrag=false;
                public Display() {
                        addMouseListener(new MouseAdapter() {
                                public void mousePressed(MouseEvent e) {
                                        firstDrag=true;
                                        start=end=null;
                                }
                                public void mouseReleased(MouseEvent e) {
                                        if(start!=null && end!=null) {
                                                final double Fx=end.x-start.x;
                                                final double Fy=-(end.y-start.y);
                                                final int cx=getSize().width/2;
                                                final int cy=getSize().height/2;
                                                final double x=(end.x-cx)-body.getXPosition();
                                                final double y=-(end.y-cy)-body.getYPosition();
                                                for(int i=0;i<4;i++) {
                                                        body.applyForce(Fx,Fy,x,y,0.05);
                                                        body.move(0.05);
                                                        repaint();
                                                }
                                                System.out.println("Force ("+Fx+','+Fy+") applied at ("+x+','+y+')');
                                        }
                                }
                        });
                        addMouseMotionListener(new MouseMotionAdapter() {
                                public void mouseDragged(MouseEvent e) {
                                        if(firstDrag) {
                                                start=e.getPoint();
                                                firstDrag=false;
                                        } else if(start!=null)
                                                end=e.getPoint();
                                }
                        });
                }
                public void paint(Graphics g) {
                        final Graphics2D g2=(Graphics2D)g;
                        final int cx=getSize().width/2;
                        final int cy=getSize().height/2;
                        final double x=body.getXPosition();
                        final double y=body.getYPosition();
                        g2.translate(x,-y);
                        g2.rotate(-body.getAngle(),cx,cy);
                        g2.setColor(Color.red);
                        g2.fillRect(cx-50,cy-10,100,20);
                }
                public Dimension getPreferredSize() {
                        return new Dimension(200,200);
                }
        }
}

