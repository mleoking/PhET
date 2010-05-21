package edu.colorado.phet.reids.admin.v2;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.reids.admin.jintellitype.JIntellitypeSupport;
import edu.colorado.phet.reids.admin.util.FileUtils;
import edu.colorado.phet.reids.admin.util.FrameSetup;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

public class TimesheetApp {
    public static Object[] columnNames = {"Start", "End", "Elapsed", "Category", "Notes", "Report"};
    private JFrame frame = new JFrame("Timesheet App");
    private TimesheetModel timesheetModel = new TimesheetModel();
    private ArrayList recentFiles = new ArrayList();
    private File currentFile;
    private String WINDOW_HEIGHT = "window.h";
    private String WINDOW_WIDTH = "window.w";
    private String WINDOW_Y = "window.y";
    private String WINDOW_X = "window.x";
    private String RECENT_FILES = "recentFiles";
    private String CURRENT_FILE = "currentFile";
    private JMenu fileMenu = new JMenu("File");
    private File PREFERENCES_FILE = new File(System.getProperty("user.home", "."), ".timesheet/timesheet-app.properties");

    private void updateIconImage() throws IOException {
        BufferedImage image = new PhetResources("timesheet").getImage((timesheetModel.isClockedIn() ? "x-office-running.png" : "x-office-calendar.png"));
        System.out.println("image = " + image);
        frame.setIconImage(image);
    }

    public TimesheetApp() throws IOException {
        JIntellitypeSupport.init(new Runnable() {
            public void run() {
                System.out.println("Clocking in for Work");
                timesheetModel.startNewTask();
            }
        }, new Runnable() {
            public void run() {
                System.out.println("Clocking out for Home");
                timesheetModel.clockOut();
                try {
                    save();
                }
                catch (IOException e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });

        updateIconImage();
        timesheetModel.addClockedInListener(new TimesheetModel.ClockedInListener() {
            public void clockedInChanged() {
                try {
                    updateIconImage();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        final DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{}, columnNames) {
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) return Date.class;
                if (columnIndex == 2) return Long.class;
                if (columnIndex == 3) return String.class;
                if (columnIndex == 4) return String.class;
                if (columnIndex == 5) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };

        timesheetModel.addItemAddedListener(new TimesheetModel.ItemAddedListener() {
            public void itemAdded(final Entry entry) {
                tableModel.addRow(toRow(entry));
                final int rowCount = tableModel.getRowCount();
                entry.addTimeListener(new TimesheetModel.TimeListener() {
                    public void timeChanged() {
                        //update end and elapsed time
                        tableModel.setValueAt(entry.getEndDate(), rowCount - 1, 1);
                        tableModel.setValueAt(entry.getElapsedSeconds(), rowCount - 1, 2);
                    }
                });
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    try {
                        save();
                        return true;
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        });

        tableModel.setColumnIdentifiers(columnNames);

        final JTable table = new JTable(tableModel);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(400);
        table.getColumnModel().getColumn(5).setPreferredWidth(10);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            public final DateFormat DISPLAY_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm:ss a");

            protected void setValue(Object value) {
                setText(DISPLAY_FORMAT.format(value));
            }
        });
        table.setDefaultRenderer(Long.class, new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                long v = ((Long) value).longValue();
                String text = Util.secondsToElapsedTimeString(v);
                super.setValue(text);
            }
        });
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {
                    System.out.println("table.row() = " + table.getSelectedRow() + ", col = " + table.getSelectedColumn());
                    tableModel.setValueAt(new Long(System.currentTimeMillis()), table.getSelectedRow(), table.getSelectedColumn());
                }
            }
        });
        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    Object data = table.getModel().getValueAt(row, column);
                    if (column == 0) getEntry(row).setStartTime(parseStartTime(data.toString()));
                    if (column == 1) getEntry(row).setEndTime(parseStartTime(data.toString()));
                    if (column == 3) getEntry(row).setCategory(data.toString());
                    if (column == 4) getEntry(row).setNotes(data.toString());
                    if (column == 5) getEntry(row).setReport((Boolean) data);
                }
            }
        });
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        final JScrollPane scrollPane = new JScrollPane(table);
        timesheetModel.addItemAddedListener(new TimesheetModel.ItemAddedListener() {
            public void itemAdded(Entry entry) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                    }
                });
            }
        });
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(new ControlPanel(timesheetModel, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    save();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }), BorderLayout.SOUTH);
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        table.setDefaultRenderer(Date.class, renderer);
    }

    private long parseStartTime(String s) {
        try {
            return Entry.LOAD_FORMAT.parse(s).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return 0;
        }
    }

    private Entry getEntry(int row) {
        return timesheetModel.getEntry(row);
    }

    private Object[] toRow(Entry entry) {
        return new Object[]{entry.getStartDate(), entry.getEndDate(), entry.getElapsedSeconds(), entry.getCategory(), entry.getNotes(), entry.isReport()};
    }

    static class ControlPanel extends JPanel {
        private JLabel totalTime;
        private TimesheetModel timesheetModel;

        ControlPanel(final TimesheetModel timesheetModel, final ActionListener saveAction) {
            this.timesheetModel = timesheetModel;
            JButton clockIn = new JButton("Clock In");
            clockIn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timesheetModel.startNewTask();
                }
            });
            add(clockIn);
//            JButton newTask = new JButton("New Task");
//            newTask.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    timesheetModel.startNewTask();
//                }
//            });
//            add(newTask);

            JButton clockOut = new JButton("Clock Out");
            clockOut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timesheetModel.clockOut();
                }
            });
            add(clockOut);

            JButton monthlyReport = new JButton("Monthly Report");
            monthlyReport.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            });
            add(monthlyReport);

            final JButton save = new JButton("Save");
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveAction.actionPerformed(e);
                }
            });
            add(save);

            totalTime = new JLabel();
            add(totalTime);
            updateTimeReadout();
            timesheetModel.addTimeListener(new TimesheetModel.TimeListener() {
                public void timeChanged() {
                    updateTimeReadout();
                }
            });
        }

        private void updateTimeReadout() {
            totalTime.setText("Time: " + Util.secondsToElapsedTimeString(timesheetModel.getTotalTimeSeconds()));
        }
    }

    public void load() throws IOException {
        final JFileChooser jFileChooser = currentFile == null ? new JFileChooser() : new JFileChooser(currentFile.getParentFile());
        int option = jFileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = jFileChooser.getSelectedFile();
            load(selectedFile);
        } else {
            System.out.println("TimesheetApp.load, didn't save");
        }

    }

    private void load(File selectedFile) throws IOException {
        currentFile = selectedFile;
        timesheetModel.loadTSV(currentFile);
//        addCurrentToRecent();
        frame.setTitle("Timesheet: " + selectedFile.getName() + " [" + selectedFile.getAbsolutePath() + "]");
        timesheetModel.clearChanges();
    }


    public void saveAs() throws IOException {
        File selected = selectSaveFile();
        if (selected != null) {
            save(selected);
        } else {

            System.out.println("Didn't save");
        }
    }

    public void save() throws IOException {
        File selected = currentFile;
        if (currentFile == null) {
            selected = selectSaveFile();
        }
        if (selected != null) {
            save(selected);
        } else {
            System.out.println("didn't save");
        }
    }

    private File selectSaveFile() {
        File selected = null;
        final JFileChooser chooser = currentFile == null ? new JFileChooser() : new JFileChooser(currentFile.getParentFile());
        int val = chooser.showSaveDialog(frame);
        if (val == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.exists()) {
                int option = JOptionPane.showConfirmDialog(frame, "File exists, overwrite?");
                if (option == JOptionPane.YES_OPTION) {
                    selected = file;
                }
            } else {
                selected = file;
            }
        }
        return selected;
    }

    private void save(File selected) throws IOException {
        this.currentFile = selected;
//        addCurrentToRecent();
        currentFile.getParentFile().mkdirs();
        String s = timesheetModel.toTSV();
        FileUtils.writeString(currentFile, s);
        System.out.println("Saved to: " + currentFile.getAbsolutePath());
        timesheetModel.clearChanges();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TimesheetApp().start();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    private void savePreferences() throws IOException {
//        System.out.println( "prefFile.getAbsolutePath() = " + prefFile.getAbsolutePath() );
        Properties properties = new Properties();
        properties.put(WINDOW_X, frame.getX() + "");
        properties.put(WINDOW_Y, frame.getY() + "");
        properties.put(WINDOW_WIDTH, frame.getWidth() + "");
        properties.put(WINDOW_HEIGHT, frame.getHeight() + "");

        properties.put(RECENT_FILES, getRecentFileListString());
        properties.put(CURRENT_FILE, currentFile == null ? "null" : currentFile.getAbsolutePath());

        PREFERENCES_FILE.getParentFile().mkdirs();
        properties.store(new FileOutputStream(PREFERENCES_FILE), "auto-generated on " + new Date());
        System.out.println("Stored prefs: " + properties);
    }

    private String getRecentFileListString() {
        String s = "";
        for (int i = 0; i < recentFiles.size(); i++) {
            File file = (File) recentFiles.get(i);
            s += file.getAbsolutePath();
            if (i < recentFiles.size() - 1) {
                s += ",";
            }
        }
        return s;
    }

    private void loadPreferences() throws IOException {

        if (!PREFERENCES_FILE.exists()) {
            savePreferences();
        }
        Properties p = new Properties();
        p.load(new FileInputStream(PREFERENCES_FILE));
        Rectangle r = new Rectangle();
        r.x = Integer.parseInt(p.getProperty(WINDOW_X, "100"));
        r.y = Integer.parseInt(p.getProperty(WINDOW_Y, "100"));
        r.width = Integer.parseInt(p.getProperty(WINDOW_WIDTH, "800"));
        r.height = Integer.parseInt(p.getProperty(WINDOW_HEIGHT, "600"));
        frame.setSize(r.width, r.height);
        frame.setLocation(r.x, r.y);


        String recentFiles = p.getProperty(RECENT_FILES, "");
        System.out.println("Loaded prefs from " + PREFERENCES_FILE.getAbsolutePath() + ", r=" + r + ", recent=" + recentFiles);
        StringTokenizer stringTokenizer = new StringTokenizer(recentFiles, ",");
        this.recentFiles.clear();
        while (stringTokenizer.hasMoreTokens()) {
            final File file = new File(stringTokenizer.nextToken());
            if (!this.recentFiles.contains(file)) {
                this.recentFiles.add(file);
            }
        }
//        updateMenuWithRecent();
        String currentFile = p.getProperty(CURRENT_FILE, "");
        System.out.println("currentFile = " + currentFile);
        if (new File(currentFile).exists()) {
            load(new File(currentFile));
        }
    }

    private void start() throws IOException {
        frame.pack();
        new FrameSetup.CenteredWithInsets(200, 200).initialize(frame);
        loadPreferences();
        frame.setVisible(true);
    }
}