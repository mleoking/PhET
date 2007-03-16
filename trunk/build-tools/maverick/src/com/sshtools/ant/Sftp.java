
				/******************************************************************************
 * Copyright (c) 2003-2004 3SP Ltd. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code and
 * its use is subject to the terms of the 3SP Software License. You may not use
 * this file except in compliance with the license terms.
 *
 * You should have received a copy of the 3SP Software License along with this
 * software; see the file LICENSE.html.  If not, write to or contact:
 *
 * 3SP Ltd, No. 3 The Glade Business Center, Forum Road,
 * Nottingham, NG5 9RW, ENGLAND
 *
 * Email:     support@3sp.com
 * Telephone: +44 (0)115 962 4446
 * WWW:       http://3sp.com
 *****************************************************************************/

			
package com.sshtools.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpFileAttributes;
import com.maverick.sftp.SftpFileOutputStream;
import com.maverick.sftp.SftpStatusException;
import com.maverick.sftp.TransferCancelledException;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshException;
import com.sshtools.sftp.SftpClient;


/**
 * Basic SFTP client. Performs the following actions:
 * <ul>
 *   <li> <strong>put</strong> - send files to a remote server. This is the
 *   default action.</li>
 *   <li> <strong>get</strong> - retreive files from a remote server.</li>
 *   <li> <strong>del</strong> - delete files from a remote server.</li>
 *   <li> <strong>chmod</strong> - changes the mode of files on a remote server.</li>
 * </ul>
 *
 */
public class Sftp extends SshSubTask {
    protected static final int SEND_FILES = 0;
    protected static final int GET_FILES = 1;
    protected static final int DEL_FILES = 2;
    protected static final int MK_DIR = 4;
    protected static final int CHMOD = 5;
    protected static final int RMDIR = 6;

    //private Ssh parent = null;
    protected static final String[] ACTION_STRS = {
        "Sending", "Getting", "Deleting", "Listing", "Making directory", "Change Mode", "Remove Directory"
    };
    protected static final String[] COMPLETED_ACTION_STRS = {
        "Sent", "Retrieved", "Deleted", "Listed", "Created directory",
        "Mode changed", "Directory Removed"
    };
    private String remotedir = ".";

    //  private File listing;
    private boolean verbose = false;
    private boolean newerOnly = false;
    private int action = SEND_FILES;
    private Vector filesets = new Vector();
    private Vector dirCache = new Vector();
    private int transferred = 0;
    private String remoteFileSep = "/";
    private boolean skipFailedTransfers = false;
    private int skipped = 0;
    private String chmod = null;
    private String umask = "022";
    private FileUtils fileUtils = FileUtils.newFileUtils();
    private boolean textMode = false;
    String remoteEOL = "LF";

    /**
     * Set to true to receive notification about each file as it is
     * transferred.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Set the umask to be applied to new files/directories.
     */
    public void setUmask(String umask){
      this.umask = umask;
    }

    /**
     * Sets the remote working directory
     * */
    public void setRemotedir(String remotedir) {
        this.remotedir = remotedir;
    }

    /**
     * Sets the remote EOL for text mode transfers, default LF
     * @param remoteEOL String
     */
    public void setRemoteEol(String remoteEOL) {
        this.remoteEOL = remoteEOL;
    }

    /**
     * Sets the transfer mode for the SFTP operation
     * @param textMode boolean
     */
    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
    }

    /**
     * A synonym for <tt>depends</tt>. Set to true to transmit only new or changed
     * files.
     */
    public void setNewer(boolean newer) {
        this.newerOnly = newer;
    }

    /**
     * Set to true to transmit only files that are new or changed from their
     * remote counterparts. The default is to transmit all files.
     */
    public void setDepends(boolean depends) {
        this.newerOnly = depends;
    }

    /**
     * Sets the file permission mode (Unix only) for files sent to the server.
     */
    public void setMode(String theMode) {
        this.chmod = theMode;
    }

    /**
     *  A set of files to upload or download
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    public void addFilelist(FileList list) {
      filesets.addElement(list);
    }

    /**
     * Sets the FTP action to be taken. Currently accepts "put", "get", "del",
     * "mkdir" and "chmod"
     */
    public void setAction(Action action) throws BuildException {
        this.action = action.getAction();
    }

    /**
     * If true, enables unsuccessful file put, delete and get
     * operations to be skipped with a warning and the remainder
     * of the files still transferred.
     */
    public void setSkipFailedTransfers(boolean skipFailedTransfers) {
        this.skipFailedTransfers = skipFailedTransfers;
    }

    /** Checks to see that all required parameters are set.  */
    protected void checkConfiguration() throws BuildException {
        /* if ( (action == LIST_FILES) && (listing == null)) {
           throw new BuildException("listing attribute must be set for list "
                                    + "action!");
         }*/
        if ((action == MK_DIR) && (remotedir == null)) {
            throw new BuildException("remotedir attribute must be set for " +
                "mkdir action!");
        }

        if ((action == CHMOD) && (chmod == null)) {
            throw new BuildException("chmod attribute must be set for chmod " +
                "action!");
        }
    }


    protected int transferFiles(SftpClient sftp, FileList list)
       throws SftpStatusException, SshException, BuildException {

      String dir = null;
      if ((list.getDir(parent.getProject()) == null) &&
              ((action == SEND_FILES) || (action == GET_FILES))) {
          throw new BuildException("the dir attribute must be set for send " +
              "and get actions");
      } else {
          if ((action == SEND_FILES) || (action == GET_FILES)) {
              dir = list.getDir(parent.getProject()).getAbsolutePath();
          }
      }

      return transferFiles(sftp, list.getFiles(parent.getProject()), dir);
    }

    /**
     * For each file in the fileset, do the appropriate action: send, get,
     * delete, or list.
     */
    protected int transferFiles(SftpClient sftp, FileSet fs)
        throws SftpStatusException, SshException, BuildException {
       FileScanner ds;

       if(action == SEND_FILES) {
         ds = fs.getDirectoryScanner(parent.getProject());
       }
       else {
         ds = new SftpDirectoryScanner(sftp);
         fs.setupDirectoryScanner(ds, parent.getProject());
         ds.scan();
       }

       String[] dsfiles = ds.getIncludedFiles();

       String dir = null;
       if ((ds.getBasedir() == null) &&
               ((action == SEND_FILES) || (action == GET_FILES))) {
           throw new BuildException("the dir attribute must be set for send " +
               "and get actions");
       } else {
           if ((action == SEND_FILES) || (action == GET_FILES)) {
               dir = ds.getBasedir().getAbsolutePath();
           }
       }

       return transferFiles(sftp, dsfiles, dir);
     }


    protected int transferFiles(SftpClient sftp, String[] dsfiles, String dir)
       throws SftpStatusException, SshException, BuildException {

        try {

            for (int i = 0; i < dsfiles.length; i++) {
                switch (action) {
                case SEND_FILES: {
                    sendFile(sftp, dir, dsfiles[i]);

                    break;
                }

                case GET_FILES: {
                    getFile(sftp, dir, dsfiles[i]);

                    break;
                }

                case DEL_FILES: {
                    delFile(sftp, dsfiles[i]);

                    break;
                }

                case CHMOD: {
                    chmod(sftp, dsfiles[i]);
                    transferred++;

                    break;
                }

                default:throw new BuildException("unknown ftp action " +
                        action);
                }
            }
        } finally {

        }

        return dsfiles.length;
    }

    /**
     * Sends all files specified by the configured filesets to the remote
     * server.
     */
    protected void transferFiles(SftpClient sftp)
        throws SftpStatusException, SshException, BuildException {
        transferred = 0;
        skipped = 0;

        if (filesets.size() == 0) {
            throw new BuildException("at least one fileset or filelist must be specified.");
        } else {
            // get files from filesets
            for (int i = 0; i < filesets.size(); i++) {

                Object fs = filesets.elementAt(i);

                if (fs != null && fs instanceof FileSet) {
                    transferFiles(sftp, (FileSet)fs);
                } else if(fs != null && fs instanceof FileList) {
                    transferFiles(sftp, (FileList)fs);
                }
            }
        }

        log(transferred + " files " + COMPLETED_ACTION_STRS[action]);

        if (skipped != 0) {
            log(skipped + " files were not successfully " +
                COMPLETED_ACTION_STRS[action]);
        }
    }

    /**
     * Correct a file path to correspond to the remote host requirements. This
     * implementation currently assumes that the remote end can handle
     * Unix-style paths with forward-slash separators. This can be overridden
     * with the <code>separator</code> task parameter. No attempt is made to
     * determine what syntax is appropriate for the remote host.
     */
    protected String resolveFile(String file) {
        return file.replace(System.getProperty("file.separator").charAt(0),
            remoteFileSep.charAt(0));
    }

    /**
     * Creates all parent directories specified in a complete relative
     * pathname. Attempts to create existing directories will not cause
     * errors.
     */
    protected void createParents(SftpClient sftp, String filename)
        throws SftpStatusException, SshException, BuildException {
        Vector parents = new Vector();
        File dir = new File(filename);
        String dirname;

        while ((dirname = dir.getParent()) != null) {
            dir = new File(dirname);
            parents.addElement(dir);
        }

        for (int i = parents.size() - 1; i >= 0; i--) {
            dir = (File) parents.elementAt(i);

            if (!dirCache.contains(dir)) {
                log("creating remote directory " + resolveFile(dir.getPath()),
                    Project.MSG_VERBOSE);

                 sftp.mkdir(resolveFile(dir.getPath()));

                dirCache.addElement(dir);
            }
        }
    }

    /**
     * Checks to see if the remote file is current as compared with the local
     * file. Returns true if the remote file is up to date.
     */
    protected boolean isUpToDate(SftpClient sftp, File localFile,
        String remoteFile) throws SshException, BuildException {
        try {
            log("Checking date for " + remoteFile, Project.MSG_VERBOSE);

            SftpFileAttributes attrs = sftp.stat(remoteFile);

            // SFTP uses seconds since Jan 1 1970 UTC
            long remoteTimestamp = attrs.getModifiedTime().longValue() * 1000; //files[0].getTimestamp().getTime().getTime();

            // Java uses milliseconds since Jan 1 1970 UTC
            long localTimestamp = localFile.lastModified();

            if (this.action == SEND_FILES) {
                return remoteTimestamp > localTimestamp;
            } else {
                return localTimestamp > remoteTimestamp;
            }
        } catch (SftpStatusException ex) {
            return false;
        }
    }

    /**
     * Sends a single file to the remote host. <code>filename</code> may
     * contain a relative path specification. When this is the case, <code>sendFile</code>
     * will attempt to create any necessary parent directories before sending
     * the file. The file will then be sent using the entire relative path
     * spec - no attempt is made to change directories. It is anticipated that
     * this may eventually cause problems with some FTP servers, but it
     * simplifies the coding.
     */
    protected void sendFile(SftpClient sftp, String dir, String filename)
        throws SftpStatusException, SshException, BuildException {
        InputStream instream = null;
        SftpFileOutputStream out = null;

        try {
            File file = parent.getProject().resolveFile(new File(dir, filename).getPath());
            String remotefile = resolveFile(filename);

            if (newerOnly && isUpToDate(sftp, file, remotefile)) {
                return;
            }

            if (verbose) {
                log("transferring " + file.getAbsolutePath() + " to " +
                    remotedir + remotefile);
            }

            instream = new BufferedInputStream(new FileInputStream(file));
            createParents(sftp, filename);
            sftp.put(file.getAbsolutePath(), remotefile);

            log("File " + file.getAbsolutePath() + " copied to " + parent.host,
                Project.MSG_VERBOSE);
            transferred++;
        } catch(TransferCancelledException ex ) {
          if(skipFailedTransfers == true) {
            log("File transfer cancelled", Project.MSG_WARN);
            skipped++;
          } else
            throw new BuildException("File transfer cancelled");
        } catch (SftpStatusException ex1) {
            String s = "Could not put file: " + ex1.getMessage();

            if (skipFailedTransfers == true) {
                log(s, Project.MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        }
        catch (IOException ex1) {
            String s = "Could not put file: " + ex1.getMessage();

            if (skipFailedTransfers == true) {
                log(s, Project.MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException ex) {
                // ignore it
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /** Delete a file from the remote host.  */
    protected void delFile(SftpClient sftp, String filename)
        throws SftpStatusException, SshException, BuildException {
        if (verbose) {
            log("deleting " + filename);
        }

        try {
            String remotefile = resolveFile(filename);
            sftp.rm(remotefile);
            log("File " + filename + " deleted from " + parent.host,
                Project.MSG_VERBOSE);
            transferred++;
        } catch (SftpStatusException ex) {
            String s = "could not delete file: " + ex.getMessage();

            if (skipFailedTransfers == true) {
                log(s, Project.MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        }
    }

    protected void chmod(SftpClient sftp, String filename)
        throws SftpStatusException, SshException, BuildException {

      if (verbose) {
        log("changing mode of " + filename);
      }

      sftp.chmod(Integer.parseInt(chmod, 8), resolveFile(filename));
    }

    /**
     * Retrieve a single file to the remote host. <code>filename</code> may
     * contain a relative path specification. <p>
     *
     * The file will then be retreived using the entire relative path spec -
     * no attempt is made to change directories. It is anticipated that this
     * may eventually cause problems with some FTP servers, but it simplifies
     * the coding.</p>
     */
    protected void getFile(SftpClient sftp, String dir, String filename)
        throws SftpStatusException, SshException, BuildException {
        try {
            String localfile = filename;

            if (localfile.indexOf("/") >= 0) {
                localfile = localfile.substring(filename.lastIndexOf("/"));
            }

            File file = parent.getProject().resolveFile(new File(dir, localfile).getAbsolutePath());
            log(dir);
            log(filename);
            log(file.getAbsolutePath());

            if (newerOnly && isUpToDate(sftp, file, resolveFile(filename))) {
                return;
            }

            if (verbose) {
                log("transferring " + filename + " to " +
                    file.getAbsolutePath());
            }

            File pdir = fileUtils.getParentFile(file);

            if (!pdir.exists()) {
                pdir.mkdirs();
            }

            // Get the file
            sftp.get(filename, file.getAbsolutePath());

            if (verbose) {
                log("File " + file.getAbsolutePath() + " copied from " +
                    parent.host);
            }

            SftpFileAttributes attrs = sftp.stat(filename);

            /**
             * Try to set the last modifed time of the file to that of the
             * remote file.
             */
            try {
              java.lang.reflect.Method m = file.getClass().getMethod("setLastModified",
                 new Class[] {long.class});
              m.invoke(file, new Object[] { new Long(attrs.getModifiedTime().longValue() * 1000)});
            }
            catch(Throwable ex1) {
            }

            transferred++;
        } catch(TransferCancelledException ex) {
          if(skipFailedTransfers == true) {
            log("File transfer cancelled", Project.MSG_WARN);
            skipped++;
          } else
            throw new BuildException("File transfer cancelled");
        } catch (SftpStatusException ioe) {
            String s = "could not get file: " + ioe.getMessage();

            if (skipFailedTransfers == true) {
                log(s, Project.MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        }
        catch (IOException ioe) {
            String s = "could not get file: " + ioe.getMessage();

            if (skipFailedTransfers == true) {
                log(s, Project.MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        }
    }

    protected void removeDirectory(SftpClient sftp, String dir) {

      log("Removing directory " + dir);

      try {
          sftp.rm(dir, true, true);
      } catch (SftpStatusException ex) {
          log(ex.getMessage());
      } catch(SshException ex) {
        Ssh.processException(ex);
      }

    }

    /**
     * Create the specified directory on the remote host.
     *
     * @param sftp The SFTP client connection
     * @param dir The directory to create
     */
    protected void makeRemoteDir(SftpClient sftp, String dir)
        throws BuildException {

        log("Creating directory: " + dir);

        try {
            sftp.mkdir(dir);
        } catch (SftpStatusException ex) {
            log(ex.getMessage());
        } catch(SshException ex) {
          Ssh.processException(ex);
        }
    }

    /** Runs the task.  */
    public void execute(SshClient ssh) throws BuildException {
        try {
          if(chmod!=null)
            Integer.parseInt(chmod, 8);
        } catch (NumberFormatException ex) {
            throw new BuildException(
                "mode attribute format is incorrect, use octal number format i.e 0777");
        }

        executeSFTPTask(ssh);
    }

    protected void executeSFTPTask(SshClient ssh) throws BuildException {
        SftpClient sftp = null;

        try {
            sftp = new SftpClient(ssh);

            if(!remoteEOL.equalsIgnoreCase("LF")
               && !remoteEOL.equalsIgnoreCase("CR")
               && !remoteEOL.equalsIgnoreCase("CRLF"))
               throw new BuildException("Only \"LF\", \"CR\" and \"CRLF\" EOL conventions are supported");

            if(remoteEOL.equalsIgnoreCase("CRLF"))
                sftp.setRemoteEOL(SftpClient.EOL_CRLF);
            else if(remoteEOL.equalsIgnoreCase("CR"))
                sftp.setRemoteEOL(SftpClient.EOL_CR);
            else
                sftp.setRemoteEOL(SftpClient.EOL_LF);

            sftp.setTransferMode(textMode
                                 ? SftpClient.MODE_TEXT
                                 : SftpClient.MODE_BINARY);

            sftp.umask(umask);

            if (action == MK_DIR) {
                makeRemoteDir(sftp, remotedir);
            } else if(action == RMDIR) {
                removeDirectory(sftp, remotedir);
            } else {
                if (remotedir.trim().length() > 0) {
                    log("Setting the remote directory "); //, Project.MSG_VERBOSE);
                    sftp.cd(remotedir);
                }

                // Get the absolute path of the remote directory
                remotedir = sftp.pwd();
                log("Remote directory is " + remotedir);

                if (!remotedir.endsWith("/")) {
                    remotedir += "/";
                }

                log(ACTION_STRS[action] + " files");
                transferFiles(sftp);
            }
        } catch(SftpStatusException ex) {
           Ssh.processException(ex);
        } catch (SshException ex) {
           Ssh.processException(ex);
        } catch (ChannelOpenException ex) {
           Ssh.processException(ex);
        } finally {
            if ((sftp != null) && !sftp.isClosed()) {
                    log("Quitting SFTP", Project.MSG_VERBOSE);
                    try {
                      sftp.quit();
                    }
                    catch(Exception ex1) {
                      Ssh.processException(ex1);
                    }
            }
        }
    }

    protected class SftpDirectoryScanner extends DirectoryScanner {
        protected SftpClient sftp = null;

        public SftpDirectoryScanner(SftpClient sftp) {
            super();
            this.sftp = sftp;
        }

        public void scan() {
            if (includes == null) {
                // No includes supplied, so set it to 'matches all'
                includes = new String[1];
                includes[0] = "**";
            }

            if (excludes == null) {
                excludes = new String[0];
            }

            filesIncluded = new Vector();
            filesNotIncluded = new Vector();
            filesExcluded = new Vector();
            dirsIncluded = new Vector();
            dirsNotIncluded = new Vector();
            dirsExcluded = new Vector();
            scandir(remotedir, true);
        }

        protected void scandir(String dir, boolean fast) {
            try {
                SftpFile[] children = sftp.ls(dir);

                if (!dir.endsWith("/")) {
                    dir += "/";
                }

                for(int i=0;i<children.length;i++) {
                    SftpFile file = children[i];

                    if (!file.getFilename().equals(".") &&
                            !file.getFilename().equals("..")) {
                        if (file.isDirectory()) {
                            String name = dir + file.getFilename();

                            if (isIncluded(name)) {
                                if (!isExcluded(name)) {
                                    dirsIncluded.addElement(name);

                                    if (fast) {
                                        scandir(dir + file.getFilename(), fast);
                                    }
                                } else {
                                    dirsExcluded.addElement(name);

                                    if (fast && couldHoldIncluded(name)) {
                                        scandir(dir + file.getFilename(), fast);
                                    }
                                }
                            } else {
                                dirsNotIncluded.addElement(name);

                                if (fast && couldHoldIncluded(name)) {
                                    scandir(dir + file.getFilename(), fast);
                                }
                            }

                            if (!fast) {
                                scandir(dir + file.getFilename(), fast);
                            }
                        } else {
                            if (file.isFile()) {
                                String name = dir + file.getFilename();

                                if (isIncluded(name)) {
                                    if (!isExcluded(name)) {
                                        filesIncluded.addElement(name);
                                    } else {
                                        filesExcluded.addElement(name);
                                    }
                                } else {
                                    filesNotIncluded.addElement(name);
                                }
                            }
                        }
                    }
                }

              } catch(SftpStatusException ex) {
                Ssh.processException(ex);
              } catch(SshException t) {
                Ssh.processException(t);
              }

        }
    }

    /**
     * an action to perform, one of
     * "send", "put", "recv", "get", "del", "delete", "list", "mkdir", "chmod"
     */
    public static class Action extends EnumeratedAttribute {
        private static final String[] validActions = {
            "send", "put", "recv", "get", "del", "delete", "list", "mkdir",
            "chmod", "rmdir"
        };

        public String[] getValues() {
            return validActions;
        }

        public int getAction() {
            String actionL = getValue().toLowerCase(Locale.US);

            if (actionL.equals("send") || actionL.equals("put")) {
                return SEND_FILES;
            } else if (actionL.equals("recv") || actionL.equals("get")) {
                return GET_FILES;
            } else if (actionL.equals("del") || actionL.equals("delete")) {
                return DEL_FILES;
            } else if(actionL.equals("rmdir")) {
              return RMDIR;
            }
            /*else if (actionL.equals("list")) {
              return LIST_FILES;
                   }*/
            else if (actionL.equals("chmod")) {
                return CHMOD;
            } else if (actionL.equals("mkdir")) {
                return MK_DIR;
            }

            return SEND_FILES;
        }
    }
}
