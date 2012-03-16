/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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

package com.jme3.asset.plugins;

import com.jme3.asset.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>UrlLocator</code> is a locator that combines a root url
 * and the given path in the AssetKey to construct a new url
 * that allows locating the asset.
 * @author Kiirill Vainer
 */
public class UrlLocator implements AssetLocator {

    private static final Logger logger = Logger.getLogger(UrlLocator.class.getName());
    private URL root;

    private static class UrlAssetInfo extends AssetInfo {

        private InputStream in;

        public UrlAssetInfo(AssetManager manager, AssetKey key, InputStream in){
            super(manager, key);
            this.in = in;
        }

        @Override
        public InputStream openStream() {
            return in;
        }
    }

    public void setRootPath(String rootPath) {
        try {
            this.root = new URL(rootPath);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid rootUrl specified", ex);
        }
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        String name = key.getName();
        
        try{
            URL url = new URL(root, name);
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(false);
            InputStream in;
            try {
                in = conn.getInputStream();
                if (in == null)
                    return null;
            } catch (FileNotFoundException ex){
                return null;
            }
            
            return new UrlAssetInfo(manager, key, in);
        }catch (IOException ex){
            logger.log(Level.WARNING, "Error while locating " + name, ex);
            return null;
        }
    }


}
