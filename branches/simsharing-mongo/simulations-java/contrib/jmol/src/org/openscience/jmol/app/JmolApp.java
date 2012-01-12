/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-26 23:35:44 -0500 (Fri, 26 Jun 2009) $
 * $Revision: 11131 $
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jmol.app;

import org.jmol.api.*;
import org.jmol.export.history.HistoryFile;
import org.jmol.i18n.GT;
import org.jmol.api.JmolViewer;
import org.jmol.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

public class JmolApp {

  /**
   * The data model.
   */

  public int startupWidth, startupHeight;
  public Point border;
  public boolean haveBorder;

  public File userPropsFile;
  public HistoryFile historyFile;

  public String menuFile;
  
  public boolean splashEnabled = true;
  public boolean useIndependentCommandThread;
  public boolean transparentBackground;
  public boolean checkScriptNoFiles;
  public boolean checkScriptAndOpenFiles;
  public boolean exitUponCompletion;

  public boolean haveConsole = true;
  public boolean haveDisplay = true;
  public boolean isDataOnly;
  public boolean isPrintOnly;
  public boolean isSilent;
  public boolean listCommands;
  
  public String commandOptions = "";
  public String modelFilename;
  public String scriptFilename;
  public String script1 = "";
  public String script;
  public String script2 = "";
  public Point jmolPosition;
  
  public JmolViewer viewer;
  public JmolAdapter modelAdapter;

  public JmolApp() {
    // defer parsing until we can set a few options ourselves
  }
  
  /**
   * standard Jmol application entry point
   * @param args
   */
  public JmolApp(String[] args) {
    
    if (System.getProperty("javawebstart.version") != null) {

      // If the property is found, Jmol is running with Java Web Start. To fix
      // bug 4621090, the security manager is set to null.
      System.setSecurityManager(null);
    }
    if (System.getProperty("user.home") == null) {
      System.err.println(GT
          ._("Error starting Jmol: the property 'user.home' is not defined."));
      System.exit(1);
    }
    File ujmoldir = new File(new File(System.getProperty("user.home")), ".jmol");
    ujmoldir.mkdirs();
    userPropsFile = new File(ujmoldir, "properties");
    historyFile = new HistoryFile(new File(ujmoldir, "history"),
        "Jmol's persistent values");

    
    parseCommandLine(args);
  }

  public void parseCommandLine(String[] args) {

    Options options = getOptions();

    CommandLine line = null;
    try {
      CommandLineParser parser = new PosixParser();
      line = parser.parse(options, args);
    } catch (ParseException exception) {
      System.err.println("Unexpected exception: " + exception.toString());
    }
    
    args = line.getArgs();
    if (args.length > 0) {
      modelFilename = args[0];
    }

    checkOptions(line, options);

  }

  private Options getOptions() {
    Options options = new Options();
    options.addOption("b", "backgroundtransparent", false, GT
        ._("transparent background"));
    options.addOption("h", "help", false, GT._("give this help page"));
    options.addOption("n", "nodisplay", false, GT
        ._("no display (and also exit when done)"));
    options.addOption("c", "check", false, GT
        ._("check script syntax only - no file loading"));
    options.addOption("C", "checkload", false, GT
        ._("check script syntax only - with file loading"));
    options.addOption("d", "debug", false, GT._("debug"));
    options.addOption("i", "silent", false, GT._("silent startup operation"));
    options.addOption("l", "list", false, GT
        ._("list commands during script execution"));
    options.addOption("L", "nosplash", false, GT
        ._("start with no splash screen"));
    options.addOption("o", "noconsole", false, GT
        ._("no console -- all output to sysout"));
    options.addOption("p", "printOnly", false, GT
        ._("send only output from print messages to console (implies -i)"));
    options.addOption("t", "threaded", false, GT
        ._("independent command thread"));
    options.addOption("x", "exit", false, GT
        ._("exit after script (implicit with -n)"));

    OptionBuilder.withLongOpt("script");
    OptionBuilder.withDescription(GT
        ._("script file to execute or '-' for System.in"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("s"));

    OptionBuilder.withLongOpt("multitouch");
    OptionBuilder.withDescription(GT
        ._("use multitouch interface (requires \"sparshui\" parameter"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("M"));


    OptionBuilder.withLongOpt("jmolscript1");
    OptionBuilder.withDescription(GT
        ._("Jmol script to execute BEFORE -s option"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("J"));

    OptionBuilder.withLongOpt("jmolscript2");
    OptionBuilder.withDescription(GT
        ._("Jmol script to execute AFTER -s option"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("j"));

    OptionBuilder.withLongOpt("menu");
    OptionBuilder.withDescription("menu file to use");
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("m"));

    OptionBuilder.withArgName(GT._("property=value"));
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription(GT._("supported options are given below"));
    options.addOption(OptionBuilder.create("D"));

    OptionBuilder.withLongOpt("geometry");
    // OptionBuilder.withDescription(GT._("overall window width x height, e.g. {0}",
    // "-g512x616"));
    OptionBuilder.withDescription(GT._("window width x height, e.g. {0}",
        "-g500x500"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("g"));

    OptionBuilder.withLongOpt("quality");
    // OptionBuilder.withDescription(GT._("overall window width x height, e.g. {0}",
    // "-g512x616"));
    OptionBuilder
        .withDescription(GT
            ._("JPG image quality (1-100; default 75) or PNG image compression (0-9; default 2, maximum compression 9)"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("q"));

    OptionBuilder.withLongOpt("write");
    OptionBuilder.withDescription(GT._("{0} or {1}:filename", new Object[] {
        "CLIP", "GIF|JPG|JPG64|PNG|PPM" }));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("w"));
    return options;
  }
  
  private void checkOptions(CommandLine line, Options options) {
    if (line.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Jmol", options);

      // now report on the -D options
      System.out.println();
      System.out.println(GT._("For example:"));
      System.out.println();
      System.out
          .println("Jmol -ions myscript.spt -w JPEG:myfile.jpg > output.txt");
      System.out.println();
      System.out.println(GT
          ._("The -D options are as follows (defaults in parenthesis) and must be called preceding '-jar Jmol.jar':"));
      System.out.println();
      System.out.println("  cdk.debugging=[true|false] (false)");
      System.out.println("  cdk.debug.stdout=[true|false] (false)");
      System.out.println("  display.speed=[fps|ms] (ms)");
      //System.out.println("  JmolConsole=[true|false] (true)");
      System.out.println("  logger.debug=[true|false] (false)");
      System.out.println("  logger.error=[true|false] (true)");
      System.out.println("  logger.fatal=[true|false] (true)");
      System.out.println("  logger.info=[true|false] (true)");
      System.out.println("  logger.logLevel=[true|false] (false)");
      System.out.println("  logger.warn=[true|false] (true)");
      System.out.println("  plugin.dir (unset)");
      System.out.println("  user.language=[ca|cs|de|en_GB|en_US|es|fr|hu|it|ko|nl|pt_BR|tr|zh_TW] (en_US)");

      System.exit(0);
    }

    // Process more command line arguments
    // these are also passed to viewer

    // debug mode
    if (line.hasOption("d")) {
      Logger.setLogLevel(Logger.LEVEL_DEBUG);
    }

    // note that this is set up so that if JmolApp is 
    // invoked with just new JmolApp(), we can 
    // set options ourselves. 
    
    // print command output only (implies silent)

    commandOptions = (isDataOnly ? "JmolData " : "Jmol ");
    if (line.hasOption("p"))
      isPrintOnly = true;
    if (isPrintOnly) {
      commandOptions += "-p";
      isSilent = true;
    }

    // silent startup
    if (line.hasOption("i"))
      isSilent = true;
    if (isSilent)
      commandOptions += "-i";

    // output to sysout
    if (line.hasOption("o"))
      haveConsole = false;
    if (!haveConsole)
      commandOptions += "-o";

    // transparent background
    if (line.hasOption("b"))
      transparentBackground = true;
    if (transparentBackground)
      commandOptions += "-b";

    // independent command thread
    if (line.hasOption("t"))
      useIndependentCommandThread = true;
    if (useIndependentCommandThread)
      commandOptions += "-t";

    // list commands during script operation
    if (line.hasOption("l"))
      listCommands = true;
    if (listCommands)
      commandOptions += "-l";

    // no splash screen
    if (line.hasOption("L"))
      splashEnabled = false;
    if (!splashEnabled)
      commandOptions += "-L";

    // check script only -- don't open files
    if (line.hasOption("c"))
      checkScriptNoFiles = true;
    else if (line.hasOption("C"))
      checkScriptAndOpenFiles = true;
    
    if (checkScriptNoFiles)
      commandOptions += "-c";
    else if (checkScriptAndOpenFiles)
      commandOptions += "-c";
      
    // menu file
    if (line.hasOption("m")) {
      menuFile = line.getOptionValue("m");
    }

    // run pre Jmol script
    if (line.hasOption("J")) {
      commandOptions += "-J";
      script1 = line.getOptionValue("J");
    }

    // use SparshUI
    if (line.hasOption("M")) {
      commandOptions += "-multitouch-" + line.getOptionValue("M");
    }

    // run script from file
    if (line.hasOption("s")) {
      commandOptions += "-s";
      scriptFilename = line.getOptionValue("s");
    }

    // run post Jmol script
    if (line.hasOption("j")) {
      commandOptions += "-j";
      script2 = line.getOptionValue("j");
    }

    Point b = null;    
    if (haveDisplay) {
      Dimension size;
      String vers = System.getProperty("java.version");
      if (vers.compareTo("1.1.2") < 0) {
        System.out.println("!!!WARNING: Swing components require a "
            + "1.1.2 or higher version VM!!!");
      }

      size = historyFile.getWindowSize("Jmol");
      if (size != null) {
        startupWidth = size.width;
        startupHeight = size.height;
      }
      historyFile.getWindowBorder("Jmol");
      // first one is just approximate, but this is set in doClose()
      // so it will reset properly -- still, not perfect
      // since it is always one step behind.
      if (b == null || b.x > 50)
        border = new Point(12, 116);
      else
        border = new Point(b.x, b.y);
      // note -- the first time this is run after changes it will not work
      // because there is a bootstrap problem.
    }
    int width = 500;
    int height = 500;
    // INNER frame dimensions
    if (line.hasOption("g")) {
      String geometry = line.getOptionValue("g");
      int indexX = geometry.indexOf('x');
      if (indexX > 0) {
        width = Parser.parseInt(geometry.substring(0, indexX));
        height = Parser.parseInt(geometry.substring(indexX + 1));
      } else {
        width = height = Parser.parseInt(geometry);
      }
      startupWidth = -1;
    }

    if (startupWidth <= 0 || startupHeight <= 0) {
      if (haveDisplay) {
        startupWidth = width + border.x;
        startupHeight = height + border.y;
      } else {
        startupWidth = width;
        startupHeight = height;
      }
    }

    // write image to clipboard or image file
    if (line.hasOption("w")) {
      int quality = -1;
      if (line.hasOption("q"))
        quality = Parser.parseInt(line.getOptionValue("q"));
      String type_name = line.getOptionValue("w");
      if (type_name != null) {
        if (type_name.length() == 0)
          type_name = "JPG:jpg";
        if (type_name.indexOf(":") < 0)
          type_name += ":jpg";
        int i = type_name.indexOf(":");
        String type = type_name.substring(0, i).toUpperCase();
        type_name = " \"" + type_name.substring(i + 1) + "\"";
        if (type.indexOf(" ") < 0)
          type += " " + quality;
        script2 += ";write image " + width + " " + height + " " + type
            + type_name;
      }
    }

    // the next two are coupled -- if the -n command line option is 
    // given, then the -x is added, but not vice-versa. 
    // however, if this is an application-embedded object, then
    // it is ok to have no display and no exit.
    
    if (line.hasOption("n")) {
       // no display (and exit)
      haveDisplay = false;
      exitUponCompletion = true;
    }
    if (line.hasOption("x"))
      // exit when script completes (or file is read)
      exitUponCompletion = true;

    if (!haveDisplay)
      commandOptions += "-n";
    if (exitUponCompletion) {
      commandOptions += "-x";
      script2 += ";exitJmol // " + commandOptions;
    }
    
  }

  public void startViewer(JmolViewer viewer, SplashInterface splash) {  
    this.viewer = viewer;
    try {
    } catch (Throwable t) {
      System.out.println("uncaught exception: " + t);
      t.printStackTrace();
    }
    
    // Open a file if one is given as an argument -- note, this CAN be a
    // script file
    if (modelFilename != null) {
      if (script1 == null)
        script1 = "";
      script1 = (modelFilename.endsWith(".spt") ? "script " : "load ") 
          + Escape.escape(modelFilename) + ";" + script1;
    }

    // OK, by now it is time to execute the script

    // then command script
    if (script1 != null && script1.length() > 0) {
      if (!isSilent)
        Logger.info("Executing script: " + script1);
      if (splash != null)
        splash.showStatus(GT._("Executing script 1..."));
      viewer.script(script1);
    }

    // next the file

    if (scriptFilename != null) {
      if (!isSilent)
        Logger.info("Executing script from file: " + scriptFilename);
      if (splash != null)
        splash.showStatus(GT._("Executing script file..."));
      if (scriptFilename.equals("-")) {

        // -s - option

        Scanner scan = new Scanner(System.in);
        String linein = "";
        StringBuffer script = new StringBuffer();
        while (scan.hasNextLine() && (linein = scan.nextLine()) != null
            && !linein.equals("!quit"))
          script.append(linein).append("\n");
        viewer.script(script.toString());
      } else {
        viewer.evalFile(scriptFilename);
      }
    }
    // then command script
    if (script2 != null && script2.length() > 0) {
      if (!isSilent)
        Logger.info("Executing script: " + script2);
      if (splash != null)
        splash.showStatus(GT._("Executing script 2..."));
      viewer.script(script2);
    }    
  }
  
}
