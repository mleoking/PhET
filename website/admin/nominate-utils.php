<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/db-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");

	function is_contribution_nominated_by($contribution_id, $contributor_id) {
		$row = db_get_row_by_condition(
			'contribution_nomination',
			array(
				'contribution_id' => $contribution_id,
				'contributor_id'  => $contributor_id
			)
		);
		
		if (!$row) return false;
		
		return true;
	}
	
	function get_all_nominations_for_contribution($contribution_id) {
		return db_get_rows_by_condition('contribution_nomination', array('contribution_id' => $contribution_id));
	}
	
	function get_nomination_count_for_contribution($contribution_id) {
		return count(get_all_nominations_for_contribution($contribution_id));
	}

	function nominate_contribution($contribution_id) {
		global $contributor_id;
		
		if (is_contribution_nominated_by($contribution_id, $contributor_id)) {
			return true;
		}
		
		return db_insert_row(
			'contribution_nomination',
			array(
				'contribution_id' => $contribution_id,
				'contributor_id'  => $contributor_id
			)
		);
	}
	
	function get_nomination_statistics() {
		static $contrib_to_nom_count = null;
		
		if ($contrib_to_nom_count != null) {
			return $contrib_to_nom_count;
		}
		
		$nominations = db_get_all_rows('contribution_nomination');
		
		$contrib_to_nom_count = array();
		
		foreach ($nominations as $nomination) {
			$contribution_id = $nomination['contribution_id'];
			
			if (!isset($contrib_to_nom_count["$contribution_id"])) {
				$contrib_to_nom_count["$contribution_id"] = 0;
			}
			
			$contrib_to_nom_count["$contribution_id"] = $contrib_to_nom_count["$contribution_id"] + 1;
		}
		
		asort($contrib_to_nom_count);
		
		return $contrib_to_nom_count;
	}
	
	function is_gold_star_contribution($contribution_id) {
		$stats = get_nomination_statistics();
		$count = get_nomination_count_for_contribution($contribution_id);
		
		if (count($stats) > 10) {
			$cutoff_index = (int)floor(count($stats) * 0.33);
			$cutoff       = $stats[$cutoff_index];
			
			if ($count >= $cutoff) {
				return true;
			}
		}
		
		return false;
	}
	
	function get_gold_star_html($image_width = 37) {
		return "<img src=\"../images/gold-star.jpg\" width=\"$image_width\" alt=\"Image of Gold Star\" title=\"Gold Star Contribution: This contribution has received a Gold Star for its quality and usefulness to many teachers.\" />";
	}
	
	function get_gold_star_html_for_contribution($contribution_id, $image_width = 37) {
		if (is_gold_star_contribution($contribution_id)) {
			return get_gold_star_html($image_width);
		}
		else {
			return "";
		}
	}
?>