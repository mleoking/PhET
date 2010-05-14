package edu.colorado.phet.recordandplayback.gui;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.*;
import edu.colorado.phet.recordandplayback.model.RecordModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
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

public class RecordAndPlaybackControlPanel<T> extends PhetPCanvas {
    RecordModel<T> model;
    JComponent simPanel;
    Function createRightControl;
    Color timelineColor;
    double maxTime;

    PiccoloTimeControlPanel.BackgroundNode backgroundNode = new PiccoloTimeControlPanel.BackgroundNode();
    private ArrayList<PNode> nodes = new ArrayList<PNode>();
    private Dimension prefSizeM = new Dimension(800, 100);
    ModePanel<T> modePanel;
    JButton clearButton = new JButton(PhetCommonResources.getString("Common.clear"));
    PNode rightmostControl;
    RewindButton rewind = new RewindButton(50);

    PSwing clearButtonNode = new PSwing(clearButton);
    PSwing modePanelNode;

    PlayPauseButton playPause = new PlayPauseButton(75);

    ToolTipHandler playPauseTooltipHandler = new ToolTipHandler(PhetCommonResources.getString("Common.ClockControlPanel.Pause"), this);

    public void updatePlayPauseButton() {
        playPause.setPlaying(!model.isPaused());
        playPauseTooltipHandler.setText(model.isPaused() ? PhetCommonResources.getString("Common.ClockControlPanel.Play") : PhetCommonResources.getString("Common.ClockControlPanel.Pause"));
    }

    StepButton stepButton = new StepButton(50);
    Timeline timeline;

    public static interface Function {
        PNode createControl();
    }

    public RecordAndPlaybackControlPanel(final RecordModel<T> model, JComponent simPanel, Function createRightControl, Color timelineColor, double maxTime) {
        this.model = model;
        this.simPanel = simPanel;
        this.createRightControl = createRightControl;
        this.timelineColor = timelineColor;
        this.maxTime = maxTime;
        timeline = new Timeline<T>(model, this, timelineColor, maxTime);
        rightmostControl = createRightControl.createControl();
        modePanel = new ModePanel<T>(model);
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

        rightmostControl.setOffset(0, prefSizeM.getHeight() / 2 - rightmostControl.getFullBounds().getHeight() / 2);

        rewind.addListener(new DefaultIconButton.Listener() {
            public void buttonPressed() {
                model.setRecord(false);
                model.setPlaybackIndexFloat(0.0);
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
                boolean isLastStep = model.getPlaybackIndex() == model.getRecordingHistory().size();
                stepButton.setEnabled(model.isPaused() && !isLastStep);
            }
        });
        stepButton.addListener(new DefaultIconButton.Listener() {
            public void buttonPressed() {
                if (model.isPlayback()) model.stepPlayback();
                else if (model.isRecord()) model.stepRecord();
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

        setPreferredSize(prefSizeM);

        simPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateSize();
                    }
                });
            }
        });

        updateSize();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                myUpdateLayout();
            }
        });
        myUpdateLayout();
    }

    private class MyButtonNode extends PText {
        String text;
        Icon icon;
        ActionListener action;

        private MyButtonNode(String text, Icon icon, final ActionListener action) {
            super(text);
            this.text = text;
            this.icon = icon;
            this.action = action;

            addInputEventListener(new PBasicInputEventHandler() {
                public void mousePressed(PInputEvent event) {
                    action.actionPerformed(null);
                }

            });
        }

    }

    protected void addControl(PNode node) {
        addScreenChild(node);
        double offsetX = nodes.size() == 0 ? 0 : nodes.get(nodes.size() - 1).getFullBounds().getMaxX() + 5;
        node.setOffset(offsetX, node.getOffset().getY() + 10);
        nodes.add(node);
    }

    private Icon stringToIcon(String string) {
        return new ImageIcon(PhetCommonResources.getImage("clock/" + string));
    }

//  implicit def functionToButtonListener(f: () => Unit): DefaultIconButton.Listener = new DefaultIconButton.Listener() {
//    def buttonPressed = {f()}
//  }


    public void updateRewindEnabled() {
        boolean enabled = model.isPlayback() && model.getRecordingHistory().size() > 0 && model.getTime() != model.getMinRecordedTime();
        rewind.setEnabled(enabled);
    }

    public void myUpdateLayout() {
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
//    val b: PBounds = blist.foldLeft(blist(0))((a, b) => new PBounds(a.createUnion(b)))
        Rectangle2D expanded = RectangleUtils.expand(union, 0, 0);
        backgroundNode.setSize((int) (halfWidth * 2), (int) expanded.getHeight());
        backgroundNode.setOffset(playPause.getFullBounds().getCenterX() - halfWidth, expanded.getY());
    }

    void updateSize() {
        if (simPanel.getWidth() > 0) {
            Dimension pref = new Dimension(simPanel.getWidth(), prefSizeM.height);
            setPreferredSize(pref);
            updateLayout();
        }
    }
}