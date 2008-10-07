import idx3d.*;
import java.awt.*;
import java.applet.*;

public class demo4 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;
	
	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			idx3d_Texture bkgrd=idx3d_Texture.blendTopDown(
				idx3d_TextureFactory.CHECKERBOARD(this.size().width,this.size().height,4,0x000000,0x999999),
				new idx3d_Texture(getDocumentBase(),"idxbkgrd.jpg"));
			scene.environment.setBackground(bkgrd);
			
			idx3d_Texture envmap=new idx3d_Texture(getDocumentBase(),"skymap.jpg");
			idx3d_Texture texture=new idx3d_Texture(getDocumentBase(),"spectrum.jpg");
			
			idx3d_Material mode1=new idx3d_Material(0x0066FF);
			mode1.setFlat(true);
			
			idx3d_Material mode2=new idx3d_Material(0x330099);
			mode2.setEnvmap(envmap);
			mode2.setReflectivity(63);
			
			idx3d_Material mode3=new idx3d_Material();
			mode3.setEnvmap(envmap);
			
			idx3d_Material mode4=new idx3d_Material(getDocumentBase(),"glass.material");
			mode4.setTransparency(88);
			
			idx3d_Material mode5=new idx3d_Material(texture);
			
			idx3d_Material mode6=new idx3d_Material(texture);
			mode6.setEnvmap(envmap);
			mode6.setReflectivity(96);
			
			idx3d_Material mode7=new idx3d_Material(texture);
			mode7.setEnvmap(envmap);
			mode7.setReflectivity(96);
			mode7.setTransparency(64);
			
			scene.addMaterial("Mode1",mode1);
			scene.addMaterial("Mode2",mode2);
			scene.addMaterial("Mode3",mode3);
			scene.addMaterial("Mode4",mode4);
			scene.addMaterial("Mode5",mode5);
			scene.addMaterial("Mode6",mode6);
			scene.addMaterial("Mode7",mode7);
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,144,120));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(0.6f,-1f,1f),0x332211,100,40));
			scene.addLight("Light3",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xccaa88,200,120));
					
			scene.environment.ambient=0x554433;
			scene.defaultCamera.setFov(120f);
				
			scene.addObject("Torusknot",idx3d_ObjectFactory.TORUSKNOT(5f,1f,0.28f,1.2f,0.48f,0.8f,88,9));
			scene.object("Torusknot").rotate(0.2f,3.5f,-0.5f);
			scene.object("Torusknot").setMaterial(scene.material("Mode4"));
			scene.object("Torusknot").scale(0.72f);
			
	}

	public synchronized void paint(Graphics g)
	{
	}

	public void start()
	{
		if (idx_Thread == null)
		{
			idx_Thread = new Thread(this);
			idx_Thread.start();
		}
	}
	
	public void stop()
	{
		if (idx_Thread != null)
		{
			idx_Thread.stop();
			idx_Thread = null;
		}
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try{ idx_Thread.sleep(10); }
			catch (InterruptedException e){}
		}
	}

	public synchronized void update(Graphics g)
	{
		if (autorotation) scene.object("Torusknot").rotate(-0.01f,0.02f,0.05f);
		
		scene.render();
		g.drawImage(scene.getImage(),0,0,this);
	}		

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		setMovingCursor();
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='1') {scene.object("Torusknot").setMaterial(scene.material("Mode1")); return true; }
		if ((char)key=='2') {scene.object("Torusknot").setMaterial(scene.material("Mode2")); return true; }
		if ((char)key=='3') {scene.object("Torusknot").setMaterial(scene.material("Mode3")); return true; }
		if ((char)key=='4') {scene.object("Torusknot").setMaterial(scene.material("Mode4")); return true; }
		if ((char)key=='5') {scene.object("Torusknot").setMaterial(scene.material("Mode5")); return true; }
		if ((char)key=='6') {scene.object("Torusknot").setMaterial(scene.material("Mode6")); return true; }
		if ((char)key=='7') {scene.object("Torusknot").setMaterial(scene.material("Mode7")); return true; }
		
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		if (evt.modifiers == Event.META_MASK) scene.shift(-dy,-dx,0);
		else scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
		return true;
	}
	
	private void setMovingCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);
	}
	
	private void setNormalCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);		
	}
	
	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}
}
