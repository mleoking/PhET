package edu.colorado.phet.recordandplayback.test;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Particle node graphic class for example application.
 *
 * @author Sam Reid
 */
public class ParticleNode extends PNode {
    ParticleNode(final Particle particle) {
        final PhetPPath path = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.blue, new BasicStroke(4), Color.black);
        addChild(path);
        path.setOffset(particle.getX(), particle.getY());
        particle.addListener(new Particle.Listener() {
            public void moved() {
                path.setOffset(particle.getX(), particle.getY());
            }
        });
        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler() {
            public void mouseDragged(PInputEvent event) {
                particle.translate(event.getCanvasDelta().width, event.getCanvasDelta().getHeight());
            }
        });
    }
}
