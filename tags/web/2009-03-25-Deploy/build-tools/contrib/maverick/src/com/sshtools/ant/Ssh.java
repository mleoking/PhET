
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.HostKeyVerification;
import com.maverick.ssh.PasswordAuthentication;
import com.maverick.ssh.PublicKeyAuthentication;
import com.maverick.ssh.Shell;
import com.maverick.ssh.ShellProcess;
import com.maverick.ssh.ShellTimeoutException;
import com.maverick.ssh.SshAuthentication;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshConnector;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.SshPublicKey;
import com.maverick.ssh.SshSession;
import com.maverick.ssh1.Ssh1Context;
import com.maverick.ssh2.Ssh2Client;
import com.maverick.ssh2.Ssh2Context;
import com.sshtools.net.SocketTransport;
import com.sshtools.publickey.InvalidPassphraseException;
import com.sshtools.publickey.SshKeyPair;
import com.sshtools.publickey.SshPrivateKeyFile;
import com.sshtools.publickey.SshPrivateKeyFileFactory;

/**
 * Provides an SSH ant task that enables both file transfer over SFTP,
 * the execution of commands and the ability to start a shell and execute
 * commands.
 *
 * @author Lee David Painter
 */
public class Ssh extends Task {
    protected String host;
    protected int port = 22;
    protected String username;
    protected String password;
    protected String keyfile;
    protected String passphrase;
    protected String cipher;
    protected String mac;
    protected String fingerprint;
    protected String sftpcmd = null;
    protected boolean verifyhost = false;
    protected boolean solarisBugWorkaround = false;
    protected SshClient ssh;
    protected Vector tasks = new Vector();
    protected String newline = "\n";
    protected SshConnector connector;
    protected int version = SshConnector.SSH1 | SshConnector.SSH2;
    protected String matcher = null;
    protected SocketTransport transport;
    protected String prompt = null;
    protected String eol = null;
    protected String term = "vt100";
    protected int cols = 80;
    protected int rows = 24;
    String exitCommand = null;
    boolean failOnUnknownOS = true;
    String promptCommand = null;
    int promptTimeout = -1;
    int shellInitPeriod = -1;

    Shell shell;

    public Ssh() {
        super();
    }

    protected void validate() throws BuildException {
        if (host == null) {
            throw new BuildException("You must provide a host to connect to!");
        }

        if (username == null) {
            throw new BuildException(
                "You must supply a username for authentication!");
        }

        if ((password == null || password.equals("")) && (keyfile == null || keyfile.equals(""))) {
            throw new BuildException(
                "You must supply either a password or keyfile/passphrase to authenticate!");
        }

        if (verifyhost && (fingerprint == null)) {
            throw new BuildException(
                "Public key fingerprint required to verify the host");
        }

        if(tasks.size()==0) {
          throw new BuildException("You must provide at least one <sftp>, <shell> or <exec> nested element!");
        }
    }

   public static void processException(Throwable t) throws BuildException {

      String reason = null;

      if(t instanceof SshIOException) {
        t = ((SshIOException)t).getRealException();
      }

      if(t instanceof ChannelOpenException) {
        ChannelOpenException ex = (ChannelOpenException)t;

        switch(ex.getReason()) {
          case ChannelOpenException.ADMINISTRATIVIVELY_PROHIBITED:
            reason = "Administratively prohibited";
            break;
          case ChannelOpenException.CONNECT_FAILED:
            reason = "Connect failed";
            break;
          case ChannelOpenException.RESOURCE_SHORTAGE:
            reason = "Resource shortage";
            break;
          case ChannelOpenException.UNKNOWN_CHANNEL_TYPE:
            reason = "Unknown channel type";
            break;
          default:
             reason = "Unknown failure";
        }
      } else if(t instanceof SshException) {
        SshException ex = (SshException)t;

        switch(ex.getReason()) {
          case SshException.BAD_API_USAGE:
            reason = "Bad API usage";
            break;
          case SshException.CANCELLED_CONNECTION:
            reason = "The connection was cancelled";
            break;
          case SshException.CHANNEL_FAILURE:
            reason = "Channel failure";
            break;
          case SshException.CONNECT_FAILED:
            reason  = "Connect failed";
            break;
          case SshException.INTERNAL_ERROR:
            reason = "Internal error";
            break;
          case SshException.KEY_EXCHANGE_FAILED:
            reason = "Key exchange failed";
            break;
          case SshException.PROTOCOL_VIOLATION:
            reason = "Protocol violation";
            break;
          case SshException.REMOTE_HOST_DISCONNECTED:
            reason = "Remote host disconnected";
            break;
          case SshException.UNEXPECTED_TERMINATION:
            reason = "Unexpected termination";
            break;
          case SshException.UNSUPPORTED_ALGORITHM:
            reason = "Unsupported algorithm";
            break;
          default:
            reason = "Unknown failure";
        }
      }

      if(reason!=null)
        throw new BuildException(reason + ": " + t.getMessage(), t);
      else
        throw new BuildException(t);
    }

    protected void connectAndAuthenticate() throws BuildException {

        log("Initializing J2SSH Maverick");

        try {

            connector = SshConnector.getInstance();

            if(version!=SshConnector.SSH1 && version!=SshConnector.SSH2
               && version!=(SshConnector.SSH1 | SshConnector.SSH2))
              throw new BuildException("Invalid version specified! Set to either 1 for SSH1, 2 for SSH2, or do not set to use default of SSH2 with SSH1 as a fallback option");

            if((version & SshConnector.SSH1) != 0)
              log("SSH1 is supported");

            if((version & SshConnector.SSH2) != 0)
              log("SSH2 is supported");

            connector.setSupportedVersions(version);

            log("Configuring SSH contexts");

            if((version & SshConnector.SSH1) != 0) {
              Ssh1Context context = (Ssh1Context) connector.getContext(SshConnector.SSH1);
              if(cipher!=null && cipher.equalsIgnoreCase("3DES")) {
                log("Setting SSH1 cipher to 3DES");
                context.setCipherType(Ssh1Context.CIPHER_3DES);
              } else if(cipher!=null && cipher.equalsIgnoreCase("DES")) {
                log("Setting SSH1 cipher to DES");
                context.setCipherType(Ssh1Context.CIPHER_DES);
              } else {
                log("SSH1 default cipher 3DES will be used");
                context.setCipherType(Ssh1Context.CIPHER_3DES);
              }

              context.setHostKeyVerification(new HostKeyVerification(){
                 public boolean verifyHost(String hostname, SshPublicKey key) {
                   if(verifyhost) {
                    try {
                      return key.getFingerprint().equals(fingerprint);
                    }
                    catch (SshException ex) {
                      log("Failed to generate host key fingerprint");
                      return false;
                    }
                   } else
                     return true;
                 }
               });

               if(sftpcmd!=null)
                 context.setSFTPProvider(sftpcmd);
            }

            if((version & SshConnector.SSH2) != 0) {
              Ssh2Context context = (Ssh2Context) connector.getContext(SshConnector.SSH2);
              context.setPreferredPublicKey(Ssh2Context.PUBLIC_KEY_SSHRSA);
              if(cipher!=null && cipher.equalsIgnoreCase("3DES")) {
                log("Setting SSH2 cipher to 3DES");
                context.setPreferredCipherCS(Ssh2Context.CIPHER_TRIPLEDES_CBC);
                context.setPreferredCipherSC(Ssh2Context.CIPHER_TRIPLEDES_CBC);
              } else if(cipher!=null && cipher.equalsIgnoreCase("BLOWFISH")) {
                log("Setting SSH2 cipher to Blowfish");
                context.setPreferredCipherCS(Ssh2Context.CIPHER_BLOWFISH_CBC);
                context.setPreferredCipherSC(Ssh2Context.CIPHER_BLOWFISH_CBC);
              } else if(cipher!=null) {
                log("Setting SSH2 cipher to " + cipher);
                context.setPreferredCipherCS(cipher);
                context.setPreferredCipherSC(cipher);
              } else {
                log("SSH2 default cipher Blowfish will be used");
                context.setPreferredCipherCS(Ssh2Context.CIPHER_BLOWFISH_CBC);
                context.setPreferredCipherSC(Ssh2Context.CIPHER_BLOWFISH_CBC);
              }

              if(mac != null && mac.equalsIgnoreCase("SHA1")) {
                log("Setting SSH2 MAC to SHA1");
                context.setPreferredMacCS(Ssh2Context.HMAC_SHA1);
                context.setPreferredMacSC(Ssh2Context.HMAC_SHA1);
              } else if(mac != null && mac.equalsIgnoreCase("MD5")) {
                log("Setting SSH2 MAC to MD5");
                context.setPreferredMacCS(Ssh2Context.HMAC_MD5);
                context.setPreferredMacSC(Ssh2Context.HMAC_MD5);
              } else {
                log("Setting SSH2 default MAC to MD5");
                context.setPreferredMacCS(Ssh2Context.HMAC_MD5);
                context.setPreferredMacSC(Ssh2Context.HMAC_MD5);
              }

              context.setHostKeyVerification(new HostKeyVerification(){
                 public boolean verifyHost(String hostname, SshPublicKey key) {
                   if(verifyhost) {
                    try {
                      return key.getFingerprint().equals(fingerprint);
                    }
                    catch (SshException ex) {
                      log("Failed to generate host key fingerprint");
                      return false;
                    }
                   } else
                     return true;
                 }
               });
            }

            log("Creating connection to " + host + (port!=22? ":" + String.valueOf(port) : ""));

            if (ssh == null) {

                log("Connecting....");
                ssh = connector.connect(transport = new SocketTransport(host, port),
                                        username,
                                        true);
                int result;
                boolean authenticated = false;
                log("Authenticating " + username);

                if (keyfile != null && !keyfile.equals("")) {
                    log("Performing public key authentication");

                    PublicKeyAuthentication pk = new PublicKeyAuthentication();

                    // Open up the private key file
                    SshPrivateKeyFile file = SshPrivateKeyFileFactory.parse(new FileInputStream(keyfile));

                    // If the private key is passphrase protected then ask for the passphrase
                    if (file.isPassphraseProtected() && (passphrase == null)) {
                        throw new BuildException(
                            "Private key file is passphrase protected, please supply a valid passphrase!");
                    }

                    // Get the key
                    SshKeyPair pair = file.toKeyPair(passphrase);
                    pk.setUsername(username);
                    pk.setPrivateKey(pair.getPrivateKey());
                    pk.setPublicKey(pair.getPublicKey());

                    // Try the authentication
                    result = ssh.authenticate(pk);

                    if (result == SshAuthentication.COMPLETE) {
                      log("Public key authentication completed");
                      authenticated = true;
                    } else if (result == SshAuthentication.FURTHER_AUTHENTICATION_REQUIRED) {
                      log("Public key authentication completed, attempting password authentication");
                    } else {
                        throw new BuildException(
                            "Public Key Authentication failed!");
                    }
                }

                if ((password != null) && (authenticated == false)) {
                    log("Performing password authentication");

                    PasswordAuthentication pwd = new PasswordAuthentication();
                    pwd.setPassword(password);
                    result = ssh.authenticate(pwd);

                    if (result == SshAuthentication.COMPLETE) {
                        log("Password Authentication completed");
                    } else if (result == SshAuthentication.FURTHER_AUTHENTICATION_REQUIRED) {
                        throw new BuildException(
                            "Password Authentication succeeded but further authentication required!");
                    } else {
                        throw new BuildException(
                            "Password Authentication failed!");
                    }
                }
                startShell();
            }
          } catch(Exception ex) {
             processException(ex);
          }

    }

    protected void startShell() throws SshException, ChannelOpenException, ShellTimeoutException {

        if(ssh instanceof Ssh2Client && solarisBugWorkaround){
          SshSession session = ssh.openSessionChannel();
          session.startShell();
        }

        shell = new Shell(ssh);

        if(prompt!=null)
            shell.setPrompt(prompt);

        if(eol!=null)
            shell.setEOL(eol);

        if(exitCommand!=null)
            shell.setExitCommand(exitCommand);

        shell.setFailOnUknownOS(true);

        if(promptCommand!=null)
            shell.setPromptCommand(null);

        if(promptTimeout > 0)
            shell.setPromptTimeoutPeriod(promptTimeout);

        if(shellInitPeriod > 0)
            shell.setShellInitTimePeriod(shellInitPeriod);

        shell.createSession(term, cols, rows);

        log("Operating system is " +
            shell.getEnvironment().getOperatingSystem());

        if(matcher==null || matcher.equalsIgnoreCase("simple"))
            log("Using simple pattern matcher");
        else {
            log("Using regex pattern matcher");
            ShellProcess.setMatcher(new RegexMatcher());
        }

    }

    protected void disconnect() throws BuildException {
        try {
            log("Disconnecting from " + host);
            shell.exit();
            ssh.disconnect();
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }

    public void execute() throws org.apache.tools.ant.BuildException {

        validate();
        connectAndAuthenticate();
        executeSubTasks();
        disconnect();
    }

    protected void executeSubTasks() throws BuildException {
        Enumeration it = tasks.elements();
        SshSubTask task;

        while (it.hasMoreElements()) {
            task = (SshSubTask) it.nextElement();
            task.setParent(this);
            task.execute(ssh);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSolarisBugWorkaround(boolean solarisBugWorkaround){
      this.solarisBugWorkaround = solarisBugWorkaround;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setEOL(String eol) {
        if(eol.equalsIgnoreCase("crlf"))
          this.eol = "\r\n";
        if(eol.equalsIgnoreCase("cr"))
            this.eol = "\r";
        if(eol.equalsIgnoreCase("lf"))
            this.eol = "\n";
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setVersion(int version) {
      this.version = version;
    }

    public void setNewline(String newline) {
        this.newline = newline;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSftpcmd(String sftpcmd) {
      this.sftpcmd = sftpcmd;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setExitCommand(String exitCommand) {
        this.exitCommand = exitCommand;
    }

    public void setFailOnUnknownOS(boolean failOnUnknownOS) {
        this.failOnUnknownOS = failOnUnknownOS;
    }

    public void setPromptCommand(String promptCommand) {
        this.promptCommand = promptCommand;
    }

    public void setPromptTimeout(int promptTimeout) {
        this.promptTimeout = promptTimeout;
    }

    public void setShellInitPeriod(int shellInitPeriod) {
        this.shellInitPeriod = shellInitPeriod;
    }

    public Vector getTasks() {
        return tasks;
    }

    protected boolean hasMoreSftpTasks() {
        Enumeration it = tasks.elements();

        while (it.hasMoreElements()) {
            if (it.nextElement().getClass().equals(Sftp.class)) {
                return true;
            }
        }

        return false;
    }

    public SshSubTask createExec() {
        SshSubTask task = new Exec(false);
        tasks.addElement(task);

        return task;
    }

    public SshSubTask createSftp() {
        SshSubTask task = new Sftp();
        tasks.addElement(task);

        return task;
    }

    public class Exec extends SshSubTask  {
        private String term = null;
        private int cols = 80;
        private int rows = 34;
        private Vector commands = new Vector();
        String cmd;
        boolean isShell;
        public Exec(boolean isShell) {
            this.isShell = isShell;
        }

        public void execute(SshClient ssh) throws BuildException {
            this.validate();

            try {

                if(shell==null)
                    shell = new Shell(ssh);

                performTasks(shell);
            } catch (Exception ex) {
                processException(ex);
            }
        }

        protected void validate() throws BuildException {
            if (ssh == null) {
                throw new BuildException("Invalid SSH session");
            }

            if (!ssh.isConnected()) {
                throw new BuildException("The SSH session is not connected");
            }
        }

        protected void allocatePseudoTerminal(SshSession session)
            throws BuildException {
            try {
                if (term != null) {
                    if (!session.requestPseudoTerminal(term, cols, rows, 0, 0)) {
                        throw new BuildException(
                            "The server failed to allocate a pseudo terminal");
                    }
                }
              } catch(SshException t) {
                processException(t);
              }

        }

        protected void performTasks(Shell shell)
            throws BuildException {

           try {
               ShellProcess process = shell.execute(cmd);

               if (commands.size() > 0) {
                   Enumeration it = commands.elements();
                   Object obj;

                   while (it.hasMoreElements()) {
                       obj = it.nextElement();

                       if (obj instanceof Write) {
                           ((Write) obj).execute(process);
                       } else if (obj instanceof Read) {
                           ((Read) obj).execute(process);
                       } else {
                           throw new BuildException("Unexpected shell operation " +
                                                    obj.toString());
                       }
                   }
               } else {

                   try {
                       String line;
                       while ((line = process.readLine()) != null) {
                           log(line);
                       }
                   } catch (ShellTimeoutException ex) {
                       processException(ex);
                   }
               }

           } catch(Exception ex) {
               throw new BuildException(ex);
           }
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public void setCols(int cols) {
            this.cols = cols;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        /**
         * Reading/Writing to the session/command
         */
        public Write createWrite() {
            Write write = new Write();
            commands.addElement(write);

            return write;
        }

        public Read createRead() {
            Read read = new Read();
            commands.addElement(read);

            return read;
        }

        public class Read {
            protected String taskString = "";
            private int timeout = 0;
            private boolean echo = true;

            public void execute(ShellProcess process) throws BuildException {


                try {
                    int line = process.expect(taskString, timeout, !echo);

                    if(echo) {
                       // Output all of the lines returned
                       log(process.getLine(line));
                    }
                } catch (ShellTimeoutException ex1) {
                    throw new BuildException("Timeout in expect: " +
                            taskString);
                }
            }

            /**
             *  the message as nested text
             */
            public void addText(String s) {
                setString(Ssh.this.getProject().replaceProperties(s));
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }

            public void setEcho(boolean echo) {
                this.echo = echo;
            }

            /**
             * the message as an attribute
             */
            public void setString(String s) {
                taskString += s;
            }
        }

        public class Write {
            protected boolean echo = true;
            protected String taskString = "";
            protected boolean newline = true;

            public void execute(ShellProcess process) throws BuildException {

                try {
                    process.type(taskString);

                    if (newline) {
                        process.carriageReturn();
                    }
                } catch (IOException ex) {
                    processException(ex);
                }

            }

            /**
             *  the message as nested text
             */
            public void addText(String s) {
                setString(Ssh.this.getProject().replaceProperties(s));
            }

            /**
             * the message as an attribute
             */
            public void setString(String s) {
                taskString += s;
            }

            public void setEcho(boolean echo) {
                this.echo = echo;
            }

            public void setNewline(boolean newline) {
                this.newline = newline;
            }
        }
    }

    class RegexMatcher implements ShellProcess.Matcher {
        public boolean matches(String line, String pattern) {
            return line.matches(pattern);
        }
    }
}
