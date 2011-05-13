package Tux2.ClayGen;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author Tux2
 */
public class ClayGenPlayerListener extends PlayerListener {
    private final ClayGen plugin;

    public ClayGenPlayerListener(ClayGen instance) {
        plugin = instance;
    }

    //Insert Player related code here
}

