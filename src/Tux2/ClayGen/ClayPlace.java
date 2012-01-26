package Tux2.ClayGen;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ClayPlace implements Listener {
	
	ClayGen plugin;
	public static final int GRAVEL = 13;
	
	public ClayPlace(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace (BlockPlaceEvent event) {
		Block thegravelblock = event.getBlock();
		if(thegravelblock.getTypeId() == GRAVEL) {
			plugin.gravelPlaced(thegravelblock);
		}
	}
	
	

}
