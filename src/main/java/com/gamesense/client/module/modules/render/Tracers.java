package com.gamesense.client.module.modules.render;

import java.util.ArrayList;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * @author Hoosiers
 * @since 8/12/20
 * some GL from Osiris/KAMI was referenced.
 */

public class Tracers extends Module {

	public Tracers() {
		super("Tracers", Category.Render);
	}

	Setting.Boolean colorType;
	Setting.Integer renderDistance;
	Setting.Mode pointsTo;
	Setting.ColorSetting nearColor;
	Setting.ColorSetting midColor;
	Setting.ColorSetting farColor;

	public void setup() {
		renderDistance = registerInteger("Distance", "Distance", 100, 10, 260);

		ArrayList<String> link = new ArrayList<>();
		link.add("Head");
		link.add("Feet");

		pointsTo = registerMode("Draw To", "DrawTo", link, "Feet");
		colorType = registerBoolean("Color Sync", "ColorSync", true);
		nearColor=registerColor("Near Color","NearColor",new GSColor(255,0,0, 255));
		midColor=registerColor("Middle Color","MidColor",new GSColor(255,255,0, 255));
		farColor=registerColor("Far Color","FarColor",new GSColor(0,255,0, 255));
	}

	GSColor tracerColor;

	public void onWorldRender(RenderEvent event) {
		mc.world.loadedEntityList.stream()
				.filter(e->e instanceof EntityPlayer)
				.filter(e->e != mc.player)
				.forEach(e->{
					if (mc.player.getDistance(e) > renderDistance.getValue()) {
						return;
					}
					else {
						if (Friends.isFriend(e.getName())) {
							tracerColor = ColorMain.getFriendGSColor();
						}
						else if (Enemies.isEnemy(e.getName())) {
							tracerColor = ColorMain.getEnemyGSColor();
						}
						else {
							if (mc.player.getDistance(e) < 20) {
								tracerColor = nearColor.getValue();
							}
							if (mc.player.getDistance(e) >= 20 && mc.player.getDistance(e) < 50) {
								tracerColor = midColor.getValue();
							}
							if (mc.player.getDistance(e) >= 50) {
								tracerColor = farColor.getValue();
							}

							if (colorType.getValue()) {
								tracerColor = getDistanceColor((int) mc.player.getDistance(e));
							}
						}
					}
					drawLineToEntityPlayer(e, tracerColor);
				});
	}

	public void drawLineToEntityPlayer(Entity e, GSColor color) {
		double[] xyz = interpolate(e);
		drawLine1(xyz[0],xyz[1],xyz[2], e.height, color);
	}

	public static double[] interpolate(Entity entity) {
		double posX = interpolate(entity.posX, entity.lastTickPosX);
		double posY = interpolate(entity.posY, entity.lastTickPosY);
		double posZ = interpolate(entity.posZ, entity.lastTickPosZ);
		return new double[] { posX, posY, posZ };
	}

	public static double interpolate(double now, double then) {
		return then + (now - then) * mc.getRenderPartialTicks();
	}

	public void drawLine1(double posx, double posy, double posz, double up, GSColor color) {
		Vec3d eyes=ActiveRenderInfo.getCameraPosition().add(mc.getRenderManager().viewerPosX,mc.getRenderManager().viewerPosY,mc.getRenderManager().viewerPosZ);
		GameSenseTessellator.prepare();
		if (pointsTo.getValue().equalsIgnoreCase("Head")) {
			GameSenseTessellator.drawLine(eyes.x, eyes.y, eyes.z, posx, posy+up, posz, color);
		}
		else {
			GameSenseTessellator.drawLine(eyes.x, eyes.y, eyes.z, posx, posy, posz, color);
		}
		GameSenseTessellator.release();
	}

	private GSColor getDistanceColor(int distance) {
		if (distance > 50) {
			distance = 50;
		}

		int red = (int) (255 - (distance * 5.1));
		int green = 255 - red;

		return new GSColor(red, green, 0, 255);
	}
}