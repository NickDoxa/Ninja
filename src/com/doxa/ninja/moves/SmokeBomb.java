package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SmokeBomb extends MoveBase implements Listener {

	Main plugin;
	public SmokeBomb(Main main) {
		this.plugin = main;
	}

	public void createItemSB() {
		setName("Smoke Bomb", ChatColor.GRAY + "" + ChatColor.BOLD + "Smoke Bomb");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.FIREBALL);
		setDescription("The smoke bomb is a common technique for ninja to escape! By throwing the bomb it emits a smoke particle and blinds enemies within range!"
				+ " To use: right click with smoke bomb!");
	}
	
	public void createSBItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<String, Long> sb_cd = new HashMap<String, Long>();
	List<Snowball> sb_list = new ArrayList<Snowball>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (plugin.isInProtectedRegion(player))
				return;
			if (sb_cd.containsKey(player.getName())) {
				if (sb_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (sb_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use Smoke Bomb for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			sb_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.SMOKEBOMB) * 1000));
			
			Snowball sb = player.launchProjectile(Snowball.class);
			sb_list.add(sb);
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		Projectile p = event.getEntity();
		if (p instanceof Snowball && sb_list.contains(p)) {
			List<Location> loc_list = new ArrayList<Location>();
			loc_list.add(p.getLocation());
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 1, p.getLocation().getY(), p.getLocation().getZ()));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 1, p.getLocation().getY(), p.getLocation().getZ() + 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 1, p.getLocation().getY(), p.getLocation().getZ() - 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 1, p.getLocation().getY(), p.getLocation().getZ() -1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 1, p.getLocation().getY(), p.getLocation().getZ()));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 1, p.getLocation().getY(), p.getLocation().getZ() + 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ() - 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ() + 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 2, p.getLocation().getY(), p.getLocation().getZ() + 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 2, p.getLocation().getY(), p.getLocation().getZ()));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 2, p.getLocation().getY(), p.getLocation().getZ() - 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 1, p.getLocation().getY(), p.getLocation().getZ() + 2));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() + 1, p.getLocation().getY(), p.getLocation().getZ() - 2));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ() + 2));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ() - 2));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 1, p.getLocation().getY(), p.getLocation().getZ() + 2));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 1, p.getLocation().getY(), p.getLocation().getZ() - 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 2, p.getLocation().getY(), p.getLocation().getZ() + 1));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 2, p.getLocation().getY(), p.getLocation().getZ()));
			loc_list.add(new Location(p.getWorld(), p.getLocation().getX() - 2, p.getLocation().getY(), p.getLocation().getZ() - 1));
			for (Location l : loc_list) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 0, 0, 0, 0, 10);
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 0, 1, 0, 0, 10);
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 0, 0, 1, 0, 10);
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 0, 0, 0, 1, 10);
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 1, 0, 0, 0, 10);
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					if (l.getBlockZ() == player.getLocation().getBlockZ() && l.getBlockX() == player.getLocation().getBlockX()
							&& (player.getLocation().getY()-l.getBlockY()) < 3) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 1));
					}
				}
			}
			p.remove();
			sb_list.remove(p);
			loc_list.clear();
		} else {
			return;
		}
	}
	
}
