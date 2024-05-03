package br.com.sicro.homes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Main extends JavaPlugin {

    private FileConfiguration homesConfig;
    private File homesFile;

    @Override
    public void onEnable() {
        System.out.println("Plugin de Homes inicilizada com sucesso!");
        this.getCommand("sethome").setExecutor(this);
        this.getCommand("home").setExecutor(this);
        this.getCommand("delhome").setExecutor(this);
        this.getCommand("homes").setExecutor(this); // Adicionando o comando /homes
        // Carregue o arquivo de configuração de homes
        homesFile = new File(getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            homesFile.getParentFile().mkdirs();
            saveResource("homes.yml", false);
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    @Override
    public void onDisable() {
        // Salve o arquivo de configuração de homes
        saveHomesConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("sethome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Uso correto: /sethome <Nome>");
                return true;
            }

            String homeName = args[0];
            if (homesConfig.contains(homeName)) {
                player.sendMessage(ChatColor.RED + "Essa home já existe.");
                return true;
            }

            homesConfig.set(homeName + ".world", player.getLocation().getWorld().getName());
            homesConfig.set(homeName + ".x", player.getLocation().getX());
            homesConfig.set(homeName + ".y", player.getLocation().getY());
            homesConfig.set(homeName + ".z", player.getLocation().getZ());
            homesConfig.set(homeName + ".yaw", player.getLocation().getYaw());
            homesConfig.set(homeName + ".pitch", player.getLocation().getPitch());
            saveHomesConfig();
            player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' definida com sucesso!");
            return true;
        } else if (command.getName().equalsIgnoreCase("home")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Uso correto: /home <Nome>");
                return true;
            }

            String homeName = args[0];
            if (homesConfig.contains(homeName)) {
                teleportToHome(player, homeName);
            } else {
                player.sendMessage(ChatColor.RED + "A home '" + homeName + "' não existe.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("delhome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Uso correto: /delhome <Nome>");
                return true;
            }

            String homeName = args[0];
            if (homesConfig.contains(homeName)) {
                homesConfig.set(homeName, null);
                saveHomesConfig();
                player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' removida com sucesso!");
            } else {
                player.sendMessage(ChatColor.RED + "A home '" + homeName + "' não existe.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("homes")) {
            listHomes(player);
            return true;
        }

        return false;
    }

    private void saveHomesConfig() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            getLogger().warning(ChatColor.RED + "Não foi possível salvar o arquivo de homes.");
        }
    }

    private void teleportToHome(Player player, String homeName) {
        String worldName = homesConfig.getString(homeName + ".world");
        double x = homesConfig.getDouble(homeName + ".x");
        double y = homesConfig.getDouble(homeName + ".y");
        double z = homesConfig.getDouble(homeName + ".z");
        float yaw = (float) homesConfig.getDouble(homeName + ".yaw");
        float pitch = (float) homesConfig.getDouble(homeName + ".pitch");

        player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
        player.sendMessage(ChatColor.GREEN + "Teleportado para home '" + homeName + "'.");
    }

    private void listHomes(Player player) {
        Set<String> homeNames = homesConfig.getKeys(false);
        if (homeNames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Nenhuma home foi definida ainda.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Homes:");
            for (String homeName : homeNames) {
                player.sendMessage(ChatColor.YELLOW + "- " + homeName);
            }
        }
    }
}
