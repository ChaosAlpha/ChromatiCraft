/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.world.World;

public interface SidedBlock {

	boolean canPlaceOn(World world, int x, int y, int z, int side);

	void setSide(World world, int x, int y, int z, int side);



}