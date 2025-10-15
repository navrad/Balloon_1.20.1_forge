package heighteffect;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "heighteffect");

    public static final RegistryObject<Item> BALLOON = ITEMS.register("balloon",
            () -> new UsableItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(4)
            ));
}