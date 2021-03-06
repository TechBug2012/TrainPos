package com.hektropolis.TrainPos;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mattbstanciu on 6/12/17.
 */
public class TrainPos extends JavaPlugin {

    Logger log = this.getLogger();
    WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

    public void onEnable() {
        this.saveDefaultConfig();
        this.saveConfig();
        this.reloadConfig();

        getWorldEdit();

        getCommand("setboard").setExecutor(new Commands(this));
        getCommand("delboard").setExecutor(new Commands(this));
        getCommand("trainpos").setExecutor(new Commands(this));
    }

    public void getWorldEdit() {
        if (we == null) {
            log.log(Level.SEVERE, "This plugin requires WorldEdit 6.1.5 or above.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
    Location deserializeLocation(String s) {
        String[] locParts = s.split(",");
        World w = Bukkit.getServer().getWorld(locParts[0]);
        double x = Double.parseDouble(locParts[1]);
        double y = Double.parseDouble(locParts[2]);
        double z = Double.parseDouble(locParts[3]);

        return new Location(w, x, y, z);
    }

    @SuppressWarnings("deprecation") //getData and setData are deprecated but there is no un-deprecated method afaik
    public void getBlocks(String color, int x, int y, int z) {
        Block posBlock = getServer().getWorld("hektor_city").getBlockAt(x, y, z);
        byte data = posBlock.getData();

        switch (color) {
            case "blue":
                data = 3;
                break;
            case "orange":
                data = 1;
                break;
            case "green":
                data = 5;
                break;
            case "red":
                data = 14;
                break;
            case "reset":
                data = 0;
                break;
            default:
                break;
        }

        if (data != (byte)0) {
            posBlock.setType(Material.STAINED_GLASS);
            posBlock.setData(data);
        } else {
            posBlock.setType(Material.ICE);
        }
    }

    public void setBlocks(String color1, String color2) {
        for (String key : this.getConfig().getKeys(false)) {
            Location min = deserializeLocation(this.getConfig().getString(key + ".minLoc"));
            Location max = deserializeLocation(this.getConfig().getString(key + ".maxLoc"));

            if (max.getX() - min.getX() == 0) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                        getBlocks(color1, (int)min.getX(), y , z);
                    }
                }
            } else if (max.getZ() - min.getZ() == 0) {
                for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                    for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                        getBlocks(color1, x, y, (int)min.getZ());
                    }
                }
            }
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                getBlocks(color2, (int)min.getX(), y, (int)min.getZ());
                getBlocks(color2,(int)max.getX(), y, (int)max.getZ());
            }
        }
    }
}
