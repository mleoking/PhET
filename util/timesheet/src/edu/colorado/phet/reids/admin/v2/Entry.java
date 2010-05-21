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
    private Date start;
    private Date end;
    private String notes;
    private boolean report;//should the item be indicated in a progress report?

    private boolean running = false;
    private ArrayList<TimesheetModel.TimeListener> timeListeners = new ArrayList<TimesheetModel.TimeListener>();
    private Timer timer = new Timer(30, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (running) setEndTime(new Date());
        }
    });
    private ArrayList<TimesheetModel.ClockedInListener> clockedInListeners = new ArrayList<TimesheetModel.ClockedInListener>();
    private String category;
    public static final DateFormat STORAGE_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm:ss.SSS a");

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isReport() {
        return report;
    }

    public Entry(Date start, Date end, String category, String notes, boolean report, boolean running) {
        this.start = start;
        this.end = end;
        this.category = category;
        this.notes = notes;
        this.report = report;
        this.running = running;
        if (running)
            timer.start();
    }

    public Entry(Date start, Date end, String category, String notes, boolean report) {
        this(start, end, category, notes, report, false);
    }

    public long getElapsedTime() {
        return end.getTime() - start.getTime();
    }

    public void clockOut() {
        if (running) {
            timer.stop();
            setEndTime(new Date());
        }
        running = false;
        for (TimesheetModel.ClockedInListener clockedInListener : clockedInListeners) {
            clockedInListener.clockedInChanged();
        }
    }

    private void setEndTime(Date date) {
        if (date.getTime() != end.getTime()) {
            end = date;
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
        return STORAGE_FORMAT.format(start) + "," + STORAGE_FORMAT.format(end) + "," + Util.millisecondsToElapsedTimeString(getElapsedTime()) + "," + category + ",\"" + notes + "\"" + "," + report;
    }

    public static Entry parseCSV(String line) {
//        System.out.println( "line = " + line );
        StringTokenizer st = new StringTokenizer(line, ",");
        try {
            final Date start = STORAGE_FORMAT.parse(st.nextToken());
            final Date end = STORAGE_FORMAT.parse(st.nextToken());
            st.nextToken();//skip elapsed time
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
            return new Entry(start, end, category, line.substring(startQuote + 1, endQuote), report);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
