package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.model.MutableBoolean;
import edu.colorado.phet.movingman.view.MovingManSimulationPanelWithCharts;
import edu.colorado.phet.movingman.view.MovingManSimulationPanelWithPlayAreaSliders;
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

import javax.swing.*;

/**
 * This is the moving man application, redesigned + rewritten in 2010 to be part of the new motion series suite of sims.
 *
 * @author Sam Reid
 */
public class MovingManApplication extends PiccoloPhetApplication {
    //This state is handled in the MovingManApplication since the SpecialFeaturesMenu is a singleton, but should
    //be able to handle all modules independently.  This value must get propagated to the menu and to the modules.
    private MutableBoolean positiveToTheRight = new MutableBoolean(true);//True if positive coordinates are to the right.

    public MovingManApplication(PhetApplicationConfig config) {
        super(config);
        final IntroModule introModule = new IntroModule();
        introModule.getPositiveToTheRight().addObserver(new SimpleObserver() {
            public void update() {
                if (getActiveModule() == introModule) {
                    positiveToTheRight.setValue(introModule.getPositiveToTheRight().getValue());
                }
            }
        });
        addModule(introModule);
        final ChartingModule chartingModule = new ChartingModule();
        chartingModule.getPositiveToTheRight().addObserver(new SimpleObserver() {
            public void update() {
                if (getActiveModule() == chartingModule) {
                    positiveToTheRight.setValue(introModule.getPositiveToTheRight().getValue());
                }
            }
        });
        addModule(chartingModule);

        addModuleObserver(new ModuleObserver() {
            public void moduleAdded(ModuleEvent event) {
            }

            public void activeModuleChanged(ModuleEvent event) {
                positiveToTheRight.setValue(((MovingManModule) getActiveModule()).getPositiveToTheRight().getValue());
            }

            public void moduleRemoved(ModuleEvent event) {
            }
        });
        getPhetFrame().addMenu(new SpecialFeaturesMenu(this));
        positiveToTheRight.addObserver(new SimpleObserver() {
            public void update() {
                ((MovingManModule) getActiveModule()).setPositiveToTheRight(positiveToTheRight.getValue());
            }
        });
    }

    public MutableBoolean getPositiveToTheRight() {
        return positiveToTheRight;
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "moving-man-ii", MovingManApplication.class);
    }

    private class IntroModule extends MovingManModule {
        private IntroModule() {
            super("Introduction");
        }

        protected JComponent createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
            return new MovingManSimulationPanelWithPlayAreaSliders(model, recordAndPlaybackModel, positiveToTheRight);
        }
    }

    private class ChartingModule extends MovingManModule {

        public ChartingModule() {
            super("Charts");
        }

        protected JComponent createSimulationPanel(MovingManModel model, RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
            return new MovingManSimulationPanelWithCharts(model, recordAndPlaybackModel, positiveToTheRight);
        }

        protected RecordAndPlaybackControlPanel<MovingManState> createRecordAndPlaybackPanel() {
            RecordAndPlaybackControlPanel<MovingManState> panel = super.createRecordAndPlaybackPanel();
            panel.setTimelineNodeVisible(false);//Hide timeline panel in chart panel, since it is redundant with in-chart cursor bar
            return panel;
        }
    }
}