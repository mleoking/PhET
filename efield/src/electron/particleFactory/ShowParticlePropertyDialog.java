// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.particleFactory;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

// Referenced classes of package electron.particleFactory:
//            ParticlePropertyDialog

public class ShowParticlePropertyDialog
    implements ActionListener
{
    public static class HideListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            jf.setVisible(false);
        }

        Component jf;

        public HideListener(Component component)
        {
            jf = component;
        }
    }


    public ShowParticlePropertyDialog(double d, double d1)
    {
        ppd = new ParticlePropertyDialog(d, d1);
        jf = new JFrame();
        jf.setContentPane(ppd);
        ppd.getDoneButton().addActionListener(new HideListener(jf));
        jf.pack();
    }

    public ParticlePropertyDialog getDialog()
    {
        return ppd;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        jf.setVisible(true);
    }

    ParticlePropertyDialog ppd;
    JFrame jf;
}
