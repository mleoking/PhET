package edu.colorado.phet.reids.admin;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class StretchingPanel extends JPanel {
    public StretchingPanel(final TimesheetModel timesheetModel) {
        add(new JLabel("Elapsed: MSE"));
        final JTextField textField = new JTextField(8);
        {
            textField.setEditable(false);
        }
        add(textField);
        TimesheetModel.TimeListener timeListener = new TimesheetModel.TimeListener() {
            public void timeChanged() {
                long time = getTimeSinceBeginningOfLast(timesheetModel);
                textField.setText(Util.secondsToElapsedTimeString(time));
                if (timesheetModel.getEntryCount() > 0) {
                    if (entryMatches(timesheetModel.getLastEntry()) && time == 5 * 60)//TODO: this plays sound during loading CSV
                        playNotification("C:\\workingcopy\\phet-svn\\trunk\\simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\cork.wav");//todo: take out absolute paths
                    if (time == 60 * 60) {
                        playNotification("C:\\workingcopy\\phet-svn\\trunk\\simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\tada.WAV");
                    }
                }
            }
        };
        timesheetModel.addTimeListener(timeListener);
        timeListener.timeChanged();
        JButton button = new JButton("S&E");
        {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timesheetModel.startNewEntry("maintenance", "stretching & exercise");
                }
            });
        }
        add(button);
        setBorder(BorderFactory.createEtchedBorder());
    }

    private long getTimeSinceBeginningOfLast(TimesheetModel timesheetModel) {
        long elapsed = 0;
        for (int i = timesheetModel.getEntryCount() - 1; i >= 0; i--) {
            Entry entry = timesheetModel.getEntry(i);
            elapsed += entry.getElapsedSeconds();
            if (entryMatches(entry)) {
                break;
            }
        }
        return elapsed;
    }

    private boolean entryMatches(Entry entry) {
        return entry.getCategory().equals("maintenance") && entry.getNotes().equals("stretching & exercise");
    }

    public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        playNotification("C:\\workingcopy\\phet-svn\\trunk\\simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\tada.WAV");
    }

    private static void playNotification(String pathname) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(pathname));
            clip.open(inputStream);
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}