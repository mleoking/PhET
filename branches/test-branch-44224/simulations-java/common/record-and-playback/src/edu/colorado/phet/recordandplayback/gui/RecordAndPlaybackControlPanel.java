package edu.colorado.phet.recordandplayback.gui;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.*;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The RecordAndPlaybackControlPanel is a replacement for the time control panel in phet simulations that
 * uses a RecordAndPlaybackModel to allow recording and playback of a sim.
 *
 * @author Sam Reid
 * @param <T> the type of model state that is being recorded, typically immutable
 */
public class RecordAndPlaybackControlPanel<T> extends PhetPCanvas {
    private RecordAndPlaybackModel<T> model;
    private JComponent simPanel;//used to determine the size of this component
    private PiccoloTimeControlPanel.BackgroundNode backgroundNode = new PiccoloTimeControlPanel.BackgroundNode();
    private JButton clearButton = new JButton(PhetCommonResources.getString("Common.clear"));
    private RewindButton rewind = new RewindButton(50);
    private PSwing clearButtonNode = new PSwing(clearButton);
    private PSwing modePanelNode;
    private PlayPauseButton playPause = new PlayPauseButton(75);
    private ToolTipHandler playPauseTooltipHandler = new ToolTipHandler(PhetCommonResources.getString("Common.ClockControlPanel.Pause"), this);
    private StepButton stepButton = new StepButton(50);
    private double stepTimeChange = 0.0;//todo: add this as a parameter
    private PNode controlLayer;
    protected TimelineNode timelineNode;

    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, double maxTime) {
        this(model, simPanel, maxTime, Color.blue);
    }

    /**
     * Creates a record and playback control panel that also contains a playback speed slider.
     *
     * @param model
     * @param simPanel
     * @param maxTime
     * @param timelineColor
     */
    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, double maxTime, Color timelineColor) {
        this(model, simPanel, timelineColor, maxTime);
        addControl(new PlaybackSpeedSlider<T>(model));
    }

    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, Color timelineColor, double maxTime) {
        this.model = model;
        this.simPanel = simPanel;
        timelineNode = new TimelineNode<T>(model, this, timelineColor, maxTime);
        ModePanel<T> modePanel = new ModePanel<T>(model);
        modePanelNode = new PSwing(modePanel);

        setBorder(null);
        setBackground(new JPanel().getBackground());
        addScreenChild(backgroundNode);

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.clearHistory();
                model.setRecord(true);
            }
        });

        rewind.addListener(new DefaultIconButton.Listener() {
            public void buttonPressed() {
                model.setRecord(false);
                model.rewind();
                model.setPaused(true);
            }
        });
        SimpleObserver updateRewindEnabled = new SimpleObserver() {
            public void update() {
                boolean enabled = model.isPlayback() && model.getNumRecordedPoints() > 0 && model.getTime() != model.getMinRecordedTime();
                rewind.setEnabled(enabled);
            }
        };
        model.addObserver(updateRewindEnabled);
        updateRewindEnabled.update();

        rewind.addInputEventListener(new ToolTipHandler(PhetCommonResources.getString("Common.rewind"), this));

        playPause.addListener(new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                model.setPaused(!playPause.isPlaying());
            }
        });
        playPause.addInputEventListener(playPauseTooltipHandler);

        SimpleObserver updatePlayPauseButton = new SimpleObserver() {
            public void update() {
                playPause.setPlaying(!model.isPaused());
                playPauseTooltipHandler.setText(model.isPaused() ? PhetCommonResources.getString("Common.ClockControlPanel.Play") : PhetCommonResources.getString("Common.ClockControlPanel.Pause"));
            }
        };
        model.addObserver(updatePlayPauseButton);
        updatePlayPauseButton.update();

        stepButton.setEnabled(false);
        stepButton.addInputEventListener(new ToolTipHandler(PhetCommonResources.getString("Common.ClockControlPanel.Step"), this));
        model.addObserver(new SimpleObserver() {
            public void update() {
                boolean isLastStep = model.getTime() == model.getMaxRecordedTime();
                stepButton.setEnabled(model.isPaused() && !isLastStep);
            }
        });
        stepButton.addListener(new DefaultIconButton.Listener() {
            public void buttonPressed() {
                model.step();
            }
        });
        addScreenChild(timelineNode);
        controlLayer = new PNode();
        addScreenChild(controlLayer);

        addControl(clearButtonNode);
        addControl(modePanelNode);
        addControl(rewind);
        addControl(playPause);
        addControl(stepButton);

        setPreferredSize(new Dimension((int) getLayer().getFullBounds().getWidth(), (int) getLayer().getFullBounds().getHeight()));

        //todo: this seems like it could suffer from the problem CM & I discovered that Component resize events are neither synchronous nor immediate
        simPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                relayout();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                relayout();
            }
        });
        relayout();
    }

    public void addControl(PNode node) {
        controlLayer.addChild(node);
        relayout();
    }

    private double getControlLayerHeight() {
        double h = controlLayer.getChild(0).getFullBounds().getHeight();
        for (int i = 1; i < controlLayer.getChildrenCount(); i++) {
            h = Math.max(h, controlLayer.getChild(i).getFullBounds().getHeight());
        }
        return h;
    }

    private void relayout() {
        double x = 0;

        for (int i = 0; i < controlLayer.getChildrenCount(); i++) {
            PNode child = controlLayer.getChild(i);
            child.setOffset(x, getControlLayerHeight() / 2 - child.getFullBounds().getHeight() / 2);  //assumes controls are origined at 0,0
            double insetDX = 2.0;//distance between controls
            x = x + child.getFullBounds().getWidth() + insetDX;
        }//TODO: center so that play button is in the very middle
        double playButtonCenter = playPause.getFullBounds().getCenterX();
        controlLayer.setOffset(simPanel.getWidth() / 2 - playButtonCenter, timelineNode.getVisible() ? timelineNode.getFullBounds().getHeight() : 0.0);

        backgroundNode.setSize((int) controlLayer.getFullBounds().getWidth() + 4, (int) controlLayer.getFullBounds().getHeight() + 2);
        backgroundNode.setOffset(controlLayer.getOffset().getX() - 2, controlLayer.getOffset().getY() - 1);

        if (simPanel.getWidth() > 0) {
            Dimension pref = new Dimension(simPanel.getWidth(), (int) getLayer().getFullBounds().getHeight() + 1);
            setPreferredSize(pref);
            if (getParent() != null) {
                getParent().doLayout(); //This is necessary to solve this problem: 6/10/2010 Fixed: Record and playback timeline doesn't synchronize size at the right time (seems one behind); When switching back and forth tabs in moving man, the playback timeline changes size.
            }
        }
    }

    public void setTimelineNodeVisible(boolean timelineNodeVisible) {
        timelineNode.setVisible(timelineNodeVisible);
        relayout();
    }
}