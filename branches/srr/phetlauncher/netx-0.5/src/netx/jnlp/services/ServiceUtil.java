// Copyright (C) 2001-2003 Jon A. Maxwell (JAM)
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


package netx.jnlp.services;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.security.*;
import javax.jnlp.*;
import netx.jnlp.*;
import netx.jnlp.runtime.*;

/**
 * Provides static methods to interact useful for using the JNLP
 * services.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
public class ServiceUtil {

    /**
     * Returns the BasicService reference, or null if the service is
     * unavailable.
     */
    public static BasicService getBasicService() {
        return (BasicService) getService("javax.jnlp.BasicService");
    }

    /**
     * Returns the ClipboardService reference, or null if the service is
     * unavailable.
     */
    public static ClipboardService getClipboardService() {
        return (ClipboardService) getService("javax.jnlp.ClipboardService");
    }

    /**
     * Returns the DownloadService reference, or null if the service is
     * unavailable.
     */
    public static DownloadService getDownloadService() {
        return (DownloadService) getService("javax.jnlp.DownloadService");
    }

    /**
     * Returns the ExtensionInstallerService reference, or null if the service is
     * unavailable.
     */
    public static ExtensionInstallerService getExtensionInstallerService() {
        return (ExtensionInstallerService) getService("javax.jnlp.ExtensionInstallerService");
    }

    /**
     * Returns the FileOpenService reference, or null if the service is
     * unavailable.
     */
    public static FileOpenService getFileOpenService() {
        return (FileOpenService) getService("javax.jnlp.FileOpenService");
    }

    /**
     * Returns the FileSaveService reference, or null if the service is
     * unavailable.
     */
    public static FileSaveService getFileSaveService() {
        return (FileSaveService) getService("javax.jnlp.FileSaveService");
    }

    /**
     * Returns the PersistenceService reference, or null if the service is
     * unavailable.
     */
    public static PersistenceService getPersistenceService() {
        return (PersistenceService) getService("javax.jnlp.PersistenceService");
    }

    /**
     * Returns the PrintService reference, or null if the service is
     * unavailable.
     */
    public static PrintService getPrintService() {
        return (PrintService) getService("javax.jnlp.PrintService");
    }

    /**
     * Returns the service, or null instead of an UnavailableServiceException
     */
    private static Object getService(String name) {
        try {
            return ServiceManager.lookup(name);
        }
        catch (UnavailableServiceException ex) {
            return null;
        }
    }

    /**
     * Creates a Proxy object implementing the specified interface
     * when makes all calls in the security context of the system
     * classes (ie, AllPermissions).  This means that the services
     * must be more than extremely careful in the operations they
     * perform.
     */
    static Object createPrivilegedProxy(Class iface, final Object receiver) {
        return Proxy.newProxyInstance(XServiceManagerStub.class.getClassLoader(),
                                      new Class[] { iface },
                                      new PrivilegedHandler(receiver));
    }

    /** 
     * calls the object's method using privileged access 
     */
    private static class PrivilegedHandler implements InvocationHandler {
        private final Object receiver;

        PrivilegedHandler(Object receiver) {
            this.receiver = receiver;
        }

        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            if (JNLPRuntime.isDebug()) {
                System.err.println("call privileged method: "+method.getName());
                if (args != null)
                    for (int i=0; i < args.length; i++)
                        System.err.println("           arg: "+args[i]);
            }

            PrivilegedExceptionAction invoker = new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    return method.invoke(receiver, args);
                }
            };

            Object result = AccessController.doPrivileged(invoker);

            if (JNLPRuntime.isDebug())
                System.err.println("        result: "+result);

            return result;
        }
    };

}


