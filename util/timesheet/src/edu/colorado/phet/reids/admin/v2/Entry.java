package edu.colorado.phet.reids.admin.v2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 20, 2010
 * Time: 5:11:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Entry {
    private long startSeconds;
    private long endSeconds;
    private String category;
    private String notes;
    private boolean report;//should the item be indicated in a progress report?

    private boolean running = false;
    private ArrayList<TimesheetModel.TimeListener> timeListeners = new ArrayList<TimesheetModel.TimeListener>();
    private Timer timer = new Timer(30, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (running) setEndTime(Util.newDateToNearestSecond().getTime() / 1000);
        }
    });
    private ArrayList<TimesheetModel.ClockedInListener> clockedInListeners = new ArrayList<TimesheetModel.ClockedInListener>();
    public static final DateFormat STORAGE_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm:ss a");

    public Date getStartDate() {
        return new Date(startSeconds * 1000);
    }

    public Date getEndDate() {
        return new Date(endSeconds * 1000);
    }

    public String getNotes() {
        return notes;
    }

    public boolean isReport() {
        return report;
    }

    public Entry(long startSeconds, long endSeconds, String category, String notes, boolean report, boolean running) {
        this.startSeconds = startSeconds;
        this.endSeconds = endSeconds;
        this.category = category;
        this.notes = notes;
        this.report = report;
        this.running = running;
        if (running)
            timer.start();
    }

    public Entry(long startSeconds, long endSeconds, String category, String notes, boolean report) {
        this(startSeconds, endSeconds, category, notes, report, false);
    }

    public long getElapsedSeconds() {
        return endSeconds - startSeconds;
    }

    public void clockOut() {
        if (running) {
            timer.stop();
            setEndTime(Util.newDateToNearestSecond().getTime() / 1000);
        }
        running = false;
        for (TimesheetModel.ClockedInListener clockedInListener : clockedInListeners) {
            clockedInListener.clockedInChanged();
        }
    }

    private void setEndTime(long seconds) {
        if (seconds != endSeconds) {
            endSeconds = seconds;
            for (TimesheetModel.TimeListener timeListener : timeListeners) {
                timeListener.timeChanged();
            }
        }
    }

    public void addTimeListener(TimesheetModel.TimeListener timeListener) {
        timeListeners.add(timeListener);
    }

    public boolean isRunning() {
        return running;
    }

    public void addClockedInListener(TimesheetModel.ClockedInListener clockedInListener) {
        clockedInListeners.add(clockedInListener);
    }

    public String getCategory() {
        return category;
    }

    public String toCSV() {
        return STORAGE_FORMAT.format(getStartDate()) + "," + STORAGE_FORMAT.format(getEndDate()) + "," + category + ",\"" + notes + "\"" + "," + report;
    }

    public static Entry parseCSV(String line) {
//        System.out.println( "line = " + line );
        StringTokenizer st = new StringTokenizer(line, ",");
        try {
            final Date start = STORAGE_FORMAT.parse(st.nextToken());
            final Date end = STORAGE_FORMAT.parse(st.nextToken());
            //everything inside the quotes is notes
            int startQuote = line.indexOf('"');
            int endQuote = line.indexOf('"', startQuote + 1);
            String category = "";
            try {
                category = st.nextToken();
                if (category.startsWith("\"") && category.endsWith("\"")) {
                    category = "";//no category specified
                }
            }
            catch (NoSuchElementException e) {
            }
            boolean report = Boolean.parseBoolean(line.substring(line.lastIndexOf('"')));
            return new Entry(start.getTime() / 1000, end.getTime() / 1000, category, line.substring(startQuote + 1, endQuote), report);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public long getEndSeconds() {
        return endSeconds;
    }
}
