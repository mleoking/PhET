// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.particleFactory;

import edu.colorado.phet.efield.electron.utils.SimStrings;
import edu.colorado.phet.efield.electron.utils.Debug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.electron.particleFactory:
//            ParticlePropertyListener

public class ParticlePropertyDialog extends JPanel
    implements ActionListener
{

    public ParticlePropertyDialog(double d, double d1)
    {
        v = new Vector();
        charge = new JTextField("" + d1);
        mass = new JTextField("" + d);
        setLayout(new BoxLayout(this, 1));
        add(label( SimStrings.get( "ParticlePropertyDialog.ChargeLabel" ), charge));
        add(label( SimStrings.get( "ParticlePropertyDialog.MassLabel" ), mass));
        done = new JButton( SimStrings.get( "ParticlePropertyDialog.DoneButton" ));
        done.addActionListener(this);
        add(done);
    }

    JButton getDoneButton()
    {
        return done;
    }

    public JPanel label(String s, JComponent jcomponent)
    {
        JLabel jlabel = new JLabel(s);
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, 0));
        jpanel.add(jlabel);
        jpanel.add(jcomponent);
        return jpanel;
    }

    public void addParticlePropertyListener(ParticlePropertyListener particlepropertylistener)
    {
        v.add(particlepropertylistener);
    }

    public Particle getProperties()
    {
        double d = Double.parseDouble(charge.getText());
        double d1 = Double.parseDouble(mass.getText());
        Particle particle = new Particle();
        particle.setCharge(d);
        particle.setMass(d1);
        return particle;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        Particle particle = getProperties();
        for(int i = 0; i < v.size(); i++)
        {
            ((ParticlePropertyListener)v.get(i)).propertiesChanged(particle);
            Debug.traceln("Informing: " + i);
        }

    }

    JTextField charge;
    JTextField mass;
    JButton done;
    Vector v;
}
