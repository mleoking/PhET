/**
 * Class: TestClock
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: Dec 30, 2003
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class TestClock extends PhetApplication {

    public TestClock(ApplicationDescriptor descriptor, Module m, AbstractClock clock) {
        super(descriptor, m, clock);
    }

    static class MyModule extends Module {
        protected MyModule(String name, AbstractClock clock) {
            super("Test");
            setApparatusPanel(new ApparatusPanel());
            setModel(new BaseModel(clock));
            Particle p = new Particle(100, 200);
            super.add(p, new ParticleGraphic(p), 10);
        }

        public void addModelElement(ModelElement modelElement) {
            super.addModelElement(modelElement);
        }
    }

    static class Particle extends Observable implements ModelElement {
        double x;
        double y;

        Particle(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void stepInTime(double dt) {
            x = (x + 10) % 100;
            y = (y + 10) % 100;
            setChanged();
            notifyObservers();
        }
    }

    static class ParticleGraphic implements Graphic, Observer {
        private Particle p;

        public ParticleGraphic(Particle p) {
            this.p = p;
        }

        public void update(Observable o, Object arg) {

        }

        public void paint(Graphics2D g) {
            g.fillRect((int) p.x, (int) p.y, 10, 10);
        }
    }


    public static void main(String[] args) {
        AbstractClock clock = new ThreadedClock(10, 20, false);
        final MyModule m = new MyModule("asdf", clock);
        ApplicationDescriptor ad = new ApplicationDescriptor("appname", "mydescritpion", "myversion");
        TestClock tc = new TestClock(ad, m, clock);
        clock.addClockTickListener(new ClockTickListener() {
            public void clockTicked(AbstractClock c, double dt) {
                m.getApparatusPanel().repaint();
            }
        });
        tc.startApplication(m);
        tc.getApplicationView().getPhetFrame().setSize(400,400);
        tc.getApplicationView().getPhetFrame().repaint();
    }
}
