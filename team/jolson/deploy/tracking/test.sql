SELECT CONCAT(totals.sim_major_version, '.', totals.sim_minor_version) AS version, totals.val AS total, last_year.val AS last_year, last_month.val AS last_month, last_week.val AS last_week, last_day.val AS last_day
FROM (
	SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val
	FROM session, (
		SELECT id FROM sim_name WHERE name = 'pendulum-lab'
	) AS x
	WHERE (session.sim_name = x.id AND session.sim_dev = false)
	GROUP BY session.sim_major_version, sim_minor_version
) as totals,
(
	SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val
	FROM session, (
		SELECT id FROM sim_name WHERE name = 'pendulum-lab'
	) AS x
	WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND session.sim_dev = false)
	GROUP BY session.sim_major_version, sim_minor_version
) as last_year,
(
	SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val
	FROM session, (
		SELECT id FROM sim_name WHERE name = 'pendulum-lab'
	) AS x
	WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH) AND session.sim_dev = false)
	GROUP BY session.sim_major_version, sim_minor_version
) as last_month,
(
	SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val
	FROM session, (
		SELECT id FROM sim_name WHERE name = 'pendulum-lab'
	) AS x
	WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 WEEK) AND session.sim_dev = false)
	GROUP BY session.sim_major_version, sim_minor_version
) as last_week,
(
	SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val
	FROM session, (
		SELECT id FROM sim_name WHERE name = 'pendulum-lab'
	) AS x
	WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY) AND session.sim_dev = false)
	GROUP BY session.sim_major_version, sim_minor_version
) as last_day
WHERE (
	totals.sim_major_version = last_year.sim_major_version
	AND last_year.sim_major_version = last_month.sim_major_version
	AND last_month.sim_major_version = last_week.sim_major_version
	AND last_week.sim_major_version = last_day.sim_major_version
	AND totals.sim_minor_version = last_year.sim_minor_version
	AND last_year.sim_minor_version = last_month.sim_minor_version
	AND last_month.sim_minor_version = last_week.sim_minor_version
	AND last_week.sim_minor_version = last_day.sim_minor_version
)
ORDER BY totals.sim_major_version, totals.sim_minor_version
;

