package io.github.vexo.utils.skyblock

import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB

fun Entity.removeHitbox() {
    this.entityBoundingBox = AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)
}