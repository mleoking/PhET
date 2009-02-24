<?php
/************************************************************************************
* This is sample PHP script that proccesses an AJAX request and returns 
* a simple response to be processed in the Javascript of the calling page.
**************************************************************************************/

//create the header
header( 'Expires: Mon, 26 Jul 1997 05:00:00 GMT' );
header( 'Last-Modified: ' . gmdate( 'D, d M Y H:i:s' ) . ' GMT' );
header( 'Cache-Control: no-store, no-cache, must-revalidate' );
header( 'Cache-Control: post-check==0, pre-check==0', false );
header( 'Pragma: no-cache' );


/************************************************************************************
*
* In a real application, we might make a DB query to collect information, based
* on the value selected in the select box. 
*
* However, here we will simply chose what to return based on some hard-coded data. 
*
* In this example, we create an "array" as a string to send back to the page to be processed in
* javascript.  Since we cannot actually return a real 2D array, we fake it using delimeters.
*
* tdAutoSelectBox uses a | (pipe) to seperate the first dimension, and a semi-colon to
* seperate the name/value pair elements.  The tdAutoSelectBox will parse this into
* an array.  We could also send back XML for more advanced XML processing.
*
**************************************************************************************/

$AJAXResponse = "";

if(isset($_REQUEST['stateCode'])) {
    $StateCd = $_REQUEST['stateCode'];
} else {
    $StateCd = "";
}

if(isset($_REQUEST['cityCode'])) {
    $CityCd = $_REQUEST['cityCode'];
} else {
    $CityCd = "";
}

if ($StateCd != "") {
    if ($StateCd == "AZ") { 
        $AJAXResponse = "Flagstaff;FLG|Phoenix;PHX|Scottsdale;SCF|Tempe;TMP|Tucson;TUS";
    } else if ($StateCd == "CA") {
        $AJAXResponse = "Fresno;FCH|Los Angeles;LAX|Oakland;OAK|Pasadena;PAS|Sacramento;SAC|San Francisco;SFO";
    } else if ($StateCd == "CO") {
        $AJAXResponse = "Boulder;BOL|Colorado Springs;COS|Denver;DEN|Ft. Collins;FNL|Grand Junction;GJT|Trinidad;TAD";
    } else {
        $AJAXResponse = "nothing;0";
    }
} else {
    if ($CityCd == "FLG") { 
        $AJAXResponse = "86001|86002|86003|86004|86011";
    } else if ($CityCd == "PHX") {
        $AJAXResponse = "85015|85016|85038|85078|85079|85080|85082|85085|85098";
    } else if ($CityCd == "SCF") {
        $AJAXResponse = "85250|85251|85252|85253|85254|85255|85256|85257|85258|85259|85260|85262";
    } else if ($CityCd == "TMP") {
        $AJAXResponse = "85281|85282|85283|85284|85285|85287|85289";
    } else if ($CityCd == "TUS") {
        $AJAXResponse = "85701|85702|85703|85704|85705|85706|85707|85708|85709|85710|85711|85712|85713|85714|85715|85716|85717|85718|85719|85720|85721|85722|85723|85724|85725|85726";
    } else if ($CityCd == "FCH") {
        $AJAXResponse = "93650|93701|93702|93703|93704|93705|93706|93707|93708|93709|93710|93711|93712|93714|93715|93716|93717|93718|93720|93721|93722|93724|93725|93726|93727|93728|93729";
    } else if ($CityCd == "LAX") {
        $AJAXResponse = "90011|90012|90013|90014|90015|90016|90017|90018|90019|90020|90021|90022|90023|90024|90025|90026|90027|90028|90029|90030|90031|90032|90033|90034|90035|90036|90037|90038|90039|90040|90041|90042|90043";
    } else if ($CityCd == "OAK") {
        $AJAXResponse = "94601|94602|94603|94604|94605|94606|94607|94608|94609|94610|94611|94612|94613|94614|94615|94617|94618|94619|94620|94621|94622|94623|94624|94625|94627|94643|94649|94659|94660|94661|94662|94666";
    } else if ($CityCd == "PAS") {
        $AJAXResponse = "91101|91102|91103|91104|91105|91106|91107|91108|91109|91110|91114|91115|91116|91117|91118|91121|91123|91124|91125|91126|91129|91131|91175|91182|91184|91185|91186|91187|91188|91189|91191|91199";
    } else if ($CityCd == "SAC") {
        $AJAXResponse = "94203|94204|94205|94206|94207|94208|94209|94211|94229|94230|94232|94234|94235|94236|94237|94239|94240|94243|94244|94245|94246|94247|94248|94249|94250|94252|94253|94254|94256|94257|94258|94259|94261|94262|94263";
    } else if ($CityCd == "SFO") {
        $AJAXResponse = "94101|94102|94103|94104|94105|94106|94107|94108|94109|94110|94111|94112|94114|94115|94116|94117|94118|94119|94120|94121|94122|94123|94124|94125|94126|94127|94128|94129|94130|94131|94132|94133|94134";
    } else if ($CityCd == "BOL") {
        $AJAXResponse = "80301|80302|80303|80304|80305|80306|80307|80308|80309|80310|80314|80321|80322|80323|80328|80329";
    } else if ($CityCd == "COS") {
        $AJAXResponse = "80901|80903|80904|80905|80906|80907|80908|80909|80910|80911|80912|80913|80914|80915|80916|80917|80918|80919|80920|80921|80922|80925|80926|80928|80929|80930|80931|80932|80933|80934|80935|80936|80937|80940";
    } else if ($CityCd == "DEN") {
        $AJAXResponse = "80002|80012|80014|80022|80030|80031|80033|80123|80127|80201|80202|80203|80204|80205|80206|80207|80208|80209|80210|80211|80212|80214|80215|80216|80217|80218|80219|80220|80221|80222|80223|80224|80225";
    } else if ($CityCd == "FNL") {
        $AJAXResponse = "80521|80522|80523|80524|80525|80526|80527|80528|80553";
    } else if ($CityCd == "GJT") {
        $AJAXResponse = "81501|81502|81503|81504|81505|81506";
    } else if ($CityCd == "TAD") {
        $AJAXResponse = "81082";
    } else {
        $AJAXResponse = "nothing;0";
    }
}

echo $AJAXResponse;

?>
