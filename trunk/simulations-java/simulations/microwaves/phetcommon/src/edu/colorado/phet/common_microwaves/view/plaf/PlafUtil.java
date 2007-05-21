/*, 2003.*/
package edu.colorado.phet.common_microwaves.view.plaf;

//import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 You could use it like this:

 final JMenu viewMenu = new JMenu("View");
 JMenuItem[] items=PlafUtil.getLookAndFeelItems();
 for (int i = 0; i < items.length; i++) {
 JMenuItem item = items[i];
 viewMenu.add(item);
 }
 */
public class PlafUtil {
    static {
        UIManager.installLookAndFeel( SimStrings.get( "PlafUtil.CanaryText" ), new ClientLookAndFeel().getClass().getName());
        UIManager.installLookAndFeel( SimStrings.get( "PlafUtil.Lecture1Text" ), new LectureLookAndFeel().getClass().getName());
        UIManager.installLookAndFeel( SimStrings.get( "PlafUtil.Lecture2Text" ), new LectureLookAndFeel2().getClass().getName());
    }

    public static JMenuItem[] getLookAndFeelItems() {
        UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
        ButtonGroup bg = new ButtonGroup();
        ArrayList container = new ArrayList();
        LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
        for (int i = 0; i < inst.length; i++) {
            final UIManager.LookAndFeelInfo lookAndFeelInfo = inst[i];

            JRadioButtonMenuItem jrb = new JRadioButtonMenuItem(lookAndFeelInfo.getName());
            bg.add(jrb);
//            if (currentLookAndFeel.)
            if (lookAndFeelInfo.getName().equals(currentLookAndFeel.getName()))
                jrb.setSelected(true);

            container.add(jrb);
            jrb.addActionListener(new ActionListener() {
                //Could get all the ownerless jdialogs like this:
                /*
                    Frame unseenowner=new JDialog().getOwner();
                    then add all its owned windows.
                */
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                        Frame[] frames = JFrame.getFrames();
                        ArrayList alreadyChecked = new ArrayList();
                        for (int i = 0; i < frames.length; i++) {
                            SwingUtilities.updateComponentTreeUI(frames[i]);
                            if (!alreadyChecked.contains(frames[i])) {
                                Window[] owned = frames[i].getOwnedWindows();
                                for (int j = 0; j < owned.length; j++) {
                                    Window window = owned[j];
                                    SwingUtilities.updateComponentTreeUI(window);
                                }
                            }
                            alreadyChecked.add(frames[i]);
                        }
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch (UnsupportedLookAndFeelException e1) {
                        e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }
            });
        }
        JMenuItem[] items = (JMenuItem[]) container.toArray(new JMenuItem[0]);
        return items;
    }

}
