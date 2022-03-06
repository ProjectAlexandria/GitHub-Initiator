DROP VIEW IF EXISTS view_project_analyzers CASCADE;

CREATE VIEW view_project_analyzers AS
SELECT
    p.external_identifier, s.name as source,
    (SELECT count(mmv.id)>0 FROM version mv
                                     left join analysis a on mv.id = a.version_id
                                     left join maven_module mmv on a.id = mmv.analysis_id
     where mv.id = v.id) as maven,
    (SELECT count(np.id)>0 FROM version mv
                                    left join analysis a on mv.id = a.version_id
                                    left join npm_project np on a.id = np.analysis_id
     where mv.id = v.id) as node,
    (SELECT count(df.id)>0 FROM version mv
                                    left join analysis a on mv.id = a.version_id
                                    left join docker_file df on a.id = df.analysis_id
     where mv.id = v.id) as dockerfile
FROM project p
         right join version v on p.id = v.project_id
         right outer join source s on s.id = p.source_id
where v.default_version = true;

DROP VIEW IF EXISTS view_project_analyzers_stats;

CREATE VIEW view_project_analyzers_stats AS
SELECT
    unnest(ARRAY ['all', 'maven', 'node', 'dockerfile']) AS "Projects",
    unnest(ARRAY [
        ( SELECT count(v.*) AS count FROM view_project_analyzers v),
        ( SELECT count(v.*) AS count FROM view_project_analyzers v WHERE v.maven = true),
        ( SELECT count(v.*) AS count FROM view_project_analyzers v WHERE v.node = true),
        ( SELECT count(v.*) AS count FROM view_project_analyzers v WHERE v.dockerfile = true)
        ]) as count;