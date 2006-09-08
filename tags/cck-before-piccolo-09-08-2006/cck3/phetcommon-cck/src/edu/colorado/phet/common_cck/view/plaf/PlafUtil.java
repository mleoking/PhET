/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.view.plaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * You could use it like this:
 * <p/>
 * final JMenu viewMenu = new JMenu("View");
 * JMenuItem[] items=PlafUtil.getLookAndFeelItems();
 * for (int i = 0; i < items.length; i++) {
 * JMenuItem item = items[i];
 * viewMenu.add(item);
 * }
 */
public class PlafUtil {

    static {
//        UIManager.installLookAndFeel( "Canary", new ClientLookAndFeel().getClass().getName() );
//        UIManager.installLookAndFeel( "Lecture I", new LectureLookAndFeel().getClass().getName() );
        UIManager.installLookAndFeel( "Lecture", new LectureLookAndFeel2().getClass().getName() );
//        UIManager.installLookAndFeel( "Playful", new PlayfulLookAndFeel().getClass().getName() );
        //        UIManager.installLookAndFeel( "Anim", new AnimFactoryLookAndFeel().getClass().getName() );
    }
//
//    public static void applyPlayful() {
//        try {
//            UIManager.setLookAndFeel( PlayfulLookAndFeel.class.getName() );
//            updateFrames();
//        }
//        catch( ClassNotFoundException e ) {
//            e.printStackTrace();
//        }
//        catch( InstantiationException e ) {
//            e.printStackTrace();
//        }
//        catch( IllegalAccessException e ) {
//            e.printStackTrace();
//        }
//        catch( UnsupportedLookAndFeelException e ) {
//            e.printStackTrace();
//        }
//    }

    public static void updateFrames() {
        Frame[] frames = JFrame.getFrames();
        ArrayList alreadyChecked = new ArrayList();
        for( int i = 0; i < frames.length; i++ ) {
            SwingUtilities.updateComponentTreeUI( frames[i] );
            if( !alreadyChecked.contains( frames[i] ) ) {
                Window[] owned = frames[i].getOwnedWindows();
                for( int j = 0; j < owned.length; j++ ) {
                    Window window = owned[j];
                    SwingUtilities.updateComponentTreeUI( window );
                }
            }
            alreadyChecked.add( frames[i] );
        }
    }

    public static JMenuItem[] getLookAndFeelItems() {
        UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
        ButtonGroup bg = new ButtonGroup();
        ArrayList container = new ArrayList();
        LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
        for( int i = 0; i < inst.length; i++ ) {
            final UIManager.LookAndFeelInfo lookAndFeelInfo = inst[i];

            JRadioButtonMenuItem jrb = new JRadioButtonMenuItem( lookAndFeelInfo.getName() );
            bg.add( jrb );
            //            if (currentLookAndFeel.)
            if( lookAndFeelInfo.getName().equals( currentLookAndFeel.getName() ) ) {
                jrb.setSelected( true );
            }

            container.add( jrb );
            jrb.addActionListener( new ActionListener() {
                //Could get all the ownerless jdialogs like this:
                /*
                    Frame unseenowner=new JDialog().getOwner();
                    then add all its owned windows.
                */
                public void actionPerformed( ActionEvent e ) {
                    try {
                        if( UIManager.getLookAndFeel().getClass().getName().equals( lookAndFeelInfo.getClassName() ) ) {
                            //do not reapply an existing look and feel
                        }
                        else {
                            UIManager.setLookAndFeel( lookAndFeelInfo.getClassName() );
                            updateFrames();
                        }
                    }
                    catch( ClassNotFoundException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( InstantiationException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( IllegalAccessException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( UnsupportedLookAndFeelException e1 ) {
                        e1.printStackTrace();
                    }

                }
            } );
        }
        JMenuItem[] items = (JMenuItem[])container.toArray( new JMenuItem[0] );
        return items;
    }

}
