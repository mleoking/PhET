<?
    include_once("password-protection.php");
	include_once("db.inc");

    print "<b>SIMULATIONS CURRENTLY IN THE DATABASE</b><br><br>";
    
    // start selecting SIMULATIONS from database table
    $sql  = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
    $sql_result= mysql_query($sql);

    while ($row = mysql_fetch_row($sql_result)) {
        $sim_id     = $row[0];
        $sim_name    = $row[1];
        $rating     = $row[2];
        $type       = $row[3];
        $size       = $row[4];
        $sim_launch_url    = $row[5];
        $sim_image_url  = $row[6];
        $sim_desc       = $row[7];
        $keywords   = $row[8];
        $sim_system_req  = $row[9];

        print "$sim_id;$sim_name;$rating;$type;$size;$sim_launch_url;$sim_image_url;$sim_desc;$keywords;$sim_system_req<br>(<a href=ctrl_sim_delete.php?sim_id=$sim_id&delete=0>DELETE?</a>)<br><br>";
    } 

    print "<br><br>";
?>
