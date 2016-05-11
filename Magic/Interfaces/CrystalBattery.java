/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalBattery extends CrystalReceiver, CrystalSource, ChargingPoint {

	public int getMaxStorage(CrystalElement e);

	public float getFillFraction(CrystalElement e);

}
