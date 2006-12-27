// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.popupMenu;

import electron.gui.ParticlePanel;
import electron.gui.mouse.ParticleSelector;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

// Referenced classes of package electron.gui.popupMenu:
//            MenuConstructor

public class ParticlePopupListener
    implements MouseListener
{

    public ParticlePopupListener(ParticlePanel particlepanel, MenuConstructor menuconstructor)
    {
        mc = menuconstructor;
        ps = new ParticleSelector(particlepanel);
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
        if(mouseevent.isPopupTrigger())
        {
            if(last != null)
                last.getPopupMenu().setVisible(false);
            phys2d.Particle particle = ps.selectAt(mouseevent.getPoint());
            if(particle != null)
            {
                last = mc.getMenu(particle);
                last.setMenuLocation(mouseevent.getPoint().x, mouseevent.getPoint().y);
                JPopupMenu jpopupmenu = last.getPopupMenu();
                jpopupmenu.show(mouseevent.getComponent(), mouseevent.getX(), mouseevent.getY());
            }
        }
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    ParticleSelector ps;
    MenuConstructor mc;
    JMenu last;
}
