
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
		
		new_next_row.className  = 'error-message';
		new_next_row.input_form = element.form;
		
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
					var error_message_text = document.createTextNode(element.title);
				
					error_message_element.appendChild(error_message_text);
				}
			}
		}
	}
	
	return true;
}

var num_invalid_fields;

function validate_entire_form(specified_form) {
 	num_invalid_fields = 0;
	
	// Count number of invalid fields:
	$('tr.error-message').each(
		function() {
			if (this.input_form == specified_form && this.childNodes[1].childNodes.length != 0) {
				++num_invalid_fields;
			}
		}
	);
	
	if (num_invalid_fields > 0) {
		alert("Please fill in all required information before proceeding.");
		
		return false;
	}
	
	return true;
}