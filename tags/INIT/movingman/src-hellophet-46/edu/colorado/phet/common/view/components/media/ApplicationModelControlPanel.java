/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.components.media;

import edu.colorado.phet.common.model.ApplicationModel;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 10:24:21 PM
 * Copyright (runner) May 18, 2003 by Sam Reid
 */
public class ApplicationModelControlPanel extends JPanel {
    Resettable rh;

    JButton play;
    JButton pause;
//    JButton reset;
    JButton step;
    ApplicationModel model;

    Resettable resetHandler;
    private JButton resetButton;

    public ApplicationModelControlPanel(ApplicationModel runner) {
        this(runner, null);
    }

    public ApplicationModelControlPanel(final ApplicationModel runner, final Resettable rh) {
        this.model = runner;
        ImageLoader cil = new ImageLoader();

        String root = "images/icons/java/media/";
        BufferedImage playU = cil.loadBufferedImage(root + "Play24.gif");
        BufferedImage pauseU = cil.loadBufferedImage(root + "Pause24.gif");
        BufferedImage stepU = cil.loadBufferedImage(root + "StepForward24.gif");
        ImageIcon playIcon = new ImageIcon(playU);
        ImageIcon pauseIcon = new ImageIcon(pauseU);
        ImageIcon stepIcon = new ImageIcon(stepU);
        this.rh = rh;
        play = new JButton("Play", playIcon);
        pause = new JButton("Pause", pauseIcon);
        step = new JButton("Step", stepIcon);
        step.setEnabled(false);

        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setRunning(true);
                play.setEnabled(false);
                pause.setEnabled(true);
                step.setEnabled(false);
            }
        });
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setRunning(false);
                play.setEnabled(true);
                pause.setEnabled(false);
                step.setEnabled(true);
            }
        });

        step.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.execute(new Command() {
                    public void doIt() {
                        //model.stepInTime();
                        model.tickOnce();
                    }
                });
//                model.execute(new SingleStepCommand());
//                new SingleStepCommand(model).doItLater();
//                model.execute(new SingleStepCommand(model));

//                new SingleStepCommand().doIt();
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(play);
        add(pause);
        add(step);
//        if( rh != null ) {
//            BufferedImage resetU = cil.loadBufferedImage( root + "Stop24.gif" );
//            ImageIcon resetIcon = new ImageIcon( resetU );
//            resetButton = new JButton( "Reset", resetIcon );
//            resetButton.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    rh.reset();
//                }
//            } );
//            add( resetButton );
//        }


        BufferedImage resetU = cil.loadBufferedImage(root + "Stop24.gif");
        ImageIcon resetIcon = new ImageIcon(resetU);
        resetButton = new JButton("Reset", resetIcon);
        resetButton.addActionListener(new ResetActionListener());
        if (rh != null)
            add(resetButton);

        play.setEnabled(false);
        pause.setEnabled(true);
    }

    class ResetActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resetHandler.reset();
        }

    }

    public void setResettable(Resettable r) {
        if (r == null) {
            remove(resetButton);
        } else {
            resetHandler = r;
            add(resetButton);
        }
    }

//    public void setModel(BaseModel model) {
//        this.model = model;
//    }
}
