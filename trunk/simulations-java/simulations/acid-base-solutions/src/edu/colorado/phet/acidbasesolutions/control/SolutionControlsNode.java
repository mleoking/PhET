package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
    private final LabelNode concentrationLabelNode;
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
        
        concentrationLabelNode = new LabelNode( ABSStrings.LABEL_CONCENTRATION );
        addChild( concentrationLabelNode );
        
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
        concentrationLabelNode.setOffset( xOffset, yOffset );
        xOffset = concentrationLabelNode.getXOffset() + 15;
        yOffset = concentrationLabelNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( concentrationControlNode ) + 5;
        concentrationControlNode.setOffset( xOffset, yOffset );
        xOffset = concentrationLabelNode.getXOffset();
        yOffset = concentrationControlNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        strengthLabelNode.setOffset( xOffset, yOffset );
        xOffset = strengthLabelNode.getXOffset() + 15;
        yOffset = strengthLabelNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( strengthSliderNode );
        strengthSliderNode.setOffset( xOffset, yOffset );
        
        // separator
        final double sepWidth = this.getFullBoundsReference().getWidth();
        PNode sepNode1 = new SeparatorNode( sepWidth );
        addChild( sepNode1 );
        xOffset = 0;
        yOffset = concentrationLabelNode.getFullBoundsReference().getMinY() - 3;
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
    
    public void setSolute( Solute solute ) {
        soluteComboBox.setSolute( solute );
        
        // concentration slider is hidden and reads "zero" for no solute
        setConcentrationControlZero( solute instanceof NoSolute );
        
        // strength slider is hidden for no solute
        setStrengthControlVisible( ! ( solute instanceof NoSolute ) );
        
        // strength control is enabled only for custom solutes
        strengthSliderNode.setEnabled( solute instanceof ICustomSolute );
        
        // for specific (immutable) solutes, set strength control
        if ( !( solute instanceof ICustomSolute || solute instanceof NoSolute ) ) {
            setStrength( solute.getStrength() );
        }
        
        // new solute uses the existing concentration
        solute.setConcentration( getConcentration() );
        
        // custom solute uses the existing strength
        if ( solute instanceof ICustomSolute ) {
            ( (ICustomSolute) solute ).setStrength( getStrength() );
        }
    }
    
    public Solute getSolute() {
        return SoluteFactory.createSolute( soluteComboBox.getSoluteName() );
    }
    
    public void setConcentration( double concentration ) {
        concentrationControlNode.setValue( concentration );
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
    
    private void setConcentrationControlZero( boolean isZero ) {
        concentrationControlNode.setVisible( !isZero );
        if ( isZero ) {
            concentrationLabelNode.setText( ABSStrings.LABEL_CONCENTRATION + " 0.0" );
        }
        else {
            concentrationLabelNode.setText( ABSStrings.LABEL_CONCENTRATION );
        }
    }
    
    private void setStrengthControlVisible( boolean visible ) {
        strengthLabelNode.setVisible( visible );
        strengthSliderNode.setVisible( visible );
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
            solution.getSolute().setConcentration( solutionControls.getConcentration() );
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

        public void soluteChanged() {
            solutionControls.setSolute( solution.getSolute() );
        }
        
        public void concentrationChanged() {
            solutionControls.setConcentration( solution.getSolute().getConcentration() );
        }

        public void strengthChanged() {
            solutionControls.setStrength( solution.getSolute().getStrength() );
        }
        
    }
}
