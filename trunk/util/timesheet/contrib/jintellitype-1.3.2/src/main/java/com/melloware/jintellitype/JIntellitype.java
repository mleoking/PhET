/**
 * JIntellitype
 * -----------------
 * Copyright 2005-2008 Emil A. Lefkof III, Melloware Inc.
 *
 * I always give it my best shot to make a program useful and solid, but
 * remeber that there is absolutely no warranty for using this program as
 * stated in the following terms:
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.melloware.jintellitype;

import java.awt.event.InputEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

/**
 * JIntellitype A Java Implementation for using the Windows API Intellitype
 * commands and the RegisterHotKey and UnRegisterHotkey API calls for globally
 * responding to key events. Intellitype are commands that are using for Play,
 * Stop, Next on Media keyboards or some laptops that have those special keys.
 * <p>
 * JIntellitype class that is used to call Windows API calls using the
 * JIntellitype.dll.
 * <p>
 * This file comes with native code in JINTELLITYPE.DLL The DLL should go in
 * C:/WINDOWS/SYSTEM or in your current directory
 * <p>
 * <p>
 * Copyright (c) 1999-2008
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 1.3.1
 */
public final class JIntellitype implements JIntellitypeConstants {

   /**
    * Static variable to hold singleton.
    */
   private static JIntellitype jintellitype = null;

   /**
    * Static variable for double checked thread safety.
    */
   private static boolean isInitialized = false;
   
   /**
    * Static variable to hold the libary location if set
    */
   private static String libraryLocation = null;

   

   /**
    * Listeners collection for Hotkey events
    */
   private final List<HotkeyListener> hotkeyListeners = Collections
            .synchronizedList(new CopyOnWriteArrayList<HotkeyListener>());

   /**
    * Listeners collection for Hotkey events
    */
   private final List<IntellitypeListener> intellitypeListeners = Collections
            .synchronizedList(new CopyOnWriteArrayList<IntellitypeListener>());

   /**
    * Handler is used by JNI code to keep different JVM instances separate
    */
   @SuppressWarnings("unused")
   private int handler = 0;

   /**
    * Private Constructor to prevent instantiation. Initialize the library for
    * calling.
    */
   private JIntellitype() {
      // Load JNI library
      try {
         if (getLibraryLocation() != null) {
            System.load(getLibraryLocation());
         } else {
            System.loadLibrary("JIntellitype");
         }
         
         initializeLibrary();
      } catch (UnsatisfiedLinkError ex) {
         throw new JIntellitypeException(ex);
      } catch (RuntimeException ex) {
         throw new JIntellitypeException(ex);
      }
   }

   /**
    * Gets the singleton instance of the JIntellitype object.
    * <p>
    * But the possibility of creation of more instance is only before the
    * instance is created. Since all code defined inside getInstance method is
    * in the synchronized block, even the subsequent requests will also come and
    * wait in the synchronized block. This is a performance issue. The same can
    * be solved using double-checked lock. Following is the implementation of
    * Singleton with lazy initialization and double-checked lock.
    * <p>
    * @return an instance of JIntellitype class
    */
   public static JIntellitype getInstance() {
      if (!isInitialized) {
         synchronized (JIntellitype.class) {
            if (!isInitialized) {
               jintellitype = new JIntellitype();
               isInitialized = true;
            }
         }
      }
      return jintellitype;
   }

   /**
    * Adds a listener for hotkeys.
    * <p>
    * @param listener the HotKeyListener to be added
    */
   public void addHotKeyListener(HotkeyListener listener) {
      hotkeyListeners.add(listener);
   }

   /**
    * Adds a listener for intellitype commands.
    * <p>
    * @param listener the IntellitypeListener to be added
    */
   public void addIntellitypeListener(IntellitypeListener listener) {
      intellitypeListeners.add(listener);
   }

   /**
    * Cleans up all resources used by JIntellitype.
    */
   public void cleanUp() {
      try {
         terminate();
      } catch (UnsatisfiedLinkError ex) {
         throw new JIntellitypeException(ERROR_MESSAGE, ex);
      } catch (RuntimeException ex) {
         throw new JIntellitypeException(ex);
      }
   }

   /**
    * Registers a Hotkey with windows. This combination will be responded to by
    * all registered HotKeyListeners. Uses the JIntellitypeConstants for MOD,
    * ALT, CTRL, and WINDOWS keys.
    * <p>
    * @param identifier a unique identifier for this key combination
    * @param modifier MOD_SHIFT, MOD_ALT, MOD_CONTROL, MOD_WIN from
    *           JIntellitypeConstants, or 0 if no modifier needed
    * @param keycode the key to respond to in Ascii integer, 65 for A
    */
   public void registerHotKey(int identifier, int modifier, int keycode) {
      try {
         int modifiers = swingToIntelliType(modifier);
         if (modifiers == 0) {
            modifiers = modifier;
         }
         regHotKey(identifier, modifier, keycode);
      } catch (UnsatisfiedLinkError ex) {
         throw new JIntellitypeException(ERROR_MESSAGE, ex);
      } catch (RuntimeException ex) {
         throw new JIntellitypeException(ex);
      }
   }

   /**
    * Registers a Hotkey with windows. This combination will be responded to by
    * all registered HotKeyListeners. Use the Swing InputEvent constants from
    * java.awt.InputEvent.
    * <p>
    * @param identifier a unique identifier for this key combination
    * @param modifier InputEvent.SHIFT_MASK, InputEvent.ALT_MASK,
    *           InputEvent.CTRL_MASK, or 0 if no modifier needed
    * @param keycode the key to respond to in Ascii integer, 65 for A
    */
   public void registerSwingHotKey(int identifier, int modifier, int keycode) {
      try {
         regHotKey(identifier, swingToIntelliType(modifier), keycode);
      } catch (UnsatisfiedLinkError ex) {
         throw new JIntellitypeException(ERROR_MESSAGE, ex);
      } catch (RuntimeException ex) {
         throw new JIntellitypeException(ex);
      }
   }

   /**
    * Registers a Hotkey with windows. This combination will be responded to by
    * all registered HotKeyListeners. Use the identifiers CTRL, SHIFT, ALT
    * and/or WIN.
    * <p>
    * @param identifier a unique identifier for this key combination
    * @param modifierAndKeyCode String with modifiers separated by + and keycode
    *           (e.g. CTRL+SHIFT+A)
    * @see #registerHotKey(int, int, int)
    * @see #registerSwingHotKey(int, int, int)
    */
   public void registerHotKey(int identifier, String modifierAndKeyCode) {
      String[] split = modifierAndKeyCode.split("\\+");
      int mask = 0;
      for (int i = 0; i < split.length - 1; i++) {
         if ("ALT".equalsIgnoreCase(split[i])) {
            mask += JIntellitype.MOD_ALT;
         } else if ("CTRL".equalsIgnoreCase(split[i]) || "CONTROL".equalsIgnoreCase(split[i])) {
            mask += JIntellitype.MOD_CONTROL;
         } else if ("SHIFT".equalsIgnoreCase(split[i])) {
            mask += JIntellitype.MOD_SHIFT;
         } else if ("WIN".equalsIgnoreCase(split[i])) {
            mask += JIntellitype.MOD_WIN;
         }
      }
      registerHotKey(identifier, mask, split[split.length - 1].charAt(0));
   }

   /**
    * Removes a listener for hotkeys.
    */
   public void removeHotKeyListener(HotkeyListener listener) {
      hotkeyListeners.remove(listener);
   }

   /**
    * Removes a listener for intellitype commands.
    */
   public void removeIntellitypeListener(IntellitypeListener listener) {
      intellitypeListeners.remove(listener);
   }

   /**
    * Unregisters a previously registered Hotkey identified by its unique
    * identifier.
    * <p>
    * @param identifier the unique identifer of this Hotkey
    */
   public void unregisterHotKey(int identifier) {
      try {
         unregHotKey(identifier);
      } catch (UnsatisfiedLinkError ex) {
         throw new JIntellitypeException(ERROR_MESSAGE, ex);
      } catch (RuntimeException ex) {
         throw new JIntellitypeException(ex);
      }
   }

   /**
    * Checks to see if this application is already running.
    * <p>
    * @param appTitle the name of the application to check for
    * @return true if running, false if not running
    */
   public static boolean checkInstanceAlreadyRunning(String appTitle) {
      return getInstance().isRunning(appTitle);
   }

   /**
    * Checks to make sure the OS is a Windows flavor and that the JIntellitype
    * DLL is found in the path and the JDK is 32 bit not 64 bit. The DLL
    * currently only supports 32 bit JDK.
    * <p>
    * @return true if Jintellitype may be used, false if not
    */
   public static boolean isJIntellitypeSupported() {
      boolean result = false;
      String os = "none";
      String architecture = "none";


      try {
         os = System.getProperty("os.name").toLowerCase();
         architecture = System.getProperty("sun.arch.data.model").toLowerCase();
      } catch (SecurityException ex) {
         // we are not allowed to look at this property
         System.err.println("Caught a SecurityException reading the system property "
                  + "'os.name'; the SystemUtils property value will default to null.");
      }

      // only works on 32 bit Windows OS currently
      if ((os.startsWith("windows")) && (architecture.equalsIgnoreCase("32"))) {
         // try an get the instance and if it succeeds then return true
         try {
            getInstance();
            result = true;
         } catch (Exception e) {
            result = false;
         }
      }

      return result;
   }

   /**
    * Gets the libraryLocation.
    * <p>
    * @return Returns the libraryLocation.
    */
   public static String getLibraryLocation() {
      return libraryLocation;
   }

   /**
    * Sets the libraryLocation.
    * <p>
    * @param libraryLocation The libraryLocation to set.
    */
   public static void setLibraryLocation(String libraryLocation) {
      final File dll = new File(libraryLocation);
      if (!dll.isAbsolute()) {
         JIntellitype.libraryLocation = dll.getAbsolutePath();
      } else {
         // absolute path, no further calculation needed
         JIntellitype.libraryLocation = libraryLocation;
      }
   }
   
   /**
    * Notifies all listeners that Hotkey was pressed.
    * <p>
    * @param identifier the unique identifier received
    */
   protected void onHotKey(final int identifier) {
      for (final HotkeyListener hotkeyListener : hotkeyListeners) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               hotkeyListener.onHotKey(identifier);
            }
         });
      }
   }

   /**
    * Notifies all listeners that Intellitype command was received.
    * <p>
    * @param command the unique WM_APPCOMMAND received
    */
   protected void onIntellitype(final int command) {
      for (final IntellitypeListener intellitypeListener : intellitypeListeners) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               intellitypeListener.onIntellitype(command);
            }
         });
      }
   }

   /**
    * Swing modifier value to Jintellipad conversion. If no conversion needed
    * just return the original value. This lets users pass either the original
    * JIntellitype constants or Swing InputEvent constants.
    * <p>
    * @param swingKeystrokeModifier the Swing KeystrokeModifier to check
    * @return Jintellitype the JIntellitype modifier value
    */
   protected static int swingToIntelliType(int swingKeystrokeModifier) {
      int mask = 0;
      if ((swingKeystrokeModifier & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
         mask += JIntellitype.MOD_SHIFT;
      }
      if ((swingKeystrokeModifier & InputEvent.ALT_MASK) == InputEvent.ALT_MASK) {
         mask += JIntellitype.MOD_ALT;
      }
      if ((swingKeystrokeModifier & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
         mask += JIntellitype.MOD_CONTROL;
      }
      if ((swingKeystrokeModifier & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
         mask += JIntellitype.MOD_SHIFT;
      }
      if ((swingKeystrokeModifier & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK) {
         mask += JIntellitype.MOD_ALT;
      }
      if ((swingKeystrokeModifier & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
         mask += JIntellitype.MOD_CONTROL;
      }
      return mask;
   }

   private synchronized native void initializeLibrary() throws UnsatisfiedLinkError;

   private synchronized native void regHotKey(int identifier, int modifier, int keycode) throws UnsatisfiedLinkError;

   private synchronized native void terminate() throws UnsatisfiedLinkError;

   private synchronized native void unregHotKey(int identifier) throws UnsatisfiedLinkError;

   /**
    * Checks if there's an instance with hidden window title = appName running
    * Can be used to detect that another instance of your app is already running
    * (so exit..)
    * <p>
    * @param appName = the title of the hidden window to search for
    */
   private synchronized native boolean isRunning(String appName);
}