/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TieredOreCap extends WandCap {

	private static final HashMap<TieredOres, WandType> items = new HashMap();

	public static void registerAll() {
		for (TieredOres t : items.keySet()) {
			WandCap cap = new TieredOreCap(t);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				registerTexture(t, cap);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerTexture(TieredOres t, WandCap cap) {
		cap.setTexture(DirectResourceManager.getResource("Reika/ChromatiCraft/Textures/Wands/cap_"+t.name().toLowerCase(Locale.ENGLISH)+".png"));
	}

	private TieredOreCap(TieredOres t) {
		super("TIEREDCAP_"+t.name(), 0.8F, items.get(t).aspects, 0.5F, items.get(t).item, 10);
	}

	private static class WandType {

		private final ArrayList<Aspect> aspects = new ArrayList();
		private final ItemStack item;
		private final ItemStack raw;

		private WandType(ItemStack is, ItemStack raw, Aspect... as) {
			item = is;
			this.raw = raw;
			for (Aspect a : as)
				this.addAspect(a);
		}

		private WandType addAspect(Aspect a) {
			aspects.add(a);
			return this;
		}

	}

	static {
		items.put(TieredOres.FIRAXITE, new WandType(ChromaStacks.firaxiteCap, ChromaStacks.fieryIngot, Aspect.FIRE, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
		items.put(TieredOres.WATERY, new WandType(ChromaStacks.waterCap, ChromaStacks.waterIngot, Aspect.WATER, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
		items.put(TieredOres.RESO, new WandType(ChromaStacks.endCap, ChromaStacks.enderIngot, Aspect.ENTROPY, Aspect.ELDRITCH, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
		items.put(TieredOres.FOCAL, new WandType(ChromaStacks.chromaCap, ChromaStacks.complexIngot, Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY));
	}

	public static void addRecipes() {
		int i = -2;
		String ref = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? ChromaDescriptions.getParentPage()+"thaum.xml" : "";
		for (TieredOres t : items.keySet()) {
			WandType wt = items.get(t);
			AspectList al = new AspectList();
			int n = t == TieredOres.FOCAL ? 2 : 1;
			al.add(Aspect.ORDER, 20*n);
			Aspect a = wt.aspects.get(0);
			al.add(a, 50*n);
			Object[] recipe = {
					"AAA", "A A", 'A', wt.raw
			};
			ShapedArcaneRecipe ir = ThaumcraftApi.addArcaneCraftingRecipe("", wt.item, al, recipe);
			String id = "CAP_TIEREDCAP_"+t.name();
			String desc = "Novel caps";

			MathExpression cost = new MathExpression() {
				@Override
				public double evaluate(double arg) throws ArithmeticException {
					return arg/5D;
				}

				@Override
				public double getBaseValue() {
					return 0;
				}

				@Override
				public String toString() {
					return "/5";
				}
			};

			ReikaThaumHelper.addArcaneRecipeBookEntryViaXML(id, desc, "chromaticraft", ir, cost, 2, i, ChromatiCraft.class, ref);
			i++;
		}
	}

}
