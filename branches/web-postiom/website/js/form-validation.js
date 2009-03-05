/**
 * Finds the parent of an element that matches a specified tag name
 * (e.g. 'FORM', 'TR', etc.)
 * 
 * @return The parent element if found, otherwise null
 */
function find_parent_by_tagname(element, tagName) {
    parent_form = element.parentNode;

    while (parent_form.tagName != tagName) {
        parent_form = parent_form.parentNode;
    }

    if (parent_form.tagName == tagName) {
        return parent_form;
    }

    return null;
}

/**
 * Creates an 'error message' row beneath the row containing the specified
 * element. If such a row already exists, it will be cleared of any error
 * message.
 *
 * @return The column element, where the error message of the new error row
 *         should be placed.
 *
 */
function validate_create_blank_error_message_element(element) {
    // Find the containing table row:
    var parent_tr = element.parentNode;

    while (parent_tr && parent_tr.tagName != 'TR') {
        parent_tr = parent_tr.parentNode;
    }

    if (!parent_tr) return null;

    var table = parent_tr.parentNode;

    // Get the next row of the containing table row, if any:
    var next_row = parent_tr.nextSibling;

    // If there is no next row, or if it isn't the error message row, then
    // create a new error message row:
    if (!next_row || next_row.className != 'error-message') {
        var new_next_row = document.createElement("tr");

        if (element.form == undefined) {
            new_next_row.input_form = find_parent_by_tagname(element, 'FORM');
        }
        else {
            new_next_row.input_form = element.form;
        }

        new_next_row.className  = 'error-message';
//        new_next_row.input_form = element.form;

        // Insert the new row into the table at the appropriate position:
        if (!next_row) {
            table.appendChild(new_next_row);
        }
        else {
            table.insertBefore(new_next_row, next_row);
        }

        next_row = new_next_row;

        // Give the row two columns:
        next_row.appendChild(document.createElement('td'));
        next_row.appendChild(document.createElement('td'));
    }

    // Get the second column of the row:
    var second_col = next_row.childNodes[1]; // Second child node

    // Clear any children of the second column of the row:
    while (second_col.hasChildNodes()) {
        second_col.removeChild(second_col.firstChild);
    }

    return second_col;
}

/**
 * Validates a form element.
 *
 * This function assumes a certain structure to the element:
 *
 * <td>[element label]</td> <td>[element]</td>
 *
 * If the elmenet is valid (i.e. the regular expression matches the value of
 * the element), then the label is given the 'valid' class; otherwise, it is
 * given the 'invalid' class.
 */
function validate_form_element(element, vpattern) {
    var parent_td = element.parentNode;

    while (parent_td && parent_td.tagName != 'TD') {
        parent_td = parent_td.parentNode;
    }

    if (parent_td) {
        var element_label = parent_td.previousSibling;

        while (element_label && element_label.tagName != 'TD') {
            element_label = element_label.previousSibling;
        }

        if (element_label) {
            var result;

            if ((result = vpattern.exec(element.value)) != null) {
                element_label.className = 'valid';

                validate_create_blank_error_message_element(element);
            }
            else {
                element_label.className = 'invalid';

                var error_message_element = validate_create_blank_error_message_element(element);

                if (error_message_element) {
                    //var error_message_text = document.createTextNode(element.title);

                    //error_message_element.appendChild(error_message_text);

                    error_message_element.innerHTML = '<p>' + element.title + '</p>';
                }
            }
        }
    }

    return true;
}

function validate_entire_form(specified_form) {
     var num_invalid_fields = 0;

    // Count number of invalid fields:
    $('tr.error-message').each(
        function() {
            if (this.input_form == specified_form && this.childNodes[1].childNodes.length != 0) {
                ++num_invalid_fields;
            }
        }
    );

    if (num_invalid_fields > 0) {
        alert("Please fill in all required information and correct\n" +
	      "all errors (marked in red) before proceeding.");

        return false;
    }

    return true;
}

function setup_input_validation_patterns() {
    hits = 0;

    // Patterns that the input must match
    // Keeping the original email pattern for reference.  It causes
    // significant slowdowns (to the point of making the browser
    // unresponsive).  Use a lighter version instead.
    //email_pattern = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*\.(\w{2}|(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum))$/;
    email_pattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z0-9]+$/;
    name_pattern = /^\S{2,}\s+((\S\s+\S{2,})|(\S{2,})).*$/;
    title_pattern = /^\s*\S+.*$/;
    organization_pattern = /^\S{1,}.*$/;
    password_pattern = /\S+/;
    keywords_pattern = /\S{3,}.*/;
    int_pattern = /^[0-9]+$/;
    date_pattern = /^[0-9]{4}-[0-1]?[0-9]-[0-3]?[0-9]$/;

    // Go through all the document forms
    for (var i = 0; i < document.forms.length; ++i) {
        // Go through all the elements on the form
        for (var j = 0; j < document.forms[i].length; ++j) {
            // Get the form element
            element = document.forms[i][j];

            // Process if the element is an input (probably a faster way to do this?)
            if (element.nodeName == "INPUT") {
                switch (element.name) {
                    case "contributor_email":
                    case "contribution_contact_email":
                        ++hits;
                        element.pattern = email_pattern;
                        break;

                    case "contributor_name":
                    case "contribution_authors":
                        ++hits;
                        element.pattern = name_pattern;
                        break;

                    case "contribution_title":
                        ++hits;
                        element.pattern = title_pattern;
                        break;

                    case "contributor_organization":
                    case "contribution_authors_organization":
                        ++hits;
                        element.pattern = organization_pattern;
                        break;

                    case "contributor_password":
                    case "contributor_password1":
                    case "contributor_password2":
                        ++hits;
                        element.pattern = password_pattern;
                        break;

                    case "contribution_keywords":
                        ++hits;
                        element.pattern = keywords_pattern;
                        break;
			
                    case "sim_ask_later_duration":
                    case "install_ask_later_duration":
                    case "install_recommend_update_age":
                        ++hits;
                        element.pattern = int_pattern;
                        break;

                    case "install_recommend_update_date":
                        ++hits;
                        element.pattern = date_pattern;
                        break;

                    default:
                       break;
                }
            }
        }
    }

    return hits;
}

function validate_and_setup_validation_triggers() {
    // This could be improved, but at the moment it isn't taking enough
    // time to worry about.
    $('input, button, textarea, select').each(
        function() {
            if (this.pattern) {
                // Perform immediate validation:
                validate_form_element(this, this.pattern);

                // Validate on key up:
                this.onkeyup = function() {
                    validate_form_element(this, this.pattern);

                    return true;
                };

                // Validate on change (for autofill & such):
                this.onchange = function() {
                    validate_form_element(this, this.pattern);

                    return true;
                };

                // Validate on blur (for Firefox autofill):
                this.onblur = function() {
                    validate_form_element(this, this.pattern);

                    return true;
                };

                // Validate on click (for Firefox autofill):
                this.onclick = function() {
                    validate_form_element(this, this.pattern);

                    return true;
                };

                // IE6 workaround (it doesn't fire onchange for autofill):
                this.onpropertychange = function() {
                    validate_form_element(this, this.pattern);

                    return true;
                };
            }
        }
    );
}

function setup_submit_form_validation() {
    // This could be improved, but at the moment it isn't taking enough
    // time to worry about
    $('input').each(
        function() {
            if (this.getAttribute('type') == 'submit') {
                this.onclick = function() {
                    valid = validate_entire_form(this.form);
                    if (!valid) {
                        return valid;
                    }

                    return insert_submit_stamp(this);
                    //orig: return validate_entire_form(this.form);
                };
            }
        }
    );
}

// This code will disable everything on the page, except the login stuff
function disable_not_always_enabled_form_elements() {
    $('input').not('.always-enabled').disable();
    $('select').not('.always-enabled').disable();
    $('textarea').not('.always-enabled').disable();
    $('input.button').enable();
}
