SELECT (@n_max := 3);

# probability that the user only ran a Flash OR Java sim NOT both, given they ran a standalone / external website sim not linked to others
SELECT (@alpha := 0.4);

# ratio of jeffco to shared cases (0 = full jeffco case, 1 = full shared case)
SELECT (@beta := 0.6);

# estimated number of users per "computer"
SELECT (@delta := 1.0);

# need a way to bound the first and/or last seen times!

SELECT @total_preferences_count := SUM(preferences_count) FROM entity;
SELECT @total_installation_count := SUM(installation_count) FROM entity;
SELECT @count_jeffco := (@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS jeffco_count FROM entity WHERE preferences_count > @n_max;
SELECT @count_linked := COUNT(*) AS count_linked FROM entity WHERE (preferences_count <= @n_max AND preferences_count > 1);
SELECT @count_unlinked := (COUNT(*) / (@alpha + 1.0)) AS count_unlinked FROM entity WHERE (preferences_count = 1);
SELECT @estimated_unique_users := ( (@count_jeffco + @count_linked + @count_unlinked) * @delta ) AS estimated_unique_users;
SELECT
	@estimated_unique_users AS estimated_unique_users,
	@total_installation_count AS unique_installations,
	@total_preferences_count AS unique_preferences_files;




SELECT first_seen, SUM(preferences_count) FROM entity GROUP BY first_seen;



# extract counts of higher than n-max for each month recorded!
SELECT
	months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
FROM
	((
		SELECT DISTINCT first_seen FROM entity
	) AS months)
	LEFT JOIN
	((
		SELECT
			first_seen,
			COUNT(*) AS COUNT
		FROM entity
		WHERE preferences_count > @n_max
		GROUP BY first_seen
	) AS data)
	ON (months.first_seen = data.first_seen)
;



# main template, add in unique user counts
SELECT
	prefs.first_seen, installations.count AS unique_installations, prefs.count AS unique_preferences_files
FROM
	(SELECT first_seen, SUM(preferences_count) as count FROM entity GROUP BY first_seen) AS prefs,
	(SELECT first_seen, SUM(installation_count) as count FROM entity GROUP BY first_seen) AS installations
WHERE (
	prefs.first_seen = installations.first_seen
)
GROUP BY prefs.first_seen
;


#first_seen      jeffco_count    linked_count    unlinked_count  estimated_unique_users
#2009-02-01 00:00:00     3.500000000000000000000000000000        4       60.0000 67.500000000000000000000000000000
#2009-03-01 00:00:00     0.000000000000000000000000000000        0       2.8571  2.857100000000000000000000000000
SELECT
	toss.first_seen, jeffco.count AS jeffco_count, linked.count AS linked_count, unlinked.count AS unlinked_count, ( (jeffco.count + linked.count + unlinked.count) * @delta ) AS estimated_unique_users
FROM
	(SELECT first_seen, COUNT(*) AS unused FROM entity GROUP BY first_seen) AS toss,
	(	SELECT
			months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
		FROM
			((
				SELECT DISTINCT first_seen FROM entity
			) AS months)
			LEFT JOIN
			((
				SELECT
					first_seen,
					(@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS count
				FROM entity
				WHERE (preferences_count > @n_max)
				GROUP BY first_seen
			) AS data)
			ON (months.first_seen = data.first_seen)
	) AS jeffco,
	(	SELECT
			months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
		FROM
			((
				SELECT DISTINCT first_seen FROM entity
			) AS months)
			LEFT JOIN
			((
				SELECT
					first_seen,
					COUNT(*) AS COUNT
				FROM entity
				WHERE (preferences_count <= @n_max AND preferences_count > 1)
				GROUP BY first_seen
			) AS data)
			ON (months.first_seen = data.first_seen)
	) AS linked,
	(	SELECT
			months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
		FROM
			((
				SELECT DISTINCT first_seen FROM entity
			) AS months)
			LEFT JOIN
			((
				SELECT
					first_seen,
					(COUNT(*) / (@alpha + 1.0)) AS count
				FROM entity
				WHERE (preferences_count = 1)
				GROUP BY first_seen
			) AS data)
			ON (months.first_seen = data.first_seen)
	) AS unlinked
WHERE (
	toss.first_seen = jeffco.first_seen
	AND toss.first_seen = linked.first_seen
	AND toss.first_seen = unlinked.first_seen
)
GROUP BY toss.first_seen
;





#first_seen      estimated_unique_users  unique_installations    unique_preferences_files
#2009-02-01 00:00:00     67.500000000000000000000000000000       43      104
#2009-03-01 00:00:00     2.857100000000000000000000000000        0       4
SELECT
	prefs.first_seen, uniq.estimated_unique_users AS estimated_unique_users, installations.count AS unique_installations, prefs.count AS unique_preferences_files
FROM
	(	SELECT
			first_seen,
			SUM(preferences_count) as count
		FROM entity
		GROUP BY first_seen
	) AS prefs,
	(	SELECT
			first_seen,
			SUM(installation_count) as count
		FROM entity 
		GROUP BY first_seen
	) AS installations,
	(	SELECT
			toss.first_seen, jeffco.count AS jeffco_count, linked.count AS linked_count, unlinked.count AS unlinked_count, ( (jeffco.count + linked.count + unlinked.count) * @delta ) AS estimated_unique_users
		FROM
			(SELECT first_seen, COUNT(*) AS unused FROM entity GROUP BY first_seen) AS toss,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							(@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS count
						FROM entity
						WHERE (preferences_count > @n_max)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS jeffco,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							COUNT(*) AS COUNT
						FROM entity
						WHERE (preferences_count <= @n_max AND preferences_count > 1)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS linked,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							(COUNT(*) / (@alpha + 1.0)) AS count
						FROM entity
						WHERE (preferences_count = 1)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS unlinked
		WHERE (
			toss.first_seen = jeffco.first_seen
			AND toss.first_seen = linked.first_seen
			AND toss.first_seen = unlinked.first_seen
		)
		GROUP BY toss.first_seen
	) AS uniq
WHERE (
	prefs.first_seen = installations.first_seen
	AND prefs.first_seen = uniq.first_seen
)
GROUP BY prefs.first_seen
;





# full query
SELECT
	YEAR(prefs.first_seen) AS year, MONTH(prefs.first_seen) AS month, uniq.estimated_unique_users AS estimated_unique_users, installations.count AS unique_installations, prefs.count AS unique_preferences_files
FROM
	(	SELECT
			first_seen,
			SUM(preferences_count) as count
		FROM entity
		GROUP BY first_seen
	) AS prefs,
	(	SELECT
			first_seen,
			SUM(installation_count) as count
		FROM entity 
		GROUP BY first_seen
	) AS installations,
	(	SELECT
			toss.first_seen, jeffco.count AS jeffco_count, linked.count AS linked_count, unlinked.count AS unlinked_count, ( (jeffco.count + linked.count + unlinked.count) * @delta ) AS estimated_unique_users
		FROM
			(SELECT first_seen, COUNT(*) AS unused FROM entity GROUP BY first_seen) AS toss,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							(@beta * 0.5 * SUM(preferences_count) + (1.0 - @beta) * COUNT(*)) AS count
						FROM entity
						WHERE (preferences_count > @n_max)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS jeffco,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							COUNT(*) AS COUNT
						FROM entity
						WHERE (preferences_count <= @n_max AND preferences_count > 1)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS linked,
			(	SELECT
					months.first_seen, IF(data.count IS NULL, 0, data.count) AS count
				FROM
					((
						SELECT DISTINCT first_seen FROM entity
					) AS months)
					LEFT JOIN
					((
						SELECT
							first_seen,
							(COUNT(*) / (@alpha + 1.0)) AS count
						FROM entity
						WHERE (preferences_count = 1)
						GROUP BY first_seen
					) AS data)
					ON (months.first_seen = data.first_seen)
			) AS unlinked
		WHERE (
			toss.first_seen = jeffco.first_seen
			AND toss.first_seen = linked.first_seen
			AND toss.first_seen = unlinked.first_seen
		)
		GROUP BY toss.first_seen
	) AS uniq
WHERE (
	prefs.first_seen = installations.first_seen
	AND prefs.first_seen = uniq.first_seen
)
GROUP BY prefs.first_seen
ORDER BY prefs.first_seen DESC
;


