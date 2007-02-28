/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2.tests;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.arrows.StrokedFixedArrow;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.clock2.components.DefaultClockStatePanel;
import edu.colorado.phet.coreadditions.clock2.*;
import edu.colorado.phet.coreadditions.clock2.clocks.SwingTimerClock;
import edu.colorado.phet.coreadditions.clock2.clocks.ThreadClock;
import edu.colorado.phet.coreadditions.clock2.clocks.UtilTimerClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

/**
 * Test application which allows user to dynamically switch clocks.
 */
public class ClockTest {
    private ApparatusPanel panel;
    private double angleInit = 0;
    DefaultClock clock;
    private SimulationTimeListener mover;
    private WallToSimulationTimeConverter identityConverter = new IdentityTimeConverter();
    private ConstantTimeConverter constantConverter = new ConstantTimeConverter(30);

    public static void main(String[] args) {
        new ClockTest().testGraphics();
    }

    int w = 80;
    int h = 80;
    double x = 100;
    double y = 100;

    double simulationTime=0;
    Font font=new Font("Lucida Sans",0,30);
    DecimalFormat df=new DecimalFormat("####.000");
    public void testGraphics() {
        panel = new ApparatusPanel();
        panel.setBackground(Color.green);

        panel.addGraphic(new Graphic() {
            public void paint(Graphics2D g) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                double dtheta = Math.PI / 34;
                for (double angle = angleInit; angle < Math.PI * 2 + angleInit; angle += dtheta) {
                    double value = angle * 300;
                    int valueInt = (int) value;
                    valueInt %= 255;
                    angleInit += Math.PI / 9400;
                    Color color = Color.blue;
                    StrokedFixedArrow arrow = new StrokedFixedArrow(color, 10);
                    int length = (int) (250 + Math.random() * 10);
                    PhetVector pv = PhetVector.parseAngleAndMagnitude(angle, length);
                    int endx = (int) pv.getX();
                    int endy = (int) pv.getY();
                    arrow.drawLine(g, (int)x, (int)y, endx + (int)x, endy + (int)y);
                }
                g.setColor(Color.red);
                g.fillRect((int)x, (int)y, w, h);
                g.setFont(font);
                g.setColor(Color.black);
                g.drawString("Simulation Time="+df.format(simulationTime/1000),50,50);
            }
        }, 0);
        JFrame f = new JFrame("GRaphics test");
        f.setContentPane(panel);

        f.setSize(900, 900);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setVisible(true);

        clock = new DefaultClock(new SwingTimerClock(20), new IdentityTimeConverter());

        JFrame j2 = new JFrame("Controls");
        DefaultClockStatePanel dcsp = new DefaultClockStatePanel(clock);
        j2.setContentPane(dcsp);
        j2.pack();
        j2.setVisible(true);

        mover = new SimulationTimeListener() {
            public void simulationTimeIncreased(double dt) {
                simulationTime+=dt;
                double vx = 1.0 / 30;
                double vy = 2.0 / 30;
                x += vx * dt;
                y += vy * dt;
                if (x > 900)
                    x = 0;
                if (y > 900)
                    y = 0;
                panel.repaint();
            }
        };
        clock.addSimulationTimeListener(mover);


        clock.addSimulationTimeListener(new SimulationTimeListener() {
            public void simulationTimeIncreased(double dt) {
                System.out.println("DT=" + dt);
            }
        });

        clock.start();

        JFrame controlFrame = new JFrame("Controls");
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        JButton startSwing = new JButton("Start Swing Thread");
        final JCheckBox identityTime = new JCheckBox("Identity Time (constant if not selected)", true);
        identityTime.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (identityTime.isSelected()) {
                    clock.setWallToSimulationTimeConverter(identityConverter);
                } else {
                    clock.setWallToSimulationTimeConverter(constantConverter);
                }
            }
        });
        clock.addClockStateListener(new ClockStateListener() {
            public void clockStarted(AbstractClock source) {
            }

            public void clockStopped(AbstractClock source) {
            }

            public void clockDestroyed(AbstractClock source) {
            }

            public void clockDelayChanged(AbstractClock source, int newDelay) {
                constantConverter.setTimePerTick(newDelay);
            }
        });
        startSwing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clock.kill();
                clock = new DefaultClock(new SwingTimerClock(clock.getRequestedDelay()), clock.getWallToSimulationTimeConverter());
                clock.addSimulationTimeListener(mover);
                clock.start();
            }
        });
        JButton startThread = new JButton("Start Thread");
        startThread.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clock.kill();
                clock = new DefaultClock(new ThreadClock(clock.getRequestedDelay(), 500), clock.getWallToSimulationTimeConverter());
                clock.addSimulationTimeListener(mover);
                clock.start();
            }
        });
        JButton startUtil = new JButton("Start Util Timer");
        startUtil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clock.kill();
                clock = new DefaultClock(new UtilTimerClock(clock.getRequestedDelay()), clock.getWallToSimulationTimeConverter());
                clock.addSimulationTimeListener(mover);
                clock.start();
            }
        });

        controlPanel.add(startSwing);
        controlPanel.add(startThread);
        controlPanel.add(startUtil);
        controlPanel.add(identityTime);
        controlFrame.setContentPane(controlPanel);
        controlFrame.pack();
        controlFrame.setVisible(true);
        controlFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void testClock() {
        int reqWait = 100;
        int reqPause = 500;
        ThreadClock tc = new ThreadClock(reqWait, reqPause);
        WallToSimulationAdapter adapter = new WallToSimulationAdapter(new IdentityTimeConverter());
        adapter.addSimulationTimeListener(new SimulationTimeListener() {
            public void simulationTimeIncreased(double dt) {
                System.out.println("dt = " + dt);
            }
        });
        tc.addTickListener(adapter);
        tc.start();
        SwingTimerClock stc = new SwingTimerClock(reqWait);
        stc.addTickListener(adapter);
//        stc.start();
        JFrame jf = new JFrame();
        jf.setVisible(true);
    }

    public static void testTicks(String[] args) {
        int reqWait = 100;
        int reqPause = 500;
        ThreadClock tc = new ThreadClock(100, reqPause);
        tc.addTickListener(new TickListener() {
            public void clockTicked(AbstractClock source) {
                System.out.println("timectick = ");
            }
        });
        tc.start();
    }
}
