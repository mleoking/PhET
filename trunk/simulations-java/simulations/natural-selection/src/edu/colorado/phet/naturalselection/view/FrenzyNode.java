package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class FrenzyNode extends PNode {

    private NaturalSelectionModel model;

    private static final double PADDING = 5.0;
    private static final double ROUND = 10.0;

    /**
     * Time left in milliseconds
     */
    private double timeLeft;


    private SwingLayoutNode layoutNode;
    private PText timeLeftDisplay;

    public FrenzyNode( NaturalSelectionModel model, double timeLeft ) {
        //super( new GridBagLayout() );
        this.model = model;
        this.timeLeft = timeLeft;

        layoutNode = new SwingLayoutNode( new GridBagLayout() );

        PText frenzyLabel = new PText( "WOLF FEEDING FRENZY!" );
        frenzyLabel.setFont( new PhetFont( 24, true ) );
        frenzyLabel.setTextPaint( Color.RED );

        PText getBunnyLabel = new PText( "GET THE BUNNIES!" );
        getBunnyLabel.setFont( new PhetFont( 24, true ) );
        getBunnyLabel.setTextPaint( Color.RED );

        PText timeLeftLabel = new PText( "Time Left" );
        timeLeftLabel.setFont( new PhetFont( 18, true ) );
        timeLeftLabel.setTextPaint( Color.BLACK );

        timeLeftDisplay = new PText( timeString( timeLeft ) );
        timeLeftDisplay.setFont( new PhetFont( 18, true ) );
        timeLeftDisplay.setTextPaint( Color.BLACK );
        timeLeftDisplay.setJustification( 1f );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 0, 10, 0, 0 );
        layoutNode.addChild( frenzyLabel, c );
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets( 0, 10, 0, 0 );
        layoutNode.addChild( getBunnyLabel, c );
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets( 0, 20, 0, 10 );
        layoutNode.addChild( timeLeftLabel, c );
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets( 0, 20, 0, 10 );
        layoutNode.addChild( timeLeftDisplay, c );


        PPath background = createBackground();

        addChild( background );

        layoutNode.setOffset( PADDING, PADDING );

        addChild( layoutNode );


    }

    private PPath createBackground() {
        PPath background = new PPath();

        double outerWidth = getPlacementWidth();
        double outerHeight = getPlacementHeight();

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, ROUND );
        path.quadTo( 0, 0, ROUND, 0 );
        path.lineTo( outerWidth - ROUND, 0 );
        path.quadTo( outerWidth, 0, outerWidth, ROUND );
        path.lineTo( outerWidth, outerHeight - ROUND );
        path.quadTo( outerWidth, outerHeight, outerWidth - ROUND, outerHeight );
        path.lineTo( ROUND, outerHeight );
        path.quadTo( 0, outerHeight, 0, outerHeight - ROUND );
        path.lineTo( 0, ROUND );
        path.closePath();
        background.setStroke( new BasicStroke( 3f ) );
        background.setStrokePaint( Color.RED );
        background.setPaint( Color.WHITE );
        background.setPathTo( path.getGeneralPath() );
        return background;
    }

    private String timeString( double time ) {
        String valueString = String.valueOf( time / 1000 );
        // TODO: does valueOf use commas for different locales?
        int decimalIndex = valueString.indexOf( "." );
        if ( decimalIndex == -1 ) {
            valueString += ".00";
        }
        else {
            int precision = valueString.length() - ( decimalIndex + 1 );
            if ( precision == 1 ) {
                valueString += "0";
            }
            else if ( precision > 2 ) {
                valueString = valueString.substring( 0, decimalIndex + 3 );
            }
        }
        return valueString + " sec";
    }

    public void setTimeLeft( double time ) {
        timeLeft = time;
        timeLeftDisplay.setText( timeString( timeLeft ) );
    }

    public double getPlacementWidth() {
        return layoutNode.getContainer().getPreferredSize().getWidth() + PADDING * 2;
    }

    public double getPlacementHeight() {
        return layoutNode.getContainer().getPreferredSize().getHeight() + PADDING * 2;
    }
}
