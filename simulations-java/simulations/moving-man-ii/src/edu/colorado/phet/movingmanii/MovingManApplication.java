package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanelWithCharts;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanelWithPlayAreaSliders;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

import javax.swing.*;

/**
 * This is the moving man application, redesigned + rewritten in 2010 to be part of the new motion series suite of sims.
 *
 * @author Sam Reid
 */
public class MovingManApplication extends PiccoloPhetApplication {

    public MovingManApplication(PhetApplicationConfig config) {
        super(config);
        addModule(new IntroModule());
        addModule(new ChartingModule());
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "moving-man-ii", MovingManApplication.class);
    }

    private class IntroModule extends MovingManModule {
        private IntroModule() {
            super("Introduction");
        }

        protected JComponent createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
            return new MovingManSimulationPanelWithPlayAreaSliders(model, recordAndPlaybackModel);
        }
    }

    private class ChartingModule extends MovingManModule {
        public ChartingModule() {
            super("Charts");
        }

        protected JComponent createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
            return new MovingManSimulationPanelWithCharts(model, recordAndPlaybackModel);
        }
    }
}