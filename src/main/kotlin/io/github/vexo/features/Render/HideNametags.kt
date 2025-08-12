package io.github.vexo.features.Render


import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.*
import net.minecraft.init.Items
import net.minecraft.network.play.server.S0EPacketSpawnObject
import net.minecraft.network.play.server.S1CPacketEntityMetadata
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.tileentity.TileEntityEndPortal
import net.minecraft.tileentity.TileEntitySign
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent