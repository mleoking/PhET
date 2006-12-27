/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.movingman.application.motionandcontrols.AccelAndControls;
import edu.colorado.phet.movingman.application.motionandcontrols.LinearAndPanel;
import edu.colorado.phet.movingman.application.motionandcontrols.MotionAndControls;
import edu.colorado.phet.movingman.application.motionandcontrols.OscillateAndPanel;
import edu.colorado.phet.movingman.common.plaf.PlafUtil;
import edu.colorado.phet.movingman.elements.DataSeries;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 1:11:38 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManControlPanel extends JPanel {
    private MovingManModule module;
    private JPanel controllerContainer;
    private JButton play;
    private JButton pause;
    private JButton reset;
    private JButton record;
    private JButton rewind;
    private JPanel mediaPanel;
    MotionAndControls selectedMotion;
    private MotionActivation mact;
    private JButton slowMotion;
    private JComboBox comboBox;

    public JButton getAnotherPauseButton() {
        return anotherPauseButton;
    }

    private JButton anotherPauseButton;

    public JSpinner getInitialPositionSpinner() {
        return initialPositionSpinner;
    }

    private JSpinner initialPositionSpinner;

    public JButton getStartMotionButton() {
        return startMotion;
    }

    private JButton startMotion;

    public MovingManControlPanel(final MovingManModule module) {

        this.module = module;
        final Dimension preferred = new Dimension(200, 400);
        StepMotion stay = new StepMotion() {
            public double stepInTime(Man man, double dt) {
                return man.getX();
            }
        };

        mact = new MotionActivation(module);
        JPanel jp = new JPanel();
        jp.add(new JLabel("No controls.  Click 'Run Motion' to start standing still."));

        setSize(preferred);
        setPreferredSize(preferred);
        setLayout(new BorderLayout());

        controllerContainer = new JPanel();
        controllerContainer.setPreferredSize(new Dimension(200, 200));
        controllerContainer.setSize(new Dimension(200, 200));
        add(controllerContainer, BorderLayout.CENTER);

        ImageIcon pauseIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/Pause24.gif"));
        pause = new JButton("Pause", pauseIcon);
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //pausing from playback leaves it alone
                module.setPauseMode();
                play.setEnabled(true);
                slowMotion.setEnabled(true);
                pause.setEnabled(false);
                record.setEnabled(true);
            }
        });

        ImageIcon recordIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/Movie24.gif"));
        record = new JButton("Manual Control", recordIcon);
        final ActionListener manualSetup = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setRecordMode();
                record.setEnabled(false);
                pause.setEnabled(true);
                play.setEnabled(false);
                slowMotion.setEnabled(false);
            }
        };
        record.addActionListener(manualSetup);

        ImageIcon playIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/Play24.gif"));
        play = new JButton("Playback", playIcon);
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setPlaybackMode(1.0);
                play.setEnabled(false);
                slowMotion.setEnabled(true);
                pause.setEnabled(true);
            }
        });
        ImageIcon resetIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/Stop24.gif"));
        reset = new JButton("Reset", resetIcon);
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.reset();
                getInitialPositionSpinner().setValue(new Double(0));
            }
        });

        ImageIcon rewindIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/Rewind24.gif"));
        rewind = new JButton("Rewind", rewindIcon);
        rewind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.rewind();
            }
        });
        record.setEnabled(false);

        final JCheckBox positionBox = new JCheckBox("Position", true);
        positionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setPositionGraphVisible(positionBox.isSelected());
            }
        });
        final JCheckBox velocityBox = new JCheckBox("Velocity", true);
        velocityBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setVelocityGraphVisible(velocityBox.isSelected());
            }
        });
        final JCheckBox accelerationBox = new JCheckBox("Acceleration", true);
        accelerationBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setAccelerationGraphVisible(accelerationBox.isSelected());
            }
        });
        JPanel boxes = new JPanel();
        boxes.setLayout(new BoxLayout(boxes, BoxLayout.Y_AXIS));
        boxes.add(positionBox);
        boxes.add(velocityBox);
        boxes.add(accelerationBox);
        boxes.setBorder(BorderFactory.createTitledBorder("Show Plots"));
        add(boxes, BorderLayout.SOUTH);

        startMotion = new JButton("Run Motion", playIcon);
        startMotion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setMotionMode(selectedMotion);//.getStepMotion());
                play.setEnabled(false);
                record.setEnabled(false);
                pause.setEnabled(true);
                slowMotion.setEnabled(false);
                startMotion.setEnabled(false);
                anotherPauseButton.setEnabled(true);
            }
        });


        ImageIcon slowIcon = new ImageIcon(new ImageLoader().loadImage("images/icons/java/media/StepForward24.gif"));
        slowMotion = new JButton("Slow Motion Playback", slowIcon);
        slowMotion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setPlaybackMode(.4);
                play.setEnabled(true);
                slowMotion.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        startMotion.setEnabled(false);
        mediaPanel = new JPanel();
        mediaPanel.add(record);
        mediaPanel.add(play);
        mediaPanel.add(slowMotion);
        mediaPanel.add(pause);
        mediaPanel.add(rewind);
        mediaPanel.add(reset);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        initialPositionSpinner = new JSpinner(new SpinnerNumberModel(0.0, -10, 10, 1));
        initialPositionSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Number loc = (Number) initialPositionSpinner.getValue();
                double init = loc.doubleValue();
                module.setInitialPosition(init);
                module.setPauseMode();
                anotherPauseButton.setEnabled(false);
                pause.setEnabled(false);
                startMotion.setEnabled(true);
            }
        });
        TitledBorder tb = BorderFactory.createTitledBorder("Initial Position");
//        tb.setTitleColor(Color.black);
//        tb.setTitleFont(new Font("dialog", 0, 22));
        initialPositionSpinner.setBorder(tb);
        add(panel, BorderLayout.NORTH);
        anotherPauseButton = new JButton("Pause", pauseIcon);
        anotherPauseButton.setEnabled(false);
        anotherPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pause.doClick(50);
                startMotion.setEnabled(true);
                anotherPauseButton.setEnabled(false);
            }
        });

        JButton changeControl = new JButton("Change number of smoothing points.");
        changeControl.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String value = JOptionPane.showInputDialog("How many smoothing points?", "15");
                        module.setNumSmoothingPoints(Integer.parseInt(value));
                    }
                }
        );

        MotionAndControls still = new MotionAndControls(stay, jp) {
            public void collidedWithWall() {
            }
        };
        still.setName("Stand Very Still");
        final String init = ("Choose Motion");
        final Object[] motions = new Object[]{new LinearAndPanel(module),
                                              new OscillateAndPanel(module),
                                              new AccelAndControls(module),
                                              still};
        String hyphens = "---------------";
        final String recordMouseString = "Manual Control";
        comboBox = new JComboBox();
        comboBox.addItem(init);
        comboBox.addItem(hyphens);
        comboBox.addItem(recordMouseString);
        comboBox.addItem(hyphens);
        for (int i = 0; i < motions.length; i++) {
            comboBox.addItem(motions[i]);
        }

        comboBox.setBorder(BorderFactory.createRaisedBevelBorder());
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Object selected = comboBox.getSelectedItem();
                module.getManGraphic().setShowIdea(false);
                if (selected instanceof String) {
                    if (selected.equals(init)) {
                    } else if (selected.equals(recordMouseString)) {
                        manualSetup.actionPerformed(null);
                    } else {
                        comboBox.setSelectedIndex(0);
                    }
                } else {
                    MotionAndControls macy = (MotionAndControls) selected;
                    selectedMotion = macy;
                    mact.setupInDialog(macy, MovingManControlPanel.this);
                }
            }
        });
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        final JMenu viewMenu = new JMenu("View");
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for (int i = 0; i < items.length; i++) {
            JMenuItem item = items[i];
            viewMenu.add(item);
        }
//        addLookAndFeelItems(viewMenu);

        new Thread(new Runnable() {
            public void run() {
                try {
                    while (MovingManModule.FRAME == null || !MovingManModule.FRAME.isVisible())
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                MovingManModule.FRAME.getJMenuBar().add(viewMenu);
                MovingManModule.FRAME.setExtendedState(JFrame.MAXIMIZED_HORIZ);
                MovingManModule.FRAME.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        }).start();

        northPanel.add(comboBox);

        final JCheckBox invertAxes = new JCheckBox("Invert X-Axis", false);
//        invertAxes.setFont(new Font("dialog",0,8));
        invertAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setRightDirPositive(!invertAxes.isSelected());
            }
        });
        northPanel.add(invertAxes);
        panel.add(northPanel, BorderLayout.NORTH);

    }

    public static DataSeries parseString(String str) {
        DataSeries data = new DataSeries();
        StringTokenizer st = new StringTokenizer(str);
        while (st.hasMoreElements()) {
            String s = (String) st.nextElement();
            double value = Double.parseDouble(s);
            data.addPoint(value);
        }
        return data;
    }

    public static String toString(DataSeries data) {
        StringBuffer string = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            double x = data.pointAt(i);
            string.append(x);
            if (i < data.size())
                string.append(",");
        }
        return string.toString();
    }

    public void invokeMotionMode(MotionAndControls mac) {
        pause.setEnabled(true);
        play.setEnabled(false);
        record.setEnabled(false);
        module.setMotionMode(mac);
    }

    public void setPaused() {
        pause.setEnabled(false);
        play.setEnabled(true);
        record.setEnabled(true);
    }

    public Container getControllerContainer() {
        return controllerContainer;
    }

    public JComponent getMediaPanel() {
        return mediaPanel;
    }

    public void resetComboBox() {
        comboBox.setSelectedIndex(0);
    }

}
