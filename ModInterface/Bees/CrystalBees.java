/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Effect;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flower;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.BasicFlowerProvider;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.TraitsBee;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler.Combs;
import Reika.DragonAPI.ModInteract.ItemHandlers.OreBerryBushHandler.BerryTypes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;

public class CrystalBees {

	private static final String LOGGER_TAG = "CrystalBees";

	private static final Random rand = new Random();

	protected static BasicBee protective;
	protected static BasicBee luminous;
	protected static BasicBee hostile;

	protected static BasicBee crystal;
	protected static BasicBee purity;

	protected static AdvancedBee chroma;
	protected static AdvancedBee lumen;
	protected static AdvancedBee aura;
	protected static AdvancedBee multi;

	protected static Fertility superFertility;
	protected static Territory superTerritory;
	protected static Speeds superSpeed;
	protected static Flowering superFlowering;
	protected static Life superLife;
	protected static Life blinkLife;
	//protected static IAlleleTolerance anyTemperature; green
	//protected static IAlleleTolerance anyHumidity; gray

	private static MultiAllele multiFlower;

	private static ColorBlendList chromaColor;
	private static ColorBlendList auraColor;
	private static ColorBlendList lumenColor;
	private static ColorBlendList[] crystalColors;
	private static ColorBlendList multiColor;

	private static final IErrorState conditionalsUnavailable = new IErrorState() {

		private IIcon icon;

		@Override
		public short getID() {
			return 1600;
		}

		@Override
		public String getUniqueName() {
			return "ChromatiCraft:noconditionals";
		}

		@Override
		public String getDescription() {
			return "Conditionals Unavailable";
		}

		@Override
		public String getHelp() {
			return "Specialized products are unavailable due to their conditions not being met.";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister register) {
			icon = register.registerIcon("chromaticraft:forestry-no-conditionals");
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon() {
			return icon;
		}

	};

	protected static final EnumMap<CrystalElement, CrystalBee> beeMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, CrystalEffect> effectMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, CrystalAllele> flowerMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, ItemHashMap<ProductCondition>> productConditions = new EnumMap(CrystalElement.class);

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_TAG);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			loadColorData();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void loadColorData() {
		chromaColor = new ColorBlendList(5F, ChromaFX.getChromaColorTiles());
		auraColor = new ColorBlendList(18F, 0xffff00, 0xffffff, 0x000000, 0x8000ff, 0xff0000);
		lumenColor = new ColorBlendList(10F, 0x0000ff, 0xffffff, 0x22aaff);
		crystalColors = new ColorBlendList[16];
		multiColor = new ColorBlendList(20F);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int c = e.getColor();
			int c1 = ReikaColorAPI.mixColors(c, 0x000000, 0.5F);
			int c2 = ReikaColorAPI.mixColors(c, 0xffffff, 0.5F);
			crystalColors[i] = new ColorBlendList(40F, c, c, c, c1, c, c, c, c2);
			multiColor.addColor(c);
		}
	}

	public static void register() {
		superFertility = Fertility.createNew("multiply", 8, false);
		superSpeed = Speeds.createNew("accelerated", 4F, false);
		superFlowering = Flowering.createNew("naturalistic", 240, false);
		superTerritory = Territory.createNew("exploratory", 32, 16, false);
		superLife = Life.createNew("eon", 600, false);
		blinkLife = Life.createNew("blink", 2, false);
		//anyTemperature = Tolerance.createNew("", new OmniToleranceCheck(), false);
		//anyHumidity = Tolerance.createNew("", new OmniToleranceCheck(), false);

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			BeeTraits traits = CrystalBeeTypes.list[i].getTraits();
			CrystalBee bee = new CrystalBee(color, traits);
			CrystalEffect eff = new CrystalEffect(color);
			CrystalAllele flw = new CrystalAllele(color);
			effectMap.put(color, eff);
			flowerMap.put(color, flw);
			bee.register();
			beeMap.put(color, bee);
		}

		multiFlower = new MultiAllele();

		ForestryAPI.errorStateRegistry.registerErrorState(conditionalsUnavailable);

		protective = new BasicBee("Protective", "Vitreus Auxilium", Speeds.SLOWER, Life.ELONGATED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, 0xFF5993);
		luminous = new BasicBee("Luminous", "Vitreus Lumens", Speeds.SLOW, Life.SHORTER, Flowering.SLOWER, Fertility.HIGH, Territory.DEFAULT, 0xBAEBFF);
		hostile = new BasicBee("Hostile", "Vitreus Inimicus", Speeds.SLOWEST, Life.SHORT, Flowering.SLOW, Fertility.LOW, Territory.DEFAULT, 0xFF6A00);

		crystal = new BasicBee("Crystalline", "Vitreus Crystallum", Speeds.NORMAL, Life.SHORTEST, Flowering.SLOWEST, Fertility.LOW, Territory.DEFAULT, 0x46A7FF);
		purity = new BasicBee("Pure", "Purus Mundi", Speeds.SLOWER, Life.NORMAL, Flowering.AVERAGE, Fertility.NORMAL, Territory.DEFAULT, 0xffffff);
		//crystal.setCave();
		//purity.setCave();

		protective.register();
		luminous.register();
		hostile.register();
		crystal.register();
		purity.register();

		chroma = new AdvancedBee("Iridescent", "Auram Stellans", Speeds.SLOWER, Life.NORMAL, Flowering.SLOWEST, Fertility.NORMAL, Territory.DEFAULT, chromaColor, EnumTemperature.COLD, ProgressStage.ALLOY);
		multi = new AdvancedBee("Polychromatic", "Pigmentum Pluralis", Speeds.SLOWEST, Life.ELONGATED, Flowering.AVERAGE, Fertility.LOW, Territory.DEFAULT, multiColor, EnumTemperature.WARM, ProgressStage.CTM);
		aura = new AdvancedBee("Radiant", "Auram Pharus", Speeds.SLOW, Life.LONG, Flowering.SLOW, Fertility.NORMAL, Territory.DEFAULT, auraColor, EnumTemperature.ICY, ProgressStage.CTM);
		lumen = new AdvancedBee("Luminescent", "Auram Ardens", Speeds.NORMAL, Life.SHORTENED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, lumenColor, EnumTemperature.NORMAL, ProgressStage.DIMENSION);

		chroma.register();
		multi.register();
		lumen.register();
		aura.register();

		addBreeding(CrystalElement.RED, CrystalElement.YELLOW, CrystalElement.ORANGE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GREEN, CrystalElement.LIME);
		addBreeding(CrystalElement.RED, CrystalElement.WHITE, CrystalElement.PINK);
		addBreeding(CrystalElement.RED, CrystalElement.BLUE, CrystalElement.PURPLE);
		addBreeding(CrystalElement.WHITE, CrystalElement.BLACK, CrystalElement.GRAY);
		addBreeding(CrystalElement.BLUE, CrystalElement.GREEN, CrystalElement.CYAN);
		addBreeding(CrystalElement.BLUE, CrystalElement.WHITE, CrystalElement.LIGHTBLUE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GRAY, CrystalElement.LIGHTGRAY);
		addBreeding(CrystalElement.PINK, CrystalElement.PURPLE, CrystalElement.MAGENTA);

		addBreeding(purity, crystal, CrystalElement.WHITE);
		addBreeding(protective, hostile, CrystalElement.BLACK);
		addBreeding(protective, crystal, CrystalElement.RED);
		addBreeding(luminous, crystal, CrystalElement.BLUE);

		addBreeding("Rural", crystal, CrystalElement.BROWN);
		addBreeding("Industrious", crystal, CrystalElement.YELLOW);
		addBreeding("Tropical", hostile, CrystalElement.GREEN);

		protective.addBreeding("Heroic", crystal, 10);
		hostile.addBreeding("Demonic", crystal, 10);
		luminous.addBreeding("Ended", purity, 5);

		chroma.addBreeding(beeMap.get(CrystalElement.PURPLE), beeMap.get(CrystalElement.WHITE), 5);
		lumen.addBreeding(beeMap.get(CrystalElement.BLUE), beeMap.get(CrystalElement.BLACK), 5);
		aura.addBreeding(lumen, beeMap.get(CrystalElement.YELLOW), 3);
		multi.addBreeding(aura, chroma, 2);

		protective.addProduct(new ItemStack(Blocks.obsidian), 2);
		hostile.addProduct(new ItemStack(Items.gunpowder), 4);
		luminous.addProduct(new ItemStack(Items.glowstone_dust), 5);
		protective.addProduct(Combs.HONEY.getItem(), 10);
		hostile.addProduct(Combs.HONEY.getItem(), 10);
		luminous.addProduct(Combs.HONEY.getItem(), 10);
		crystal.addProduct(ChromaStacks.crystalPowder, 5);
		purity.addProduct(new ItemStack(Items.ghast_tear), 1);

		chroma.addSpecialty(ChromaStacks.iridCrystal, 2);
		lumen.addSpecialty(ChromaStacks.lumaDust, 2);
		aura.addSpecialty(ChromaStacks.echoCrystal, 2);
		for (int i = 0; i < 16; i++) {
			multi.addSpecialty(ChromaStacks.getChargedShard(CrystalElement.elements[i]), 4);
			ItemStack is = ChromaItems.BERRY.getStackOfMetadata(i);
			multi.addProduct(is, 20);
			FlowerProviderMulti.conditions.put(is, new RainbowTreeCheck());
		}

		GameRegistry.registerWorldGenerator(HiveGenerator.instance, -5);
	}

	protected static final CrystalBee getBeeFor(CrystalElement color) {
		return beeMap.get(color);
	}

	public static BeeSpecies getCrystalBee() {
		return crystal;
	}

	public static BeeSpecies getPureBee() {
		return purity;
	}

	public static BeeSpecies getElementalBee(CrystalElement e) {
		return beeMap.get(e);
	}

	private static final void addBreeding(String in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(BeeSpecies in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(CrystalElement in1, CrystalElement in2, CrystalElement out) {
		CrystalBee p1 = beeMap.get(in1);
		CrystalBee p2 = beeMap.get(in2);
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(p1, p2, 8);
	}

	private static class AdvancedBee extends BasicBee {

		private final ColorBlendList colorList;
		private final ProgressionCheck progress;

		private AdvancedBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, ColorBlendList c, EnumTemperature t, ProgressStage p) {
			super(name, latin, s, l, f, f2, a, 0xffffff, t);
			colorList = c;
			progress = new ProgressionCheck(p);
		}

		@Override
		public String getDescription() {
			return "Hybridized from the crystal bees, these bees are mysterious and have unknown but surely significant beneficial effects.";
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			World world = ibh.getWorld();
			int x = ibh.getCoordinates().posX;
			int y = ibh.getCoordinates().posY;
			int z = ibh.getCoordinates().posZ;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return progress.check(world, x, y, z, ibg, ibh);
			}
			return false;
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public IAllele getEffectAllele() {
			return Effect.NONE.getAllele();
		}

		@Override
		public IAllele getFlowerAllele() {
			return this == multi ? multiFlower : super.getFlowerAllele();
		}

		@Override
		public int getOutlineColor() {
			return colorList.getColor(DragonAPICore.getSystemTimeAsInt()/30D);
		}

	}

	private static class BasicBee extends TraitsBee {

		public final int outline;

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color) {
			this(name, latin, s, l, f, f2, a, color, EnumTemperature.NORMAL);
		}

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, EnumTemperature t) {
			super(name, "bee."+name.toLowerCase(Locale.ENGLISH), latin, "Reika", new BeeTraits());
			traits.speed = s;
			traits.lifespan = l;
			traits.flowering = f;
			traits.fertility = f2;
			traits.area = a;
			traits.tempDir = Tolerance.NONE;
			traits.humidDir = Tolerance.NONE;
			traits.tempTol = 0;
			traits.humidTol = 0;
			traits.temperature = t;
			traits.humidity = EnumHumidity.NORMAL;
			traits.temperature = t;
			outline = color;
		}

		private BasicBee setCave() {
			traits.isCaveDwelling = true;
			traits.isNocturnal = true;
			return this;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees do little on their own, but perhaps they could be purified into something stronger.";
		}

		@Override
		public boolean hasEffect() {
			return false;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAllele getFlowerAllele() {
			return Flower.VANILLA.getAllele();
		}

		@Override
		public IAllele getEffectAllele() {
			return Effect.NONE.getAllele();
		}

		@Override
		public int getOutlineColor() {
			return outline;
		}

	}

	private static final class CrystalEffect extends BasicGene implements IAlleleBeeEffect {

		public final CrystalElement color;

		public CrystalEffect(CrystalElement color) {
			super("effect.cavecrystal."+color.name().toLowerCase(Locale.ENGLISH), color.displayName);
			this.color = color;
		}

		@Override
		public boolean isCombinable() {
			return false;
		}

		@Override
		public IEffectData validateStorage(IEffectData ied) {
			return null;
		}

		@Override
		public IEffectData doEffect(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (ibg.getPrimary() instanceof CrystalBee && ((CrystalBee)ibg.getPrimary()).color == color) {
				if (ibg.getSecondary() instanceof CrystalBee && ((CrystalBee)ibg.getSecondary()).color == color) {
					World world = ibh.getWorld();
					ChunkCoordinates c = ibh.getCoordinates();
					int[] r = ibg.getTerritory();
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
					for (EntityLivingBase e : li) {
						CrystalBlock.applyEffectFromColor(600, 0, e, color);
					}
				}
			}
			return null;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			return null;
		}

	}

	private static final class CrystalAllele extends BasicGene implements IAlleleFlowers {

		public final CrystalElement color;
		private final FlowerProviderCrystal provider;

		public CrystalAllele(CrystalElement color) {
			super("flower.cavecrystal."+color.name().toLowerCase(Locale.ENGLISH), color.displayName);
			this.color = color;
			provider = new FlowerProviderCrystal(color);
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	private static final class MultiAllele extends BasicGene implements IAlleleFlowers {

		private final FlowerProviderMulti provider;

		public MultiAllele() {
			super("flower.rainbowleaf", "Rainbow Leaves");
			provider = new FlowerProviderMulti();
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	public static class FlowerProviderMulti extends BasicFlowerProvider {

		private static final ItemHashMap<ProductCondition> conditions = new ItemHashMap();

		private FlowerProviderMulti() {
			super(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), 0, "Rainbow Leaves");
		}

		@Override
		public String getDescription() {
			return "Shimmering, multicolored leaves";
		}

		@Override
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ModularLogger.instance.log(LOGGER_TAG, "Flower provider "+this.getDescription()+" affecting products "+li+" for "+bee1.getName()+"; map="+conditions);
			Iterator<ItemStack> it = li.iterator();
			while (it.hasNext()) {
				ItemStack is = it.next();
				ProductCondition c = conditions.get(is);
				ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+": "+c);
				if (c != null) {
					boolean flag = false;
					if (bee1.getUID().equals(bee2.getUID())) {
						if (bee1.getUID().equals(multi.getUID())) {
							if (this.areConditionalsAvailable(world, x, y, z, ibg, ibh)) {
								ibh.getErrorLogic().setCondition(false, conditionalsUnavailable);
								if (c.check(world, x, y, z, ibg, ibh)) {
									ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" passed.");
									flag = true;
								}
							}
							else {
								ModularLogger.instance.log(LOGGER_TAG, "Conditionals unavailable. Removing.");
								ibh.getErrorLogic().setCondition(true, conditionalsUnavailable);
							}
						}
					}
					if (!flag) {
						ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" failed. Removing.");
						it.remove();
					}
				}
			}
			ItemStack[] ret = li.toArray(new ItemStack[li.size()]);
			return ret;
		}

		private boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderMulti))
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.DIMENSION.isPlayerAtStage(ep);
			}
			return false;
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	public static class FlowerProviderCrystal extends BasicFlowerProvider {

		public final CrystalElement color;

		private FlowerProviderCrystal(CrystalElement color) {
			super(ChromaBlocks.CRYSTAL.getBlockInstance(), color.ordinal(), color.name().toLowerCase(Locale.ENGLISH));
			this.color = color;
		}

		@Override
		public String getDescription() {
			return color.displayName;
		}

		@Override
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ItemHashMap<ProductCondition> map = productConditions.get(color);
			ModularLogger.instance.log(LOGGER_TAG, "Flower provider "+this.getDescription()+" affecting products "+li+" for "+bee1.getName()+"; map="+map);
			if (map != null) {
				Iterator<ItemStack> it = li.iterator();
				while (it.hasNext()) {
					ItemStack is = it.next();
					ProductCondition c = map.get(is);
					ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+": "+c);
					if (c != null) {
						boolean flag = false;
						if (bee1.getUID().equals(bee2.getUID())) {
							if (bee1.getUID().equals(beeMap.get(color).getUID())) {
								if (this.areConditionalsAvailable(world, x, y, z, ibg, ibh)) {
									ibh.getErrorLogic().setCondition(false, conditionalsUnavailable);
									if (c.check(world, x, y, z, ibg, ibh)) {
										ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" passed.");
										flag = true;
									}
								}
								else {
									ModularLogger.instance.log(LOGGER_TAG, "Conditionals unavailable. Removing.");
									ibh.getErrorLogic().setCondition(true, conditionalsUnavailable);
								}
							}
						}
						if (!flag) {
							ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" failed. Removing.");
							it.remove();
						}
					}
				}
			}
			ItemStack[] ret = li.toArray(new ItemStack[li.size()]);
			return ret;
		}

		private boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderCrystal))
				return false;
			if (((FlowerProviderCrystal)ibg.getFlowerProvider()).color != color)
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
			}
			return false;
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	private static final class CrystalBee extends BeeSpecies {

		public final CrystalElement color;
		public final Speeds speed;
		public final Fertility fertility;
		public final Flowering flowering;
		public final Life lifespan;
		public final Territory area;
		public final Tolerance tempDir;
		public final Tolerance humidDir;
		public final int tempTol;
		public final int humidTol;
		public final EnumTemperature temperature;
		public final EnumHumidity humidity;

		public CrystalBee(CrystalElement color, BeeTraits traits) {
			super(color.displayName+" Crystal", "bee.crystal."+color.name().toLowerCase(Locale.ENGLISH), "Vitreus "+color.displayName, "Reika");
			this.color = color;
			speed = traits.speed;
			fertility = traits.fertility;
			flowering = traits.flowering;
			lifespan = traits.lifespan;
			area = traits.area;
			tempDir = traits.tempDir;
			humidDir = traits.humidDir;
			tempTol = traits.tempTol;
			humidTol = traits.humidTol;
			temperature = traits.temperature;
			humidity = traits.humidity;

			this.addConditionalProduct(ChromaItems.BERRY.getStackOf(color), 25, false, new LeafCheck(color));
			this.addConditionalProduct(ItemColoredModInteract.ColoredModItems.COMB.getItem(color), 8, true, new CrystalPlantCheck(color));
			this.addProduct(ForestryHandler.Combs.HONEY.getItem(), 15);
			this.addConditionalProduct(ChromaOptions.isVanillaDyeMoreCommon() ? new ItemStack(Items.dye, 1, color.ordinal()) : ChromaItems.DYE.getStackOf(color), 20, false, new FlowerCheck(color));
			switch(color) {
				case BLACK:
					this.addConditionalProduct(ChromaStacks.auraDust, 5, true, new ProgressionCheck(TieredPlants.FLOWER.level));
					break;
				case RED:
					this.addSpecialty(ChromaStacks.etherBerries, 5);
					break;
				case GREEN:
					this.addSpecialty(ForestryHandler.Combs.SILKY.getItem(), 10);
					this.addSpecialty(ChromaStacks.livingEssence, 5);
					break;
				case PURPLE:
					if (ModList.TINKERER.isLoaded())
						this.addSpecialty(BerryTypes.XP.getStack(), 3);
					this.addSpecialty(ChromaStacks.voidDust, 10);
					break;
				case BROWN: {
					ArrayList<ItemStack> li = OreDictionary.getOres("nuggetIron");
					if (!li.isEmpty())
						this.addSpecialty(li.get(0), 5);
					break;
				}
				case ORANGE:
					this.addSpecialty(new ItemStack(Items.blaze_powder), 10);
					this.addConditionalProduct(ChromaStacks.fireEssence, 5, true, new ProgressionCheck(TieredOres.FIRESTONE.level));
					break;
				case BLUE:
					this.addConditionalProduct(ChromaStacks.beaconDust, 5, true, new ProgressionCheck(TieredPlants.DESERT.level));
					break;
				case YELLOW:
					this.addSpecialty(ChromaStacks.energyPowder, 5);
					break;
				case WHITE:
					this.addConditionalProduct(ChromaStacks.purityDust, 5, true, new ProgressionCheck(TieredPlants.CAVE.level));
					break;
				case CYAN:
					this.addConditionalProduct(ChromaStacks.waterDust, 5, true, new ProgressionCheck(TieredOres.WATERY.level));
					break;
				case LIME:
					this.addConditionalProduct(ChromaStacks.spaceDust, 5, true, new ProgressionCheck(TieredOres.SPACERIFT.level));
					break;
				case GRAY:
					this.addSpecialty(ChromaStacks.teleDust, 5);
					break;
				case LIGHTGRAY:
					this.addSpecialty(ChromaStacks.icyDust, 5);
					break;
				default:
					break;
			}
		}

		private void addConditionalProduct(ItemStack is, int chance, boolean specialty, ProductCondition c) {
			if (specialty)
				this.addSpecialty(is, chance);
			else
				this.addProduct(is, chance);
			ItemHashMap<ProductCondition> map = productConditions.get(color);
			if (map == null) {
				map = new ItemHashMap();
				productConditions.put(color, map);
			}
			map.put(is, c);
		}

		@Override
		public boolean isNocturnal() {
			return color == CrystalElement.BLUE;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			World world = ibh.getWorld();
			int x = ibh.getCoordinates().posX;
			int y = ibh.getCoordinates().posY;
			int z = ibh.getCoordinates().posZ;
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderCrystal))
				return false;
			if (((FlowerProviderCrystal)ibg.getFlowerProvider()).color != color)
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees seem to enjoy the magic aura of the cave crystals. " +
					"So much so, in fact, that they will only thrive around their corresponding color.";
		}

		@Override
		public EnumTemperature getTemperature() {
			return temperature;
		}

		@Override
		public EnumHumidity getHumidity() {
			return humidity;
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAllele getFlowerAllele() {
			return flowerMap.get(color);
		}

		@Override
		public Speeds getProductionSpeed() {
			return speed;
		}

		@Override
		public Fertility getFertility() {
			return fertility;
		}

		@Override
		public Flowering getFloweringRate() {
			return flowering;
		}

		@Override
		public Life getLifespan() {
			return lifespan;
		}

		@Override
		public Territory getTerritorySize() {
			return area;
		}

		@Override
		public boolean isCaveDwelling() {
			return true;
		}

		@Override
		public int getTemperatureTolerance() {
			return tempTol;
		}

		@Override
		public int getHumidityTolerance() {
			return humidTol;
		}

		@Override
		public Tolerance getHumidityToleranceDir() {
			return humidDir;
		}

		@Override
		public Tolerance getTemperatureToleranceDir() {
			return tempDir;
		}

		@Override
		public boolean isTolerantFlyer() {
			return color == CrystalElement.CYAN;
		}

		@Override
		public int getOutlineColor() {
			return crystalColors[color.ordinal()].getColor(DragonAPICore.getSystemTimeAsInt()/5D-color.ordinal()*32);//color.getJavaColor().getRGB();
		}

		@Override
		public IAllele getEffectAllele() {
			return effectMap.get(color);
		}

	}

	private static abstract class ProductCondition {

		public abstract boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh);

	}

	private static class ProgressionCheck extends ProductCondition {

		private final ProgressStage progress;

		private ProgressionCheck(ProgressStage p) {
			progress = p;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			return ep != null && progress.isPlayerAtStage(ep);
		}

	}

	private static class CrystalPlantCheck extends ProductCondition {

		private final CrystalElement color;

		private CrystalPlantCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.PLANT.getBlockInstance(), color.ordinal());
		}
	}

	private static class FlowerCheck extends ProductCondition {

		private final CrystalElement color;

		private FlowerCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.DYEFLOWER.getBlockInstance(), color.ordinal());
		}
	}

	private static class LeafCheck extends ProductCondition {

		private final CrystalElement color;

		private LeafCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.findLeaf(world, x, y, z, r, r2);
		}

		private boolean findLeaf(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						if (b instanceof BlockDyeLeaf && world.getBlockMetadata(dx, dy, dz) == color.ordinal()) {
							if (last)
								return true;
							else
								last = true;
						}
						else
							last = false;
					}
				}
			}
			return false;
		}

	}

	private static class RainbowTreeCheck extends ProductCondition {

		private RainbowTreeCheck() {

		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.findLeaf(world, x, y, z, r, r2);
		}

		private boolean findLeaf(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						if (b == ChromaBlocks.RAINBOWLEAF.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == 0) {
							if (last)
								return true;
							else
								last = true;
						}
						else
							last = false;
					}
				}
			}
			return false;
		}

	}

}
