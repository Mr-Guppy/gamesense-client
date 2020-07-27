package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.BossbarEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;


public class NoRender extends Module {
    public NoRender() {
        super("NoRender", Category.Render);
    }

    public Setting.b armor;
    Setting.b fire;
    Setting.b blind;
    Setting.b nausea;
    public Setting.b hurtCam;
    Setting.b noOverlay;
    Setting.b noBossBar;

    public void setup() {
        armor = registerB("Armor", "NRArmor", false);
        fire = registerB("Fire", "NRFire", false);
        blind = registerB("Blind", "NRBlind", false);
        nausea = registerB("Nausea", "NRNausea", false);
        hurtCam = registerB("HurtCam", "NRHurtCam", false);
        noOverlay = registerB("No Overlay", "NRNoOverlay", false); //need to make sure this works better
        noBossBar = registerB("No Boss Bar", "NRNoBossBar", false);
    }

    public void onUpdate(){
        if(blind.getValue() && mc.player.isPotionActive(MobEffects.BLINDNESS)) mc.player.removePotionEffect(MobEffects.BLINDNESS);
        if(nausea.getValue() && mc.player.isPotionActive(MobEffects.NAUSEA)) mc.player.removePotionEffect(MobEffects.NAUSEA);
    }

    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<>(event -> {
        if(fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
        if(noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) event.setCanceled(true);
        if(noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) event.setCanceled(true);
    });

    // Disable Water and lava fog
    @EventHandler
    private Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<>(event -> {
        if (noOverlay.getValue()) {
            if (event.getState().getMaterial().equals(Material.WATER)
                    || event.getState().getMaterial().equals(Material.LAVA)) {
                event.setDensity(0);
                event.setCanceled(true);
            }
        }
    });

    // Disable screen overlays Overlays
    @EventHandler
    private Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener<>(event -> {
        event.setCanceled(true);
    });

    @EventHandler
    private Listener<RenderGameOverlayEvent> renderGameOverlayEventListener = new Listener<>(event -> {
        if (noOverlay.getValue()) {
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)){
                event.setCanceled(true);
            }
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL)){
                event.setCanceled(true);
            }
        }
    });

    // Bossbar
    @EventHandler
    private Listener<BossbarEvent> bossbarEventListener = new Listener<>(event -> {
        if (noBossBar.getValue()) {
            event.cancel();
        }
    });

    public void onEnable(){
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable(){
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}
