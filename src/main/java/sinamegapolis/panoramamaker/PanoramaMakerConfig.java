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

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class PanoramaMakerConfig {

    // Fullres screenshots: Take panorama screenshots without changing the render size
    public static boolean fullScreen = false;

    // Panorama Picture Resolution
    public static int panoramaSize = 1024;

}
