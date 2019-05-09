package db

import com.github.mjdbc.BindBean
import com.github.mjdbc.DbMapper
import com.github.mjdbc.Sql
import java.sql.ResultSet

data class Version(val version: Int)

class VersionMapper: DbMapper<Version>{
    override fun map(r: ResultSet): Version {
        return Version(r.getInt("version"))
    }
}

interface VersionSql {
    @Sql("SELECT * FROM version LIMIT 1")
    fun getVersion(): Version

    @Sql("INSERT INTO version (version) VALUES (:version)")
    fun insert(@BindBean version: Version)

    @Sql("CREATE TABLE version (version int NOT NULL)")
    fun create()
}
