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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Dome extends MoveBase implements Listener {

	Main plugin;
	public Dome(Main main) {
		plugin = main;
	}
	
	public void createItemDome() {
		setName("Dome", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Dome");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.DOME);
		setDescription("");
	}
	
	public void createDomeItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<String, Long> dome_cd = new HashMap<String, Long>();
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
			if (dome_cd.containsKey(player.getName())) {
				if (dome_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (dome_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use Dome for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			dome_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.DOME) * 1000));
			List<Block> blocks = new ArrayList<>();
			Block block = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() + 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
			Block block2 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() + 1, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ()));
			Block block3 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() + 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + 1));
			Block block4 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() + 1, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() + 1));
			Block block5 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ() + 2));
			Block block6 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() + 2));
			Block block7 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + 2));
			Block block8 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() + 2));
			Block block9 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ() + 1));
			Block block10 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ() + 1));
			Block block11 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()));
			Block block12 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 2, player.getLocation().getBlockZ()));
			Block block13 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() + 2, player.getLocation().getBlockZ() + 1));
			Block block14 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() + 2, player.getLocation().getBlockZ()));
			Block block15 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 2, player.getLocation().getBlockZ() + 1));
			Block block16 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()));
			Block block17 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ() - 1));
			Block block18 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() - 1));
			Block block19 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ() - 1));
			Block block20 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 1, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() - 1));
			Block block21 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + 1));
			Block block22 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 2, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ() + 1));
			Block block23 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
			Block block24 = player.getWorld().getBlockAt
					(new Location(player.getWorld(), player.getLocation().getBlockX() - 2, player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ()));
			
			blocks.add(block2);blocks.add(block3);blocks.add(block4);blocks.add(block5);blocks.add(block6);blocks.add(block7);blocks.add(block);
			blocks.add(block8);blocks.add(block9);blocks.add(block10);blocks.add(block11);blocks.add(block12);blocks.add(block13);blocks.add(block14);
			blocks.add(block15);blocks.add(block16);blocks.add(block17);blocks.add(block18);blocks.add(block19);blocks.add(block20);
			blocks.add(block21);blocks.add(block22);blocks.add(block23);blocks.add(block24);
			
			for (Block b : blocks) {
				if (b.getType().equals(Material.AIR)) {
					b.setType(Material.SANDSTONE);
					player.getWorld().spawnParticle(Particle.DRIPPING_HONEY, b.getX(), b.getY(), b.getZ(), 0);
					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
			            public void run() {
							b.setType(Material.AIR);
						}
					}, 10*20);
				}
			}
		}
	}
	
}
