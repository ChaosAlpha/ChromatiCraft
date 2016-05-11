/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class MinerRecipe extends PylonRecipe {

	public MinerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.resonanceDust, -2, 0);
		this.addAuxItem(ChromaStacks.resonanceDust, 2, 0);
		this.addAuxItem(ChromaStacks.resonanceDust, 0, -2);
		this.addAuxItem(ChromaStacks.voidCore, 0, 2);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);


		this.addAuxItem(ChromaStacks.chromaIngot, -4, -4);
		this.addAuxItem(ChromaStacks.chromaIngot, -4, 4);
		this.addAuxItem(ChromaStacks.chromaIngot, 4, -4);
		this.addAuxItem(ChromaStacks.chromaIngot, 4, 4);

		this.addAuxItem(ChromaStacks.enderIngot, -2, -2);
		this.addAuxItem(ChromaStacks.enderIngot, -2, 2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, -2);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 2);

		this.addAuraRequirement(CrystalElement.BROWN, 5000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.YELLOW, 15000);
	}

}
