package electron.electricField;

import electron.gui.popupMenu.MenuConstructor;
import phys2d.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FieldMenuConstructor implements MenuConstructor {
    ChargeFieldSource cfs;
    Component paintMe;

    public FieldMenuConstructor(ChargeFieldSource cfs, Component paintMe) {
        this.cfs = cfs;
        this.paintMe = paintMe;
    }

    public JMenu getMenu(Particle p) {
        JMenu jm = new JMenu("Particle Menu");
        ShowEField se = (new ShowEField(cfs, p, paintMe));
        se.setSelected(!cfs.isIgnoring(p));
        jm.add(se);
        return jm;
    }

    public static class ShowEField extends JCheckBoxMenuItem implements ActionListener {
        ChargeFieldSource cfs;
        Particle p;
        Component paintMe;

        public ShowEField(ChargeFieldSource cfs, Particle p, Component paintMe) {
            super("Show Contribution to Electric Field", true);
            this.cfs = cfs;
            this.p = p;
            addActionListener(this);
            this.paintMe = paintMe;
        }

        public void actionPerformed(ActionEvent ae) {
            if (isSelected()) {
                cfs.removeFromIgnore(p);
            } else {
                //util.Debug.traceln("Ignoring: "+p);
                cfs.ignore(p);
            }
            paintMe.repaint();
        }
    }
}
