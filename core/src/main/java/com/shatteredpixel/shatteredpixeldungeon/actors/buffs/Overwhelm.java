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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Overwhelm extends Buff {
    public static final float WARN = 0.5f;
    public static final float DANGER = 0.8f;
    public static final float MAX_EVO = 1f;
    public static final String LEVEL = "evoFactor";
    public static final String RESET = "reset";
    public static final String TIME = "time";
    private float level = 0f;
    public void updateLevel(Object cause) {
        updateLevel(cause, false);
    }
    public void updateLevel(Object cause, boolean suppressMsg) {
        float oldLevel = level;
        float multiplier = 1f; // evolve faster on ascension
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            multiplier = (1+(26 - Dungeon.depth))/10f ;
        }
        multiplier = Math.max(1,multiplier);
        if (cause == null || cause.equals(TIME)) {
            level += 0.00001f*multiplier; // time factor
        } else {
            if (cause.equals(RESET) || cause instanceof LevelTransition) {
                if (level >= WARN && !suppressMsg) {
                    GLog.p(Messages.get(this,"relief"));
                }
                level = 0;
                return;
            }
            if (cause instanceof Hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment || cause instanceof Wand) {
                level += 0.02f* multiplier; // mob killed because of hero
            }
            if (cause instanceof Trap) {
                level += 0.005f * multiplier; // triggered traps
            }
        }
        level = Math.min(level, MAX_EVO);
        if (oldLevel < WARN && level >= WARN && !suppressMsg) {
            GLog.w(Messages.get(this,"warn"));
        } else if (oldLevel < DANGER && level >= DANGER && !suppressMsg) {
            GLog.n(Messages.get(this,"danger"));
        }

    }
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( LEVEL, level );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        level = bundle.getFloat( LEVEL );
    }

    @Override
    public boolean act() {
        System.out.printf("Evolution: %.6f\n", level);

        if (Dungeon.level.locked || Dungeon.depth % 5 == 0 || Dungeon.branch != 0) {
            updateLevel(RESET, true);
            spend(TICK);
            return true;
        }

        if (!(target.isAlive() && target instanceof Hero)) {
            diactivate();
        } else {
            updateLevel(TIME);
            spend(TICK);
        }
         // time factor
        return true;
    }

    public float getLevel() {
        return level;
    }
}
