========================= Summary ==========================

AsWing is an Open Source Flash ActionScript GUI framework 
and library that let programmer make their flash application
(or RIA) UI easily, its usage is similar to  Java Swing(JFC).
It provides a set of GUI components, we intent to implement 
it in pure object oriented ActionScript 2, and pluggable look 
and feels will be implemented too. Its also provide many Utils
classes.

More and Newest information can be found at:

  http://www.aswing.org
  
========================= Usage ==========================
This framework is like a ActionScript language Swing.
To create a JFrame:
  var frame:JFrame = new JFrame("A JFrame's Title");
To location and resize and show it:
  frame.setBounds(100, 100, 550, 400);
  frame.show();

To and listeners to a component:
it is a little different from java swing.
In java Swing style:
  frame.addWindowListener(yourListener);
In AsWing will be this style:
  frame.addEventListener(yourListener);//this listener maybe has the handler func
  or
  frame.addEventListener(JWindow.ON_WINDOW_CLOSED, yourHandlerFunc, yourHandler);
  
We hope to provide tutorials on how to use the AsWing
framework, now, you can download tutorials and api document at our website.

=================== Development Environment ====================

To compile and build AsWing you need the MTASC compiler.
This compiler is available for free/open-source from:

  http://www.mtasc.org
  
To debug/log you need a logger tool.
There are many open source loggers you can found at OSFlash

  http://www.osflash.org

And we suggest you edit as files by Eclipse and 
ASDT Plugin, they are free at:

  http://www.eclipse.org
  http://sourceforge.net/projects/aseclipseplugin/

--- Compiling --- 

To compile with AsWing you just need to include the aswing classpath.
There are tutorials of compile AsWing at AsWing Home Page.

AsWing can just be compiled by MTASC well.

=========================== License ===============================

AsWing is licensed under a BSD-style license.The license can be found
in the LICENSE.txt file.
