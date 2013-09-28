package Tux2.ClayGen;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ClayBreak implements Listener {
	
	ClayGen plugin;
	
	public ClayBreak(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak (BlockBreakEvent event) {
		if(event.isCancelled()) {
			return;
		}
		if(event.getBlock().getType() == Material.CLAY) {
			Block clayblock = event.getBlock();
			int claycount = 0;
			if(plugin.customdrops) {
				if(plugin.clayblocks.containsKey(plugin.compileBlockString(clayblock))) {
	    			ClayDelay theblock = plugin.clayblocks.get(plugin.compileBlockString(clayblock));
	    			claycount = plugin.getNumberOfDrops(theblock);
	    			plugin.clayblocks.remove(plugin.compileBlockString(clayblock));
	    			plugin.saveClayBlocks();
	    		}else {
	    			if(clayblock.hasMetadata("ClayDrops")) {
		    			claycount = clayblock.getMetadata("ClayDrops").get(0).asByte();
	    			}
	        		//claycount = clayblock.getData();
	    		}
			}else {
				claycount = plugin.defaultclaydrop;
			}
			
			//If the clay count is zero, this must be a world generated block.
			//Set it to the default amount of clay.
			if(claycount == 0) {
				claycount = plugin.defaultclaydrop;
			}
			//drop the items
			for(int i = 0; i < claycount; i++) {
	    		clayblock.getWorld().dropItemNaturally(clayblock.getLocation(), new ItemStack(Material.CLAY_BALL, 1));
			}
			//turn the block to air, disabling further drops.
			if(clayblock.hasMetadata("ClayDrops")) {
    			clayblock.removeMetadata("ClayDrops", plugin);
			}
			clayblock.setType(Material.AIR);
		}
	}

}
