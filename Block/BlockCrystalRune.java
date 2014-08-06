/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.EntityRuneFX;
import Reika.ChromatiCraft.Base.BlockDyeTypes;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalRune extends BlockDyeTypes {

	public BlockCrystalRune(int par1, Material par2Material) {
		super(par1, par2Material);
		blockHardness = 2;
		blockResistance = 5;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		super.randomDisplayTick(world, x, y, z, r);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 8);
		//this.runeParticles(world, x, y, z, meta, r);
	}

	@SideOnly(Side.CLIENT)
	private void runeParticles(World world, int x, int y, int z, int meta, Random rand) {
		double r = 0.75;
		double dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		double dy = rand.nextDouble();
		double dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		while (ReikaMathLibrary.py3d(dx, 0, dz) < 0.65) {
			dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		}

		CrystalElement e = CrystalElement.elements[meta];
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, x+dx+0.5, y+dy+0.5, z+dz+0.5, 0, 0, 0, e));
	}

	@Override
	public String getIconFolder() {
		return "runes/real/";
	}

	@Override
	public boolean useNamedIcons() {
		return false;
	}

}