/************************************************
    Copyright 2005 Jeff Chapman

    This file is part of BrowserLauncher2.

    BrowserLauncher2 is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BrowserLauncher2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BrowserLauncher2; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id: TestAppLogger.java,v 1.2 2005/10/28 18:53:24 jchapman0 Exp $
package edu.stanford.ejalbert.testing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

import net.sf.wraplog.AbstractLogger;
import net.sf.wraplog.Level;

class TestAppLogger
        extends AbstractLogger {
    private JTextArea debugTextArea; // in ctor
    private String[] levelText; // in ctor
    private SimpleDateFormat format; // in ctor =

    public TestAppLogger(JTextArea debugTextArea,
                         String[] levelLabels,
                         String dateFormat) {
        super();
        this.debugTextArea = debugTextArea;
        this.levelText = levelLabels;
        this.format = new SimpleDateFormat(dateFormat);
    }

    /**
     * Logs a message and optional error details.
     *
     * @param logLevel one of: Level.DEBUG, Level.INFO, Level.WARN,
     *   Level.ERROR
     * @param message the actual message; this will never be
     *   <code>null</code>
     * @param error an error that is related to the message; unless
     *   <code>null</code>, the name and stack trace of the error are logged
     * @throws Exception
     * @todo Implement this net.sf.wraplog.AbstractLogger method
     */
    protected void reallyLog(int logLevel,
                             String message,
                             Throwable error)
            throws Exception {
        if (message == null) {
            message = "null";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        String threadName = Thread.currentThread().getName();
        String dateAndTime = format.format(new Date());
        printWriter.println(dateAndTime + " [" + threadName + "] "
                            + getLevelText(logLevel) + " " + message);
        if (error != null) {
            error.printStackTrace(printWriter);
        }
        printWriter.println();
        printWriter.close();
        debugTextArea.append(stringWriter.toString());
    }

    public String getLevelText() {
        return getLevelText(getLevel());
    }

    public String[] getLevelOptions() {
        return levelText;
    }

    /**
     * Return text that represents <code>logLevel</code>.
     */
    private String getLevelText(int logLevel) {
        if (logLevel < Level.DEBUG || logLevel > Level.ERROR) {
            throw new IllegalArgumentException(
                    "logLevel must be one of those defined in net.sf.warplog.Level, but is "
                    + logLevel);
        }
        else {
            return levelText[logLevel];
        }
    }
}
