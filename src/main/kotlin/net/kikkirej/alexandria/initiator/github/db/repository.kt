package net.kikkirej.alexandria.initiator.github.db

import net.kikkirej.alexandria.initiator.github.db.Analysis
import net.kikkirej.alexandria.initiator.github.db.Project
import net.kikkirej.alexandria.initiator.github.db.Source
import net.kikkirej.alexandria.initiator.github.db.Version
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SourceRepository : CrudRepository<Source, Long>

interface ProjectRepository : CrudRepository<Project, Long>{
    fun findBySourceAndExternalIdentifier(source: Source, identifier: String): Optional<Project>
}

interface VersionRepository : CrudRepository<Version, Long> {
    fun findByProject(project: Project) : Optional<Version>
}

interface AnalysisRepository : CrudRepository<Analysis, Long>