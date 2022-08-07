package cn.inrhor.imipetcore.common.database.type

import cn.inrhor.imipetcore.common.database.Database
import cn.inrhor.imipetcore.common.database.data.PetData
import java.util.*

/**
 * 实现数据MySQL
 */
class DatabaseSQL: Database() {
    override fun deletePet(uuid: UUID, name: String) {

    }

    override fun updatePet(uuid: UUID, petData: PetData) {

    }

    override fun pull(uuid: UUID) {

    }

    override fun renamePet(uuid: UUID, oldName: String, petData: PetData) {

    }
}