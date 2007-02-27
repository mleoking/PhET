/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.io.File;

import javax.swing.UIManager;

import com.birosoft.liquid.LiquidLookAndFeel;

/**
 *
 * A launcher for the Liquid Look and Feel. Just add Launcher in front
 * of the real classname in your java command line and it'll set the
 * Liquid look and feel as the default and will not permit any changes...
 *
 * Thanks to David for contributing the URLClassLoader parts that allows
 * to invoke a .jar file directly.
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.ddscale", "true");
        UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");

        UIManager.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
//                        Object oldLF = event.getOldValue();
                        Object newLF = event.getNewValue();

                        if ((newLF instanceof LiquidLookAndFeel) == false) {
                            try {
                                UIManager.setLookAndFeel(new LiquidLookAndFeel());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

        Class mainClass = null;

        if (args[0].toLowerCase().endsWith(".jar")) {
            File file = new File (args[0]);
            JarFile jarFile = new JarFile (file);

            Manifest manifest = jarFile.getManifest();
            String mainClassName = manifest.getMainAttributes().getValue("Main-Class");
            URLClassLoader loader = new URLClassLoader(new URL[] { file.toURL() }, Launcher.class.getClassLoader());

            mainClass = Class.forName(mainClassName, true, loader);
        } else {
            mainClass = Class.forName(args[0]);
        }

        Method m = mainClass.getMethod("main", new Class[]{String[].class});

        String[] copyOfArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            copyOfArgs[i - 1] = args[i];
        }
        m.invoke(mainClass, new Object[]{copyOfArgs});
    }
}
