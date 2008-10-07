import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo12 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;
	idx3d_FXLensFlare lensFlare;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			
			scene.addMaterial("Sky",new idx3d_Material(getDocumentBase(),"nemesis.material"));
			scene.material("Sky").setFlat(true);
			
			scene.addMaterial("Glass",new idx3d_Material(getDocumentBase(),"glass.material"));
			scene.material("Glass").setTransparency(60);
			
			scene.addMaterial("Stone",new idx3d_Material(new idx3d_Texture(getDocumentBase(),"stone.jpg")));
			
			scene.addMaterial("Chrome",new idx3d_Material(getDocumentBase(),"chrome.material"));
	
			idx3d_Texture bluePlasma=new idx3d_Material(getDocumentBase(),"plasma.material").getTexture();
			bluePlasma.toAverage();
			idx3d_Texture redPlasma=bluePlasma.getClone();
			idx3d_Texture greenPlasma=bluePlasma.getClone();
			int[] redPalette={0,0xFF0000};
			int[] greenPalette={0,0xFF00};
			int[] bluePalette={0,0xFF};
						
			redPlasma.colorize(idx3d_Color.makeGradient(redPalette,256));
			greenPlasma.colorize(idx3d_Color.makeGradient(greenPalette,256));
			bluePlasma.colorize(idx3d_Color.makeGradient(bluePalette,256));
			
			scene.addMaterial("Red Plasma",new idx3d_Material(redPlasma));
			scene.addMaterial("Green Plasma",new idx3d_Material(greenPlasma));
			scene.addMaterial("Blue Plasma",new idx3d_Material(bluePlasma));
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(-0.2f,-0.2f,1f),0xFFFFFF,120,120));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,-4f),0x0066FF,120,80));
			
			
			try{
				new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),"plasmacore.3ds"),scene);
				scene.normalize();
				
				//scene.object("Sky").setMaterial(scene.material("Sky"));
				idx3d_TextureProjector.projectTop(scene.object("Sky"));
				//scene.object("Floor1").setMaterial(scene.material("Chrome"));
				//scene.object("Floor2").setMaterial(scene.material("Chrome"));
				//scene.object("Axis").setMaterial(scene.material("Chrome"));
				scene.object("Peak1").setMaterial(scene.material("Stone"));
				scene.object("Peak2").setMaterial(scene.material("Stone"));
				scene.object("Peak3").setMaterial(scene.material("Stone"));
				//scene.object("Core1").setMaterial(scene.material("Red Plasma"));
				//scene.object("Core2").setMaterial(scene.material("Green Plasma"));
				//scene.object("Core3").setMaterial(scene.material("Blue Plasma"));
				
				
				//scene.object("Demon").matrixMeltdown();
				//scene.defaultCamera.lookAt(scene.object("Demon").getCenter().transform(scene.matrix));
				
				//scene.rotate(3.84159265f/2,0f,0f);
				scene.scale(3f);
				scene.defaultCamera.setFov(120);
				//scene.shift(0,0.32f,0);
				
				lensFlare=new idx3d_FXLensFlare("Core", scene,true);
				lensFlare.preset3();

			}
			catch(Exception e){System.out.println(e+"");}
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
			try
			{
				idx_Thread.sleep(10);
			}
			catch (InterruptedException e){}
		}
	}

	public synchronized void update(Graphics g)
	{		
		if (autorotation)
		{
			float speed=0.64f;
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/2000)/8;
			scene.rotate(speed*dx,speed*dy,0f);
			//scene.defaultCamera.setFov(80f+30f*(float)Math.sin(((float)System.currentTimeMillis())/1000));
		}
		float speed=0.1f;
		scene.object("Core1").rotate(0.02f,speed,speed);
		scene.object("Core2").rotate(speed,0.05f,speed);
		scene.object("Core3").rotate(speed,speed,-0.03f);
			
		scene.render();
		lensFlare.apply();
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
		if ((char)key=='m') {for (int i=0;i<scene.objects;i++) scene.object[i].meshSmooth(); return true; }
		
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }
		
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		scene.rotate(dx,dy,0);
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
