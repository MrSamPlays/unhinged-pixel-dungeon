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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.badlogic.gdx.utils.Null;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class SurpriseTrap extends Trap {
    {
        color = ORANGE;
        shape = WAVES;

        avoidsHallways = true;
        disarmedByActivation = false;
        isAoE = true;
        isNearest = true;
    }

    @Override
    public void activate() {
        TrapCategory cat = Random.oneOf(TrapCategory.values());
        if (reclaimed) {
            cat = Random.Int(2) == 0 ? TrapCategory.DART : TrapCategory.GAS; // if trap is a reclaim trap spell, it will always be a dart trap or a gas Trap
        }
        Trap t = Reflection.newInstance(trapClasses()[Random.chances(trapChances(cat))]);
        assert t != null; // if t is null crash the game :3
        GLog.n(Messages.get(this, "trigger", t.name()));
        t.pos = this.pos;
        t.activate();

        // logic to process death
        Char target = Actor.findChar(this.pos);
        // copied from disintegration trap for target searching (if t.isnearest)
        float range = Math.max(6, Dungeon.level.viewDistance) + 0.5f;
        if (target == null && t.isNearest) {
            float closestDist = Float.MAX_VALUE;
            for (Char ch : Actor.chars()) {
                if (!ch.isAlive()) continue;
                float curDist = Dungeon.level.trueDistance(pos, ch.pos);
                //invis targets are considered to be at max range
                if (ch.invisible > 0) curDist = Math.max(curDist, range);
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (bolt.collisionPos == ch.pos
                        && (curDist < closestDist || (curDist == closestDist && target instanceof Hero))) {
                    target = ch;
                    closestDist = curDist;
                }
            }
            if (closestDist > range) {
                target = null;
            }
            if (target.equals(Dungeon.hero)) {
                Hero hero = (Hero) target;
                if (!hero.isAlive()) {
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "ondeath", t.name()));
                }
            }
        }
    }

    private Class<? extends Trap>[] trapClasses() {
        return new Class[]{
                AlarmTrap.class, SummoningTrap.class, DistortionTrap.class, GuardianTrap.class, FlockTrap.class, // mob summoning/alerting traps (summoning)
                WornDartTrap.class, PoisonDartTrap.class, DisintegrationTrap.class, GrimTrap.class, // nearest target traps (dart)
                TeleportationTrap.class, GatewayTrap.class, WarpingTrap.class, // teleportation traps
                CursingTrap.class, WeakeningTrap.class, DisarmingTrap.class, // item manipulating traps
                ConfusionTrap.class, ToxicTrap.class, CorrosionTrap.class, // Gas traps
                PitfallTrap.class, RockfallTrap.class, ExplosiveTrap.class, // level manipulation traps
                BurningTrap.class, BlazingTrap.class, ChillingTrap.class, FrostTrap.class, ShockingTrap.class, StormTrap.class, OozeTrap.class, // elemental traps
                GrippingTrap.class, FlashingTrap.class, // repeatable damage traps
        };
    }

    private float[] trapWeights() {
        return new float[]{
                1, 1, 1, 1, 2,
                4, 3, 2, 1,
                3, 1, 1,
                1, 1, 1,
                3, 2, 1,
                1, 2, 2,
                2, 1, 2, 1, 2, 1, 1,
                3, 1
        };
    }

    private float[] trapChances(TrapCategory category) {
        float[] result = new float[trapClasses().length];
        for (int i = 0; i < result.length; i++) {
            if (
                    (i < 5 && category != TrapCategory.SUMMONING) ||
                            (i >= 5 && i < 9 && category != TrapCategory.DART) ||
                            (i >= 9 && i < 12 && category != TrapCategory.TELEPORT) ||
                            (i >= 12 && i < 15 && category != TrapCategory.ITEM) ||
                            (i >= 15 && i < 18 && category != TrapCategory.GAS) ||
                            (i >= 18 && i < 21 && category != TrapCategory.LEVEL) ||
                            (i >= 21 && i < 28 && category != TrapCategory.ELEMENTAL) ||
                            (i >= 28 && i < 30 && category != TrapCategory.DAMAGE)
            ) result[i] = 0;
            else result[i] = trapWeights()[i];
        }
        return result;
    }

    public enum TrapCategory {
        SUMMONING,
        DART,
        TELEPORT,
        ITEM,
        GAS,
        LEVEL,
        ELEMENTAL,
        DAMAGE,
    }
}
