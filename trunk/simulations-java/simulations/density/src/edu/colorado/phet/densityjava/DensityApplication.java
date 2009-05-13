package edu.colorado.phet.densityjava;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

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
        boolean testLocal = false;
        if (FileUtils.isJarCodeSource() || testLocal) {
            //add natives to path
            File codeSource = FileUtils.getCodeSource();
            if (testLocal) {
                codeSource = new File("C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\density\\deploy\\density_all.jar");
            }
            File copy = new File(System.getProperty("java.io.tmpdir"), codeSource.getName());
            DensityUtils.copyTo(codeSource, copy);
            File dir = new File(copy.getParentFile(), "phet-unzipped");
            DensityUtils.unzip(copy, dir, new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getAbsolutePath().indexOf("natives") >= 0;//TODO: ignore spurious "natives" elsewhere in the jar
                }
            });
            //TODO: clear old jar or unzip dir?
            System.out.println("DensityApplication.main, unzip dir=" + dir.getAbsolutePath());
            DensityUtils.addDir(new File(dir, "natives").getAbsolutePath());
        }
        new PhetApplicationLauncher().launchSim(args, "density", DensityApplication.class);
//        }
    }

}
