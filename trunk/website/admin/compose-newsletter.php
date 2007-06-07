<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	
	
	function print_content() {
	    global $contributor_email;
	    
	    print <<<EOT
	        <h1>Compose Newsletter</h1>
	        
	        <p>
	            Using the form below, you can send email to everyone who has a PhET account 
	            and who has elected to receive emails from PhET.
	        </p>
	        
	        <p>
	            The following jokers are dynamically replaced for each recipient:
	        </p>
	        
	        <ul>
	            <li>\$NAME\$</li>
	            
	            <li>\$DATE\$</li>
	        </ul>
	        
	        <form id="#composenewsletter" action="dispatch-newsletter.php" method="post">
	            <fieldset>
	                <legend>Newsletter</legend>
	                
        	        <div class="field">
    	                <span class="label_content">
    	                    <input type="text" size="40" name="newsletter_from" value="$contributor_email" />
    	                </span>
                
                        <span class="label">
                            from address
                        </span>
                    </div>
                
    	            <div class="field">
    	                <span class="label_content">
    	                    <input type="text" size="40" name="newsletter_subject" value="Important Announcement from PhET"/>
    	                </span>
	                
    	                <span class="label">
    	                    email subject
    	                </span>
    	            </div>
	            
    	            <div class="field">
    	                <span class="label_content">
<textarea rows="30" cols="40" name="newsletter_body" onfocus="javascript:this.select();">

Dear \$NAME\$,
    
    Today is \$DATE\$.
    
Regards,
    
The PhET Team

----

If you would like to unsubscribe from the PhET mailing list, please visit http://phet.colorado.edu/teacher_ideas/user-edit-profile.php

</textarea>
    	                </span>
	                
    	                <span class="label">
    	                    email body
    	                </span>
    	            </div>
	            
    	            <div class="field">
    	                <span class="label_content">
    	                    <input type="submit" value="Send" />
    	                </span>
    	            </div>
    	            
    	            <br/>
    	        </fieldset>
	        </form>
	        
EOT;
	}
	
	print_site_page('print_content', 9);

?>