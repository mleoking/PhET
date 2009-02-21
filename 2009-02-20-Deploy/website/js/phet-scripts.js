// AJAX login stuff:

// This array stores all the 'contributor organizations' that we generated:
var generated_contributor_orgs = [];

var generated_contributor_names = [];

function post_required_info_displayed() {                    
    on_email_change();
    
    // Place focus on password if it's empty':
    var password_element = document.getElementById('contributor_password_uid');
    
    if (password_element) {
        password_element.focus();
    }
}

function select_text_in_input(field, start, end){
	if( field.createTextRange ){
		var selRange = field.createTextRange();
		selRange.collapse(true);
		selRange.moveStart("character", start);
		selRange.moveEnd("character", end);
		selRange.select();
	} 
	else if( field.setSelectionRange ){
		field.setSelectionRange(start, end);
	} 
	else {
		if( field.selectionStart ){
			field.selectionStart = start;
			field.selectionEnd = end;
		}
	}
	field.focus();
};

function select_question_marks_in_input(id) {
    var element = document.getElementById(id);
    
    if (element) {
        var value      = element.value;
        var firstIndex = value.indexOf('?');
        var lastIndex  = value.lastIndexOf('?') + 1;
        
        if (firstIndex != -1) {                            
            select_text_in_input(element, firstIndex, lastIndex);
        }
    }
}

function on_remind_me() {
    var email_element = document.getElementById('contributor_email_uid');

    var email = email_element.value;
    
    var password_element = document.getElementById('ajax_password_comment_uid');

    HTTP.updateElementWithGet('../admin/remind-password.php?contributor_email=' + 
        encodeURI(email), null, 'ajax_password_comment_uid');
}

function on_email_entered() {
    var email_element    = document.getElementById('contributor_email_uid');
    var password_element = document.getElementById('contributor_password_uid');
    
    var email    = email_element.value;
    var password = password_element.value;
        
    HTTP.updateElementWithGet('../admin/do-ajax-login.php?contributor_email=' + 
        encodeURI(email) + '&contributor_password=' + encodeURI(password), 
        null, 'required_login_info_uid', 'post_required_info_displayed();');
}

function deduce_author_organization() {
    var email_element = document.getElementById('contributor_email_uid');

    var email = email_element.value;
    
    HTTP.updateElementValueWithGet('../admin/get-contributor-org.php?contributor_email=' +
        encodeURI(email), null, 'contribution_authors_organization_uid'); 
}
    
function on_email_change() {
    var email_element = document.getElementById('contributor_email_uid');

    var email = email_element.value;
    
    var contact_element = document.getElementById('contribution_contact_email_uid');
        
    if (contact_element) {
        contact_element.value = email;
    }      
    
    // The email has changed. Now we would like to update the contributor organization
    // based on the email domain:
    var contributor_org_element = document.getElementById('contributor_organization_uid');
    var contribution_org_element = document.getElementById('contribution_authors_organization_uid');

    if (contributor_org_element) {
        // We can update contributor organization if it's blank:
        var can_overwrite_contrib = contributor_org_element.value == '';
        
        // Otherwise, the only reason we can update it is if it's holding a value
        // that we generated ourselves:
        if (!can_overwrite_contrib) {
            for (var i = 0; i < generated_contributor_orgs.length; i++) {
                if (generated_contributor_orgs[i] == contributor_org_element.value) {
                    can_overwrite_contrib = true;
                    
                    break;
                }
            }
        }
        
        if (can_overwrite_contrib) {
            var email_pattern = /^s*\w+@(\w+)(\.([\w\.]+))?\s*$/;
            
            var result;
            
            if ((result = email_pattern.exec(email)) != null) {
                var domain = result[1];
                var ext    = result[3];
                
                domain = domain.substring(0, 1).toUpperCase() + domain.substring(1, domain.length);
                
                if (ext == 'edu') {
                    contributor_org_element.value = 'University of ' + domain;
                }
                else {
                    contributor_org_element.value = domain + ', Inc.';
                }
                
                // Remember that we generated this value, so we know we can overwrite
                // it later:
                generated_contributor_orgs.push(contributor_org_element.value);
                
                var contribution_org_element = document.getElementById('contribution_authors_organization_uid');

                if (contribution_org_element) {
                    contribution_org_element.value = contributor_org_element.value;
                }
            }
        }
    }

    HTTP.updateElementWithGet('../admin/check-email.php?contributor_email=' + 
        encodeURI(email), null, 'ajax_email_comment_uid', 'on_password_change();');
}

function on_email_change_guess_data() {
	on_email_change_guess_organization();
	on_email_change_guess_name();
}

function on_email_change_guess_organization() {
    var email_element = document.getElementById('contributor_email_uid');

    var email = email_element.value;                        
   
    // The email has changed. Now we would like to update the contributor organization
    // based on the email domain:
    var contributor_org_element = document.getElementById('contributor_organization_uid');

    if (contributor_org_element) {
        // We can update contributor organization if it's blank:
        var can_overwrite_contrib = contributor_org_element.value == '';
        
        // Otherwise, the only reason we can update it is if it's holding a value
        // that we generated ourselves:
        if (!can_overwrite_contrib) {
            for (var i = 0; i < generated_contributor_orgs.length; i++) {
                if (generated_contributor_orgs[i] == contributor_org_element.value) {
                    can_overwrite_contrib = true;
                    
                    break;
                }
            }
        }
        
        if (can_overwrite_contrib) {
            var email_pattern = /^\s*[.\w]+@(\w+)(\.([\w\.]+))?\s*$/;
            
            var result;
            
            if ((result = email_pattern.exec(email)) != null) {
                var domain = result[1];
                var ext    = result[3];
                
                domain = domain.substring(0, 1).toUpperCase() + domain.substring(1, domain.length);
                
                if (ext == 'edu') {
                    contributor_org_element.value = 'University of ' + domain;
                }
                else {
                    contributor_org_element.value = domain + ', Inc.';
                }
                
                // Remember that we generated this value, so we know we can overwrite
                // it later:
                generated_contributor_orgs.push(contributor_org_element.value);
            }
        }
    }
}

function on_email_change_guess_name() {
    var email_element = document.getElementById('contributor_email_uid');

    var email_element_value = email_element.value;                        
   
    // The email has changed. Now we would like to update the contributor organization
    // based on the email domain:
    var contributor_name_element = document.getElementById('contributor_name_uid');

    if (contributor_name_element) {
        // We can update contributor organization if it's blank:
        var can_overwrite_contrib = contributor_name_element.value == '';
        
        // Otherwise, the only reason we can update it is if it's holding a value
        // that we generated ourselves:
        if (!can_overwrite_contrib) {
            for (var i = 0; i < generated_contributor_names.length; i++) {
                if (generated_contributor_names[i] == contributor_name_element.value) {
                    can_overwrite_contrib = true;
                    
                    break;
                }
            }
        }
        
        if (can_overwrite_contrib) {
            var email_pattern = /^\s*([.\w]+)@(\w+)(\.([\w\.]+))?\s*$/;
            
            var result;
            
            if ((result = email_pattern.exec(email_element_value)) != null) {
				var name;
                var username = result[1];

				if (username.indexOf('.') != -1) {
					var name_pattern = /^(\w+)\.(\w+)$/;
					
					if ((result = name_pattern.exec(username)) != null) {
						var first_name = result[1];
						var last_name  = result[2];
						
						if (first_name.length > 0 && last_name.length > 0) {
							name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1, first_name.length) + " " + 
							       last_name.substring (0, 1).toUpperCase() + last_name.substring (1, last_name.length);
						}
						else {
							name = first_name + " " + last_name;
						}
					}
				}
				else {
					var two_consonant_pattern = /([bcdfghjklmnpqrstvwxz])([bcdfghjklmnpqrstvwxz])/g;
					
					// Fix Firefox bug which reuses regexp object:
					two_consonant_pattern.lastIndex = 0;
					
					while ((result = two_consonant_pattern.exec(username)) != null) {
						// We do want to consider the second consonant next time we match, so
						// we manually bump down the 'next index' property:
						two_consonant_pattern.lastIndex = two_consonant_pattern.lastIndex - 1;
						
						var c1 = result[1].toLowerCase();
						var c2 = result[2].toLowerCase();
						
						if (c1 == c2) continue;
						if (c1 == 's' || c2 == 's') continue;
						if (c1 == 'r' && result.index > 0 || c2 == 'r') continue;
						if (c1 == 'h') continue;
					
						var index = two_consonant_pattern.lastIndex;
						
						var first_name = username.substring(0, index);
						var last_name  = username.substring(index, username.length);
						
						if (first_name.length > 0 && last_name.length > 0) {
							name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1, first_name.length) + " " + 
							       last_name.substring (0, 1).toUpperCase() + last_name.substring (1, last_name.length);
						}
						else {
							name = first_name + " " + last_name;
						}
						
						break;
					}
					
					if (name == undefined && username.length > 0) {
						name = username.substring(0, 1).toUpperCase() + username.substring(1, username.length);
					}
				}
                
				if (name != undefined) {
					contributor_name_element.value = name;
					
                    // Remember that we generated this value, so we know we can overwrite
                    // it later:
                    generated_contributor_names.push(contributor_name_element.value);
				}
            }
        }
    }
}

function on_contributor_organization_change() {
    var contributor_org_element = document.getElementById('contributor_organization_uid');
    var contribution_org_element = document.getElementById('contribution_authors_organization_uid');
    
    if (contributor_org_element && contribution_org_element) {
        if (contribution_org_element.value == '') {
            contribution_org_element.value = contributor_org_element.value;
        }
    }
}

function on_password_change() {
    var email_element    = document.getElementById('contributor_email_uid');
    var password_element = document.getElementById('contributor_password_uid');
    
    var email    = email_element.value;
    var password = password_element.value;

    HTTP.updateElementWithGet('../admin/check-password.php?contributor_email=' + 
        encodeURI(email) + '&contributor_password=' + 
        encodeURI(password), null, 'ajax_password_comment_uid', 'deduce_author_organization();');
}

function on_name_change(n) {
    var name;
    
    // When the name changes, update the authors:
    if (n) {
        name = n;                            
    }
    else {
        var name_element = document.getElementById('contributor_name_uid');
    
        if (name_element) {
            name = name_element.value;
        }
    }
     
    if (name) {   
        var authors_element = document.getElementById('contribution_authors_uid');
        
        if (authors_element) {
            authors_element.value = name;
        }
        
        var password_element = document.getElementById('contributor_password_uid');
        
        var password_url = '';
        
        if (password_element) {
            var password = password_element.value;
            
            password_url = '&contributor_password=' + encodeURI(password);
        }
        
        HTTP.updateElementWithGet('../admin/do-ajax-login.php?contributor_name=' + 
            encodeURI(name) + password_url, 
            null, 'required_login_info_uid', 'on_email_change();');
    }
}

function login_create_account() {
    var name     = document.getElementById('contributor_name_uid').value;
    var email    = document.getElementById('contributor_email_uid').value;
    var password = document.getElementById('contributor_password_uid').value;
    var org      = document.getElementById('contributor_organization_uid').value;
    
    HTTP.updateElementWithGet('../admin/do-ajax-login.php' + 
        '?contributor_name='            + encodeURI(name)       + 
        '&contributor_email='           + encodeURI(email)      +
        '&contributor_password='        + encodeURI(password)   +
        '&contributor_organization='    + encodeURI(org)        +
        '&action=create', 
        null, 'required_login_info_uid');
}

function login_login() {
    var email    = document.getElementById('contributor_email_uid').value;
    var password = document.getElementById('contributor_password_uid').value;
    
    HTTP.updateElementWithGet('../admin/do-ajax-login.php' + 
        '?contributor_email='           + encodeURI(email)      +
        '&contributor_password='        + encodeURI(password)   +
        '&action=login', 
        null, 'required_login_info_uid');                        
}

function string_starts_with(this_string, that_string) {
    var index = this_string.lastIndexOf(that_string);
    
    if (index == -1) {
        return false;
    }
    
    return index == 0;
}

function open_limited_window(url, name) {
	window.open(url, name, 'status=no,toolbar=no,location=no,menubar=no,directories=no,resizable=yes,scrollbars=no,width=640,height=480');
}


