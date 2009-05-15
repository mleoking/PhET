package edu.colorado.phet.acidbasesolutions.control;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.NullSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class SolutionControlsNode extends PhetPNode {
    
    private static final double X_SPACING = 3;
    private static final double Y_SPACING = 10;
    private static final Font LABEL_FONT = new PhetFont( 14 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final double BACKGROUND_MARGIN = 10;
    private static final double BACKGROUND_CORNER_RADIUS = 25;
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 220 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color SEPARATOR_COLOR = BACKGROUND_COLOR.darker();
    private static final Stroke SEPARATOR_STROKE = new BasicStroke( 1f );
    
    private final SoluteComboBox soluteComboBox;
    private final ConcentrationControlNode concentrationControlNode;
    private final PNode strengthLabelNode;
    private final StrengthSliderNode strengthSliderNode;
    private final ArrayList<SolutionControlsListener> listeners;
    
    public SolutionControlsNode( AqueousSolution solution ) {
        this();
        solution.addSolutionListener( new ModelViewController( solution, this ) );
        this.addSolutionControlsListener( new ViewModelController( this, solution ) );
    }

    private SolutionControlsNode() {
        super();
        
        listeners = new ArrayList<SolutionControlsListener>();
        
        PNode soluteLabel = new LabelNode( ABSStrings.LABEL_SOLUTE );
        addChild( soluteLabel );
        
        soluteComboBox = new SoluteComboBox();
        soluteComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    notifySoluteChanged();
                }
            }
        } );
        PSwing soluteComboBoxWrapper = new PSwing( soluteComboBox );
        addChild( soluteComboBoxWrapper );
        
        PNode concentrationLabel = new LabelNode( ABSStrings.LABEL_CONCENTRATION );
        addChild( concentrationLabel );
        
        concentrationControlNode = new ConcentrationControlNode( ABSConstants.CONCENTRATION_RANGE );
        concentrationControlNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyConcentrationChanged();
            }
        });
        addChild( concentrationControlNode );
        
        strengthLabelNode = new LabelNode( ABSStrings.LABEL_STRENGTH );
        addChild( strengthLabelNode );
        
        strengthSliderNode = new StrengthSliderNode( ABSConstants.WEAK_STRENGTH_RANGE, ABSConstants.STRONG_STRENGTH_RANGE );
        strengthSliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyStrengthChanged();
            }
        });
        addChild( strengthSliderNode );
        
        // layout
        double xOffset = 0;
        double yOffset = 0;
        soluteLabel.setOffset( xOffset, yOffset );
        xOffset = soluteLabel.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = soluteLabel.getFullBoundsReference().getCenterY() - ( soluteComboBoxWrapper.getFullBoundsReference().getHeight() / 2 );
        soluteComboBoxWrapper.setOffset( xOffset, yOffset );
        xOffset = soluteLabel.getXOffset();
        yOffset = soluteComboBoxWrapper.getFullBoundsReference().getMaxY() + Y_SPACING;
        concentrationLabel.setOffset( xOffset, yOffset );
        xOffset = concentrationLabel.getXOffset() + 15;
        yOffset = concentrationLabel.getFullBoundsReference().getMaxY() - ( concentrationControlNode.getFullBoundsReference().getY() - concentrationControlNode.getYOffset() ) + 5;
        concentrationControlNode.setOffset( xOffset, yOffset );
        xOffset = concentrationLabel.getXOffset();
        yOffset = concentrationControlNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        strengthLabelNode.setOffset( xOffset, yOffset );
        xOffset = strengthLabelNode.getXOffset() + 15;
        yOffset = strengthLabelNode.getFullBoundsReference().getMaxY() - ( strengthSliderNode.getFullBoundsReference().getY() - strengthSliderNode.getYOffset() );
        strengthSliderNode.setOffset( xOffset, yOffset );
        
        // separator
        final double sepWidth = this.getFullBoundsReference().getWidth();
        PNode sepNode1 = new SeparatorNode( sepWidth );
        addChild( sepNode1 );
        xOffset = 0;
        yOffset = concentrationLabel.getFullBoundsReference().getMinY() - 3;
        sepNode1.setOffset( xOffset, yOffset );
        
        // separator
        PNode sepNode2 = new SeparatorNode( sepWidth );
        addChild( sepNode2 );
        xOffset = 0;
        yOffset = strengthLabelNode.getFullBoundsReference().getMinY() - 3;
        sepNode2.setOffset( xOffset, yOffset );
        
        // put a background behind the entire panel
        PNode backgroundNode = new BackgroundNode( this );
        addChild( 0, backgroundNode );
    }
    
    public void setConcentration( double concentration ) {
        concentrationControlNode.setValue( concentration );
    }
    
    public void setSolute( Solute solute ) {
        soluteComboBox.setSolute( solute );
    }
    
    public Solute getSolute() {
        return soluteComboBox.getSolute();
    }
    
    //XXX
    public void resetSolute() {
        soluteComboBox.reset();
    }
    
    public double getConcentration() {
        return concentrationControlNode.getValue();
    }
    
    public void setStrength( double strength ) {
        strengthSliderNode.setValue( strength );
    }
    
    public double getStrength() {
        return strengthSliderNode.getValue();
    }
    
    public void setConcentrationControlVisible( boolean visible ) {
        concentrationControlNode.setVisible( visible );
    }
    
    public void setStrengthControlVisible( boolean visible ) {
        strengthLabelNode.setVisible( visible );
        strengthSliderNode.setVisible( visible );
    }
    
    public void setStrengthControlEnabled( boolean enabled ) {
        //TODO implement this
//        strengthSliderNode.setEnabled( enabled );
    }
    
    private static class LabelNode extends PText {
        
        public LabelNode( String text ) {
            super( text );
            setPickable( false );
            setFont( LABEL_FONT );
            setTextPaint( LABEL_COLOR );
        }
    }
    
    private static class BackgroundNode extends PPath {
        public BackgroundNode( PNode node ) {
            super();
            setPickable( false );
            final double m = BACKGROUND_MARGIN;
            final double r = BACKGROUND_CORNER_RADIUS;
            PBounds b = node.getFullBounds();
            setPathTo( new RoundRectangle2D.Double( b.getX() - m, b.getY() - m, b.getWidth() + ( 2 * m ), b.getHeight() + ( 2 * m ), r, r ) );
            setStroke( BACKGROUND_STROKE );
            setStrokePaint( BACKGROUND_STROKE_COLOR );
            setPaint( BACKGROUND_COLOR );
        }
    }
    
    private static class SeparatorNode extends PPath {
        public SeparatorNode( double length ) {
            super( new Line2D.Double( 0, 0, length, 0 ) );
            setPickable( false );
            setStrokePaint( SEPARATOR_COLOR );
            setStroke( SEPARATOR_STROKE );
        }
    }
    
    public interface SolutionControlsListener {
        public void soluteChanged();
        public void concentrationChanged();
        public void strengthChanged();
    }
    
    public void addSolutionControlsListener( SolutionControlsListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSolutionControlsListener( SolutionControlsListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifySoluteChanged() {
        Iterator<SolutionControlsListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().soluteChanged();
        }
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SolutionControlsListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
    
    private void notifyStrengthChanged() {
        Iterator<SolutionControlsListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
    
    /*
     * View changes are propagated to the model.
     */
    private static class ViewModelController implements SolutionControlsListener {
        
        private final SolutionControlsNode solutionControls;
        private final AqueousSolution solution;
        
        public ViewModelController( SolutionControlsNode solutionControls, AqueousSolution solution ) {
            this.solutionControls = solutionControls;
            this.solution = solution;
        }

        public void concentrationChanged() {
            solution.getSolute().setInitialConcentration( solutionControls.getConcentration() );
        }

        public void soluteChanged() {
            solution.setSolute( solutionControls.getSolute() );
        }

        public void strengthChanged() {
            Solute solute = solution.getSolute();
            if ( solute instanceof ICustomSolute ) {
                ( (ICustomSolute) solute ).setStrength( solutionControls.getStrength() );
            }
        }
    }
    
    /*
     * Model changes are propagated to the view.
     */
    private static class ModelViewController implements SolutionListener {
        
        private final AqueousSolution solution;
        private final SolutionControlsNode solutionControls;
        
        public ModelViewController( AqueousSolution solution, SolutionControlsNode solutionControls ) {
            this.solution = solution;
            this.solutionControls = solutionControls;
        }

        public void concentrationChanged() {
            solutionControls.setConcentration( solution.getSolute().getInitialConcentration() );
        }

        public void soluteChanged() {
            Solute solute = solution.getSolute();
            solutionControls.setSolute( solute );
            solutionControls.setStrengthControlEnabled( solute instanceof ICustomSolute );
            solutionControls.setConcentrationControlVisible( ! (solute instanceof NullSolute ) );
            solutionControls.setStrengthControlVisible( ! ( solute instanceof NullSolute ) );
            if ( ! (solute instanceof NullSolute) ) {
                solutionControls.setConcentration( solute.getInitialConcentration() );
                solutionControls.setStrength( solute.getStrength() );
            }
        }

        public void strengthChanged() {
            solutionControls.setStrength( solution.getSolute().getStrength() );
        }
        
    }
    
    // test
    public static void main( String[] args ) {

        Dimension canvasSize = new Dimension( 600, 300 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( canvasSize );

        SolutionControlsNode controlsNode = new SolutionControlsNode();
        canvas.getLayer().addChild( controlsNode );
        controlsNode.setOffset( 50, 50 );

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
