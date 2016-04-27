/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.AntFarm;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.Instantiable.Spline;
import Reika.DragonAPI.Instantiable.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class AntFarmTunnel extends StructurePiece {

	public final double direction;
	public final double slope;

	public final int tunnelRadius;

	public final int length;

	private final HashSet<Coordinate> air = new HashSet();
	private final HashMap<Coordinate, BlockKey> blocks = new HashMap();

	public AntFarmTunnel(AntFarmGenerator a, double dir, int len, double s, int r, int x, int y, int z, HashSet<Coordinate> airSpaces) {
		super(a);
		direction = dir;
		slope = s;

		tunnelRadius = r;
		length = len;

		this.initialize(x, y, z, airSpaces);
	}

	private void initialize(int x, int y, int z, HashSet<Coordinate> airSpaces) {
		DecimalPosition p1 = new DecimalPosition(x+0.5, y+0.5, z+0.5);
		double[] d = ReikaPhysicsHelper.polarToCartesian(length, slope, direction);
		DecimalPosition p2 = p1.offset(d[0], d[1], d[2]);
		LightningBolt b = new LightningBolt(p1, p2, 3); //was 6, then 2
		b.variance = Math.min(6, length/8D); //was 12, L/4
		b.velocity = b.variance*2;
		b.update();
		Spline s = new Spline(SplineType.CHORDAL);
		for (int i = 0; i <= b.nsteps; i++) {
			s.addPoint(new BasicSplinePoint(b.getPosition(i)));
		}
		List<DecimalPosition> li = s.get(4*length, false);
		for (DecimalPosition p : li) {
			this.generateTunnelSection(MathHelper.floor_double(p.xCoord), MathHelper.floor_double(p.yCoord), MathHelper.floor_double(p.zCoord), tunnelRadius, airSpaces);
		}
	}

	private void generateTunnelSection(int dx, int dy, int dz, int r, HashSet<Coordinate> airSpaces) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					double dd = ReikaMathLibrary.py3d(i, j, k);
					if (dd <= r+0.5) {
						int ddx = dx+i;
						int ddy = dy+j;
						int ddz = dz+k;
						Coordinate c = new Coordinate(ddx, ddy, ddz);
						BlockKey b = dd <= r-0.5 ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						if (air.contains(c) || airSpaces.contains(c))
							b = new BlockKey(Blocks.air);
						blocks.put(c, b);
						if (b.blockID == Blocks.air)
							air.add(c);
					}
				}
			}
		}
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (Coordinate c : blocks.keySet()) {
			BlockKey bk = blocks.get(c);
			world.setBlock(c.xCoord, c.yCoord, c.zCoord, bk.blockID, bk.metadata);
		}
	}

	public Collection<Coordinate> getAirSpaces() {
		return Collections.unmodifiableCollection(air);
	}

	public boolean intersectsWith(HashSet<Coordinate> space) {
		for (Coordinate c : air) {
			if (space.contains(c))
				return true;
		}
		return false;
	}

}