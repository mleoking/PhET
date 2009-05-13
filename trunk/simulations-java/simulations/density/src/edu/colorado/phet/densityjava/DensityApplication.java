package edu.colorado.phet.densityjava;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import java.io.*;
import java.util.StringTokenizer;

public class DensityApplication extends PiccoloPhetApplication {

    public DensityApplication(PhetApplicationConfig config) {
        super(config);
        addModule(new DensityModule());
    }

    class DensityModule extends Module {

        public DensityModule() {
            super("density", new ConstantDtClock(30, 30 / 1000.0));
            setSimulationPanel(new TestJMEPanel());
        }

    }

    public static void main(String[] args) throws IOException {
//        if (!Arrays.asList(args).contains("withlib")) {
//            //extract natives to user's .phet-natives directory.
//            //copy this jar file so it can be unzipped while it's being read?
//            String[] cmdArray = new String[]{PhetUtilities.getJavaPath(), "-jar", updaterBootstrap.getAbsolutePath(), src.getAbsolutePath(), dst.getAbsolutePath()};
////        log("Starting updater bootstrap with cmdArray=" + Arrays.asList(cmdArray).toString());
//            Runtime.getRuntime().exec(cmdArray);
//        } else {
        System.out.println(System.getProperty("java.io.tmpdir"));
        boolean testLocal = true;
        System.out.println("FileUtils.isJarCodeSource() = " + FileUtils.isJarCodeSource());
        System.out.println("CodeSource=" + FileUtils.getCodeSource());
        if (FileUtils.isJarCodeSource() || testLocal) {
            //add natives to path
            File nativesDir = extractNatives();
            //TODO: clear old jar or unzip nativesDir?
            System.out.println("DensityApplication.main, unzip nativesDir=" + nativesDir.getAbsolutePath());
            DensityUtils.addDir(nativesDir.getAbsolutePath());
            System.out.println("added natives to system path");
        }
        new PhetApplicationLauncher().launchSim(args, "density", DensityApplication.class);
//        }
    }

    private static File extractNatives() throws IOException {
        String natives = "jinput-dx8.dll, jinput-raw.dll, libjinput-linux.so, libjinput-linux64.so, libjinput-osx.jnilib, liblwjgl.jnilib, liblwjgl.so, liblwjgl64.so, libodejava.jnilib, libodejava.so, libodejava64.so, libopenal.so, libopenal64.so, lwjgl.dll, odejava.dll, openal.dylib, OpenAL32.dll";
        StringTokenizer stringTokenizer = new StringTokenizer(natives, ", ");
        File nativeParent = new File(System.getProperty("java.io.tmpdir"), "phet-natives");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("natives/" + token);
            File dest = new File(nativeParent, token);
            dest.getParentFile().mkdirs();
            DensityUtils.copyAndClose(inputStream, new FileOutputStream(dest), false);
            System.out.println("copied resource to " + dest.getAbsolutePath());
        }
        return nativeParent;
    }

    private static File extractNativesByUnzippingEntireLaunchedJAR(boolean testLocal) throws IOException {
        File codeSource = FileUtils.getCodeSource();
        if (testLocal) {
            codeSource = new File("C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\density\\deploy\\density_all.jar");
        }
        File copy = new File(System.getProperty("java.io.tmpdir"), codeSource.getName());
        DensityUtils.copyTo(codeSource, copy);
        System.out.println("copy = " + copy.getAbsolutePath());
        File dir = new File(copy.getParentFile(), "phet-unzipped");
        DensityUtils.unzip(copy, dir, new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().indexOf("natives") >= 0;//TODO: ignore spurious "natives" elsewhere in the jar
            }
        });
        return dir;
    }

}
