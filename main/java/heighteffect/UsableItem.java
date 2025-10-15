package heighteffect;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class UsableItem extends Item {

    public UsableItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getDamageValue() < stack.getMaxDamage()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide) {
                boolean removedSlow = player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                boolean removedDigSlow = player.removeEffect(MobEffects.DIG_SLOWDOWN);

                MinecraftForge.EVENT_BUS.post(new HeightEffectImmunityEvent(player, 30000));

                if (!player.getAbilities().instabuild) {
                    int currentDamage = stack.getDamageValue();
                    stack.setDamageValue(currentDamage + 1);

                    if (stack.getDamageValue() >= stack.getMaxDamage()) {
                        stack.shrink(1);
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return stack;
    }

    private void removeHeightEffects(Player player) {
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
        player.removeEffect(MobEffects.CONFUSION);
        player.removeEffect(MobEffects.BLINDNESS);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float durability = (float)(stack.getDamageValue() - stack.getDamageValue()) / stack.getMaxDamage();

        if (durability < 0.25F) return 0xFF0000;
        else if (durability < 0.5F) return 0xFFA500;
        else return 0x00FF00;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - (float)stack.getDamageValue() * 13.0F / (float) stack.getMaxDamage());
    }

}