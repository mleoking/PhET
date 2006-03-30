// Copyright (C) 2001-2003 Jon A. Maxwell (JAM)
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


package netx.jnlp.runtime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.jnlp.*;
import netx.jnlp.*;

/**
 * Prompt for an install location.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
class InstallDialog extends Dialog implements ActionListener {

    private static String R(String key) { return JNLPRuntime.getMessage(key); }

    private ImageIcon background;
    private TextField installField = new TextField();
    private Button ok = new Button(R("ButOk"));
    private Button cancel = new Button(R("ButCancel"));
    private Button browse = new Button(R("ButBrowse"));
    private boolean canceled = false;

    static Frame createFrame() {
        Frame f = new Frame();
        f.setIconImage(JNLPRuntime.getWindowIcon());
        return f;
    }

    InstallDialog() {
        super(createFrame(), R("CChooseCache"), true);

        background = 
            new ImageIcon(getClass().getClassLoader().getResource("netx/jnlp/resources/install.png"));

        setLayout(new BorderLayout());
        setBackground(Color.white);

        String home = System.getProperty("java.io.tmpdir");
        installField.setText((new File(home)).toString());

        ok.addActionListener(this);
        cancel.addActionListener(this);
        browse.addActionListener(this);

        Font f = new Font("SansSerif", Font.BOLD, 14);

        installField.setFont(f);
        ok.setFont(f);
        cancel.setFont(f);
        browse.setFont(f);

        Panel top = new Panel(new GridLayout(2, 1, 0, 10));
        Label l1 = new Label(R("CChooseCacheInfo"));
        Label l2 = new Label(R("CChooseCacheDir")+":");
        l1.setFont(f);
        l2.setFont(f);
        top.add(l1);
        top.add(l2);

        Panel field = new Panel(new BorderLayout());
        field.add(top, BorderLayout.NORTH);
        field.add(installField, BorderLayout.CENTER);
        field.add(browse, BorderLayout.EAST);

        Panel grid = new Panel(new GridLayout(1, 2, 8, 8));
        grid.add(ok);
        grid.add(cancel);
        
        Panel choice = new Panel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        choice.add(grid);

        add(field, BorderLayout.NORTH);
        add(new Panel(), BorderLayout.CENTER);
        add(choice, BorderLayout.SOUTH);
    }

    public Insets getInsets() {
        Insets s = super.getInsets();
        return new Insets(s.top + 55, s.left + 7, s.bottom + 6, s.right + 4);
        //return new Insets(s.top + 115, s.left + 7, s.bottom + 6, s.right + 4);
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (background == null)
            return d;

        Insets in = super.getInsets();
        d.width = background.getIconWidth() + in.left + in.right;
        d.height = Math.max(d.height + 8, background.getIconHeight() + 8 + in.top + in.bottom);

        return d;
    }

    public void paint(Graphics g) {
        Insets sin = super.getInsets();
        g.drawImage(background.getImage(), sin.left, sin.top, this);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == cancel) {
            canceled = true;
            dispose();
        }
        if (evt.getSource() == ok) {
            File f = new File(installField.getText());

            try {
                f.mkdirs();
                dispose();
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Install directory cannot be created.", 
                                              "ERROR", JOptionPane.ERROR_MESSAGE); 
            }
        }
        if (evt.getSource() == browse) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int r = fc.showOpenDialog(this); 
            if (r == JFileChooser.APPROVE_OPTION)
                installField.setText(fc.getSelectedFile().toString());
        }
    }


    static File getInstallDir() {
        InstallDialog id = new InstallDialog();

        //id.setResizable(false);
        id.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        id.setLocation(screen.width/2-id.getWidth()/2,
                       screen.height/2-id.getHeight()/2);
        id.show();

        if (id.canceled)
            return null;

        return new File(id.installField.getText());
    }

}

