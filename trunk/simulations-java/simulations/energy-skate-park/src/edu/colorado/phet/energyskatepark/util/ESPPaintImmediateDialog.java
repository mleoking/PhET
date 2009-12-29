package edu.colorado.phet.energyskatepark.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ESPPaintImmediateDialog extends JDialog {

    public ESPPaintImmediateDialog() {
    }

    public ESPPaintImmediateDialog(Frame frame) {
        super(frame);
    }

    public ESPPaintImmediateDialog(Frame frame, String title) {
        super(frame, title);
    }

    public ESPPaintImmediateDialog(Dialog owner) {
        super(owner);
    }

    public ESPPaintImmediateDialog(Dialog owner, String title) {
        super(owner, title);
    }

    public ESPPaintImmediateDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public ESPPaintImmediateDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    public ESPPaintImmediateDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public ESPPaintImmediateDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public void setVisible(boolean b) {
        if (b) {
            final Container contentPane = getContentPane();
            Timer timer = new Timer(60, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    paintImmediate((JComponent) contentPane);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
        super.setVisible(b);
    }

    private static void paintImmediate(JComponent component) {
        component.paintImmediately(0, 0, component.getWidth(), component.getHeight());
    }
}
