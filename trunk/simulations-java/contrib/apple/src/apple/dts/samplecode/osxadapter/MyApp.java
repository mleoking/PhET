/*

File: MyApp.java

Abstract: Simple Swing app demonstrating how to use the Apple EAWT 
    APIs by way of reflection, allowing a single codebase to build and run
    on platforms without those APIs installed.

Version: 2.0

Disclaimer: IMPORTANT:  This Apple software is supplied to you by 
Apple Inc. ("Apple") in consideration of your agreement to the
following terms, and your use, installation, modification or
redistribution of this Apple software constitutes acceptance of these
terms.  If you do not agree with these terms, please do not use,
install, modify or redistribute this Apple software.

In consideration of your agreement to abide by the following terms, and
subject to these terms, Apple grants you a personal, non-exclusive
license, under Apple's copyrights in this original Apple software (the
"Apple Software"), to use, reproduce, modify and redistribute the Apple
Software, with or without modifications, in source and/or binary forms;
provided that if you redistribute the Apple Software in its entirety and
without modifications, you must retain this notice and the following
text and disclaimers in all such redistributions of the Apple Software. 
Neither the name, trademarks, service marks or logos of Apple Inc. 
may be used to endorse or promote products derived from the Apple
Software without specific prior written permission from Apple.  Except
as expressly stated in this notice, no other rights or licenses, express
or implied, are granted by Apple herein, including but not limited to
any patent rights that may be infringed by your derivative works or by
other works in which the Apple Software may be incorporated.

The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.

IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Copyright © 2003-2007 Apple, Inc., All Rights Reserved

*/

package apple.dts.samplecode.osxadapter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.io.FilenameFilter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

import java.lang.reflect.*;

public class MyApp extends JFrame implements ActionListener {

    protected JDialog aboutBox, prefs;

    protected JLabel imageLabel;
    protected JComboBox colorComboBox;
    
    protected JMenu fileMenu, helpMenu;
    protected JMenuItem openMI, optionsMI, quitMI;
    protected JMenuItem docsMI, supportMI, aboutMI;

    // Check that we are on Mac OS X.  This is crucial to loading and using the OSXAdapter class.
    public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    
    protected BufferedImage currentImage;
    
    Color[] colors = { Color.WHITE, Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE };
    String[] colorNames = { "White", "Black", "Red", "Blue", "Yellow", "Orange" };

    // Ask AWT which menu modifier we should be using.
    final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        
    public MyApp() {
        super("OSXAdapter");
                
        addMenus();
        
        // Main content area; set up a JLabel to display images selected by the user
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(imageLabel = new JLabel("Open an image to view it"));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);

        // set up a simple about box
        aboutBox = new JDialog(this, "About OSXAdapter");
        aboutBox.getContentPane().setLayout(new BorderLayout());
        aboutBox.getContentPane().add(new JLabel("OSXAdapter", JLabel.CENTER));
        aboutBox.getContentPane().add(new JLabel("\u00A92003-2007 Apple, Inc.", JLabel.CENTER), BorderLayout.SOUTH);
        aboutBox.setSize(160, 120);
        aboutBox.setResizable(false);

        // Preferences dialog lets you select the background color when displaying an image
        prefs = new JDialog(this, "OSXAdapter Preferences");
        colorComboBox = new JComboBox(colorNames);
        colorComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (currentImage != null) {
                    imageLabel.setBackground((Color)colors[colorComboBox.getSelectedIndex()]);
                }
            }
        });
        
        JPanel masterPanel = new JPanel();
        masterPanel.setBorder(new TitledBorder("Window background color:"));
        masterPanel.add(colorComboBox);
        prefs.getContentPane().add(masterPanel);
        prefs.setSize(240, 100);
        prefs.setResizable(false);

        // Set up our application to respond to the Mac OS X application menu
        registerForMacOSXEvents();
        
        setSize(320, 240);
    }
	
    // Generic registration with the Mac OS X application menu
    // Checks the platform, then attempts to register with the Apple EAWT
    // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
    public void registerForMacOSXEvents() {
        if (MAC_OS_X) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }
	
    // General info dialog; fed to the OSXAdapter as the method to call when 
    // "About OSXAdapter" is selected from the application menu
    public void about() {
        aboutBox.setLocation((int)this.getLocation().getX() + 22, (int)this.getLocation().getY() + 22);
        aboutBox.setVisible(true);
    }

    // General preferences dialog; fed to the OSXAdapter as the method to call when
    // "Preferences..." is selected from the application menu
    public void preferences() {
        prefs.setLocation((int)this.getLocation().getX() + 22, (int)this.getLocation().getY() + 22);
        prefs.setVisible(true);
    }

    // General quit handler; fed to the OSXAdapter as the method to call when a system quit event occurs
    // A quit event is triggered by Cmd-Q, selecting Quit from the application or Dock menu, or logging out
    public boolean quit() {	
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
        return (option == JOptionPane.YES_OPTION);
    }
    
    public void addMenus() {
        JMenu fileMenu = new JMenu("File");
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(fileMenu = new JMenu("File"));
        fileMenu.add(openMI = new JMenuItem("Open..."));
        openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_MASK));
        openMI.addActionListener(this);
        
        // Quit/prefs menu items are provided on Mac OS X; only add your own on other platforms
        if (!MAC_OS_X) {
            fileMenu.addSeparator();
            fileMenu.add(optionsMI = new JMenuItem("Options"));
            optionsMI.addActionListener(this);

            fileMenu.addSeparator();
            fileMenu.add(quitMI = new JMenuItem("Quit"));
            quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
            quitMI.addActionListener(this);
        }
        
        mainMenuBar.add(helpMenu = new JMenu("Help"));
        helpMenu.add(docsMI = new JMenuItem("Online Documentation"));
        helpMenu.addSeparator();
        helpMenu.add(supportMI = new JMenuItem("Technical Support"));
        // About menu item is provided on Mac OS X; only add your own on other platforms
        if (!MAC_OS_X) {
            helpMenu.addSeparator();
            helpMenu.add(aboutMI = new JMenuItem("About OSXAdapter"));
            aboutMI.addActionListener(this);
        }

        setJMenuBar (mainMenuBar);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == quitMI) {
            quit();
        } else if (source == optionsMI) {
            preferences();
        } else if (source == aboutMI) {
            about();
        } else if (source == openMI) {
            // File:Open action shows a FileDialog for loading displayable images
            FileDialog openDialog = new FileDialog(this);
            openDialog.setMode(FileDialog.LOAD);
            openDialog.setFilenameFilter(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String[] supportedFiles = ImageIO.getReaderFormatNames();
                    for (int i = 0; i < supportedFiles.length; i++) {
                        if (name.endsWith(supportedFiles[i])) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            openDialog.setVisible(true);
            String filePath = openDialog.getDirectory() + openDialog.getFile();
            if (filePath != null) {
                loadImageFile(filePath);
            }
        }
    }

    public void loadImageFile(String path) {
        try {
            currentImage = ImageIO.read(new File(path));
            imageLabel.setIcon(new ImageIcon(currentImage));
            imageLabel.setBackground((Color)colors[colorComboBox.getSelectedIndex()]);
            imageLabel.setText("");
        } catch (IOException ioe) {
            System.out.println("Could not load image " + path);
        }
        repaint();
    }
    
    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyApp().setVisible(true);
            }
        });
    }    
}