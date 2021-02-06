package com.doxa.ninja.moves.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import com.doxa.ninja.Main;

public class HealEffect {
	private int taskID;
	private final Player player;
	
	public HealEffect(Player player) {
		this.player = player;
	}
	
	public void startSpellEffect() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
			double var = 0;
			int times_left = 0;
			Location loc, first, second;
			ParticleData particle = new ParticleData(player.getUniqueId());
			@Override
			public void run() {
				if (!particle.hasID()) {
					particle.setID(taskID);
				}
				
				var += Math.PI / 32;
				
				loc = player.getLocation();
				first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
				second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));
				
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, first, 0);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, second, 0);
				if (times_left > 5 * 20) {
					
				}
			}
			
		}, 0, 1);
	}
}
