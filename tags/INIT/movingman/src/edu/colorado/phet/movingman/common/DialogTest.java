package edu.colorado.phet.movingman.common;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DialogTest extends javax.swing.JPanel {
    JLabel label;
    ImageIcon icon = new ImageIcon("images/middle.gif");
    JFrame frame;
    String simpleDialogDesc = "Some simple message dialogs";
    String iconDesc = "A JOptionPane has its choice of icons";
    String moreDialogDesc = "Some more dialogs";

    public DialogTest(JFrame frame) {
        this.frame = frame;

        //create the components
        JPanel frequentPanel = createSimpleDialogBox();
        JPanel featurePanel = createFeatureDialogBox();
        JPanel iconPanel = createIconDialogBox();
        label = new JLabel("Click the \"Show it!\" button"
                + " to bring up the selected dialog.",
                JLabel.CENTER);

        //lay them out
        Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
        frequentPanel.setBorder(padding);
        featurePanel.setBorder(padding);
        iconPanel.setBorder(padding);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Simple Modal Dialogs", null,
                frequentPanel, simpleDialogDesc);
        tabbedPane.addTab("More Dialogs", null,
                featurePanel, moreDialogDesc);
        tabbedPane.addTab("Dialog Icons", null,
                iconPanel, iconDesc);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    void setLabel(String newText) {
        label.setText(newText);
    }


    private JPanel createSimpleDialogBox() {
        final int numButtons = 4;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        final ButtonGroup group = new ButtonGroup();

        JButton showItButton = null;

        final String defaultMessageCommand = "default";
        final String yesNoCommand = "yesno";
        final String yeahNahCommand = "yeahnah";
        final String yncCommand = "ync";

        radioButtons[0] = new JRadioButton("OK (in the L&F's words)");
        radioButtons[0].setActionCommand(defaultMessageCommand);

        radioButtons[1] = new JRadioButton("Yes/No (in the L&F's words)");
        radioButtons[1].setActionCommand(yesNoCommand);

        radioButtons[2] = new JRadioButton("Yes/No (in the programmer's words)");
        radioButtons[2].setActionCommand(yeahNahCommand);

        radioButtons[3] = new JRadioButton("Yes/No/Cancel (in the programmer's words)");
        radioButtons[3].setActionCommand(yncCommand);

        for (int i = 0; i < numButtons; i++) {
            group.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        showItButton = new JButton("Show it!");
        showItButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String command = group.getSelection().getActionCommand();

                //ok dialog
                if (command == defaultMessageCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.");

                    //yes/no dialog
                } else if (command == yesNoCommand) {
                    int n = JOptionPane.showConfirmDialog(
                            frame, "Do you like green eggs and ham?",
                            "An Inane Question",
                            JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        setLabel("Ewww!");
                    } else if (n == JOptionPane.NO_OPTION) {
                        setLabel("Me neither!");
                    } else {
                        setLabel("Come on -- tell me!");
                    }

                    //yes/no (not in those words)
                } else if (command == yeahNahCommand) {
                    String string1 = "Yes, I like them";
                    String string2 = "No, they're disgusting!";
                    Object[] options = {string1, string2};
                    int n = JOptionPane.showOptionDialog(frame,
                            "Do you like green eggs and ham?",
                            "A Silly Question",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            string1);
                    if (n == JOptionPane.YES_OPTION) {
                        setLabel("You're kidding!");
                    } else if (n == JOptionPane.NO_OPTION) {
                        setLabel("I don't like them, either.");
                    } else {
                        setLabel("Come on -- 'fess up!");
                    }

                    //yes/no/cancel (not in those words)
                } else if (command == yncCommand) {
                    String string1 = "Yes, please";
                    String string2 = "No, thanks";
                    String string3 = "Don't give me anything!";
                    Object[] options = {string1, string2, string3};
                    int n = JOptionPane.showOptionDialog(frame,
                            "Would you like some green eggs to go "
                            + "with that ham?",
                            "A Silly Question",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            string3);
                    if (n == JOptionPane.YES_OPTION) {
                        setLabel("Here you go: green eggs and ham!");
                    } else if (n == JOptionPane.NO_OPTION) {
                        setLabel("OK, just the ham, then.");
                    } else if (n == JOptionPane.CANCEL_OPTION) {
                        setLabel("Well, I'm certainly not going to eat them!");
                    } else {
                        setLabel("Please tell me what you want!");
                    }
                }
                return;
            }
        });

        return createPane(simpleDialogDesc + ":",
                radioButtons,
                showItButton);
    }

    //number of buttons *must* be even
    private JPanel create2ColPane(String description,
                                  JRadioButton[] radioButtons,
                                  JButton showButton) {
        JLabel label = new JLabel(description);
        int numPerColumn = radioButtons.length / 2;

        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(0, 2));
        for (int i = 0; i < numPerColumn; i++) {
            grid.add(radioButtons[i]);
            grid.add(radioButtons[i + numPerColumn]);
        }

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(label);
        grid.setAlignmentX(0.0f);
        box.add(grid);

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(box, BorderLayout.NORTH);
        pane.add(showButton, BorderLayout.SOUTH);

        return pane;
    }

    private JPanel createPane(String description,
                              JRadioButton[] radioButtons,
                              JButton showButton) {

        int numChoices = radioButtons.length;
        JPanel box = new JPanel();
        JLabel label = new JLabel(description);

        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(label);

        for (int i = 0; i < numChoices; i++) {
            box.add(radioButtons[i]);
        }

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(box, BorderLayout.NORTH);
        pane.add(showButton, BorderLayout.SOUTH);
        return pane;
    }

    /*
     * These dialogs are implemented using showMessageDialog, but
     * you can specify the icon (using similar code) for any other
     * kind of dialog, as well.
     */
    private JPanel createIconDialogBox() {
        JButton showItButton = null;

        final int numButtons = 6;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        final ButtonGroup group = new ButtonGroup();

        final String plainCommand = "plain";
        final String infoCommand = "info";
        final String questionCommand = "question";
        final String errorCommand = "error";
        final String warningCommand = "warning";
        final String customCommand = "custom";

        radioButtons[0] = new JRadioButton("Plain (no icon)");
        radioButtons[0].setActionCommand(plainCommand);

        radioButtons[1] = new JRadioButton("Information icon");
        radioButtons[1].setActionCommand(infoCommand);

        radioButtons[2] = new JRadioButton("Question icon");
        radioButtons[2].setActionCommand(questionCommand);

        radioButtons[3] = new JRadioButton("Error icon");
        radioButtons[3].setActionCommand(errorCommand);

        radioButtons[4] = new JRadioButton("Warning icon");
        radioButtons[4].setActionCommand(warningCommand);

        radioButtons[5] = new JRadioButton("Custom icon");
        radioButtons[5].setActionCommand(customCommand);

        for (int i = 0; i < numButtons; i++) {
            group.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        showItButton = new JButton("Show it!");
        showItButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String command = group.getSelection().getActionCommand();

                //no icon
                if (command == plainCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.",
                            "A plain message",
                            JOptionPane.PLAIN_MESSAGE);
                    //information icon
                } else if (command == infoCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.",
                            "Inane informational dialog",
                            JOptionPane.INFORMATION_MESSAGE);

                    //XXX: It doesn't make sense to make a question with
                    //XXX: only one button.
                    //XXX: See "Yes/No (but not in those words)" for a better solution.
                    //question icon
                } else if (command == questionCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "You shouldn't use a message dialog "
                            + "(like this)\n"
                            + "for a question, OK?",
                            "Inane question",
                            JOptionPane.QUESTION_MESSAGE);
                    //error icon
                } else if (command == errorCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                    //warning icon
                } else if (command == warningCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.",
                            "Inane warning",
                            JOptionPane.WARNING_MESSAGE);
                    //custom icon
                } else if (command == customCommand) {
                    JOptionPane.showMessageDialog(frame,
                            "Eggs aren't supposed to be green.",
                            "Inane custom dialog",
                            JOptionPane.INFORMATION_MESSAGE,
                            icon);
                }
            }
        });

        return create2ColPane(iconDesc + ":",
                radioButtons,
                showItButton);
    }

    private JPanel createFeatureDialogBox() {
        final int numButtons = 4;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        final ButtonGroup group = new ButtonGroup();

        JButton showItButton = null;

        final String pickOneCommand = "pickone";
        final String nonAutoCommand = "nonautooption";
        final String customOptionCommand = "customoption";
        final String nonModalCommand = "nonmodal";

        radioButtons[0] = new JRadioButton("Pick one of several choices");
        radioButtons[0].setActionCommand(pickOneCommand);

        radioButtons[1] = new JRadioButton("Non-auto-closing dialog");
        radioButtons[1].setActionCommand(nonAutoCommand);

        radioButtons[2] = new JRadioButton("Customized dialog");
        radioButtons[2].setActionCommand(customOptionCommand);

        radioButtons[3] = new JRadioButton("Non-modal dialog");
        radioButtons[3].setActionCommand(nonModalCommand);

        for (int i = 0; i < numButtons; i++) {
            group.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        showItButton = new JButton("Show it!");
        showItButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String command = group.getSelection().getActionCommand();

                //pick one of many
                //XXX: Currently, the choices (if < 20) are presented in a
                //XXX: lightweight combo box.  This means that the popped-
                //XXX: list is clipped to the dialog window.
                //XXX: See bug #4131624.
                if (command == pickOneCommand) {
                    Object[] possibilities = {"ham", "spam", "yam"};
                    String s = (String) JOptionPane.showInputDialog(
                            frame,
                            "Complete the sentence: "
                            + "\"Green eggs and...\"",
                            "Customized Dialog",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            possibilities,
                            "ham");
                    if (s != null) {
                        s = s.trim();
                        if (s.length() > 0) {
                            setLabel("Green eggs and... " + s + "!");
                            return;
                        }
                    }
                    setLabel("Come on, finish the sentence!");

                    //non-auto-closing dialog
                } else if (command == nonAutoCommand) {
                    final JOptionPane optionPane = new JOptionPane(
                            "The only way to close this dialog is by\n"
                            + "pressing one of the following buttons.\n"
                            + "Do you understand?",
                            JOptionPane.QUESTION_MESSAGE,
                            JOptionPane.YES_NO_OPTION);

                    // You can't use pane.createDialog() because that
                    // method sets up the JDialog with a property change
                    // listener that automatically closes the window
                    // when a button is clicked.
                    final JDialog dialog = new JDialog(frame,
                            "Click a button",
                            true);
                    dialog.setContentPane(optionPane);
                    dialog.setDefaultCloseOperation(
                            JDialog.DO_NOTHING_ON_CLOSE);
                    dialog.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent we) {
                            setLabel("Thwarted user attempt to close window.");
                        }
                    });
                    optionPane.addPropertyChangeListener(
                            new PropertyChangeListener() {
                                public void propertyChange(PropertyChangeEvent e) {
                                    String prop = e.getPropertyName();

                                    if (dialog.isVisible()
                                            && (e.getSource() == optionPane)
                                            && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                                            prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                                        //If you were going to check something
                                        //before closing the window, you'd do
                                        //it here.
                                        dialog.setVisible(false);
                                    }
                                }
                            });
                    dialog.pack();
                    dialog.setLocationRelativeTo(frame);
                    dialog.show();

                    int value = ((Integer) optionPane.getValue()).intValue();
                    if (value == JOptionPane.YES_OPTION) {
                        setLabel("Good.");
                    } else if (value == JOptionPane.NO_OPTION) {
                        setLabel("Try using the window decorations "
                                + "to close the non-auto-closing dialog. "
                                + "You can't!");
                    } else {
                        setLabel("Something's wrong -- this shouldn't happen.");
                    }

                    //custom non-auto-closing dialog
                    //NOTE: if you don't intend to check the input,
                    //then just use showInputDialog instead.
                } else if (command == customOptionCommand) {
                    CustomDialog dialog = new CustomDialog(frame,
                            "geisel",
                            DialogTest.this);
                    dialog.pack();
                    dialog.setLocationRelativeTo(frame);
                    dialog.show();

                    String s = dialog.getValidatedText();
                    if (s != null) {
                        //The text is valid.
                        setLabel("Congratulations!  "
                                + "You entered \""
                                + s
                                + "\".");
                    }

                    //non-modal dialog
                } else if (command == nonModalCommand) {
                    //Create the dialog.
                    JDialog dialog = new JDialog(frame, "A Non-Modal Dialog");

                    //Add contents to it.
                    JLabel label = new JLabel("This is a non-modal dialog");
                    label.setHorizontalAlignment(JLabel.CENTER);
                    Container contentPane = dialog.getContentPane();
                    contentPane.add(label, BorderLayout.CENTER);

                    //Show it.
                    dialog.setSize(new Dimension(300, 150));
                    dialog.setLocationRelativeTo(frame);
                    dialog.show();
                }
            }
        });

        return createPane(moreDialogDesc + ":",
                radioButtons,
                showItButton);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DialogTest");

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1, 1));
        contentPane.add(new DialogTest(frame));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}

