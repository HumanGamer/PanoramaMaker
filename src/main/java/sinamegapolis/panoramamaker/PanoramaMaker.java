/**
 * This class was created by <SinaMegapolis>. It's distributed as
 * part of the PanoramaMaker Mod. Get the Source Code in github:
 * https://github.com/SinaMegapolis/PanoramaMaker
 *
 * PanoramaMaker is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 */
package sinamegapolis.panoramamaker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("panoramamaker")
public class PanoramaMaker {

    public PanoramaMaker()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

	private void setup(final FMLCommonSetupEvent e) {

        if(!ModList.get().isLoaded("quark"))
            MinecraftForge.EVENT_BUS.register(new PanoramaScreenshotMaker());
    }
}
