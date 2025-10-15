package heighteffect;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("heighteffect")
public class HeightEffectMod {
    public static final String MODID = "heighteffect";
    public static final Logger LOGGER = LogManager.getLogger();

    public HeightEffectMod() {

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new HeightEffectHandler());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemInit.ITEMS.register(modEventBus);


        MinecraftForge.EVENT_BUS.register(new HeightEffectHandler());

        LOGGER.info("Мод HeightEffect загружен!");
    }
}