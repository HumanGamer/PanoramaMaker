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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PanoramaScreenshotMaker {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    File panoramaDir;
    File currentDir;
    float rotationYaw, rotationPitch;
    int panoramaStep;
    boolean takingPanorama;
    int currentWidth, currentHeight, redBits = 8, greenBits = 8, blueBits = 8, refreshRate = 60;

    int panoramaSize = PanoramaMakerConfig.panoramaSize;
    boolean fullscreen = PanoramaMakerConfig.fullScreen;
    boolean isFullscreen;

    @SubscribeEvent
    public void takeScreenshot(ScreenshotEvent event) {
        if(takingPanorama)
            return;

        if(Screen.hasControlDown() && Screen.hasShiftDown() && Minecraft.getInstance().currentScreen == null) {
            takingPanorama = true;
            panoramaStep = 0;

            if(panoramaDir == null)
                panoramaDir = new File(event.getScreenshotFile().getParentFile(), "panoramas");
            if(!panoramaDir.exists())
                panoramaDir.mkdirs();

            int i = 0;
            String ts = getTimestamp();
            do {
                if(fullscreen) {
                    if(i == 0)
                        currentDir = new File(panoramaDir + "_fullres", ts);
                    else currentDir = new File(panoramaDir, ts + "_" + i + "_fullres");
                } else {
                    if(i == 0)
                        currentDir = new File(panoramaDir, ts);
                    else currentDir = new File(panoramaDir, ts + "_" + i);
                }
            } while(currentDir.exists());

            currentDir.mkdirs();

            event.setCanceled(true);

            ITextComponent panoramaDirComponent = new StringTextComponent(currentDir.getName());
            panoramaDirComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, currentDir.getAbsolutePath())).setUnderlined(true);
            event.setResultMessage(new TranslationTextComponent("panoramamaker.panoramaSaved", panoramaDirComponent));
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if(takingPanorama) {
            if(event.phase == TickEvent.Phase.START) {
                if(panoramaStep == 0) {
                    mc.gameSettings.hideGUI = true;
                    currentWidth = mc.mainWindow.getWidth();
                    currentHeight = mc.mainWindow.getHeight();
                    if (mc.mainWindow.getVideoMode().isPresent()) {
                        redBits = mc.mainWindow.getVideoMode().get().getRedBits();
                        greenBits = mc.mainWindow.getVideoMode().get().getGreenBits();
                        blueBits = mc.mainWindow.getVideoMode().get().getBlueBits();
                        refreshRate = mc.mainWindow.getVideoMode().get().getRefreshRate();
                    }
                    rotationYaw = mc.player.rotationYaw;
                    rotationPitch = mc.player.rotationPitch;


                    if(!fullscreen) {
                        isFullscreen = mc.mainWindow.isFullscreen();
                        if (isFullscreen)
                            mc.mainWindow.toggleFullscreen();
                        resize(new VideoMode(panoramaSize, panoramaSize, redBits, greenBits, blueBits, refreshRate));
                    }
                }

                switch(panoramaStep) {
                    case 1:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = 0;
                        break;
                    case 2:
                        mc.player.rotationYaw = -90;
                        mc.player.rotationPitch = 0;
                        break;
                    case 3:
                        mc.player.rotationYaw = 0;
                        mc.player.rotationPitch = 0;
                        break;
                    case 4:
                        mc.player.rotationYaw = 90;
                        mc.player.rotationPitch = 0;
                        break;
                    case 5:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = -90;
                        break;
                    case 6:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = 90;
                        break;
                }
                mc.player.prevRotationYaw = mc.player.rotationYaw;
                mc.player.prevRotationPitch = mc.player.rotationPitch;
            } else {
                if(panoramaStep > 0)
                    saveScreenshot(currentDir, "panorama_" + (panoramaStep - 1) + ".png", mc.mainWindow.getWidth(), mc.mainWindow.getHeight(), mc.getFramebuffer());
                panoramaStep++;
                if(panoramaStep == 7) {
                    mc.gameSettings.hideGUI = false;
                    takingPanorama = false;

                    mc.player.rotationYaw = rotationYaw;
                    mc.player.rotationPitch = rotationPitch;
                    mc.player.prevRotationYaw = mc.player.rotationYaw;
                    mc.player.prevRotationPitch = mc.player.rotationPitch;

                    if (!fullscreen) {
                        if (isFullscreen)
                            mc.mainWindow.toggleFullscreen();
                        resize(new VideoMode(currentWidth, currentHeight, redBits, greenBits, blueBits, refreshRate));
                    }
                }
            }
        }
    }

    private static void resize(VideoMode mode)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.mainWindow.func_224797_a(Optional.of(mode));
        mc.mainWindow.prevWindowWidth = mode.getWidth();
        mc.mainWindow.prevWindowHeight = mode.getHeight();
        mc.mainWindow.width = mode.getWidth();
        mc.mainWindow.height = mode.getHeight();
        mc.mainWindow.videoModeChanged = false;
        mc.mainWindow.updateVideoMode();
        mc.updateWindowSize();
    }

    private static void saveScreenshot(File dir, String screenshotName, int width, int height, Framebuffer buffer) {
        try {
            NativeImage nativeImage = ScreenShotHelper.createScreenshot(width, height, buffer);
            File file2 = new File(dir, screenshotName);

            net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeImage, file2);
            nativeImage.write(file2);

        } catch(Exception exception) { }
    }

    private static String getTimestamp() {
        String s = DATE_FORMAT.format(new Date()).toString();
        return s;
    }

    @SubscribeEvent
    public void onEnteringWorld(EntityJoinWorldEvent event){
        if(event.getEntity()instanceof PlayerEntity && event.getWorld().isRemote){
            ((PlayerEntity) event.getEntity()).sendStatusMessage(new TranslationTextComponent("panoramamaker.enterWorld"), false);
        }
    }
}
