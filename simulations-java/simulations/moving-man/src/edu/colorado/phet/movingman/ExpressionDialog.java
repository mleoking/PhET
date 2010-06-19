package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingman.view.GoButton;

import javax.swing.*;
import java.awt.*;

/**
 * @author Sam Reid
 */
public class ExpressionDialog extends JDialog {
    public ExpressionDialog(PhetFrame frame, MovingManModule module) {
        super(frame, "Expression Evaluator");
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        {
            contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPane.add(new JLabel("Enter an expression as a function of time t"));
            JPanel expressionPanel = new JPanel();
            {
                expressionPanel.add(new JLabel("x = "));
                expressionPanel.add(new JTextField("sin(t/pi) + 2", 14));
            }
            contentPane.add(expressionPanel);
            PhetPCanvas goButtonCanvas = new PhetPCanvas();
            {
                GoButton goButton = new GoButton(module.recordAndPlaybackModel, module.getMovingManModel().getPositionMode());
                goButtonCanvas.addScreenChild(goButton);
                Dimension buttonDim = new Dimension((int) goButton.getFullBounds().getWidth(), (int) goButton.getFullBounds().getHeight());
                goButtonCanvas.setPreferredSize(buttonDim);
                goButtonCanvas.setMaximumSize(buttonDim);
                goButtonCanvas.setBorder(null);
                goButtonCanvas.setBackground(contentPane.getBackground());
            }
            contentPane.setFillNone();//Center the go button
            contentPane.add(goButtonCanvas);
        }
        setContentPane(contentPane);
        pack();
        SwingUtils.centerDialogInParent(this);
    }
}