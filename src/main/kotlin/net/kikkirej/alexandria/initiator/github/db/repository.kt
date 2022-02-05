package net.kikkirej.alexandria.initiator.github.db

import net.kikkirej.alexandria.initiator.github.db.Analysis
import net.kikkirej.alexandria.initiator.github.db.Project
import net.kikkirej.alexandria.initiator.github.db.Source
import net.kikkirej.alexandria.initiator.github.db.Version
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SourceRepository : CrudRepository<Source, Long>

interface ProjectRepository : CrudRepository<Project, Long>{
    fun findByExternalIdentifierAndSource(externalIdentifier: String, source: Source): Optional<Project>
}

interface VersionRepository : CrudRepository<Version, Long> {
    fun findByProjectAndName(project: Project, name: String) : Optional<Version>
}

interface AnalysisRepository : CrudRepository<Analysis, Long>