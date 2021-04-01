package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.doxa.ninja.moves.particles.ParticleData;
import com.doxa.ninja.moves.particles.SoulEffect;

public class Chidori extends MoveBase implements Listener {

	Main plugin;
	public Chidori(Main main) {
		plugin = main;
	}
	
	public void createItemChidori() {
		setName("Chidori", ChatColor.BLUE + "" + ChatColor.BOLD + "Chidori");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.CHIDORI);
		setDescription("Chidori (A thousand birds) is a move known to few ninja."
				+ " By loading up their chakra a ninja can create a lightning-type ball to strike their"
				+ " opponent! To use: right click, then left click your opponent. The longer you load your"
				+ " chakra the harder you hit! after 5 seconds your chakra returns to normal. Using Chidori"
				+ " and Rasengan at the same time will overload your chakra and damage you badly!");
	}
	
	public void createChiItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	private Map<Player, Boolean> Active_Map = new HashMap<Player, Boolean>();
	public void setActiveMap(Player p, Boolean b) {
		Active_Map.put(p, b);
	}
	public boolean isActive(Player p) {
		return Active_Map.get(p);
	}

	public void createChidori(Player player) {
		SoulEffect effect = new SoulEffect(player);
		effect.startSpellEffect();
	}
	
	Map<String, Long> chi_cd = new HashMap<String, Long>();
	Map<Player, ParticleData> particles = new HashMap<Player, ParticleData>();
	public ParticleData getParticles(Player player) {
		return particles.get(player);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (plugin.isInProtectedRegion(player))
				return;
			if (chi_cd.containsKey(player.getName())) {
				if (chi_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (chi_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			chi_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.CHIDORI) * 1000));
			particles.put(player, new ParticleData(player.getUniqueId()));
			Active_Map.put(player, true);
			plugin.useChakra(0.2, player);
			plugin.chakraOverloadMain(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (5 * 20), 3, true, false, false));
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
			chakraBuild(player);
			createChidori(player);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	if (isActive(player)) {
	            		try {
		            	Active_Map.put(player, false);
		            	removeParticles(player);
	            		} catch (NullPointerException e) {
	            			return;
	            		}
	            	}
	            }
			}, (5 * 20));
		}
	}
	
	Map<Player, Double> chakraDamage = new HashMap<Player, Double>();
	private void chakraBuild(Player player) {
		chakraDamage.put(player, 0D);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    @Override
		    public void run() {
		    	if (Active_Map.get(player)) {
		    		double old_val = chakraDamage.get(player);
		    		chakraDamage.put(player, old_val+=1.0);
		    	} else {
		    		chakraDamage.put(player, 0D);
		    	}
		    }
		}, 0, 2 * 20);
	}
	
	public void removeParticles(Player player) {
    	getParticles(player).endTask();
    	getParticles(player).removeID();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSmack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			//CANCEL OUT IF TAIJUTSU
			Entity damaged = event.getEntity();
			if (damaged instanceof Player) {
				try {
				if (((HumanEntity) damaged).getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Substitution")) {
					return;
				}
				} catch (NullPointerException e) {
					return;
				}
				Player d = (Player) damaged;
				try {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Taijutsu")
						&& isActive(d)) {
					removeParticles(d);
					Active_Map.put(d, false);
					return;
				}
				} catch (NullPointerException e) {
					return;
				}
			}
			try {
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			} catch(NullPointerException e) {
				return;
			}
			if (!Active_Map.get(player))
				return;
			Entity entity = event.getEntity();
			double damage = plugin.getDamage(MoveType.CHIDORI, player) + (chakraDamage.get(player)/2);
			if (entity instanceof Player) {
				final double health = ((Player) entity).getHealth();
				double new_health = (health - damage);
				if (new_health > 0.1) {
					((Player) entity).setHealth(new_health);
					event.setDamage(0.1);
				} else {
					((Player) entity).setHealth(0.1);
					event.setDamage(0.1);
				}
				((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1, true, false, false));
				((Player) entity).getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
			}
			final Location loc = entity.getLocation();
			entity.getWorld().spawnParticle(Particle.SPELL_INSTANT, loc.getX(), loc.getY(), loc.getZ(),
        			10);
	    	removeParticles(player);
			Active_Map.put(player, false);
		}
	}
	
	@EventHandler
	public void onGetDamaged(EntityDamageByEntityEvent event) {
		
	}
	
	public void chakraOverload(Player player) {
		try {
			if (isActive(player)) {
				removeParticles(player);
		    	player.damage(plugin.getDamage(MoveType.CHAKRA_OVERLOAD, player));
		    	Active_Map.put(player, false);
			}
		} catch (NullPointerException e) {
			plugin.writeReport("", "particles");
		}
	}
	
}
