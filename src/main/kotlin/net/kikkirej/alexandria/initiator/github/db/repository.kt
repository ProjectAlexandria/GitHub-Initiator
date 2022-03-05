package net.kikkirej.alexandria.initiator.github.db

import net.kikkirej.alexandria.initiator.github.db.Analysis
import net.kikkirej.alexandria.initiator.github.db.Project
import net.kikkirej.alexandria.initiator.github.db.Source
import net.kikkirej.alexandria.initiator.github.db.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SourceRepository : JpaRepository<Source, Long>

interface ProjectRepository : JpaRepository<Project, Long>{
    fun findByExternalIdentifierAndSource(externalIdentifier: String, source: Source): Optional<Project>
}

interface VersionRepository : JpaRepository<Version, Long> {
    fun findByProjectAndName(project: Project, name: String) : Optional<Version>
}

interface AnalysisRepository : JpaRepository<Analysis, Long>