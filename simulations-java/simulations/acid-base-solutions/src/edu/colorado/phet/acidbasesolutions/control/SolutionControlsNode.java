package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Controls related to the solution in the beaker.
 * Includes solute selection, solute concentration and solute strength.
 * This control panel dynamically reconfigures itself based on the type of solute.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionControlsNode extends PhetPNode {
    
    private static final double X_SPACING = 3;
    private static final double Y_SPACING = 10;
    private static final Font LABEL_FONT = new PhetFont( 14 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final double BACKGROUND_MARGIN = 10;
    private static final double BACKGROUND_CORNER_RADIUS = 25;
    private static final Color BACKGROUND_COLOR = ABSColors.COLOR_PANEL_BACKGROUND;
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color SEPARATOR_COLOR = BACKGROUND_COLOR.darker();
    private static final Stroke SEPARATOR_STROKE = new BasicStroke( 1f );
    private static final String STRENGTH_LABEL_PATTERN = ABSStrings.LABEL_STRENGTH;
    
    private final SoluteComboBox soluteComboBox;
    private final ConcentrationControlNode concentrationControlNode;
    private final LabelNode concentrationLabelNode;
    private final LabelNode strengthLabelNode;
    private final StrengthSliderNode strengthSliderNode;
    private final ArrayList<SolutionControlsListener> listeners;
    
    /**
     * Public constructor, handles connection to model.
     * @param canvas
     * @param solution
     */
    public SolutionControlsNode( PSwingCanvas canvas, AqueousSolution solution ) {
        this( canvas );
        solution.addSolutionListener( new ModelViewController( solution, this ) );
        this.addSolutionControlsListener( new ViewModelController( this, solution ) );
        setSolute( solution.getSolute() );
    }

    /*
     * Private constructor, has no knowledge of the model.
     */
    private SolutionControlsNode( PSwingCanvas canvas ) {
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
        soluteComboBox.setEnvironment( soluteComboBoxWrapper, canvas ); // hack required by PComboBox
        
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
    
    public void setSoluteComboBoxEnabled( boolean enabled ) {
        soluteComboBox.setEnabled( enabled );
    }
    
    public void setConcentrationControlEnabled( boolean enabled ) {
        concentrationControlNode.setEnabled( enabled );
    }
    
    public void setStrengthControlEnabled( boolean enabled ) {
        strengthSliderNode.setEnabled( enabled );
    }
    
    public void setSolute( Solute solute ) {
        soluteComboBox.setSelectedSoluteName( solute.getName() );
        
        // concentration control is hidden for no solute
        setConcentrationControlVisible( ! ( solute instanceof NoSolute ) );
        
        // strength slider is hidden for no solute
        setStrengthControlVisible( ! ( solute instanceof NoSolute ) );
        
        // strength control is enabled only for custom solutes
        strengthSliderNode.setEnabled( solute instanceof ICustomSolute );
        
        // strength K symbol
        setStrengthSymbol( solute );
        
        // set concentration and strength
        if ( !( solute instanceof NoSolute ) ) {
            setConcentration( solute.getConcentration() );
            setStrength( solute.getStrength() );
        }
    }
    
    public Solute getSolute() {
        Solute solute = SoluteFactory.createSolute( soluteComboBox.getSelectedSoluteName() );
        // use existing concentration for all solutes
        solute.setConcentration( getConcentration() );
        // use existing strength for custom solutes
        if ( solute instanceof ICustomSolute ) {
            ( (ICustomSolute) solute ).setStrength( getStrength() );
        }
        return solute;
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
    
    private void setConcentrationControlVisible( boolean visible ) {
        concentrationLabelNode.setVisible( visible );
        concentrationControlNode.setVisible( visible );
    }
    
    private void setStrengthControlVisible( boolean visible ) {
        strengthLabelNode.setVisible( visible );
        strengthSliderNode.setVisible( visible );
    }
    
    private void setStrengthSymbol( Solute solute ) {
        strengthLabelNode.setHTML( MessageFormat.format( STRENGTH_LABEL_PATTERN, solute.getStrengthSymbol() ) );
    }
    
    private static class LabelNode extends HTMLNode {
        
        public LabelNode( String text ) {
            super( text );
            setPickable( false );
            setFont( LABEL_FONT );
            setHTMLColor( LABEL_COLOR );
        }
        
        public void setHTML( String text ) {
            super.setHTML( HTMLUtils.toHTMLString( text ) );
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
            solutionControls.setStrengthSymbol( solution.getSolute() );
        }
        
        public void concentrationChanged() {
            solutionControls.setConcentration( solution.getSolute().getConcentration() );
        }

        public void strengthChanged() {
            solutionControls.setStrength( solution.getSolute().getStrength() );
        }
        
    }
}
