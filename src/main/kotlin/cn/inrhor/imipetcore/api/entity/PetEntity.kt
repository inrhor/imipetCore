package cn.inrhor.imipetcore.api.entity

import cn.inrhor.imipetcore.api.manager.OptionManager.getActionOption
import cn.inrhor.imipetcore.api.manager.MetaManager.setMeta
import cn.inrhor.imipetcore.api.entity.ai.AttackAi
import cn.inrhor.imipetcore.api.entity.ai.HookAi
import cn.inrhor.imipetcore.api.entity.ai.WalkAi
import cn.inrhor.imipetcore.common.database.data.PetData
import cn.inrhor.imipetcore.common.option.StateOption
import cn.inrhor.imipetcore.common.script.kether.evalStrPetData
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.module.ai.*
import taboolib.platform.util.sendLang

/**
 * 宠物实体
 *
 * @param owner 主人
 * @param petData 宠物数据
 */
class PetEntity(val owner: Player, val petData: PetData) {

    /**
     * 实体
     */
    var entity: LivingEntity? = null

    /**
     * 模型
     */
    var modelEntity: ModeledEntity? = null

    /**
     * 释放宠物
     */
    fun spawn() {
        if (petData.isDead()) {
            owner.sendLang("PET-IS-DEAD")
            return
        }
        if (entity != null) return
        petData.following = true
        if (entity != null) return
        entity = owner.world.spawnEntity(owner.location, petData.petOption().entityType) as LivingEntity
        entity?.setMeta("entity", petData.name)
        entity?.setMeta("owner", owner.uniqueId)
        initAction()
        updateModel()
    }

    /**
     * 初始化行为
     */
    fun initAction() {
        entity?.clearGoalAi()
        entity?.clearTargetAi()
        petData.petOption().action.forEach {
            val id = it.id
            val pri = it.priority
            when (id) {
                "attack" -> {
                    entity?.addGoalAi(AttackAi(this@PetEntity), pri)
                }
                "walk" -> {
                    entity?.addGoalAi(WalkAi(this@PetEntity), pri)
                }
                else -> {
                    val actionOption = id.getActionOption()
                    if (actionOption != null) {
                        entity?.addGoalAi(HookAi(actionOption, this@PetEntity), pri)
                    }
                }
            }
        }
    }

    /**
     * 删除宠物
     */
    fun back() {
        petData.following = false
        if (petData.isDead()) return
        entity?.remove()
        entity = null
    }

    /**
     * 更新宠物模型
     */
    fun updateModel() {
        val name = owner.evalStrPetData(petData.petOption().default.displayName, petData)
        val modelID = petData.petOption().model.id
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null && petData.isFollow() && modelID.isNotEmpty()) {
            val me = ModelEngineAPI.api.modelManager
            val active = me.createActiveModel(modelID)
            if (modelEntity == null) modelEntity = me.createModeledEntity(entity)
            modelEntity?.addActiveModel(active)
            modelEntity?.detectPlayers()
            modelEntity?.isInvisible = true
            modelEntity?.nametagHandler?.setCustomName("name", name) // 标签 tag_name
            modelEntity?.nametagHandler?.setCustomNameVisibility("name", true)
        }else {
            entity?.customName = name
            entity?.isCustomNameVisible = true
        }
    }

    /**
     * @return 模型动作配置
     */
    fun getStateOption(): List<StateOption> {
        return petData.petOption().model.state
    }

    /**
     * @return 模型动作配置
     */
    fun getStateOption(action: String): StateOption? {
        getStateOption().forEach {
            if (it.id == action) return it
        }
        return null
    }

}