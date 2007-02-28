import java.awt.*;
import java.awt.event.*;
import JSci.physics.*;

/**
* Two-body collision simulator.
* @author Mark Hale
* @version 1.0
*/
public final class Collision extends Frame {
        private ClassicalParticle2D A=new ClassicalParticle2D();
        private ClassicalParticle2D B=new ClassicalParticle2D();
        private TextField massA=new TextField("2.0");
        private TextField massB=new TextField("2.0");
        private TextField velXA=new TextField("3.0");
        private TextField velXB=new TextField("3.0");
        private TextField velYA=new TextField("3.0");
        private TextField velYB=new TextField("-1.0");
        private VectorDisplay display=new VectorDisplay(4);
        private Label energyBefore=new Label();
        private Label energyAfter=new Label();
        private Label momentumXBefore=new Label();
        private Label momentumXAfter=new Label();
        private Label momentumYBefore=new Label();
        private Label momentumYAfter=new Label();
        public static void main(String arg[]) {
                Frame app=new Collision();
                app.setSize(500,500);
                app.setVisible(true);
        }
        public Collision() {
                super("Collision!");
                final Panel controls=new Panel();
                controls.setLayout(new GridLayout(4,3));
                final Button button=new Button("Collide");
                button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                collide();
                        }
                });
                controls.add(button);
                final Label labelA=new Label("Particle A");
                labelA.setForeground(Color.red);
                controls.add(labelA);
                final Label labelB=new Label("Particle B");
                labelB.setForeground(Color.blue);
                controls.add(labelB);
                controls.add(new Label("Mass:"));
                controls.add(massA);
                controls.add(massB);
                controls.add(new Label("X Velocity:"));
                controls.add(velXA);
                controls.add(velXB);
                controls.add(new Label("Y Velocity:"));
                controls.add(velYA);
                controls.add(velYB);
                final Panel info=new Panel();
                info.setLayout(new GridLayout(4,3));
                info.add(new Panel());
                info.add(new Label("Before"));
                info.add(new Label("After"));
                info.add(new Label("Energy:"));
                info.add(energyBefore);
                info.add(energyAfter);
                info.add(new Label("X Momentum:"));
                info.add(momentumXBefore);
                info.add(momentumXAfter);
                info.add(new Label("Y Momentum:"));
                info.add(momentumYBefore);
                info.add(momentumYAfter);
                add(controls,"North");
                add(display,"Center");
                add(info,"South");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                collide();
        }
        private void collide() {
                A.setMass(parseDouble(massA.getText()));
                A.setVelocity(parseDouble(velXA.getText()),parseDouble(velYA.getText()));
                B.setMass(parseDouble(massB.getText()));
                B.setVelocity(parseDouble(velXB.getText()),parseDouble(velYB.getText()));
                display.setVector(0,-A.getXVelocity(),A.getYVelocity());
                display.setVector(1,-B.getXVelocity(),B.getYVelocity());
                energyBefore.setText(Double.toString(A.energy()+B.energy()));
                momentumXBefore.setText(Double.toString(A.getXMomentum()+B.getXMomentum()));
                momentumYBefore.setText(Double.toString(A.getYMomentum()+B.getYMomentum()));
                A.collide(B, 0.0);
                display.setVector(2,A.getXVelocity(),-A.getYVelocity());
                display.setVector(3,B.getXVelocity(),-B.getYVelocity());
                energyAfter.setText(Double.toString(A.energy()+B.energy()));
                momentumXAfter.setText(Double.toString(A.getXMomentum()+B.getXMomentum()));
                momentumYAfter.setText(Double.toString(A.getYMomentum()+B.getYMomentum()));
        }
        private static double parseDouble(String s) {
                return Double.valueOf(s).doubleValue();
        }
        private final class VectorDisplay extends Canvas {
                private float vecX[],vecY[];
                private Color color[];
                public VectorDisplay(int n) {
                        vecX=new float[n];
                        vecY=new float[n];
                        color=new Color[n];
                        color[0]=Color.red;
                        color[1]=Color.blue;
                        color[2]=Color.red;
                        color[3]=Color.blue;
                }
                public void setVector(int i,double dx,double dy) {
                        final double norm=Math.sqrt(dx*dx+dy*dy);
                        vecX[i]=(float)(dx/norm);
                        vecY[i]=(float)(dy/norm);
                        repaint();
                }
                public void paint(Graphics g) {
                        final int ox=getSize().width/2;
                        final int oy=getSize().height/2;
                        int endX,endY;
                        for(int i=0;i<vecX.length;i++) {
                                endX=ox+(int)((ox-20)*vecX[i]);
                                endY=oy+(int)((oy-20)*vecY[i]);
                                g.setColor(color[i]);
                                g.drawLine(ox,oy,endX,endY);
                        }
                }
                public Dimension getPreferredSize() {
                        return new Dimension(100,100);
                }
        }
}

