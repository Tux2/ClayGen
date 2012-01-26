package Tux2.ClayGen;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class ClayUpdate implements Runnable{
	
	ClayGen plugin;

	public ClayUpdate(ClayGen plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void run() {
		try {
			if(plugin.doneblocks.size() > 0) {
				LinkedList<Block> theblocks = (LinkedList<Block>)plugin.doneblocks.clone();
				for(Block theblock : theblocks) {
					theblock.setType(Material.CLAY);
					plugin.doneblocks.remove(theblock);
				}
			}
		}catch (ConcurrentModificationException e) {
			System.out.println("[ClayGen] Uhoh, this should never happen, please tell Tux2 that you met with an concurrent modification exception!");
		}
		
	}
	
}
