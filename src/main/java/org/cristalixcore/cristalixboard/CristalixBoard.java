package org.cristalixcore.cristalixboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;

public final class CristalixBoard extends JavaPlugin implements Listener, CommandExecutor {

    private Scoreboard scoreboard;
    public Objective objective;
    private FileConfiguration config;



    @Override
    public void onEnable() {
        loadConfig();
        createScoreboard();

        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("cristalixboard").setExecutor(this);

    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("cristalix", "dummy",
                ChatColor.translateAlternateColorCodes('&', config.getString("Title")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String line : config.getStringList("Lines")) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', line));
            score.setScore(config.getInt("DefaultScore"));
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setScoreboard(scoreboard);
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cristalixboard") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfigScoreboard();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c!&7] &fКонфиг успешно перезагружен"));
            return true;
        }
        return false;
    }

    private void reloadConfigScoreboard() {
        reloadConfig();
        config = getConfig();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        createScoreboard();
    }
}
