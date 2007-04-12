<?php
    # Simple password protection
    #
    # (c) http://www.phpbuddy.com
    # Author: Ranjit Kumar
    # Feel free to use this script but keep this message intact!
    # 
    # To protect a page include this file in your PHP pages!

    include_once("login-info.php");

    session_start();

    //you can change the username and password by changing the above two strings 

    if (!isset($HTTP_SESSION_VARS['user'])) {
	
    	if(isset($HTTP_POST_VARS['u_name'])) 
    		$u_name = $HTTP_POST_VARS['u_name'];
	
    	if(isset($HTTP_POST_VARS['u_password'])) 
    		$u_password = $HTTP_POST_VARS['u_password'];
	
    	if(!isset($u_name)) {
    		?>
    		<HTML>
    		<HEAD>
    		<TITLE><?php echo $HTTP_SERVER_VARS['HTTP_HOST']; ?> : Authentication Required</TITLE>
    		</HEAD>
    		<BODY bgcolor=#ffffff>
    		<table border=0 cellspacing=0 cellpadding=0 width=100%>
    			 <TR><TD>
    			 <font face=verdana size=2><B>(Access Restricted to Authorized Personnel)</b> </font></td>
    			 </tr></table>
    		<P></P>
    		<font face=verdana size=2>
    		<center>
    		<?php
    		$form_to = "http://$HTTP_SERVER_VARS[HTTP_HOST]$HTTP_SERVER_VARS[PHP_SELF]";
		
    		if(isset($HTTP_SERVER_VARS["QUERY_STRING"]))
    		    $form_to = $form_to ."?". $HTTP_SERVER_VARS["QUERY_STRING"];
		
    		?>
    		<form method=post action=<?php echo $form_to; ?>>
    		<table border=0 width=350>
    		<TR>
    		<TD><font face=verdana size=2><B>User Name</B></font></TD>
    		<TD><font face=verdana size=2><input type=text name=u_name size=20></font></TD></TR>
    		<TR>
    		<TD><font face=verdana size=2><B>Password</B></font></TD>
    		<TD><font face=verdana size=2><input type=password name=u_password size=20></font></TD>
    		</TR>
    		</table>
    		<input type=submit value=Login></form>
    		</center>
    		</font>
    		</BODY>
    		</HTML>
		
    		<?php
    		exit;
    	}
    	else {
		
    		function login_error($host,$php_self) {
    			echo "<HTML><HEAD>
    			<TITLE>$host :  Administration</TITLE>
    			</HEAD><BODY bgcolor=#ffffff>
    			<table border=0 cellspacing=0 cellpadding=0 width=100%>
    				 <TR><TD align=left>
    				 <font face=verdana size=2><B> &nbsp;You Need to log on to access this part of the site! </b> </font></td>
    				 </tr></table>
    			<P></P>
    			<font face=verdana size=2>
    			<center>";
						
    			echo "Error: You are not authorized to access this part of the site!
    			<B><a href=$php_self>Click here</a></b> to login again.<P>
    			</center>
    			</font>
    			</BODY>
    			</HTML>";
    			session_unregister("adb_password");
    			session_unregister("user");
    			exit;
    		}
		
    		$user_checked_passed = false;		
		
    		if(isset($HTTP_SESSION_VARS['adb_password'])) {
			
    			$adb_session_password = $HTTP_SESSION_VARS['adb_password'];
			
    			if(ADMIN_PASSWORD != $adb_session_password) 
    				login_error($HTTP_SERVER_VARS['HTTP_HOST'],$HTTP_SERVER_VARS['PHP_SELF']);
    			else {
    				$user_checked_passed = true;
    			}
    		}		
		
    		if($user_checked_passed == false) {
			
    			if(strlen($u_name)< 2) 
    				login_error($HTTP_SERVER_VARS['HTTP_HOST'],$HTTP_SERVER_VARS['PHP_SELF']);
			
    			if(ADMIN_USERNAME != $u_name) //if username not correct
    				login_error($HTTP_SERVER_VARS['HTTP_HOST'],$HTTP_SERVER_VARS['PHP_SELF']);		
			
    			if(isdefined("ADMIN_PASSWORD")) {
				
    				if(ADMIN_PASSWORD == $u_password) {
					
    					session_register("adb_password");
    					session_register("user");
					
    					$adb_password = ADMIN_PASSWORD;
    					$user = $u_name;
    				}
    				else { //password in-correct
    					login_error($HTTP_SERVER_VARS['HTTP_HOST'],$HTTP_SERVER_VARS['PHP_SELF']);
    				}
    			}
    			else {
    				login_error($HTTP_SERVER_VARS['HTTP_HOST'],$HTTP_SERVER_VARS['PHP_SELF']);
    			}
				
    			$page_location = $HTTP_SERVER_VARS['PHP_SELF'];
    			if(isset($HTTP_SERVER_VARS["QUERY_STRING"]))
    			$page_location = $page_location ."?". $HTTP_SERVER_VARS["QUERY_STRING"];
			
    			header ("Location: ". $page_location);
    		}
    	}
    }
?>