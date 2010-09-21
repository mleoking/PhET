import org.aswing.ASColor;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.FloorPane;
import org.aswing.geom.Dimension;
import org.aswing.JButton;
import org.aswing.JComboBox;
import org.aswing.JFrame;
import org.aswing.JLoadPane;
import org.aswing.JScrollPane;
import org.aswing.JViewport;

import test.CenterJViewport;
import test.CenterViewportLayout;
/**
 * @author Igor Sadovskiy
 */
class test.LoadPaneTest2 {

    private static var instance:LoadPaneTest2;

    private static var FIT_PANE:String = "Fit Pane";
    private static var FIT_WIDTH:String = "Fit Width";
    private static var FIT_HEIGHT:String = "Fit Height";
    private static var STRETCH_PANE:String = "Stretch Pane";

    public static function main(Void):Void {
        Stage.scaleMode = "noScale";
        Stage.align = "TL";
        try{
            trace("try LoadPaneTest2");
            if (instance == null) {
                instance = new LoadPaneTest2(); 
            }        
        } catch(e) {
            trace("error : " + e);
        }       
    }   
    
    private var frame:JFrame;
    private var scrollPane:JScrollPane;
    private var imageReview:JLoadPane;
    private var zoomCombo:JComboBox;
    private var reloadBtn:JButton; 
    
    public function LoadPaneTest2(Void) {
        
        trace("ImageReviewTest");
        
        zoomCombo = new JComboBox([FIT_PANE,FIT_WIDTH,FIT_HEIGHT,STRETCH_PANE,25,50,75,100,150,200,300]);
        zoomCombo.setSelectedItem(100);
        zoomCombo.addActionListener(onZoomChanged, this);
        
        reloadBtn = new JButton("Reload");
        reloadBtn.addActionListener(onReloadClick, this);
        
        imageReview = new JLoadPane();
        imageReview.setBorder(new LineBorder(null, ASColor.BLUE));
        imageReview.setPrefferSizeStrategy(JLoadPane.PREFER_SIZE_IMAGE);
        imageReview.setHorizontalAlignment(JLoadPane.CENTER); 
        imageReview.setVerticalAlignment(JLoadPane.CENTER); 
		imageReview.addEventListener(JLoadPane.ON_LOAD_INIT, onImageLoaded, this);
        imageReview.setPath("../res/test.jpg");
        
        scrollPane = new JScrollPane(imageReview);
        scrollPane.getViewport().getViewportPane().setBorder(new LineBorder(null, ASColor.RED));
        scrollPane.getViewport().getViewportPane().addEventListener(Component.ON_RESIZED, onScrollPaneResized, this);
        
        frame = new JFrame();
        frame.getContentPane().append(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().append(zoomCombo, BorderLayout.NORTH);
        frame.getContentPane().append(reloadBtn, BorderLayout.SOUTH);
        frame.setSize(800, 600);
        frame.show();
    }
    
    private function onImageLoaded(Void):Void {
    	//fitScrollPane();	
    } 
    
    private function onZoomChanged(Void):Void {
        var scale = zoomCombo.getSelectedItem();
        
        if (! isNaN(scale)) {
            imageReview.setCustomScale(scale);
        } else if (scale == FIT_PANE) {
            imageReview.setScaleMode(JLoadPane.SCALE_FIT_PANE);
        } else if (scale == FIT_WIDTH) {
            imageReview.setScaleMode(JLoadPane.SCALE_FIT_WIDTH);
        } else if (scale == FIT_HEIGHT) {
            imageReview.setScaleMode(JLoadPane.SCALE_FIT_HEIGHT);
        } else if (scale == STRETCH_PANE) {
            imageReview.setScaleMode(JLoadPane.SCALE_STRETCH_PANE);
        }       
        fitScrollPane();    
    }
    
    private function fitScrollPane(Void):Void {
    	
        var viewport:Component = scrollPane.getViewport().getViewportPane();
        var viewSize:Dimension = viewport.getInsets().getInsideSize(viewport.getSize());
        
        if (imageReview.getScaleMode() == FloorPane.SCALE_FIT_HEIGHT || imageReview.getScaleMode() == FloorPane.SCALE_FIT_WIDTH) {
         	imageReview.setSize(viewSize);
            imageReview.setPreferredSize(null);
        } else if (imageReview.getScaleMode() == FloorPane.SCALE_FIT_PANE || imageReview.getScaleMode() == FloorPane.SCALE_STRETCH_PANE) 
        {
            imageReview.setPreferredSize(viewSize);
        } 
        else  
        {
            imageReview.setPreferredSize(null);
        }
        imageReview.revalidate();    	
    }

    private function onScrollPaneResized(Void):Void {
        fitScrollPane();
    }
    
    private function onReloadClick(Void):Void {
        //imageReview.reload();
        var vp:JViewport = JViewport(scrollPane.getViewport());
        vp.setHorizontalAlignment(JViewport.RIGHT);
        vp.setVerticalAlignment(JViewport.TOP);
    }
}