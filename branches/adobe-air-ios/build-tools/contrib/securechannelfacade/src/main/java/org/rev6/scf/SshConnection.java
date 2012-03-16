package org.rev6.scf;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * 
 * 
 * SshConnection is an abstraction of a JSch Session which handles 
 * the establishment of an ssh connection to a remote server.  
 * <p>
 * Once connect() has been successfully called, any number of SshTask's
 * can be run by calling the execute method until disconnect() is called.
 * <p>
 * It works with username/password authentication or ssh2 username/private key
 * authentication.
 * <p>
 * The default port of 22 is always used unless explicitly set via the setPort
 * method.
 * <p>
 * For more information on JSch, please see http://www.jcraft.com/jsch/ 
 * 
 * @author whaley
 */
public class SshConnection
{
  private static final Properties SSH_PROPERTIES = new Properties();
  static
  {
    SSH_PROPERTIES.put("StrictHostKeyChecking", "No");
  }
  
  protected final Logger logger = 
    Logger.getLogger(this.getClass().getPackage().getName());
  String host;
  int port = 22;
  String username;
  String password;
  File privateKeyFile;
  boolean usePrivateKey = false;
  
  Session sshSession; 
  
  /**
   * Default constructor. Requires that, at least setUsername, setHost, and 
   * either setPassword or setPrivateKeyFile and usePrivateKey be called. 
   */
  public SshConnection()
  {
    
  }
  
  /**
   * Requires that setPassword or setPrivateKeyFile and usePrivateKey be called. 
   * @param host The remote server to scp the file to  
   * @param username The username on the remote server
   */
  public SshConnection(String host, String username)
  {
    this.setHost(host);
    this.setUsername(username);
  }
  
  /**
   * Initializes an SshConnection to use private key authentication.
   * @param host The remote server to scp the file to  
   * @param username The username on the remote server
   * @param privateKeyFile File on the localhost machine used for 
   * privatekey authentication
   */
  public SshConnection(String host, String username, File privateKeyFile)
  {
    this.setHost(host);
    this.setUsername(username);
    this.setPrivateKeyFile(privateKeyFile);
    this.setUsePrivateKey(true);
  }
  
  /**
   * Initializes an SshConnection to use password based authentication.
   *
   * @param host The remote server to scp the file to  
   * @param username The username on the remote server
   * @param password password to authenticate the username with
   */
  public SshConnection(String host, String username, String password)
  {
    this.setHost(host);
    this.setUsername(username);
    this.setPassword(password);
  }
  /**
   * Disconnects from the ssh session.
   * @see Closeable
   */
  public void disconnect()
  {
    if (sshSession != null)
    {
      sshSession.disconnect(); 
    }
  }
  
  /**
   * Connects to and maintains a ssh connection.  If this method is called
   * twice, a reconnection is attempted
   * 
   * @throws SshException 
   *  If the members of the SshConnection class are not properly set or if 
   *  there was a problem actually connecting to the specified host.
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without first
   *  disconnecting.  
   */
  public void connect() throws SshException
  { 
    exceptIfAlreadyConnected();
    
    try
    {
      JSch jsch = new JSch();
      
      validateMembers();
      
      if (this.usePrivateKey)
      {
        jsch.addIdentity(this.privateKeyFile.getAbsolutePath());
      }

      this.sshSession = jsch.getSession(this.username, this.host, this.port);
      this.sshSession.setConfig(SSH_PROPERTIES);
      
      if (!this.usePrivateKey && this.password != null)
      {
        this.sshSession.setPassword(this.password); 
      }       
      
      this.sshSession.connect();
    }
    catch (JSchException e)
    {
      throw new SshException(e);
    }
    
  }

  private void exceptIfAlreadyConnected()
  {
    if (this.sshSession != null && this.sshSession.isConnected())
    {
      throw new IllegalStateException("connect() failed.  Connection already " 
          + "established to " + this.username + "@" + this.host + ":" 
          + Integer.toString(this.port)); 
    }
  }
  
  /**
   * Executes a task over ssh.  connect() must be called before invoking
   * @param sshTask The task to be executed.
   * @throws SshException If there was a problem executing the task or
   *                      if connect() was not called first.
   * @see SshTask
   */
  public void executeTask(SshTask sshTask) throws SshException
  {
    long startTime = System.currentTimeMillis();
    long totalTime;
    
    logger.info("Beginning SshTask of " + sshTask.toString());

    sshTask.execute(this.sshSession);
    
    totalTime = (System.currentTimeMillis() - startTime) / 1000;
    logger.info("Completed SshTask of " + sshTask.toString() + " in " 
        + totalTime + " seconds." );
  }
  
  /**
   * Disconnects the ssh session if the client of the SshConnection was
   * too lazy to and the SshConnection object gets gc'ed. 
   * 
   * Not guaranteed to be called.
   */
  protected void finalize() 
  {
    this.disconnect();
  }
  
  /**
   * @param host
   *          The host a file is being copied to.
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without 
   *  first disconnecting
   */
  public void setHost(String host)
  {
    exceptIfAlreadyConnected();
    this.host = host;
  }
  
  /**
   * @param password
   *          The password that will be used in ssh authentication.
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without 
   *  first disconnecting.          
   */
  public void setPassword(String password)
  {
    exceptIfAlreadyConnected();
    this.password = password;
  }
  
  /**
   * @param port
   *          Sets the ssh port. Default is 22.
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without
   *  first disconnecting.
   */
  public void setPort(int port)
  {
    exceptIfAlreadyConnected();
    this.port = port;
  }
  
  
  /**
   * Sets the private key file 
   * setUsePrivateKey must be set to true to use a private key 
   * @param privateKeyFile
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without
   *  first disconnecting. 
   */
  public void setPrivateKeyFile(File privateKeyFile)
  {
    exceptIfAlreadyConnected();
    this.privateKeyFile = privateKeyFile;
  }
  
  /**
   * Sets the private key file by the path of the private key file.
   * setUsePrivateKey must be set to true to use a private key 
   * @param privateKeyFileName
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without
   *  first disconnecting. 
   */
  public void setPrivateKeyFile(String privateKeyFileName)
  {
    exceptIfAlreadyConnected();
    this.setPrivateKeyFile(new File(privateKeyFileName));
  }
  
  /**
   * Determines whether a private key is used or not when
   * authenticating. Default is false.
   * @param usePrivateKey
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without first
   *  disconnecting.
   */
  public void setUsePrivateKey(boolean usePrivateKey) 
  {
    exceptIfAlreadyConnected();
    this.usePrivateKey = usePrivateKey;
  }
  
  /**
   * @param username
   *          The username that will be used in ssh authentication.
   * @throws IllegalStateException
   *  If connect() was called once successfully. connect() and any setter
   *  method can't be called after successfully connecting without 
   *  first disconnecting.          
   */
  public void setUsername(String username) 
  {
    exceptIfAlreadyConnected();
    this.username = username;
  }
  
  /**
   * Validates all private members are set correctly before beginning
   * transfer.
   * @throws SshException if members are not set appropriately.
   */
  private void validateMembers() throws SshException
  {
    if (this.host == null)
      throw new SshException("host not set.  "
          + "setHost must be called before calling sendFile");
    
    if (this.username == null)
      throw new SshException("username not set. "
          + "setUsername must be called before calling sendFile");
    
    if (this.usePrivateKey)
    {
      if (this.privateKeyFile == null || !this.privateKeyFile.canRead())
        throw new SshException("if usePrivateKey is true, then a readable "
            + "privateKeyFile must be specified.");
    }
    else
    {
      if (this.password == null)
        throw new SshException("password not set.  " + "setPassword must be "
            + "called before calling sendFile");
    }
  } 
}