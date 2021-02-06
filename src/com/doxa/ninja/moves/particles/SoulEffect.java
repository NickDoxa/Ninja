package com.doxa.ninja.moves.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import com.doxa.ninja.Main;

public class SoulEffect {

	private int taskID;
	private final Player player;
	
	public SoulEffect(Player player) {
		this.player = player;
	}
	
	public void startSpellEffect() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
			double var = 0;
			Location loc, first, second;
			ParticleData particle = new ParticleData(player.getUniqueId());
			@Override
			public void run() {
					if (!particle.hasID()) {
						particle.setID(taskID);
					}
					
					var += Math.random() / 12;
					
					loc = player.getLocation();
					first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
					second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));
					
					player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, first, 0);
					player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, second, 0);
				}
			
		}, 0, 1);
	}
}
