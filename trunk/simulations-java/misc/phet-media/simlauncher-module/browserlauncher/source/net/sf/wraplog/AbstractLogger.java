//Copyright (c) 2005, Thomas Aglassinger
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//notice, this list of conditions and the following disclaimer in the
//documentation and/or other materials provided with the distribution.
//
// * Neither the name of the author nor the names of its contributors
//may be used to endorse or promote products derived from this software
//without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
//IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
//PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
//PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.wraplog;

/**
 * Abstract base class to write messages about interesting things happening to a
 * log.
 * <p>
 * Updated to WrapLog version 1.1.
 *
 * @author Thomas Aglassinger
 */
public abstract class AbstractLogger {

    private int level = Level.DEBUG;

    private int loggedMessageCount;

    protected void checkLevel(int logLevel, String name) {
        String actualName;
        if (name == null) {
            actualName = "level";
        }
        else {
            actualName = name;
        }
        if ((logLevel < Level.DEBUG) || (logLevel > Level.ERROR)) {
            throw new IllegalArgumentException(actualName
                                               +
                    " must be one of: Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR");
        }
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable error) {
        log(Level.DEBUG, message, error);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable error) {
        log(Level.ERROR, message, error);
    }

    public int getLevel() {
        return level;
    }

    /** Count of how many messages have been logged. */
    public int getLoggedMessageCount() {
        return loggedMessageCount;
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable error) {
        log(Level.INFO, message, error);
    }

    public boolean isEnabled(int logLevel) {
        checkLevel(level, null);
        return logLevel >= level;
    }

    /**
     * Logs a message and optional error details.
     *
     * @param logLevel one of: Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR
     * @param message the actual message; this will never be <code>null</code>
     * @param error an error that is related to the message; unless <code>null</code>, the name and stack trace of the error are logged
     */
    protected abstract void reallyLog(int logLevel, String message,
                                      Throwable error)
            throws Exception;

    /**
     * Provided that <code>getLevel()</code> accepts it, log
     * <code>message</code>. Otherwise, do nothing.
     */
    public void log(int logLevel, String message) {
        log(logLevel, message, null);
    }

    /**
     * Provided that <code>getLevel()</code> accepts it, log
     * <code>message</code> and <code>error</code>. Otherwise, do nothing.
     */
    public void log(int logLevel, String message, Throwable error) {
        if (isEnabled(logLevel)) {
            try {
                reallyLog(logLevel, message, error);
                loggedMessageCount += 1;
            }
            catch (Exception error2) {
                throw new LoggingException("cannot log message: " + message,
                                           error2);
            }
        }
    }

    public void setLevel(int newLevel) {
        if ((level >= Level.DEBUG) || (level <= Level.ERROR)) {
            level = newLevel;
        }
        else {
            throw new IllegalArgumentException(
                    "newLevel must be one of: Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR");
        }
    }

    public void warn(String message) {
        warn(message, null);
    }

    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    public void warn(String message, Throwable error) {
        log(Level.WARN, message, error);
    }
}
