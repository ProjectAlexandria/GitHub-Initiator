package net.kikkirej.alexandria.initiator.github.db

import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "source")
class Source(@Id var id: Long,
             var name: String,
             var type: String = "GitHub"){
    override fun toString(): String {
        return "Source(id=$id, name='$name', type='$type')"
    }
}

@Entity(name = "project")
class Project(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0,
              @ManyToOne var source: Source,
              var url:String?,
              @Column(name = "external_identifier") var externalIdentifier: String,
              @OneToMany(cascade = [CascadeType.ALL], mappedBy = "project") var metadata: MutableSet<ProjectMetadata> = mutableSetOf()
)

@Entity(name = "project_metadata")
class ProjectMetadata(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0,
                      var key:String,
                      var type:String,
                      var value:String,
                      @ManyToOne var project: Project,
)

@Entity(name = "version")
class Version(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0,
              var default_version: Boolean = false, //in case of Filesystem there is only one "default"-version
              var name:String,
              @ManyToOne var project: Project,
              @ManyToOne var latest_analysis: Analysis? = null,
              @OneToMany(cascade = [CascadeType.ALL], mappedBy = "version") var metadata: MutableSet<VersionMetadata> = mutableSetOf()
)

@Entity(name = "version_metadata")
class VersionMetadata(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0,
                      var key:String,
                      var type:String,
                      var value:String?,
                      @ManyToOne var version: Version
)

@Entity(name = "analysis")
class Analysis(@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0,
               @ManyToOne var version: Version,
               var creationTime: Timestamp = Timestamp(System.currentTimeMillis())
)