// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.mouse;

import electron.gui.ParticlePanel;
import electron.gui.media.Resettable;
import java.awt.event.*;
import phys2d.*;

// Referenced classes of package electron.gui.mouse:
//            ParticleSelector

public class ParticleGrabber
    implements MouseListener, MouseMotionListener, Resettable
{

    public ParticleGrabber(ParticlePanel particlepanel, System2D system2d, SystemRunner systemrunner)
    {
        run = systemrunner;
        pp = particlepanel;
        selected = null;
        sys = system2d;
        ps = new ParticleSelector(particlepanel);
    }

    public void fireResetAction(System2D system2d, ParticlePanel particlepanel)
    {
        sys = system2d;
        pp = particlepanel;
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
        if(selected != null)
            sys.addParticle(selected);
        selected = null;
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
        java.awt.Point point = mouseevent.getPoint();
        Particle particle = ps.selectAt(point);
        selected = particle;
        if(particle != null)
            sys.remove(selected);
    }

    public void mouseDragged(MouseEvent mouseevent)
    {
        if(selected == null)
            return;
        selected.setPosition(new DoublePoint(mouseevent.getX(), mouseevent.getY()));
        if(!run.isActiveAndRunning())
            pp.repaint();
    }

    public void mouseMoved(MouseEvent mouseevent)
    {
    }

    ParticlePanel pp;
    Particle selected;
    System2D sys;
    SystemRunner run;
    ParticleSelector ps;
}
