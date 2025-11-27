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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

// when you carry more than you can carry
public class Overloaded extends Buff {
    private float level;
    private final float LIGHT = 0.5f;
    private final float MODERATE = 1f;
    private final float HEAVY = 1.5f;

    // 0 = unburdened, 1 = lightly burdened, 2 = moderately burdened, 3 = heavily burdened, 4 = critically burdened
    private int weightCat;
    {
        level = 0;
    }
    @Override
    public boolean act() {
        if (!(target instanceof Hero && target.isAlive())) {
            diactivate();
        } else {
            Hero hero = (Hero) target;

            int old_weightCat = weightCat;
            float excess_weight = (float) (Overloaded.getWeight() - (2*hero.STR() + 3*(Math.log(hero.lvl)/Math.log(2))));
            if (excess_weight <= 0) {
                excess_weight = 0;
            }
            level = excess_weight/10f;
            if (level <= 0) {
                weightCat = 0;
            } else if (level < LIGHT) {
                weightCat = 1;
            } else if (level >= LIGHT && level < MODERATE) {
                weightCat = 2;
            } else if (level >= MODERATE && level < HEAVY) {
                weightCat = 3;
            } else if (level > HEAVY) {
                weightCat = 4;
            }
            if (weightCat != old_weightCat) {
                switch (weightCat) {
                    case 0:
                        GLog.w(Messages.get(this, "relief"));
                        break;
                    case 1:
                        GLog.w(Messages.get(this, "light"));
                        break;
                    case 2:
                        GLog.w(Messages.get(this, "moderate"));
                        break;
                    case 3:
                        GLog.n(Messages.get(this, "heavy"));
                        break;
                    case 4:
                        GLog.n(Messages.get(this, "Critical"));
                        break;
                }
            }
        }
        System.out.printf("Total weight: %f\n",Overloaded.getWeight());
        System.out.printf("Weight Level: %f\n", getLevel());
        spend(TICK);
        return true;
    }
    public static float getWeight() {
        float totalweight = 0;
        for (Item i : Dungeon.hero.belongings) {
            totalweight += i.weightValue();
        }
        return totalweight;
    }
    public float getLevel() {
        // levitation negates effects of being overloaded
        if (target != null && target.buff(Levitation.class) != null) {
            return 0;
        }
        return level;
    }

    @Override
    public int icon() {
        if (weightCat > 0) {
            return BuffIndicator.CRIPPLE;
        }
        return BuffIndicator.NONE;
    }

    @Override
    public float iconFadePercent() {
        return 1 - (weightCat * 0.25f);
    }
    private final String LOAD = "backpack_load";
    private final String WEIGHT_CAT = "weight_category";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LOAD, level);
        bundle.put(WEIGHT_CAT, weightCat);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getFloat(LOAD);
        weightCat = bundle.getInt(WEIGHT_CAT);
    }
}
