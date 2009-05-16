/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.util.export.xml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme.image.Texture;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * Part of the jME XML IO system as introduced in the google code jmexml project.
 * 
 * @author Kai Rabien (hevee) - original author of the code.google.com jmexml project
 * @author Doug Daniels (dougnukem) - adjustments for jME 2.0 and Java 1.5
 */
public class DOMInputCapsule implements InputCapsule {

    private Document doc;
    private Element currentElem;
    private XMLImporter importer;
    private boolean isAtRoot = true;
    private Map<String, Savable> referencedSavables = new HashMap<String, Savable>();

    public DOMInputCapsule(Document doc, XMLImporter importer) {
        this.doc = doc;
        this.importer = importer;
        currentElem = doc.getDocumentElement();
    }

    private static String decodeString(String s) {
        if (s == null) {
            return null;
        }
        s = s.replaceAll("\\&quot;", "\"").replaceAll("\\&lt;", "<").replaceAll("\\&amp;", "&");
        return s;
    }

    private Element findFirstChildElement(Element parent) {
        Node ret = parent.getFirstChild();
        while (ret != null && (!(ret instanceof Element))) {
            ret = ret.getNextSibling();
        }
        return (Element) ret;
    }

    private Element findChildElement(Element parent, String name) {
        if (parent == null) {
            return null;
        }
        Node ret = parent.getFirstChild();
        while (ret != null && (!(ret instanceof Element) || !ret.getNodeName().equals(name))) {
            ret = ret.getNextSibling();
        }
        return (Element) ret;
    }

    private Element findNextSiblingElement(Element current) {
        Node ret = current.getNextSibling();
        while (ret != null) {
            if (ret instanceof Element) {
                return (Element) ret;
            }
            ret = ret.getNextSibling();
        }
        return null;
    }

    public byte readByte(String name, byte defVal) throws IOException {
        byte ret = defVal;
        try {
            ret = Byte.parseByte(currentElem.getAttribute(name));
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public byte[] readByteArray(String name, byte[] defVal) throws IOException {
    	byte[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            byte[] tmp = new byte[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Byte.parseByte(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public byte[][] readByteArray2D(String name, byte[][] defVal) throws IOException {
    	byte[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            byte[][] tmp = new byte[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
           	 	Node n = nodes.item(i);
				if (n instanceof Element && n.getNodeName().contains("array")) {
					if (strIndex < size) {
						tmp[strIndex++] = readByteArray(n.getNodeName(), null);
					} else {
						throw new IOException(
								"String array contains more elements than specified!");
					}
				}                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public int readInt(String name, int defVal) throws IOException {
        int ret = defVal;
        try {
            String s = currentElem.getAttribute(name);
            if (s.length() > 0) {
                ret = Integer.parseInt(s);
            }
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public int[] readIntArray(String name, int[] defVal) throws IOException {
        int[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            int[] tmp = new int[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Integer.parseInt(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public int[][] readIntArray2D(String name, int[][] defVal) throws IOException {
    	int[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            int[][] tmp = new int[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
           	 	Node n = nodes.item(i);
				if (n instanceof Element && n.getNodeName().contains("array")) {
					if (strIndex < size) {
						tmp[strIndex++] = readIntArray(n.getNodeName(), null);
					} else {
						throw new IOException(
								"String array contains more elements than specified!");
					}
				}                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public float readFloat(String name, float defVal) throws IOException {
        float ret = defVal;
        try {
            String s = currentElem.getAttribute(name);
            if (s.length() > 0) {
                ret = Float.parseFloat(s);
            }
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public float[] readFloatArray(String name, float[] defVal) throws IOException {
        float[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            float[] tmp = new float[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Float.parseFloat(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public float[][] readFloatArray2D(String name, float[][] defVal) throws IOException {
        float[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size_outer = Integer.parseInt(tmpEl.getAttribute("size_outer"));
            int size_inner = Integer.parseInt(tmpEl.getAttribute("size_outer"));

            float[][] tmp = new float[size_outer][size_inner];

            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size_outer; i++) {
                tmp[i] = new float[size_inner];
                for (int k = 0; k < size_inner; k++) {
                    tmp[i][k] = Float.parseFloat(strings[i]);
                }
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public double readDouble(String name, double defVal) throws IOException {
        double ret = defVal;
        try {
            ret = Double.parseDouble(currentElem.getAttribute(name));
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public double[] readDoubleArray(String name, double[] defVal) throws IOException {
    	double[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            double[] tmp = new double[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Double.parseDouble(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public double[][] readDoubleArray2D(String name, double[][] defVal) throws IOException {
    	double[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            double[][] tmp = new double[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("array")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = readDoubleArray(n.getNodeName(), null);
                    } else {
           			 throw new IOException("String array contains more elements than specified!");
           		 }
           	 }                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public long readLong(String name, long defVal) throws IOException {
        long ret = defVal;
        try {
            ret = Long.parseLong(currentElem.getAttribute(name));
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public long[] readLongArray(String name, long[] defVal) throws IOException {
    	long[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            long[] tmp = new long[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Long.parseLong(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public long[][] readLongArray2D(String name, long[][] defVal) throws IOException {
    	long[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            long[][] tmp = new long[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("array")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = readLongArray(n.getNodeName(), null);
                    } else {
           			 throw new IOException("String array contains more elements than specified!");
           		 }
           	 }                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public short readShort(String name, short defVal) throws IOException {
        String attribute = currentElem.getAttribute(name);
        if (attribute == null || attribute.length() == 0) { return defVal; }
        try {
            return Short.parseShort(attribute);
        } catch (NumberFormatException e) {
            IOException ex = new IOException("error parsing " + name);
            ex.initCause(e);
            throw ex;
        }
    }

    public short[] readShortArray(String name, short[] defVal) throws IOException {
    	short[] ret = defVal;
         try {
             Element tmpEl;
             if (name != null) {
                 tmpEl = findChildElement(currentElem, name);
             } else {
                 tmpEl = currentElem;
             }
             if (tmpEl == null) {
                 return defVal;
             }
             int size = Integer.parseInt(tmpEl.getAttribute("size"));
             short[] tmp = new short[size];
             String[] strings = tmpEl.getAttribute("data").split("\\s+");
             for (int i = 0; i < size; i++) {
                 tmp[i] = Short.parseShort(strings[i]);
             }
             ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
         }
         return ret;
    }

    public short[][] readShortArray2D(String name, short[][] defVal) throws IOException {
    	short[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            short[][] tmp = new short[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("array")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = readShortArray(n.getNodeName(), null);
                    } else {
           			 throw new IOException("String array contains more elements than specified!");
           		 }
           	 }                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public boolean readBoolean(String name, boolean defVal) throws IOException {
        boolean ret = defVal;
        try {
            String s = currentElem.getAttribute(name);
            if (s.length() > 0) {
                ret = Boolean.parseBoolean(s);
            }
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public boolean[] readBooleanArray(String name, boolean[] defVal) throws IOException {
        boolean[] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            boolean[] tmp = new boolean[size];
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (int i = 0; i < size; i++) {
                tmp[i] = Boolean.parseBoolean(strings[i]);
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public boolean[][] readBooleanArray2D(String name, boolean[][] defVal) throws IOException {
    	boolean[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            boolean[][] tmp = new boolean[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("array")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = readBooleanArray(n.getNodeName(), null);
                    } else {
           			 throw new IOException("String array contains more elements than specified!");
           		 }
           	 }                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public String readString(String name, String defVal) throws IOException {
        String ret = defVal;
        try {
            ret = decodeString(currentElem.getAttribute(name));
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public String[] readStringArray(String name, String[] defVal) throws IOException {
    	 String[] ret = defVal;
         try {
             Element tmpEl;
             if (name != null) {
                 tmpEl = findChildElement(currentElem, name);
             } else {
                 tmpEl = currentElem;
             }
             if (tmpEl == null) {
                 return defVal;
             }
             int size = Integer.parseInt(tmpEl.getAttribute("size"));
             String[] tmp = new String[size];
             NodeList nodes = currentElem.getChildNodes();
             int strIndex = 0;
             for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("String")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = ((Element) n).getAttributeNode("value").getValue();
                    } else {
            			 throw new IOException("String array contains more elements than specified!");
            		 }
            	 }                
             }
             ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
         }
         currentElem = (Element) currentElem.getParentNode();
         return ret;
    }

    public String[][] readStringArray2D(String name, String[][] defVal) throws IOException {
    	String[][] ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            String[][] tmp = new String[size][];
            NodeList nodes = currentElem.getChildNodes();
            int strIndex = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element && n.getNodeName().contains("array")) {
                    if (strIndex < size) {
                        tmp[strIndex++] = readStringArray(n.getNodeName(), null);
                    } else {
           			 throw new IOException("String array contains more elements than specified!");
           		 }
           	 }                
            }
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        currentElem = (Element) currentElem.getParentNode();
        return ret;
    }

    public BitSet readBitSet(String name, BitSet defVal) throws IOException {
        BitSet ret = defVal;
        try {
            BitSet set = new BitSet();
            String bitString = currentElem.getAttribute(name);
            String[] strings = bitString.split("\\s+");
            for (int i = 0; i < strings.length; i++) {
            	int isSet = Integer.parseInt(strings[i]);
                if (isSet == 1) {
            		set.set(i);
            	}
            }
            ret = set;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public Savable readSavable(String name, Savable defVal) throws IOException {
        Savable ret = defVal;
        if (name != null && name.equals("")) {
            System.out.println("-");
        }
        if (false) {
        } else {
            try {
                Element tmpEl = null;
                if (name != null) {
                    tmpEl = findChildElement(currentElem, name);
                    if (tmpEl == null) {
                        return defVal;
                    }
                } else if (isAtRoot) {
                    tmpEl = doc.getDocumentElement();
                    isAtRoot = false;
                } else {
                    tmpEl = findFirstChildElement(currentElem);
                }
                currentElem = tmpEl;
                ret = readSavableFromCurrentElem(defVal);
                if (currentElem.getParentNode() instanceof Element) {
                    currentElem = (Element) currentElem.getParentNode();
                } else {
                    currentElem = null;
                }
            } catch (Exception e) {
                IOException ex = new IOException();
                ex.initCause(e);
                throw ex;
            }
        }
        return ret;
    }
    
    private Savable readSavableFromCurrentElem(Savable defVal) throws
            InstantiationException, ClassNotFoundException,
            IOException, IllegalAccessException {
        Savable ret = defVal;
        Savable tmp = null;

        if (currentElem == null || currentElem.getNodeName().equals("null")) {
            return null;
        }
        String reference = currentElem.getAttribute("ref");
        if (reference.length() > 0) {
            ret = referencedSavables.get(reference);
        } else {
            String className = currentElem.getNodeName();
            if (defVal != null) {
                className = defVal.getClass().getName();
            } else if (currentElem.hasAttribute("class")) {
                className = currentElem.getAttribute("class");
            }
            tmp = (Savable) Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
            String refID = currentElem.getAttribute("reference_ID");
            if (refID.length() > 0) {
                referencedSavables.put(refID, tmp);
            }
            if (tmp != null) {
                tmp.read(importer);
                ret = tmp;
            }
        }
        return ret;
    }

    private TextureState readTextureStateFromCurrent() {
        Element el = currentElem;
        TextureState ret = null;
        try {
            ret = (TextureState) readSavableFromCurrentElem(null);
            //Renderer r = DisplaySystem.getDisplaySystem().getRenderer();
            Savable[] savs = readSavableArray("texture", new Texture[0]);
            // TODO:  Investigate why both readSavableFromCurentElem(null)
            // and readSavableArray("texture", new TExture[0]) both resolve
            // the texture file resource.  Who know what other work they
            // duplicate.
            for (int i = 0; i < savs.length; i++) {
                Texture t = (Texture) savs[i];
                TextureKey tKey = t.getTextureKey();
                t = TextureManager.loadTexture(tKey);
                ret.setTexture(t, i);
            }
        } catch (Exception e) {
            Logger.getLogger(DOMInputCapsule.class.getName()).log(Level.SEVERE, null, e);
        }
        currentElem = el;
        return ret;
    }
    
    private Savable[] readRenderStateList(Element fromElement, Savable[] defVal) {
        Savable[] ret = defVal;
        try {
            int size = RenderState.StateType.values().length;
            Savable[] tmp = new Savable[size];
            currentElem = findFirstChildElement(fromElement);
            while (currentElem != null) {
                Element el = currentElem;
                RenderState rs = null;
                if (el.getNodeName().equals("com.jme.scene.state.TextureState")) {
                    rs = readTextureStateFromCurrent();
                } else {
                    rs = (RenderState) (readSavableFromCurrentElem(null));
                }
                if (rs != null) {
                    tmp[rs.getStateType().ordinal()] = rs;
                }
                currentElem = findNextSiblingElement(el);
                ret = tmp;
            }
        } catch (Exception e) {
            Logger.getLogger(DOMInputCapsule.class.getName()).log(Level.SEVERE, null, e);
        }

        return ret;
    }

    public Savable[] readSavableArray(String name, Savable[] defVal) throws IOException {
        Savable[] ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            if (name.equals("renderStateList")) {
                ret = readRenderStateList(tmpEl, defVal);
            } else {
                int size = Integer.parseInt(tmpEl.getAttribute("size"));
                Savable[] tmp = new Savable[size];
                currentElem = findFirstChildElement(tmpEl);
                for (int i = 0; i < size; i++) {
                    tmp[i] = (readSavableFromCurrentElem(null));
                    if (i == size - 1) {
                        break;
                    }
                    currentElem = findNextSiblingElement(currentElem);
                }
                ret = tmp;
            }
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public Savable[][] readSavableArray2D(String name, Savable[][] defVal) throws IOException {
        Savable[][] ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size_outer = Integer.parseInt(tmpEl.getAttribute("size_outer"));
            int size_inner = Integer.parseInt(tmpEl.getAttribute("size_outer"));
            
            Savable[][] tmp = new Savable[size_outer][size_inner];
            currentElem = findFirstChildElement(tmpEl);
            for (int i = 0; i < size_outer; i++) {
                for (int j = 0; j < size_inner; j++) {
                    tmp[i][j] = (readSavableFromCurrentElem(null));
                    if (i == size_outer - 1 && j == size_inner - 1) {
                        break;
                    }
                    currentElem = findNextSiblingElement(currentElem);
                }
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ArrayList<Savable> readSavableArrayList(String name, ArrayList defVal) throws IOException {
        ArrayList<Savable> ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            String s = tmpEl.getAttribute("size");
            int size = Integer.parseInt(s);
            ArrayList<Savable> tmp = new ArrayList<Savable>();
            currentElem = findFirstChildElement(tmpEl);
            for (int i = 0; i < size; i++) {
                tmp.add(readSavableFromCurrentElem(null));
                if (i == size - 1) {
                    break;
                }
                currentElem = findNextSiblingElement(currentElem);
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ArrayList[] readSavableArrayListArray(String name, ArrayList[] defVal) throws IOException {
        ArrayList[] ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }
            currentElem = tmpEl;
            
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            ArrayList[] tmp = new ArrayList[size];
            for (int i = 0; i < size; i++) {
                StringBuilder buf = new StringBuilder("SavableArrayList_");
                buf.append(i);
                ArrayList al = readSavableArrayList(buf.toString(), null);
                tmp[i] = al;
                if (i == size - 1) {
                    break;
                }
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ArrayList[][] readSavableArrayListArray2D(String name, ArrayList[][] defVal) throws IOException {
        ArrayList[][] ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }
            currentElem = tmpEl;
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            
            ArrayList[][] tmp = new ArrayList[size][];
            for (int i = 0; i < size; i++) {
                ArrayList[] arr = readSavableArrayListArray("SavableArrayListArray_" + i, null);
                tmp[i] = arr;
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ArrayList readFloatBufferArrayList(String name, ArrayList<FloatBuffer> defVal) throws IOException {
        ArrayList<FloatBuffer> ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            ArrayList<FloatBuffer> tmp = new ArrayList<FloatBuffer>(size);
            currentElem = findFirstChildElement(tmpEl);
            for (int i = 0; i < size; i++) {
                tmp.add(readFloatBuffer(null, null));
                if (i == size - 1) {
                    break;
                }
                currentElem = findNextSiblingElement(currentElem);
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public Map<? extends Savable, ? extends Savable> readSavableMap(String name, Map<? extends Savable, ? extends Savable> defVal) throws IOException {
    	Map<Savable, Savable> ret;
    	Element tempEl;
    	
    	if (name != null) {
    		tempEl = findChildElement(currentElem, name);
        } else {
        	tempEl = currentElem;
        }
    	ret = new HashMap<Savable, Savable>();
    	
    	NodeList nodes = tempEl.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
    		Node n = nodes.item(i);
            if (n instanceof Element && n.getNodeName().equals("MapEntry")) {
                Element elem = (Element) n;
    			currentElem = elem;
    			Savable key = readSavable(XMLExporter.ELEMENT_KEY, null);    			
    			Savable val = readSavable(XMLExporter.ELEMENT_VALUE, null);
    			ret.put(key, val);    			
    		}
    	}
    	currentElem = (Element) tempEl.getParentNode();
        return ret;
    }

    public Map<String, ? extends Savable> readStringSavableMap(String name, Map<String, ? extends Savable> defVal) throws IOException {
    	Map<String, Savable> ret = null;
    	Element tempEl;
    	
    	if (name != null) {
    		tempEl = findChildElement(currentElem, name);
        } else {
        	tempEl = currentElem;
        }
        if (tempEl != null) {
	    	ret = new HashMap<String, Savable>();
	    	
	    	NodeList nodes = tempEl.getChildNodes();
		    for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if (n instanceof Element && n.getNodeName().equals("MapEntry")) {
					Element elem = (Element) n;
					currentElem = elem;
					String key = currentElem.getAttribute("key");
					Savable val = readSavable("Savable", null);
					ret.put(key, val);
				}
			}
        } else {
	    	return defVal;
	    }
    	currentElem = (Element) tempEl.getParentNode();
        return ret;
    }

    /**
     * reads from currentElem if name is null
     */
    public FloatBuffer readFloatBuffer(String name, FloatBuffer defVal) throws IOException {
        FloatBuffer ret = defVal;
        try {
            Element tmpEl;
            if (name != null) {
                tmpEl = findChildElement(currentElem, name);
            } else {
                tmpEl = currentElem;
            }
            if (tmpEl == null) {
                return defVal;
            }
            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            FloatBuffer tmp = BufferUtils.createFloatBuffer(size);
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (String s : strings) {
                tmp.put(Float.parseFloat(s));
            }
            tmp.flip();
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException("error reading " + name);
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public IntBuffer readIntBuffer(String name, IntBuffer defVal) throws IOException {
        IntBuffer ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            IntBuffer tmp = BufferUtils.createIntBuffer(size);
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (String s : strings) {
                tmp.put(Integer.parseInt(s));
            }
            tmp.flip();
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ByteBuffer readByteBuffer(String name, ByteBuffer defVal) throws IOException {
    	ByteBuffer ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            ByteBuffer tmp = BufferUtils.createByteBuffer(size);
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (String s : strings) {
                tmp.put(Byte.valueOf(s));
            }
            tmp.flip();
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

    public ShortBuffer readShortBuffer(String name, ShortBuffer defVal) throws IOException {
    	ShortBuffer ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            ShortBuffer tmp = BufferUtils.createShortBuffer(size);
            String[] strings = tmpEl.getAttribute("data").split("\\s+");
            for (String s : strings) {
                tmp.put(Short.valueOf(s));
            }
            tmp.flip();
            ret = tmp;
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
    }

	public ArrayList<ByteBuffer> readByteBufferArrayList(String name, ArrayList<ByteBuffer> defVal) throws IOException {
		ArrayList<ByteBuffer> ret = defVal;
        try {
            Element tmpEl = findChildElement(currentElem, name);
            if (tmpEl == null) {
                return defVal;
            }

            int size = Integer.parseInt(tmpEl.getAttribute("size"));
            ArrayList<ByteBuffer> tmp = new ArrayList<ByteBuffer>(size);
            currentElem = findFirstChildElement(tmpEl);
            for (int i = 0; i < size; i++) {
                tmp.add(readByteBuffer(null, null));
                if (i == size - 1) {
                    break;
                }
                currentElem = findNextSiblingElement(currentElem);
            }
            ret = tmp;
            currentElem = (Element) tmpEl.getParentNode();
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;
	}

	public <T extends Enum<T>> T readEnum(String name, Class<T> enumType,
			T defVal) throws IOException {
        T ret = defVal;
        try {
            String eVal = currentElem.getAttribute(name);
            if (eVal != null && eVal.length() > 0) {
                ret = Enum.valueOf(enumType, eVal);
            }
        } catch (Exception e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
        return ret;       
	}
}
