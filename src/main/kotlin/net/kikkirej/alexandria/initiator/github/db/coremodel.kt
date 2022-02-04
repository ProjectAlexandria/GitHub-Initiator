package net.kikkirej.alexandria.initiator.github.db

import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "source")
class Source(@Id var id: Long,
             var name: String,
             var type:String = "Filesystem")

@Entity(name = "project")
class Project(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long,
              @ManyToOne var source: Source,
              var url:String?,
              @Column(name = "external_identifier") var externalIdentifier: String,
              @OneToMany(cascade = [CascadeType.ALL], mappedBy = "project") var metadata: Set<ProjectMetadata>)

@Entity(name = "project_metadata")
class ProjectMetadata(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long,
                      var key:String,
                      var type:String,
                      var value:String,
                      @ManyToOne var project: Project
)

@Entity(name = "version")
class Version(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long,
              var default_version: Boolean = true, //in case of Filesystem there is only one "default"-version
              var name:String,
              @ManyToOne var project: Project,
              @ManyToOne var latest_analysis: Analysis? = null,
              @OneToMany(cascade = [CascadeType.ALL], mappedBy = "version") var metadata: Set<VersionMetadata>)

@Entity(name = "version_metadata")
class VersionMetadata(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long,
                      var key:String,
                      var type:String,
                      var value:String?,
                      @ManyToOne var version: Version
)

@Entity(name = "analysis")
class Analysis(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long,
               @ManyToOne var version: Version,
               var creationTime: Timestamp)