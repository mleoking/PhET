/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 20, 2004
 * Time: 11:21:59 AM
 * Copyright (c) Feb 20, 2004 by Sam Reid
 */
public class CCKControlPanel extends JPanel {
    private CCK2Module module;
    Font font = new Font("Lucida Sans", Font.BOLD, 24);

    public CCKControlPanel(final CCK2Module module) {
        this.module = module;
        Border b = BorderFactory.createRaisedBevelBorder();
        setBorder(BorderFactory.createTitledBorder(b, "CCK"));
        JButton jb = new JButton("Help");
        jb.setFont(font);
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setHelpVisible(true);
            }
        });
        ViewPanel vp = new ViewPanel(module);
        setLayout(new BorderLayout());
        add(vp, BorderLayout.NORTH);
        add(jb, BorderLayout.SOUTH);
        setBackground(new Color(220, 250, 230));
    }
}
