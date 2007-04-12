<?
    include_once("password-protection.php");
    ini_set('display_errors', '1');

	include_once("db.inc");

    print "<b>SIMULATIONS CURRENTLY IN THE DATABASE</b><br><br>";
    
    // start selecting SIMULATIONS from database table
    $sql  = "SELECT * FROM `simtest` ORDER BY `simname` ASC ";
    $sql_result= mysql_query($sql);

    while ($row = mysql_fetch_row($sql_result)) {
        $sim_id     = $row[0];
        $simname    = $row[1];
        $rating     = $row[2];
        $type       = $row[3];
        $size       = $row[4];
        $url_sim    = $row[5];
        $url_thumb  = $row[6];
        $desc       = $row[7];
        $keywords   = $row[8];
        $systemreq  = $row[9];

        print "$sim_id;$simname;$rating;$type;$size;$url_sim;$url_thumb;$desc;$keywords;$systemreq<br>(<a href=ctrl_sim_delete.php?simid=$sim_id&delete=0>DELETE?</a>)<br><br>";
    } 

    print "<br><br>";
?>
