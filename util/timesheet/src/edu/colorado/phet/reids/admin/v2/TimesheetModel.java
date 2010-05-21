package edu.colorado.phet.reids.admin.v2;

import edu.colorado.phet.reids.admin.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 20, 2010
 * Time: 5:11:37 PM
 * To change this template use File | Settings | File Templates.
 */
class TimesheetModel {
    private ArrayList<TimeListener> timeListeners = new ArrayList<TimeListener>();
    private TimeListener timeListenerAdapter = new TimeListener() {
        public void timeChanged() {
            for (TimeListener timeListener : timeListeners) {
                timeListener.timeChanged();
            }
        }
    };
    private ArrayList<ClockedInListener> clockedInListeners = new ArrayList<ClockedInListener>();
    private ClockedInListener clockedInListenerAdapter = new ClockedInListener() {
        public void clockedInChanged() {
            for (ClockedInListener clockedInListener : clockedInListeners) {
                clockedInListener.clockedInChanged();
            }
        }
    };

    public String toTSV() {
        String s = "";
        for (Object elm : TimesheetApp.columnNames) {
            s += elm + ",";
        }
        s = s.substring(0, s.length() - 1) + "\n";
        for (Entry entry : entries) {
            s += entry.toCSV() + "\n";
        }

        return s.substring(0, s.length() - 1);
    }

    public void clearChanges() {
//        sz//todo: change model
    }

    public void loadTSV(String str) {
        clear();
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\n");
        String header = stringTokenizer.nextToken();
        System.out.println("header = " + header);
        while (stringTokenizer.hasMoreTokens()) {
            String line = stringTokenizer.nextToken();
            if (line.trim().length() > 0) {
                Entry e = Entry.parseCSV(line);
                addEntry(e);
            }
        }
    }

    private void clear() {
        while (getEntryCount() > 0) {
            removeEntry(0);
        }
    }

    private void removeEntry(int index) {
        entries.remove(index);
        //todo: notify somebody?
    }

    public void loadTSV(File file) throws IOException {
        loadTSV(FileUtils.loadFileAsString(file));
    }

    public static interface ClockedInListener {
        public void clockedInChanged();
    }

    public void clockOut() {
        getLastEntry().clockOut();
    }

    public long getTotalTimeSeconds() {
        long sum = 0;
        for (Entry entry : entries) {
            sum += entry.getElapsedSeconds();
        }
        return sum;
    }

    public void addTimeListener(TimeListener timeListener) {
        timeListeners.add(timeListener);
    }

    public void addClockedInListener(ClockedInListener clockedInListener) {
        clockedInListeners.add(clockedInListener);
    }

    public static interface ItemAddedListener {
        void itemAdded(Entry entry);
    }

    public static interface TimeListener {
        void timeChanged();
    }

    private ArrayList<ItemAddedListener> itemAddedListeners = new ArrayList<ItemAddedListener>();

    public void addItemAddedListener(ItemAddedListener listener) {
        itemAddedListeners.add(listener);
    }

    private ArrayList<Entry> entries = new ArrayList<Entry>();

    public void addEntry(Entry entry) {
        entries.add(entry);
        entry.addTimeListener(timeListenerAdapter);
        entry.addClockedInListener(clockedInListenerAdapter);
        for (ItemAddedListener itemAddedListener : itemAddedListeners) {
            itemAddedListener.itemAdded(entry);
        }//todo: remove listeners after item is closed?
    }

    public Entry getLastEntry() {
        return entries.get(entries.size() - 1);
    }

    public void startNewEntry(String category) {
        boolean clockedIn = isClockedIn();
        if (getEntryCount() > 0)
            getLastEntry().clockOut();
        Entry e = new Entry(clockedIn ? getLastEntry().getEndSeconds() : Util.currentTimeSeconds(), Util.currentTimeSeconds(), category, "", false, true);
        addEntry(e);
        for (ClockedInListener clockedInListener : clockedInListeners) {
            clockedInListener.clockedInChanged();//todo: could rewrite this so no clocked-out notifications are sent
        }
    }

    public void startNewCategory() {
        startNewEntry("");
    }

    public boolean isClockedIn() {
        return getEntryCount() > 0 && getLastEntry().isRunning();
    }

    private int getEntryCount() {
        return entries.size();
    }

    public void startNewTask() {
        startNewEntry(getEntryCount() > 0 ? getLastEntry().getCategory() : "");
    }
}
