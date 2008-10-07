import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class LensFlareEditor extends Applet
{
	idx3d_Scene scene;
	idx3d_FXLensFlare lensFlare;
	idx3d_FXLensFlare backup;
	Image doubleBuffer=null;
	boolean initialized=false;
	
	int oldx=0;
	int oldy=0;
	
	Button 	reset, preset1, preset2, preset3, rebuild,
		addGlow,addStrike,addRing,addSecs,addRays;
	Label	glowLabel,strikeLabel,ringLabel,secsLabel,raysLabel;
	TextField	glowSize,glowColor,strikeWidth,strikeHeight,strikeColor,
		ringSize,ringColor,secsNum,secsSize,secsSizeDelta,secsColor,secsColorDelta,
		raysSize,raysNum,raysRad,raysColor;
	
	public void init()
	{
		scene=new idx3d_Scene(this.size().width,this.size().height-120);
		lensFlare=new idx3d_FXLensFlare("LensFlare", scene,false);
		lensFlare.setPos(new idx3d_Vector(0.5f,-0.5f,0.5f));
		lensFlare.preset2();
	}

	public void paint(Graphics g)
	{
		repaint();
	}
	
	private void initGUI()
	{
		initialized=true;
		
		reset=new Button("Reset");
		preset1=new Button("Preset#1");
		preset2=new Button("Preset#2");
		preset3=new Button("Preset#3");
		rebuild=new Button("Rebuild !");
		addGlow=new Button("Add !");
		addStrike=new Button("Add !");
		addRing=new Button("Add !");
		addSecs=new Button("Add !");
		addRays=new Button("Add !");
		glowLabel=new Label("Glow");
		strikeLabel=new Label("Strike");
		ringLabel=new Label("Ring");
		secsLabel=new Label("Secs");
		raysLabel=new Label("Rays");
		glowSize=new TextField("Size");
		glowColor=new TextField("Color");
		strikeWidth=new TextField("Width");
		strikeHeight=new TextField("Height");
		strikeColor=new TextField("Color");
		ringSize=new TextField("Size");
		ringColor=new TextField("Color");
		secsNum=new TextField("Num");
		secsSize=new TextField("Size");
		secsSizeDelta=new TextField("SizeDelta");
		secsColor=new TextField("Color");
		secsColorDelta=new TextField("ColorDelta");
		raysSize=new TextField("Size");
		raysNum=new TextField("Num");
		raysRad=new TextField("Rad");
		raysColor=new TextField("Color");
			
		add(reset);
		add(preset1);
		add(preset2);
		add(preset3);
		add(rebuild);
		add(addGlow);
		add(addStrike);
		add(addRing);
		add(addSecs);
		add(addRays);
		add(glowLabel);
		add(strikeLabel);
		add(ringLabel);
		add(secsLabel);
		add(raysLabel);
		add(glowSize);
		add(glowColor);
		add(strikeWidth);
		add(strikeHeight);
		add(strikeColor);
		add(ringSize);
		add(ringColor);
		add(secsNum);
		add(secsSize);
		add(secsSizeDelta);
		add(secsColor);
		add(secsColorDelta);
		add(raysSize);
		add(raysNum);
		add(raysRad);
		add(raysColor);
	}

	public void update(Graphics g)
	{
		if (!initialized) initGUI();
		
		int w=this.size().width;
		int h=this.size().height;
		
		reset.reshape(0,h-120,w/5,20);
		preset1.reshape(w/5,h-120,w/5,20);
		preset2.reshape(w*2/5,h-120,w/5,20);
		preset3.reshape(w*3/5,h-120,w/5,20);
		rebuild.reshape(w*4/5,h-120,w/5,20);
		addGlow.reshape(w-50,h-100,50,20);
		addStrike.reshape(w-50,h-80,50,20);
		addRing.reshape(w-50,h-60,50,20);
		addSecs.reshape(w-50,h-40,50,20);
		addRays.reshape(w-50,h-20,50,20);
		glowLabel.reshape(0,h-100,100,20);
		strikeLabel.reshape(0,h-80,100,20);
		ringLabel.reshape(0,h-60,100,20);
		secsLabel.reshape(0,h-40,100,20);
		raysLabel.reshape(0,h-20,100,20);
		glowSize.reshape(100,h-100,(w-150)/2,20);
		glowColor.reshape(100+(w-150)/2,h-100,(w-150)/2,20);
		strikeWidth.reshape(100,h-80,(w-150)/3,20);
		strikeHeight.reshape(100+(w-150)/3,h-80,(w-150)/3,20);
		strikeColor.reshape(100+(w-150)*2/3,h-80,(w-150)/3,20);
		ringSize.reshape(100,h-60,(w-150)/2,20);
		ringColor.reshape(100+(w-150)/2,h-60,(w-150)/2,20);
		secsNum.reshape(100,h-40,(w-150)/5,20);
		secsSize.reshape(100+(w-150)/5,h-40,(w-150)/5,20);
		secsSizeDelta.reshape(100+(w-150)*2/5,h-40,(w-150)/5,20);
		secsColor.reshape(100+(w-150)*3/5,h-40,(w-150)/5,20);
		secsColorDelta.reshape(100+(w-150)*4/5,h-40,(w-150)/5,20);
		raysSize.reshape(100,h-20,(w-150)/4,20);
		raysNum.reshape(100+(w-150)/4,h-20,(w-150)/4,20);
		raysRad.reshape(100+(w-150)*2/4,h-20,(w-150)/4,20);
		raysColor.reshape(100+(w-150)*3/4,h-20,(w-150)/4,20);
		
		scene.render();
		lensFlare.apply();
		doubleBuffer=scene.getImage();
		g.drawImage(doubleBuffer,0,0,this);
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		float dx=(float)(oldy-y)/50;
		float dy=(float)(x-oldx)/50;
		scene.rotate(-dx,-dy,0);
		oldx=x;
		oldy=y;
		repaint();
		return true;
	}
	
	public boolean action(Event evt, Object obj)
	{
		if (evt.target==reset) { lensFlare.clear(); repaint(); return true; }
		if (evt.target==preset1) { lensFlare.preset1(); repaint(); return true; }
		if (evt.target==preset2) { lensFlare.preset2(); repaint(); return true; }
		if (evt.target==preset3) { lensFlare.preset3(); repaint(); return true; }
		if (evt.target==addGlow) { addGlow(); repaint(); return true; }
		if (evt.target==addStrike) { addStrike(); repaint(); return true; }
		if (evt.target==addRing) { addRing(); repaint(); return true; }
		if (evt.target==addSecs) { addSecs(); repaint(); return true; }
		if (evt.target==addRays) { addRays(); repaint(); return true; }
		if (evt.target==rebuild) { rebuild(); repaint(); return true; }
		return super.action(evt,obj);
	}
	
	private void rebuild()
	{
		lensFlare.clear();
		addGlow();
		addStrike();
		addRing();
		addSecs();
		addRays();
	}
		
	private int getInt(TextField text)
	{
		int val=0;
		try{ val=(int)Integer.parseInt(text.getText()); }
		catch(Exception ignored){}
		return val;
	}
	
	private int getHex(TextField text)
	{
		int val=0;
		try{ val=(int)Integer.parseInt(text.getText(),16); }
		catch(Exception ignored){}
		return val;
	}
	
	private void addGlow()
	{
		lensFlare.addGlow(getInt(glowSize),getHex(glowColor));
	}
	
	private void addStrike()
	{
		lensFlare.addStrike(getInt(strikeWidth),getInt(strikeHeight),getHex(strikeColor));
	}
	
	private void addRing()
	{
		lensFlare.addRing(getInt(ringSize),getHex(ringColor));
	}
	
	private void addSecs()
	{
		lensFlare.addSecs(getInt(secsNum),getInt(secsSize),getInt(secsSizeDelta),getHex(secsColor),getInt(secsColorDelta));
	}
	
	private void addRays()
	{
		lensFlare.addRays(getInt(raysSize),getInt(raysNum),getInt(raysRad),getHex(raysColor));
	}
	
}
