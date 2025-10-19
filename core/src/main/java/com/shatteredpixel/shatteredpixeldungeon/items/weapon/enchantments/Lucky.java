/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollExile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.noosa.Visual;
import com.watabou.utils.Random;

public class Lucky extends Weapon.Enchantment {

	private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing( 0x00FF00 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 10%
		// lvl 1 ~ 12%
		// lvl 2 ~ 14%
		float procChance = (level+4f)/(level+40f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance){

			float powerMulti = Math.max(1f, procChance);

			//default is -5: 80% common, 20% uncommon, 0% rare
			//ring level increases by 1 for each 20% above 100% proc rate
			Buff.affect(defender, LuckProc.class).ringLevel = -10 + Math.round(5*powerMulti);
			// Lucky weapon procs have a further reduced chance of resetting limited drops from mobs;
			if (Random.Float() < 2 * procChance) {
				if (defender instanceof Bat)
					Dungeon.LimitedDrops.BAT_HP.count = 0;
				else if (defender instanceof Swarm)
					Dungeon.LimitedDrops.SWARM_HP.count = 0;
				else if (defender instanceof DM200)
					Dungeon.LimitedDrops.DM200_EQUIP.count = 0;
				else if (defender instanceof Golem)
					Dungeon.LimitedDrops.GOLEM_EQUIP.count = 0;
				else if (defender instanceof Necromancer)
					Dungeon.LimitedDrops.NECRO_HP.count = 0;
				else if (defender instanceof Guard)
					Dungeon.LimitedDrops.GUARD_ARM.count = 0;
				else if (defender instanceof Warlock)
					Dungeon.LimitedDrops.WARLOCK_HP.count = 0;
				else if (defender instanceof Shaman) // gnoll shaman
					Dungeon.LimitedDrops.SHAMAN_WAND.count = 0;
				else if (defender instanceof Skeleton)
					Dungeon.LimitedDrops.SKELE_WEP.count = 0;
				else if (defender instanceof Slime)
					Dungeon.LimitedDrops.SLIME_WEP.count = 0;
				else if (defender instanceof Thief)
					Dungeon.LimitedDrops.THEIF_MISC.count = 0;
			}
		} else {
			//in rare cases where we attack many times at once (e.g. gladiator fury)
			// make sure that failed luck procs override prior succeeded ones
			if (defender.buff(LuckProc.class) != null){
				defender.buff(LuckProc.class).detach();
			}
		}
		
		return damage;

	}
	
	public static Item genLoot(){
		//80% common, 20% uncommon, 0% rare
		return RingOfWealth.genConsumableDrop(-5);
	}

	public static void showFlare( Visual vis ){
		RingOfWealth.showFlareForBonusDrop(vis);
	}

	@Override
	public Glowing glowing() {
		return GREEN;
	}
	
	//used to keep track of whether a luck proc is incoming. see Mob.die()
	public static class LuckProc extends Buff {

		private int ringLevel = -5;
		
		@Override
		public boolean act() {
			detach();
			return true;
		}

		public Item genLoot(){
			detach();
			return RingOfWealth.genConsumableDrop(ringLevel);
		}
	}
	
}
