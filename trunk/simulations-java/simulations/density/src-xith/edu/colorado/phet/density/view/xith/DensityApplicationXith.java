package edu.colorado.phet.density.view.xith;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.densityjava.common.Unit;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.MassVolumeModel;
import edu.colorado.phet.densityjava.util.DensityUtils;
import edu.colorado.phet.densityjava.view.d3.DensityJMECanvas;
import edu.colorado.phet.densityjava.ModelComponents;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class DensityApplicationXith extends PiccoloPhetApplication {

    public DensityApplicationXith(PhetApplicationConfig config) {
        super(config);
        addModule(new DensityModule(getPhetFrame()));
    }

    class DensityModule extends Module {
        private final DensityModel model = new DensityModel();
        private final MassVolumeModel massVolumeModel = new MassVolumeModel();
        private final ModelComponents.DisplayDimensions displayDimensions = new ModelComponents.DisplayDimensions();
        private final XithCanvas panel;

        public DensityModule(JFrame frame) {
            super("density", new ConstantDtClock(30, 30 / 1000.0));
            panel = new XithCanvas(frame, model, massVolumeModel, displayDimensions);
            setSimulationPanel(panel);
            getClock().addClockListener(new ClockAdapter() {
                public void simulationTimeChanged(ClockEvent clockEvent) {
                    model.stepInTime(clockEvent.getSimulationTimeChange());
                }
            });
            massVolumeModel.addListener(new Unit() {
                public void update() {
                    System.out.println("DensityApplication$DensityModule.update");
                    if (massVolumeModel.isSameMass()) {
                        model.setFourBlocksSameMass();
                    } else {
                        model.setFourBlocksSameVolume();
                    }
                }
            });
        }

        public void activate() {
            super.activate();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
//                    panel.getCanvas().requestFocus();
//                    panel.activate();
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        //add natives to path
        File nativesDir = extractNatives();
        DensityUtils.addDir(nativesDir);

        new PhetApplicationLauncher().launchSim(args, "density", "density", DensityApplicationXith.class);
    }

    private static File extractNatives() throws IOException {
        //TODO: clear old jar or unzip nativesDir?
        //skipping solaris
        String natives = "windows/jinput-dx8.dll, windows/jinput-raw.dll, windows/lwjgl.dll, windows/lwjgl64.dll, " +
                "macosx/libjinput-osx.jnilib, macosx/liblwjgl.jnilib, macosx/openal.dylib, " +
                "linux/libjinput-linux.so, linux/liblwjgl.so, linux/libopenal.so, linux/libjinput-linux64.so, linux/liblwjgl64.so, linux/libopenal64.so";//, libjinput-linux.so, libjinput-linux64.so, libjinput-osx.jnilib, liblwjgl.jnilib, liblwjgl.so, liblwjgl64.so, libodejava.jnilib, libodejava.so, libodejava64.so, libopenal.so, libopenal64.so, lwjgl.dll, odejava.dll, openal.dylib, OpenAL32.dll";
        StringTokenizer stringTokenizer = new StringTokenizer(natives, ", ");
        File nativeParent = new File(System.getProperty("java.io.tmpdir"), "phet-natives");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(token);
            String outputPath = token.substring(token.indexOf('/') + 1);
            File dest = new File(nativeParent, outputPath);
            dest.getParentFile().mkdirs();
            DensityUtils.copyAndClose(inputStream, new FileOutputStream(dest), false);
            System.out.println("copied resource to " + dest.getAbsolutePath());
        }
        return nativeParent;
    }

}