/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.util;


/**
 * Logger mechanism.
 */
public final class Logger {

  private static LoggerInterface _logger = new DefaultLogger();

  public final static int LEVEL_FATAL = 1;
  public final static int LEVEL_ERROR = 2;
  public final static int LEVEL_WARN = 3;
  public final static int LEVEL_INFO = 4;
  public final static int LEVEL_DEBUG = 5;
  public final static int LEVEL_DEBUGHIGH = 6;
  public final static int LEVEL_MAX = 7;
  
  private final static boolean[] _activeLevels = new boolean[LEVEL_MAX];
  private       static boolean   _logLevel = false;
  public static boolean debugging;
  public static boolean debuggingHigh;
  
  static {
    _activeLevels[LEVEL_DEBUGHIGH] = getProperty("debugHigh",    false);
    _activeLevels[LEVEL_DEBUG] = getProperty("debug",    false);
    _activeLevels[LEVEL_INFO]  = getProperty("info",     true);
    _activeLevels[LEVEL_WARN]  = getProperty("warn",     true);
    _activeLevels[LEVEL_ERROR] = getProperty("error",    true);
    _activeLevels[LEVEL_FATAL] = getProperty("fatal",    true);
    _logLevel                  = getProperty("logLevel", false);
    debugging = (_logger != null && (_activeLevels[LEVEL_DEBUG] || _activeLevels[LEVEL_DEBUGHIGH]));
    debuggingHigh = (debugging && _activeLevels[LEVEL_DEBUGHIGH]);
  }

  private static boolean getProperty(String level, boolean defaultValue) {
    try {
      String property = System.getProperty("jmol.logger." + level);
      if (property != null) {
        return Boolean.TRUE.equals(Boolean.valueOf(property));
      }
    } catch (Exception e) {
      // applet can't do this.
    }
    return defaultValue;
  }

  /**
   * Replaces the current logger implementation by a new one.
   * 
   * @param logger New logger implementation.
   */
  public static void setLogger(LoggerInterface logger) {
    _logger = logger;
    debugging = isActiveLevel(LEVEL_DEBUG) || isActiveLevel(LEVEL_DEBUGHIGH);
  }

  /**
   * Tells if a logging level is active.
   * 
   * @param level Logging level.
   * @return Active.
   */
  public static boolean isActiveLevel(int level) {
    return _logger != null && level >= 0 && level < LEVEL_MAX 
        && _activeLevels[level];
  }

  /**
   * Changes the activation state for a logging level.
   * 
   * @param level Level.
   * @param active New activation state.
   */
  public static void setActiveLevel(int level, boolean active) {
    if (level < 0)
      level = 0;
    if (level >= LEVEL_MAX)
      level = LEVEL_MAX - 1;
    _activeLevels[level] = active;
    debugging = isActiveLevel(LEVEL_DEBUG) || isActiveLevel(LEVEL_DEBUGHIGH);
  }

  /**
   * Activates all logging levels up through a given level.
   * 
   * @param level
   */
  public static void setLogLevel(int level) {
    for (int i = LEVEL_MAX; --i >= 0;)
      setActiveLevel(i, i <= level);
  }

  /**
   * Returns the text corresponding to a level.
   * 
   * @param level Level.
   * @return Corresponding text.
   */
  public static String getLevel(int level) {
    switch (level) {
    case LEVEL_DEBUGHIGH:
      return "DEBUGHIGH";
    case LEVEL_DEBUG:
      return "DEBUG";
    case LEVEL_INFO:
      return "INFO";
    case LEVEL_WARN:
      return "WARN";
    case LEVEL_ERROR:
      return "ERROR";
    case LEVEL_FATAL:
      return "FATAL";
    }
    return "????";
  }

  /**
   * Indicates if the level is logged.
   * 
   * @return Indicator.
   */
  public static boolean logLevel() {
    return _logLevel;
  }

  /**
   * Indicates if the level is logged.
   * 
   * @param log Indicator.
   */
  public static void logLevel(boolean log) {
    _logLevel = log;
  }

  /**
   * Writes a log at DEBUG level.
   * 
   * @param txt String to write.
   */
  public static void debug(String txt) {
    if (!debugging)
      return;
    try {
      _logger.debug(txt);
    } catch (Throwable t) {
      //
    }
  }

  /**
   og* Writes a log at INFO level.
   * 
   * @param txt String to write.
   */
  public static void info(String txt) {
    try {
      if (isActiveLevel(LEVEL_INFO)) {
        _logger.info(txt);
      }
    } catch (Throwable t) {
      //
    }
  }

  /**
   * Writes a log at WARN level.
   * 
   * @param txt String to write.
   */
  public static void warn(String txt) {
    try {
      if (isActiveLevel(LEVEL_WARN)) {
        _logger.warn(txt);
      }
    } catch (Throwable t) {
      //
    }
  }

  /**
   * Writes a log at WARN level with detail on exception.
   * 
   * @param txt String to write.
   * @param e Exception.
   */
  public static void warn(String txt, Throwable e) {
    try {
      if (isActiveLevel(LEVEL_WARN)) {
        _logger.warn(txt, e);
      }
    } catch (Throwable t) {
      //
    }
  }

  /**
   * Writes a log at ERROR level.
   * 
   * @param txt String to write.
   */
  public static void error(String txt) {
    try {
      if (isActiveLevel(LEVEL_ERROR)) {
        _logger.error(txt);
      }
    } catch (Throwable t) {
      //
    }
  }

  /**
   * Writes a log at ERROR level with detail on exception.
   * 
   * @param txt String to write.
   * @param e Exception.
   */
  public static void error(String txt, Throwable e) {
    try {
      if (isActiveLevel(LEVEL_ERROR)) {
        _logger.error(txt, e);
      }
    } catch (Throwable t) {
      //
    }
  }

  public static int getLogLevel() {
    for (int i = LEVEL_MAX; --i >= 0;)
      if (isActiveLevel(i))
        return i;
    return 0;
  }

  /**
   * Writes a log at FATAL level.
   * 
   * @param txt String to write.
   */
  public static void fatal(String txt) {
    try {
      if (isActiveLevel(LEVEL_FATAL)) {
        _logger.fatal(txt);
      }
    } catch (Throwable t) {
      //
    }
  }

  /**
   * Writes a log at FATAL level with detail on exception.
   * 
   * @param txt String to write.
   * @param e Exception.
   */
  public static void fatal(String txt, Throwable e) {
    try {
      if (isActiveLevel(LEVEL_FATAL)) {
        _logger.fatal(txt, e);
      }
    } catch (Throwable t) {
      //
    }
  }

  static long startTime;
  public static void startTimer() {
    startTime = System.currentTimeMillis();  
  }

  public static long checkTimer(String msg) {
    long time = System.currentTimeMillis() - startTime;
    if (msg != null)
      info(msg + ": " + (time) + " ms");
    return time;
  }
  
  public static void checkMemory() {
    Runtime runtime = Runtime.getRuntime();
    runtime.gc();
    long bTotal = runtime.totalMemory();
    long bFree = runtime.freeMemory();
    long bMax = 0;
    try {
      bMax = runtime.maxMemory();
    } catch (Exception e) {
    }
    info("Memory: Total-Free="+ (bTotal - bFree)+"; Total=" +  bTotal + "; Free=" + bFree 
        + "; Max=" + bMax);
  }
  
}
