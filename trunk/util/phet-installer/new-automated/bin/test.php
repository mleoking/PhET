<?php

	$g_reparsed_xml = "";

	$test_xml = '<?xml version="1.0" encoding="UTF-8"?><jnlp spec="1.0+" codebase="http://phet-web.colorado.edu/simulations" href="cck/cck.jnlp"><information><title>Circuit Construction Kit</title><vendor>PhET(tm)</vendor><homepage href="http://phet-web.colorado.edu"/><description>The Circuit Construction Kit</description><description kind="short">The Circuit Construction Kit.</description><icon href="../Design/Assets/images/Phet-Kavli-logo.jpg"/> <icon kind="splash" href="../Design/Assets/images/Phet-Kavli-logo.jpg"/> <offline-allowed/></information><security></security>    <resources><j2se version="1.4+"/><jar href="cck/cck.jar"/><jar href="cck/lib/Jama-1.0.1.jar"/><jar href="cck/lib/jnlp.jar"/><jar href="cck/lib/localJNLP.jar"/><jar href="cck/lib/nanoxml-2.2.1.jar"/><jar href="cck/lib/smoothmetal.jar"/><jar href="cck/grabbag.jar"/><jar href="cck/cck-help.jar" download="lazy"/></resources>    <application-desc main-class="edu.colorado.phet.cck3.CCK3Module"/></jnlp>';

	function xml_output_tag_open($depth_level, $tag, $attrs) {
	   global $g_reparsed_xml;
      
	   for ($i = 0; $i < $depth_level; $i++) {
	       $g_reparsed_xml = $g_reparsed_xml."  ";
	   }
   
	   $g_reparsed_xml = $g_reparsed_xml."<$tag ";
   
	   foreach ($attrs as $key => $value) {
	       $g_reparsed_xml = $g_reparsed_xml.strtolower("$key").'="'.$value.'" ';
	   }
   
	   $g_reparsed_xml = $g_reparsed_xml.">\n";
	}

	function xml_output_tag_close($depth_level, $tag) {
	    global $g_reparsed_xml;
    
	    for ($i = 0; $i < $depth_level; $i++) {
	        $g_reparsed_xml = $g_reparsed_xml."  ";
	    }

	    $g_reparsed_xml = $g_reparsed_xml."</$tag>\n";
	}

	function xml_start_element($parser, $name, $attrs) {
	    global $depth;
	    global $g_tag_open;
   
	    if (!isset($depth[$parser])) {
	        $depth[$parser] = 0;
	    }
   
	    $tag = strtolower($name);
   
	    call_user_func($g_tag_open, $depth[$parser], $tag, $attrs);
   
	    $depth[$parser]++;
	}

	function xml_end_element($parser, $name) {
	    global $depth;   
	    global $g_tag_close;
   
	    $depth[$parser]--;
   
	    $tag = strtolower($name);
   
	    call_user_func($g_tag_close, $depth[$parser], $tag);
	}

	function xml_transform_file($file, $tag_open = "xml_output_tag_open", $tag_close = "xml_output_tag_close") {
	    global $g_reparsed_xml;
	    global $g_tag_open;
	    global $g_tag_close;
    
	    $g_reparsed_xml = "";
	    $g_tag_open     = $tag_open;
	    $g_tag_close    = $tag_close;
    
	    $xml_parser = xml_parser_create();
    
	    xml_set_element_handler($xml_parser, "xml_start_element", "xml_end_element");
 
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

	file_put_contents("temp.txt", $test_xml);

	echo xml_transform_file("temp.txt");

	//var_dump($xml_object);

?>
