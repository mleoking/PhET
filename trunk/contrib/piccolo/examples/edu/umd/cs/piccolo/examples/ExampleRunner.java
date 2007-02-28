package edu.umd.cs.piccolo.examples;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.PFrame;

public class ExampleRunner extends JFrame {

	private JList examples;

	public ExampleRunner() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Piccolo Example Runner");
		setSize(426, 335);
		getContentPane().setLayout(new BorderLayout());
		createExampleButtons();
		validate();
		pack();
		setVisible(true);
	}

	public void createExampleButtons() {
		Container c = getContentPane();
		Container p = new JPanel();

		p = new JPanel(new GridLayout(0, 1));
		c.add(BorderLayout.NORTH, p);

		p.add(new JCheckBox(new AbstractAction("Print Frame Rates to Console") {
			public void actionPerformed(ActionEvent e) {
				PDebug.debugPrintFrameRate = !PDebug.debugPrintFrameRate;
			}
		}));

		p.add(new JCheckBox(new AbstractAction("Show Region Managment") {
			public void actionPerformed(ActionEvent e) {
				PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
			}
		}));

		p.add(new JCheckBox(new AbstractAction("Show Full Bounds") {
			public void actionPerformed(ActionEvent e) {
				PDebug.debugFullBounds = !PDebug.debugFullBounds;
			}
		}));

		p = new JPanel(new GridLayout(0, 2));
		c.add(BorderLayout.CENTER, p);

		p.add(new JButton(new AbstractAction("ActivityExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new ActivityExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("AngleNodeExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new AngleNodeExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("BirdsEyeViewExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new BirdsEyeViewExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("CameraExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new CameraExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("CenterExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new CenterExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));
		
		p.add(new JButton(new AbstractAction("ChartLabelExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new ChartLabelExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("ClipExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new ClipExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("CompositeExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new CompositeExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("DynamicExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new DynamicExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("EventHandlerExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new EventHandlerExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("FullScreenNodeExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new FullScreenNodeExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("GraphEditorExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new GraphEditorExample();
				example.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));
		p.add(new JButton(new AbstractAction("GridExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new GridExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("GroupExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new GroupExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("HandleExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new HandleExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("HierarchyZoomExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new HierarchyZoomExample();
				example.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("KeyEventFocusExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new KeyEventFocusExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("LayoutExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new LayoutExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("LensExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new LensExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("NavigationExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new NavigationExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("NodeCacheExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new NodeCacheExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("NodeEventExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new NodeEventExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("NodeExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new NodeExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("NodeLinkExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new NodeLinkExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("PanToExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new PanToExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("PathExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new PathExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("PositionExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new PositionExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("PositionPathActivityExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new PositionPathActivityExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("PulseExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new PulseExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("ScrollingExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new ScrollingExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("SelectionExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new SelectionExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("SquiggleExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new SquiggleExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("StickyExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new StickyExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("StickyHandleLayerExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new StickyHandleLayerExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("TextExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new TextExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("Tooltip Example") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new TooltipExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("TwoCanvasExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new TwoCanvasExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));

		p.add(new JButton(new AbstractAction("WaitForActivitiesExample") {
			public void actionPerformed(ActionEvent e) {
				PFrame example = new WaitForActivitiesExample();
				example
						.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			}
		}));
	}

	public static void main(String[] args) {
		new ExampleRunner();
	}
}