import idx3d.*;
import java.awt.*;
import java.applet.*;
import java.io.*;

public final class idx3dMaterialLab extends Frame
{
	idx3d_Scene objectView;
	idx3d_Screen textureScreen;
	idx3d_Screen envmapScreen;
	
	Image doubleBuffer1=null; //object View
	Image doubleBuffer2=null; //texture
	Image doubleBuffer3=null; //envmap
	
	idx3d_Material material;
		
	int oldx=0;
	int oldy=0;
	boolean initialized=false;
		
	Button 		readMaterial,writeMaterial,openScene,
			projectTop,projectFrontal,
			generateTexture,importTexture,noTexture,
			generateEnvmap,importEnvmap,noEnvmap;
	Label		project,settings,label1,label2,label3,label4,label5,transp,refl;
	Scrollbar		transparency,reflectivity;
	TextField		color;
	Checkbox		flat;
	
	Font normal;
	
	public static void main(String args[])
	{
		new idx3dMaterialLab();
	}
	
	
	public idx3dMaterialLab()
	{
		super("idx3d Material Lab");
		resize(500+insets().left+insets().right,540+insets().top+insets().bottom);
		setResizable(false);
		setBackground(Color.lightGray);
		normal=new Font("Helvetica",0,11);
		
		material=new idx3d_Material(0x66FF);
		material.setTransparency(63);
		
		objectView=new idx3d_Scene(280,240);
		textureScreen=new idx3d_Screen(200,200);
		envmapScreen=new idx3d_Screen(200,200);
		
		objectView.setAmbient(0x333333);
		objectView.environment.setBackground(idx3d_TextureFactory.CHECKERBOARD(160,120,2,0x000000,0x999999));
		objectView.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,120));			
		//objectView.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFFFFF,160,200));
		
		idx3d_Object trefoil=idx3d_ObjectFactory.TORUSKNOT(2f,3f,0.4f,1.2f,0.48f,1.2f,120,8);
		objectView.addObject("Trefoil",trefoil);
		objectView.object("Trefoil").rotate(0.2f,3.5f,-0.5f);
		objectView.object("Trefoil").setMaterial(material);
		objectView.object("Trefoil").scale(0.4f);
		objectView.object("Trefoil").removeDuplicateVertices();
		idx3d_TextureProjector.projectTop(objectView.object("Trefoil"));
		
		show();
		
	}

	public void paint(Graphics g)
	{
		repaint();
	}
	
	private void initGUI()
	{
		initialized=true;

		readMaterial=new Button("Read Material");
		writeMaterial=new Button("Write Material");
		generateTexture=new Button("Generate");
		importTexture=new Button("Import");
		noTexture=new Button("None");
		generateEnvmap=new Button("Generate");
		importEnvmap=new Button("Import");
		noEnvmap=new Button("None");
		openScene=new Button("Import Scene from 3ds File");
		projectTop=new Button("TOP");
		projectFrontal=new Button("FRONTAL");
		project=new Label("Project Texture:");
		settings=new Label("Material Settings:");
		label1=new Label("Color:");
		label2=new Label("Transparency:");
		label3=new Label("Reflectivity:");
		label4=new Label("Texture:");
		label5=new Label("Envmap:");
		transp=new Label();
		refl=new Label();
		
		transparency=new Scrollbar(0,0,1,0,256);
		reflectivity=new Scrollbar(0,255,1,0,256);
		color=new TextField();
		flat=new Checkbox("Flatshaded");
		
		readMaterial.setFont(normal);
		writeMaterial.setFont(normal);
		generateTexture.setFont(normal);
		importTexture.setFont(normal);
		noTexture.setFont(normal);
		generateEnvmap.setFont(normal);
		importEnvmap.setFont(normal);
		noEnvmap.setFont(normal);
		openScene.setFont(normal);
		settings.setFont(normal);
		label1.setFont(normal);
		label2.setFont(normal);
		label3.setFont(normal);
		label4.setFont(normal);
		label5.setFont(normal);
		transp.setFont(normal);
		refl.setFont(normal);
		color.setFont(normal);
		flat.setFont(normal);
		projectTop.setFont(normal);
		projectFrontal.setFont(normal);
		project.setFont(normal);
		
		add(readMaterial);
		add(writeMaterial);
		add(generateTexture);
		add(importTexture);
		add(noTexture);
		add(generateEnvmap);
		add(importEnvmap);
		add(noEnvmap);
		add(openScene);
		add(settings);
		add(projectTop);
		add(projectFrontal);
		add(project);
		add(label1);
		add(label2);
		add(label3);
		add(label4);
		add(label5);
		add(transp);
		add(refl);
		add(color);
		add(transparency);
		add(reflectivity);
		add(flat);
	}
	
	public void update(Graphics g)
	{
		if (!initialized) initGUI();
		
		int xoffset=insets().left;
		int yoffset=insets().top;
		
		textureScreen.clear(0);
		textureScreen.draw(material.getTexture(),0,0,200,200);
		envmapScreen.clear(0);
		envmapScreen.draw(material.getEnvmap(),0,0,200,200);
		
		updateMaterial();
		objectView.render();
		g.drawImage(objectView.getImage(),xoffset,yoffset,this);
		g.drawImage(textureScreen.getImage(),xoffset+10,yoffset+270,this);
		g.drawImage(envmapScreen.getImage(),xoffset+230,yoffset+270,this);
		g.draw3DRect(xoffset,yoffset,280,240,true);
		g.draw3DRect(xoffset+10,yoffset+270,200,200,true);
		g.draw3DRect(xoffset+230,yoffset+270,200,200,true);
		
		readMaterial.reshape(xoffset+290,yoffset+180,200,20);
		writeMaterial.reshape(xoffset+290,yoffset+200,200,20);
		openScene.reshape(xoffset+290,yoffset+220,200,20);
		
		generateTexture.reshape(xoffset+10,yoffset+470,80,20);
		importTexture.reshape(xoffset+90,yoffset+470,80,20);
		noTexture.reshape(xoffset+170,yoffset+470,40,20);
		generateEnvmap.reshape(xoffset+230,yoffset+470,80,20);
		importEnvmap.reshape(xoffset+310,yoffset+470,80,20);
		noEnvmap.reshape(xoffset+390,yoffset+470,40,20);
		
		settings.reshape(xoffset+290,yoffset+0,100,20);
		label1.reshape(xoffset+290,yoffset+30,80,20);
		label2.reshape(xoffset+290,yoffset+50,100,20);
		label3.reshape(xoffset+290,yoffset+100,100,20);
		label4.reshape(xoffset+10,yoffset+250,100,20);
		label5.reshape(xoffset+230,yoffset+250,100,20);
		transp.reshape(xoffset+460,yoffset+70,40,20);
		refl.reshape(xoffset+460,yoffset+120,40,20);
		
		project.reshape(xoffset+10,yoffset+490,80,20);
		projectTop.reshape(xoffset+90,yoffset+490,60,20);
		projectFrontal.reshape(xoffset+150,yoffset+490,60,20);
		
		transparency.reshape(xoffset+290,yoffset+70,160,20);
		reflectivity.reshape(xoffset+290,yoffset+120,160,20);
		color.reshape(xoffset+390,yoffset+30,100,20);
		flat.reshape(xoffset+290,yoffset+150,100,20);
		
		g.setColor(new Color(0xFF000000|material.getColor()));
		g.fillRect(xoffset+370,yoffset+30,20,20);
		
	}
	
	private void renderObjectView(Graphics g)
	{
		objectView.render();
		g.drawImage(objectView.getImage(),0,0,this);
	}

	public boolean action(Event evt, Object obj)
	{
		if (evt.target==readMaterial) { readMaterial(); return true; }
		if (evt.target==writeMaterial) { writeMaterial(); return true; }
		if (evt.target==openScene) { openScene(); return true; }
		if (evt.target==importTexture) { importTexture(); return true; }
		if (evt.target==importEnvmap) { importEnvmap(); return true; }
		if (evt.target==generateTexture) { new TextureLab(this,material.textureSettings,true); return true; }
		if (evt.target==generateEnvmap) {  new TextureLab(this,material.envmapSettings,false); return true; }
		if (evt.target==noTexture) { noTexture(); return true; }
		if (evt.target==noEnvmap) { noEnvmap(); return true; }
		if (evt.target==projectTop) { projectTop(); return true; }
		if (evt.target==projectFrontal) { projectFrontal(); return true; }
		
		return super.action(evt,obj);
	}		

	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY)
		{
			hide(); dispose(); System.exit(0); return true;
		}
		
		return super.handleEvent(evt);
	}
	
	public boolean keyDown(Event evt,int key)
	{
		if (evt.target instanceof TextField) return super.keyDown(evt,key);
		if (key==32) { System.out.println(objectView.getFPS()+""); return true; }
		if (key==Event.PGUP) {objectView.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {objectView.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {objectView.shift(0f,0.2f,0f); return true; }
		if (key==Event.DOWN) {objectView.shift(0f,-0.2f,0f); return true; }
		if (key==Event.LEFT) {objectView.shift(-0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {objectView.shift(0.2f,0f,0f); return true; }
		if ((char)key=='+') {objectView.scale(1.2f); return true; }
		if ((char)key=='-') {objectView.scale(0.8f); return true; }
		if ((char)key=='m') {for (int i=0;i<objectView.objects;i++) objectView.object[i].meshSmooth(); return true; }
		if ((char)key=='a') {objectView.setAntialias(!objectView.antialias()); return true; }
		
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(objectView); return true; }
		
		return true;
	}
	
	public boolean mouseDown(Event evt,int x,int y)
	{
		if (x>280 || y>240) return true;
		requestFocus();
		oldx=x;
		oldy=y;
		repaint();
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		if (x>280 || y>240) return true;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		objectView.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		repaint();
		return true;
	}
	
	private void openScene()
	{
		File newFile=browseForFile("Read Scene from 3ds File",true);
		if (newFile==null) return;
		try{
			objectView.removeAllObjects();
			new idx3d_3ds_Importer().importFromStream(new FileInputStream(newFile),objectView);
			objectView.rebuild();
			for (int i=0; i<objectView.objects;i++)
				objectView.object[i].setMaterial(material);
			objectView.normalize();				
		}
		catch(Exception e){System.err.println(e+"");}
		repaint();
	}
	
		
	public void setSettings(idx3d_TextureSettings settings, boolean mode)
	{
		if (mode)
		{
			material.textureSettings=settings;
			material.setTexture(settings.texture);
		}
		else
		{
			material.envmapSettings=settings;
			material.setEnvmap(settings.texture);
		}
	}		
	
	private void updateMaterial()
	{
		try{
			material.setColor((int)Integer.parseInt(color.getText(),16));
		}
		catch(Exception e) {material.setColor(0);}
		material.setTransparency(transparency.getValue());
		material.setReflectivity(reflectivity.getValue());
		material.setFlat(flat.getState());
		transp.setText(material.getTransparency()+"");
		refl.setText(material.getReflectivity()+"");	
	}
	
	private void projectTop()
	{
		for (int i=objectView.objects-1;i>=0;i--)
			idx3d_TextureProjector.projectTop(objectView.object[i]);
	}
	
	private void projectFrontal()
	{
		for (int i=objectView.objects-1;i>=0;i--)
			idx3d_TextureProjector.projectFrontal(objectView.object[i]);
	}		
	
	private void importTexture()
	{
		File newFile=browseForFile("Import Texture",true);
		if (newFile==null) return;
		material.setTexture(new idx3d_Texture(newFile.getPath()));
	}
	
	private void importEnvmap()
	{
		File newFile=browseForFile("Import Envmap",true);
		if (newFile==null) return;
		material.setEnvmap(new idx3d_Texture(newFile.getPath()));
	}
	
	private void noTexture()
	{
		material.setTexture(null);
	}
	
	private void noEnvmap()
	{
		material.setEnvmap(null);
	}
	
	
	private File browseForFile(String dialogTitle, boolean mustExist)
	{
		FileDialog fd = new FileDialog(this, dialogTitle, mustExist? FileDialog.LOAD : FileDialog.SAVE);
		fd.show();
		if (fd.getDirectory()==null) return null;
		if (fd.getFile()==null) return null;
		File newFile=new File(fd.getDirectory(), fd.getFile());
		if (mustExist&&(newFile.length()==0)) newFile=null;
		return newFile;
	}
	
	private void writeMaterial()
	{
		File file=browseForFile("Save Material",false);
		if (file==null) return;
		int id;
		try
		{
			DataOutputStream out=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			out.writeInt(material.getColor());
			out.writeByte(material.getTransparency());
			out.writeByte(material.getReflectivity());
			out.writeBoolean(material.isFlat());
			
			idx3d_TextureSettings settings=null;
			
			// Texture
			if (material.getTexture()==null) id=0;
			else if (material.getTexture().path!=null) id=1;
			else id=2;
			out.writeByte(id);
			if (id==1) out.writeUTF(material.getTexture().path);
			if (id==2)
			{
				settings=material.textureSettings;
				out.writeInt(settings.width);
				out.writeInt(settings.height);
				out.writeByte(settings.type);
				out.writeFloat(settings.persistency);
				out.writeFloat(settings.density);
				out.writeByte(settings.samples);
				out.writeByte(settings.colors.length);
				for (int i=0;i<settings.colors.length;i++)
					out.writeInt(settings.colors[i]);
			}

			// Envmap
			if (material.getEnvmap()==null) id=0;
			else if (material.getEnvmap().path!=null) id=1;
			else id=2;
			out.writeByte(id);
			if (id==1) out.writeUTF(material.getEnvmap().path);
			if (id==2)
			{
				settings=material.envmapSettings;
				out.writeInt(settings.width);
				out.writeInt(settings.height);
				out.writeByte(settings.type);
				out.writeFloat(settings.persistency);
				out.writeFloat(settings.density);
				out.writeByte(settings.samples);
				out.writeByte(settings.colors.length);
				for (int i=0;i<settings.colors.length;i++)
					out.writeInt(settings.colors[i]);
			}			
			
			out.flush();
			out.close();		
		}
		catch (IOException ignored){}
	}
	
	private void readMaterial()
	{
		File file=browseForFile("Read Material",true);
		if (file==null) return;

		idx3d_Material newMaterial=null;
		try{
			newMaterial=new idx3d_Material(file.getPath());
		}
		catch(Exception ignored) {return;}
		color.setText(Integer.toHexString(newMaterial.getColor()));
		transparency.setValue(newMaterial.getTransparency());
		reflectivity.setValue(newMaterial.getReflectivity());
		flat.setState(newMaterial.isFlat());
		
		material=newMaterial;
		for (int i=0; i<objectView.objects;i++)
				objectView.object[i].setMaterial(material);
	}
}
