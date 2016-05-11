/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class ItemVacuumGun extends ItemChromaTool {

	public ItemVacuumGun(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {

		if (!world.isRemote) {
			EntityVacuum e = new EntityVacuum(world, ep);
			Vec3 vec = ep.getLookVec();
			e.setLocationAndAngles(ep.posX+vec.xCoord, ep.posY+vec.yCoord+1.5, ep.posZ+vec.zCoord, 0, 0);
			world.spawnEntityInWorld(e);

			ReikaSoundHelper.playSoundAtEntity(world, e, "random.fizz", 2, 0.7F);
		}

		return is;
	}

}
