package edu.colorado.phet.recordandplayback.gui;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.*;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
    private ArrayList<PNode> nodes = new ArrayList<PNode>();
    private Dimension preferredSize = new Dimension(800, 100);
    private JButton clearButton = new JButton(PhetCommonResources.getString("Common.clear"));
    private PNode rightmostControl;
    private RewindButton rewind = new RewindButton(50);
    private PSwing clearButtonNode = new PSwing(clearButton);
    private PSwing modePanelNode;
    private PlayPauseButton playPause = new PlayPauseButton(75);
    private ToolTipHandler playPauseTooltipHandler = new ToolTipHandler(PhetCommonResources.getString("Common.ClockControlPanel.Pause"), this);
    private StepButton stepButton = new StepButton(50);
    private double stepTimeChange;//todo: add this as a parameter

    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, double maxTime) {
        this(model, simPanel, maxTime, Color.blue);
    }

    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, double maxTime, Color timelineColor) {
        this(model, simPanel, new Function() {
            public PNode createControl() {
                return new PlaybackSpeedSlider<T>(model);
            }
        }, timelineColor, maxTime);
    }

    public RecordAndPlaybackControlPanel(final RecordAndPlaybackModel<T> model, JComponent simPanel, Function createRightControl, Color timelineColor, double maxTime) {
        this.model = model;
        this.simPanel = simPanel;
        TimelineNode timeline = new TimelineNode<T>(model, this, timelineColor, maxTime);
        rightmostControl = createRightControl.createControl();
        ModePanel<T> modePanel = new ModePanel<T>(model);
        modePanelNode = new PSwing(modePanel);

        setBorder(null);
        setBackground(new JPanel().getBackground());
        addScreenChild(backgroundNode);

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.clearHistory();
                model.setPaused(true);
                model.setRecord(true);
            }
        });

        rightmostControl.setOffset(0, preferredSize.getHeight() / 2 - rightmostControl.getFullBounds().getHeight() / 2);

        rewind.addListener(new DefaultIconButton.Listener() {
            public void buttonPressed() {
                model.setRecord(false);
                model.rewind();
                model.setPaused(true);
            }
        });
        model.addObserver(new SimpleObserver() {
            public void update() {
                updateRewindEnabled();
            }
        });
        updateRewindEnabled();


        rewind.addInputEventListener(new ToolTipHandler(PhetCommonResources.getString("Common.rewind"), this));
        rewind.setOffset(0, 12);

        playPause.addListener(new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                model.setPaused(!playPause.isPlaying());
            }
        });
        playPause.addInputEventListener(playPauseTooltipHandler);


        model.addObserver(new SimpleObserver() {
            public void update() {
                updatePlayPauseButton();
            }
        });
        updatePlayPauseButton();
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
                if (model.isPlayback()) model.stepPlayback();
                else if (model.isRecord()) model.stepRecording(stepTimeChange);
            }
        });
        stepButton.setOffset(0, 12);

        addControl(clearButtonNode);
        addControl(modePanelNode);
        addControl(rewind);
        addControl(playPause);
        addControl(stepButton);
        addControl(rightmostControl);

        addScreenChild(timeline);

        setPreferredSize(preferredSize);

        //todo: this seems like it could suffer from the problem CM & I discovered that Component resize events are neither synchronous nor immediate
        simPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });

        updateSize();
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateControlPanelLayout();
            }
        });
        updateControlPanelLayout();
    }

    private void updatePlayPauseButton() {
        playPause.setPlaying(!model.isPaused());
        playPauseTooltipHandler.setText(model.isPaused() ? PhetCommonResources.getString("Common.ClockControlPanel.Play") : PhetCommonResources.getString("Common.ClockControlPanel.Pause"));
    }

    public static interface Function {
        PNode createControl();
    }

    protected void addControl(PNode node) {
        addScreenChild(node);
        double offsetX = nodes.size() == 0 ? 0 : nodes.get(nodes.size() - 1).getFullBounds().getMaxX() + 5;
        node.setOffset(offsetX, node.getOffset().getY() + 10);
        nodes.add(node);
    }

    private void updateRewindEnabled() {
        boolean enabled = model.isPlayback() && model.getNumRecordedPoints() > 0 && model.getTime() != model.getMinRecordedTime();
        rewind.setEnabled(enabled);
    }

    private void updateControlPanelLayout() {
        double buttonDX = 2;
        playPause.setOffset(getPreferredSize().width / 2 - playPause.getFullBounds().getWidth() / 2, playPause.getOffset().getY());
        rewind.setOffset(playPause.getFullBounds().getX() - rewind.getFullBounds().getWidth() - buttonDX, rewind.getOffset().getY());
        stepButton.setOffset(playPause.getFullBounds().getMaxX() + buttonDX, stepButton.getOffset().getY());
        rightmostControl.setOffset(stepButton.getFullBounds().getMaxX(), rightmostControl.getOffset().getY());

        modePanelNode.setOffset(rewind.getFullBounds().getX() - modePanelNode.getFullBounds().width, playPause.getFullBounds().getCenterY() - modePanelNode.getFullBounds().getHeight() / 2);
        clearButtonNode.setOffset(modePanelNode.getFullBounds().getX() - clearButtonNode.getFullBounds().width, playPause.getFullBounds().getCenterY() - clearButtonNode.getFullBounds().getHeight() / 2);

        double halfWidth = playPause.getFullBounds().getCenterX() - rightmostControl.getFullBounds().getMaxX();
        ArrayList<PBounds> bounds = new ArrayList<PBounds>();
        for (PNode n : nodes) bounds.add(n.getFullBounds());

        Rectangle2D union = bounds.get(0);
        for (int i = 1; i < bounds.size(); i++) {
            union = union.createUnion(bounds.get(i));
        }
        Rectangle2D expanded = RectangleUtils.expand(union, 0, 0);
        backgroundNode.setSize((int) (halfWidth * 2), (int) expanded.getHeight());
        backgroundNode.setOffset(playPause.getFullBounds().getCenterX() - halfWidth, expanded.getY());
    }

    private void updateSize() {
        if (simPanel.getWidth() > 0) {
            Dimension pref = new Dimension(simPanel.getWidth(), preferredSize.height);
            setPreferredSize(pref);
            if (getParent() != null) {
                getParent().doLayout(); //This is necessary to solve this problem: 6/10/2010 Fixed: Record and playback timeline doesn't synchronize size at the right time (seems one behind); When switching back and forth tabs in moving man, the playback timeline changes size.
            }
        }
    }
}