package heighteffect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HeightEffectHandler {
    private static final int EFFECT_HEIGHT = 190;
    private static final int EFFECT_DURATION = 100;
    private static final int EFFECT_AMPLIFIER = 1;
    private static final int PUNISHMENT_DURATION = 100;
    private static final int DELAYED_PUNISHMENT_TIME = 6000;

    private static final Map<Player, Integer> immunityPlayers = new ConcurrentHashMap<>();
    private static final Map<Player, Integer> punishmentPlayers = new ConcurrentHashMap<>();
    private static final Map<Player, Integer> heightExposureTime = new ConcurrentHashMap<>();
    private static final Map<Player, Boolean> wasOnHeight = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            Player player = event.player;

            updateImmunity();
            updatePunishment();
            updateHeightExposure();

            if (isImmune(player)) {
                heightExposureTime.remove(player);
                wasOnHeight.put(player, false);
                return;
            }

            boolean isOnHeight = player.getY() >= EFFECT_HEIGHT;
            boolean previouslyOnHeight = wasOnHeight.getOrDefault(player, false);
            int currentTime = heightExposureTime.getOrDefault(player, 0);

            if (isOnHeight) {
                heightExposureTime.put(player, currentTime + 1);

                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        EFFECT_DURATION,
                        EFFECT_AMPLIFIER,
                        false, false, false
                ));

                player.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,
                        EFFECT_DURATION,
                        EFFECT_AMPLIFIER,
                        false, false, false
                ));

                if (!previouslyOnHeight && !isPunished(player)) {
                    applyPunishment(player);
                }

                if (currentTime >= DELAYED_PUNISHMENT_TIME && !isPunished(player)) {
                    applyDelayedPunishment(player);
                }

            } else {
                if (previouslyOnHeight) {
                    removePunishment(player);
                }
                heightExposureTime.remove(player);
            }

            wasOnHeight.put(player, isOnHeight);
        }
    }

    @SubscribeEvent
    public void onHeightEffectImmunity(HeightEffectImmunityEvent event) {
        immunityPlayers.put(event.getPlayer(), event.getImmunityTime());
        removePunishment(event.getPlayer());
        heightExposureTime.remove(event.getPlayer());
        wasOnHeight.put(event.getPlayer(), false);
    }

    private boolean isImmune(Player player) {
        return immunityPlayers.containsKey(player);
    }

    private boolean isPunished(Player player) {
        return punishmentPlayers.containsKey(player);
    }

    private void applyPunishment(Player player) {
        if (!player.level().isClientSide) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION,
                    PUNISHMENT_DURATION,
                    1,
                    false, false, true
            ));
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS,
                    PUNISHMENT_DURATION,
                    0,
                    false, false, true
            ));
            punishmentPlayers.put(player, PUNISHMENT_DURATION);
        }
    }

    private void applyDelayedPunishment(Player player) {
        if (!player.level().isClientSide) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION,
                    PUNISHMENT_DURATION * 3,
                    2,
                    false, false, true
            ));
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS,
                    PUNISHMENT_DURATION * 3,
                    1,
                    false, false, true
            ));
            punishmentPlayers.put(player, PUNISHMENT_DURATION * 3);
        }
    }

    private void removePunishment(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(MobEffects.CONFUSION);
            player.removeEffect(MobEffects.BLINDNESS);
            punishmentPlayers.remove(player);
        }
    }

    private void updateImmunity() {
        Iterator<Map.Entry<Player, Integer>> iterator = immunityPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Integer> entry = iterator.next();
            int ticks = entry.getValue() - 1;
            if (ticks <= 0) {
                iterator.remove();
            } else {
                entry.setValue(ticks);
            }
        }
    }

    private void updatePunishment() {
        Iterator<Map.Entry<Player, Integer>> iterator = punishmentPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Integer> entry = iterator.next();
            int ticks = entry.getValue() - 1;
            if (ticks <= 0) {
                Player player = entry.getKey();
                iterator.remove();
                if (!player.level().isClientSide) {
                    player.removeEffect(MobEffects.CONFUSION);
                    player.removeEffect(MobEffects.BLINDNESS);
                }
            } else {
                entry.setValue(ticks);
            }
        }
    }

    private void updateHeightExposure() {
        Iterator<Map.Entry<Player, Integer>> iterator = heightExposureTime.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Integer> entry = iterator.next();
            Player player = entry.getKey();
            if (!player.isAlive() || player.level().isClientSide) {
                iterator.remove();
            }
        }
    }
}