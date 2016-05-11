/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityCrystalPlant extends TileEntity {

	private final Random random = new Random();

	private int growthTick = 2;
	private long lastShardTick = -1;

	public boolean renderPod() {
		return growthTick <= 1;
	}

	public boolean emitsLight() {
		return growthTick == 0;
	}

	public void grow() {
		if (growthTick > 0) {
			growthTick--;
			for (int i = 2; i < 6; i++) {
				if (ReikaRandomHelper.doWithChance(25)) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = xCoord+dir.offsetX;
					int dy = yCoord+dir.offsetY;
					int dz = zCoord+dir.offsetZ;
					Block id = worldObj.getBlock(dx, dy, dz);
					int meta = worldObj.getBlockMetadata(dx, dy, dz);
					if (id == ChromaBlocks.PLANT.getBlockInstance() && meta == this.getColor().ordinal()) {
						TileEntityCrystalPlant te = (TileEntityCrystalPlant)worldObj.getTileEntity(dx, dy, dz);
						te.grow();
					}
				}
			}
		}
		this.updateLight();
	}

	public void makeRipe() {
		this.grow();
		this.grow();
	}

	public void updateLight() {
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void harvest(boolean drops) {
		growthTick = 2;
		if (drops) {
			int rand = random.nextInt(20);
			int num = 0;
			if (rand == 0) {
				num = 2;
			}
			else if (rand < 5) {
				num = 1;
			}
			int meta = this.getColor().ordinal();
			for (int i = 0; i < num; i++)
				ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ChromaItems.SEED.getStackOfMetadata(meta+16));
			long time = worldObj.getTotalWorldTime();
			if (ChromaOptions.CRYSTALFARM.getState() && time-lastShardTick >= 600 && ReikaRandomHelper.doWithChance(2)) {
				ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ChromaItems.SHARD.getStackOfMetadata(meta));
				lastShardTick = time;
			}
		}
		this.updateLight();
	}

	public boolean canHarvest() {
		return growthTick == 0;
	}

	public CrystalElement getColor() {
		return CrystalElement.elements[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.field_148860_e);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		growthTick = NBT.getInteger("growth");
		lastShardTick = NBT.getLong("shard");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("growth", growthTick);
		NBT.setLong("shard", lastShardTick);
	}

	public int getGrowthState() {
		return growthTick;
	}

}
