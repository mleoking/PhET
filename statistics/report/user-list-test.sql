SELECT @n_max := 10;
SELECT @alpha := 0.4;
SELECT @beta := 0.6;
SELECT @delta := 1.0;

SELECT
	prefs.user_total_sessions as sessions, CEIL(uniq.estimated_unique_users) AS estimated_unique_users, installations.count AS unique_installations, prefs.count AS unique_preferences_files
FROM
	(	SELECT
			user_total_sessions,
			SUM(preferences_count) as count
		FROM entity
		GROUP BY user_total_sessions
	) AS prefs,
	(	SELECT
			user_total_sessions,
			SUM(installation_count) as count
		FROM entity
		GROUP BY user_total_sessions
	) AS installations,
	(	SELECT
			toss.user_total_sessions, jeffco.count AS jeffco_count, linked.count AS linked_count, unlinked.count AS unlinked_count, ( (jeffco.count + linked.count + unlinked.count) * @delta ) AS estimated_unique_users
		FROM
			(SELECT user_total_sessions, COUNT(*) AS unused FROM entity GROUP BY user_total_sessions) AS toss,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							(@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS count
						FROM entity
						WHERE (preferences_count > @n_max)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS jeffco,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							COUNT(*) AS COUNT
						FROM entity
						WHERE (preferences_count <= @n_max AND preferences_count > 1)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS linked,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							(COUNT(*) / (@alpha + 1.0)) AS count
						FROM entity
						WHERE (preferences_count = 1)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS unlinked
		WHERE (
			toss.user_total_sessions = jeffco.user_total_sessions
			AND toss.user_total_sessions = linked.user_total_sessions
			AND toss.user_total_sessions = unlinked.user_total_sessions
		)
		GROUP BY toss.user_total_sessions
	) AS uniq
WHERE (
	prefs.user_total_sessions = installations.user_total_sessions
	AND prefs.user_total_sessions = uniq.user_total_sessions
)
GROUP BY prefs.user_total_sessions
ORDER BY prefs.user_total_sessions DESC
;





SELECT
	prefs.user_total_sessions as sessions, CEIL(uniq.estimated_unique_users) AS estimated_unique_users, installations.count AS unique_installations, prefs.count AS unique_preferences_files
FROM
	(	SELECT
			user_total_sessions,
			SUM(preferences_count) as count
		FROM entity
		GROUP BY user_total_sessions
	) AS prefs,
	(	SELECT
			user_total_sessions,
			SUM(installation_count) as count
		FROM entity
		GROUP BY user_total_sessions
	) AS installations,
	(	SELECT
			toss.user_total_sessions, jeffco.count AS jeffco_count, linked.count AS linked_count, unlinked.count AS unlinked_count, ( (jeffco.count + linked.count + unlinked.count) * @delta ) AS estimated_unique_users
		FROM
			(SELECT user_total_sessions, COUNT(*) AS unused FROM entity GROUP BY user_total_sessions) AS toss,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							(@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS count
						FROM entity
						WHERE (preferences_count > @n_max)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS jeffco,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							COUNT(*) AS COUNT
						FROM entity
						WHERE (preferences_count <= @n_max AND preferences_count > 1)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS linked,
			(	SELECT
					months.user_total_sessions, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT user_total_sessions FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							user_total_sessions,
							(COUNT(*) / (@alpha + 1.0)) AS count
						FROM entity
						WHERE (preferences_count = 1)
						GROUP BY user_total_sessions
					) AS data)
					ON (months.user_total_sessions = data.user_total_sessions)
			) AS unlinked
		WHERE (
			toss.user_total_sessions = jeffco.user_total_sessions
			AND toss.user_total_sessions = linked.user_total_sessions
			AND toss.user_total_sessions = unlinked.user_total_sessions
		)
		GROUP BY toss.user_total_sessions
	) AS uniq
WHERE (
	prefs.user_total_sessions = installations.user_total_sessions
	AND prefs.user_total_sessions = uniq.user_total_sessions
)
GROUP BY prefs.user_total_sessions
ORDER BY prefs.user_total_sessions DESC
;