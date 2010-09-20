<?php

	define("BLANK_SPACES", "    ");
	define("SYMBOL_BOUNDARY_START", '(?<=\w|\s|\d|\b|$|^)');
	define("SYMBOL_BOUNDARY_END",   '(?=\w|\s|\d|\b|$|^)');


	$g_reparsed_xml           = "";
	$g_xml_tag_open_outputted = false;
	$g_xml_tag_contents       = "xml_output_text";

	$test_xml = '<?xml version="1.0" encoding="UTF-8"?>'."\n".'<jnlp spec="1.0+" codebase="http://phet-web.colorado.edu/simulations" href="cck/cck.jnlp">'."\n".'<information>'."\n".'<title>Circuit Construction Kit</title>'."\n".'<vendor>PhET(tm)</vendor>'."\n".'<homepage href="http://phet-web.colorado.edu"/>'."\n".'<description>The Circuit Construction Kit</description>'."\n".'<description kind="short">The Circuit Construction Kit.</description>'."\n".'<icon href="../Design/Assets/images/Phet-Kavli-logo.jpg"/>'."\n".'<icon kind="splash" href="../Design/Assets/images/Phet-Kavli-logo.jpg"/>'."\n".'<offline-allowed/>'."\n".'</information>'."\n".'<security></security>'."\n".'<resources>'."\n".'<j2se version="1.4+"/>'."\n".'<jar href="cck/cck.jar"/>'."\n".'<jar href="cck/lib/Jama-1.0.1.jar"/>'."\n".'<jar href="cck/lib/jnlp.jar"/>'."\n".'<jar href="cck/lib/localJNLP.jar"/>'."\n".'<jar href="cck/lib/nanoxml-2.2.1.jar"/>'."\n".'<jar href="cck/lib/smoothmetal.jar"/>'."\n".'<jar href="cck/grabbag.jar"/>'."\n".'<jar href="cck/cck-help.jar" download="lazy"/>'."\n".'</resources>'."\n".'<application-desc main-class="edu.colorado.phet.cck3.CCK3Module"/>'."\n".'</jnlp>';

	function xml_get_parser_depth($parser) {
	    global $depth;
    
	    if (!isset($depth[$parser])) {
	        $depth[$parser] = 0;
	    }
    
	    return $depth[$parser];
	}

	function xml_output_tag_open($depth_level, $tag, $attrs) {
	   global $g_reparsed_xml;
	   global $g_xml_tag_open_outputted;
      
	   for ($i = 0; $i < $depth_level; $i++) {
	       $g_reparsed_xml = $g_reparsed_xml.BLANK_SPACES;
	   }
   
	   $g_reparsed_xml = $g_reparsed_xml."<$tag ";
   
	   foreach ($attrs as $key => $value) {
	       $g_reparsed_xml = $g_reparsed_xml.strtolower("$key").'="'.$value.'" ';
	   }
   
	   $g_reparsed_xml = $g_reparsed_xml.">";
   
	   $g_xml_tag_open_outputted = true;
	}

	function xml_output_tag_close($depth_level, $tag) {
	    global $g_reparsed_xml;
	    global $g_xml_tag_open_outputted;
    
	    if ($g_xml_tag_open_outputted) {
	        //$g_reparsed_xml = $g_reparsed_xml."\n";
        
	        for ($i = 0; $i < $depth_level; $i++) {
	            $g_reparsed_xml = $g_reparsed_xml.BLANK_SPACES;
	        }
    
	        $g_reparsed_xml = $g_reparsed_xml."</$tag>";
	    }
	}

	function xml_output_preprocessing($depth_level, $target, $data) {
	    global $g_reparsed_xml;
	    global $g_xml_tag_open_outputted;
    
	    if ($g_xml_tag_open_outputted) {
	        for ($i = 0; $i < $depth_level; $i++) {
	            $g_reparsed_xml = $g_reparsed_xml.BLANK_SPACES;
	        }
        
	        $g_reparsed_xml = $g_reparsed_xml."<?$target $data?>";
	    }
	}

	function xml_output_text($depth_level, $data) {
	    global $g_reparsed_xml;
	    global $g_xml_tag_open_outputted;
    
	    if ($g_xml_tag_open_outputted) {
	        $g_reparsed_xml = $g_reparsed_xml.$data;
	    }
	}

	function xml_reparse_start_element($parser, $name, $attrs) {
	    global $depth;
	    global $g_xml_tag_open;
	    global $g_xml_tag_open_outputted;
   
	    $tag = strtolower($name);
    
	    foreach ($attrs as $key => $value) {
	        unset($attrs[$key]);
        
	        $attrs[strtolower($key)] = $value;
	    }
    
	    $g_xml_tag_open_outputted = false;
   
	    call_user_func($g_xml_tag_open, xml_get_parser_depth($parser), $tag, $attrs);
   
	    $depth[$parser] = xml_get_parser_depth($parser) + 1;
	}

	function xml_reparse_end_element($parser, $name) {
	    global $depth;
	    global $g_xml_tag_close;
   
	    $depth[$parser] = xml_get_parser_depth($parser) - 1;
   
	    $tag = strtolower($name);
   
	    call_user_func($g_xml_tag_close, xml_get_parser_depth($parser), $tag);
	}

	function xml_reparse_preprocessing($parser, $target, $data) {
	    global $g_xml_preprocessing;
    
	    call_user_func($g_xml_preprocessing, xml_get_parser_depth($parser), $target, $data);
	}

	function xml_replace_encoded_symbol($symbol, $replacement, $data) {
	    return preg_replace('/'.SYMBOL_BOUNDARY_START.preg_quote($symbol).SYMBOL_BOUNDARY_END.'/', $replacement, $data);
	}

	function xml_reparse_character_data($parser, $data) {
	    global $g_xml_tag_contents;
    
	    $cleanup = array();
    
	    // Cleanup XML data:
    
	    // Have to replace ampersand first, since the replacements use it
	    $data = xml_replace_encoded_symbol('&', '&amp;', $data);
    
	    $cleanup['"'] = '&quot;';
	    $cleanup['<'] = '&lt;';
	    $cleanup['>'] = '&gt;';
    
	    foreach ($cleanup as $symbol => $replacement) {
	        $data = xml_replace_encoded_symbol($symbol, $replacement, $data);
	    }
    
	    call_user_func($g_xml_tag_contents, xml_get_parser_depth($parser), $data);
	}

	function xml_transform_file($file, $tag_open = "xml_output_tag_open", $tag_close = "xml_output_tag_close", $tag_contents = "xml_output_text", $preprocessing = "xml_output_preprocessing") {
	    global $g_reparsed_xml;
	    global $g_xml_tag_open;
	    global $g_xml_tag_close;
	    global $g_xml_tag_contents;
	    global $g_xml_preprocessing;
    
	    $g_reparsed_xml      = '<?xml version="1.0" encoding="UTF-8"?>'."\n";
	    $g_xml_tag_open      = $tag_open;
	    $g_xml_tag_close     = $tag_close;
	    $g_xml_tag_contents  = $tag_contents;
	    $g_xml_preprocessing = $preprocessing; 
    
	    $xml_parser = xml_parser_create();
    
	    xml_set_element_handler($xml_parser,        "xml_reparse_start_element", "xml_reparse_end_element");
	    xml_set_default_handler($xml_parser,        "xml_reparse_character_data");
	    xml_set_character_data_handler($xml_parser, "xml_reparse_character_data");
	    xml_set_processing_instruction_handler($xml_parser, "xml_reparse_preprocessing");
 
	    if (!($fp = fopen($file, "r"))) {
	        die("could not open XML input");
	    }
    
	    while ($data = fread($fp, 4096)) {
	        if (!xml_parse($xml_parser, $data, feof($fp))) {
	            die(sprintf("XML error: %s at line %d",
	                xml_error_string(xml_get_error_code($xml_parser)),
	                xml_get_current_line_number($xml_parser)));
	        }
	    }
    
	    xml_parser_free($xml_parser);
    
	    return $g_reparsed_xml;
	}

	//file_put_contents("temp.txt", $test_xml);

	//echo xml_transform_file("temp.txt");

	//xml_reparse_character_data("parser", "& & < < > > & & <!-- This is a comment -->");

	//echo $g_reparsed_xml;

	//var_dump($xml_object);

?>
