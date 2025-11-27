/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Unhinged Pixel Dungeon
 * Copyright (C) 2025-2025 Sam (MrSamPlays)
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
 *
 */



package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.HashMap;

public class AscensionChallenge extends Buff {

	private static HashMap<Class<?extends Mob>, Float> modifiers = new HashMap<>();
	static {
		modifiers.put(Rat.class,            15f);
		modifiers.put(Snake.class,          14f);
		modifiers.put(Gnoll.class,          13f);
		modifiers.put(Swarm.class,          12f);
		modifiers.put(Crab.class,           11f);
		modifiers.put(Slime.class,          10f);

		modifiers.put(Skeleton.class,       6f);
		modifiers.put(Thief.class,          5.5f);
		modifiers.put(DM100.class,          5f);
		modifiers.put(Guard.class,          4.75f);
		modifiers.put(Necromancer.class,    4.5f);

		modifiers.put(Bat.class,            3f);
		modifiers.put(Brute.class,          2.75f);
		modifiers.put(Shaman.class,         2.5f);
		modifiers.put(Spinner.class,        2.25f);
		modifiers.put(DM200.class,          2.25f);

		modifiers.put(Ghoul.class,          1.8f);
		modifiers.put(Elemental.class,      1.67f);
		modifiers.put(Warlock.class,        1.55f);
		modifiers.put(Monk.class,           1.5f);
		modifiers.put(Golem.class,          1.4f);

		modifiers.put(RipperDemon.class,    1.23f);
		modifiers.put(Succubus.class,       1.225f);
		modifiers.put(Eye.class,            1.125f);
		modifiers.put(Scorpio.class,        1.1f);
	}

	public static float statModifier(Char ch){
		if (Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null){
			return 1;
		}

		if (ch instanceof Ratmogrify.TransmogRat){
			ch = ((Ratmogrify.TransmogRat) ch).getOriginal();
		}

		if (ch.buff(AscensionBuffBlocker.class) != null){
			return 1f;
		}

		for (Class<?extends Mob> cls : modifiers.keySet()){
			if (cls.isAssignableFrom(ch.getClass())){
				return modifiers.get(cls);
			}
		}

		return 1;
	}

	//distant mobs get constantly beckoned to the hero at 2+ stacks
	public static void beckonEnemies(){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null
				&& Dungeon.hero.buff(AscensionChallenge.class).stacks >= 2f){
			for (Mob m : Dungeon.level.mobs){
				if (m.alignment == Char.Alignment.ENEMY && m.distance(Dungeon.hero) > 8) {
					m.beckon(Dungeon.hero.pos);
				}
			}
		}
	}

	//mobs move at 2x speed when not hunting/fleeing at 4 stacks or higher
	public static float enemySpeedModifier(Mob m){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null
				&& m.alignment == Char.Alignment.ENEMY
				&& Dungeon.hero.buff(AscensionChallenge.class).stacks >= 4f
				&& m.state != m.HUNTING && m.state != m.FLEEING){
			return 2;
		}

		return 1;
	}

	//hero speed is halved and capped at 1x at 6+ stacks
	public static float modifyHeroSpeed(float speed){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null
				&& Dungeon.hero.buff(AscensionChallenge.class).stacks >= 6f){
			return Math.min(speed/2f, 1f);
		}
		return speed;
	}
	// mob spawns are increased significantly, resulting in swarming
	public static float overwhelmAscent() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null && Dungeon.hero.buff(AscensionChallenge.class).stacks >= 8) {
			return GameMath.gate(1f,(Dungeon.hero.buff(AscensionChallenge.class).stacks - 7.75f)*4, 10f);
		}
		return 1;
	}

	public static boolean qualifiedForPacifist(){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			return !Dungeon.hero.buff(AscensionChallenge.class).stacksLowered;
		}
		return false;
	}

	public static void processEnemyKill(Char enemy){
		AscensionChallenge chal = Dungeon.hero.buff(AscensionChallenge.class);
		if (chal == null) return;

		if (enemy instanceof Ratmogrify.TransmogRat){
			enemy = ((Ratmogrify.TransmogRat) enemy).getOriginal();
			if (enemy == null) return;
		}

		//only enemies that are boosted count
		if (enemy.buff(AscensionBuffBlocker.class) != null){
			return;
		}

		boolean found = false;
		for (Class<?extends Mob> cls : modifiers.keySet()){
			if (cls.isAssignableFrom(enemy.getClass())){
				found = true;
				break;
			}
		}
		if (!found) return;

		// nerf reduction of amulet curses
		float oldStacks = chal.stacks;
		if (enemy instanceof Ghoul || enemy instanceof RipperDemon){
			chal.stacks -= 0.05f;
		} else {
			chal.stacks -= 0.1f;
		}
		chal.stacks = Math.max(0, chal.stacks);
		if (!chal.stacksLowered) {
			chal.stacksLowered = true;
			GLog.p(Messages.get(AscensionChallenge.class, "weaken"));
		} else if (chal.stacks < 8f && (int)(chal.stacks/2) != (int)(oldStacks/2f)){
			GLog.p(Messages.get(AscensionChallenge.class, "weaken"));
		}

		//if the hero is at the max level, grant them 10 effective xp per stack cleared
		// for the purposes of on-xp gain effects
		if (oldStacks > chal.stacks && Dungeon.hero.lvl == Hero.MAX_LEVEL){
			Dungeon.hero.earnExp(Math.round(10*(oldStacks - chal.stacks)), chal.getClass());
		}

		BuffIndicator.refreshHero();
	}

	public static int AscensionCorruptResist(Mob m){
		//default to just using their EXP value if no ascent challenge is happening
		if (Dungeon.hero.buff(AscensionChallenge.class) == null){
			return m.EXP;
		}

		if (m instanceof Ratmogrify.TransmogRat){
			m = ((Ratmogrify.TransmogRat) m).getOriginal();
		}

		if (m.buff(AscensionBuffBlocker.class) != null){
			return m.EXP;
		}

		if (m instanceof RipperDemon){
			return 10; //reduced due to their numbers
		} else if (m instanceof Ghoul){
			return 7; //half of 13, rounded up
		} else {
			for (Class<?extends Mob> cls : modifiers.keySet()){
				if (cls.isAssignableFrom(m.getClass())){
					return Math.max(13, m.EXP); //same exp as an eye
				}
			}
		}
		return m.EXP;
	}

	{
		revivePersists = true;
	}

	private float stacks = 0;
	private float damageInc = 0;

	private boolean stacksLowered = false;

	public void onLevelSwitch(){
		if (Dungeon.depth < Statistics.highestAscent){
			Statistics.highestAscent = Dungeon.depth;
			justAscended = true;
			if (Dungeon.bossLevel()){
				Dungeon.hero.buff(Hunger.class).satisfy(Hunger.STARVING);
				Buff.affect(Dungeon.hero, Healing.class).setHeal(Dungeon.hero.HT, 0, 20);
			} else {
				stacks += 2f;

				//clears any existing mobs from the level and adds one initial one
				//this helps balance difficulty between levels with lots of mobs left, and ones with few
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (!mob.reset()) {
						Dungeon.level.mobs.remove( mob );
					}
				}
				Dungeon.level.spawnMob(12);
				// roar on level switch
				Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
			}
		}
		// shopkeepers will flee if you increase the rate at which you are overwhelmed at any point during the ascent
		if (Statistics.highestAscent < 20 && stacks >= 8){
			for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
				if (m instanceof Shopkeeper){
					((Shopkeeper) m).flee();
				}
			}
		}
	}

	//messages at boss levels only trigger on first ascent
	private boolean justAscended = false;

	public void saySwitch(){
		if (Dungeon.bossLevel()){
			if (justAscended) {
				GLog.p(Messages.get(this, "break"));
				for (Char ch : Actor.chars()){
					if (ch instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) ch).sayAppeared();
					}
				}
			}
		} else {
			if (Dungeon.depth == 1){
				GLog.n(Messages.get(this, "almost"));
			} else if (stacks >= 10f){
				GLog.n(Messages.get(this, "damage"));
			} else if (stacks >= 8f){
				GLog.n(Messages.get(this, "overwhelm"));
			} else if (stacks >= 6f){
				GLog.n(Messages.get(this, "slow"));
			} else if (stacks >= 4f){
				GLog.n(Messages.get(this, "haste"));
			} else if (stacks >= 2f){
				GLog.n(Messages.get(this, "beckon"));
			}
			if (stacks > 8 || stacks > 4 && Dungeon.depth > 20){
				GLog.h(Messages.get(this, "weaken_info"));
			}
		}
		justAscended = false;
	}

	@Override
	public boolean act() {

		beckonEnemies();

		//hero starts progressively taking damage over time at 10+ stacks
		if (stacks >= 10 && !Dungeon.bossLevel()){
			damageInc += (stacks-6)/4f;
			if (damageInc >= 1){
				target.damage((int)damageInc, this);
				damageInc -= (int)damageInc;
				// random chance to inflict/extend a debuff
				if (Random.Float()*stacks > 10f) {
					Buff.prolong(Dungeon.hero, Cripple.class, 4f);
				}
				if (Random.Float()*stacks > 12f) {
					Buff.affect(Dungeon.hero, Bleeding.class).set(damageInc, this.getClass());
				}
				if (target == Dungeon.hero && !target.isAlive()){
					Badges.validateDeathFromFriendlyMagic();
					GLog.n(Messages.get(this, "on_kill"));
					Dungeon.fail(Amulet.class);
				}
			}
		} else {
			damageInc = 0;
		}

		spend(TICK);
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.AMULET;
	}

	@Override
	public void tintIcon(Image icon) {
		if (stacks < 2){
			icon.hardlight(0.5f, 1, 0);
		} else if (stacks < 4) {
			icon.hardlight(1, 1, 0);
		} else if (stacks < 6){
			icon.hardlight(1, 0.67f, 0);
		} else if (stacks < 8){
			icon.hardlight(1, 0.33f, 0);
		} else if (stacks < 10) {
			icon.hardlight(1, 0, 0);
		} else {
			icon.hardlight(0.1f,0.1f,0.1f);
		}
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		desc += "\n";
		if (stacks < 2){

			desc += "\n" + Messages.get(this, "desc_clear");

		} else {
			if (stacks >= 2)    desc += "\n" + Messages.get(this, "desc_beckon");
			if (stacks >= 4)    desc += "\n" + Messages.get(this, "desc_haste");
			if (stacks >= 6)    desc += "\n" + Messages.get(this, "desc_slow");
			if (stacks >= 8)    desc += "\n" + Messages.get(this, "desc_overwhelm");
			if (stacks >= 10)    desc += "\n" + Messages.get(this, "desc_damage");
		}

		return desc;
	}

	public static final String STACKS = "enemy_stacks";
	public static final String DAMAGE = "damage_inc";

	public static final String STACKS_LOWERED = "stacks_lowered";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, stacks);
		bundle.put(DAMAGE, damageInc);
		bundle.put(STACKS_LOWERED, stacksLowered);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stacks = bundle.getFloat(STACKS);
		damageInc = bundle.getFloat(DAMAGE);
		if (bundle.contains(STACKS_LOWERED)){
			stacksLowered = bundle.getBoolean(STACKS_LOWERED);
		//pre-v3.1 saves
		} else {
			stacksLowered = true;
		}
	}

	//chars with this buff are not boosted by the ascension challenge
	public static class AscensionBuffBlocker extends Buff{};
}
