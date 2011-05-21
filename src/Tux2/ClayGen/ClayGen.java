/*
 * Copyright (C) 2011  Joshua Reetz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Tux2.ClayGen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import net.minecraft.server.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * ClayGen for Bukkit
 *
 * @author Tux2
 */
public class ClayGen extends JavaPlugin implements Runnable {
    private final ClayGenPlayerListener playerListener = new ClayGenPlayerListener(this);
    private final ClayGenBlockListener blockListener = new ClayGenBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    final HashMap<String, ClayDelay> clayblocks = new HashMap<String, ClayDelay>();
    private String blockSaveFile = "plugins/ClayGen/claygen.blocks";
    private String claySaveFile = "plugins/ClayGen/claygen.clay";
    private Thread dispatchThread;
    private int activateblock = 45;
    public static final int CLAY = 82;
    public static final int GRAVEL = 13;
    public static final int WATER = 9;
    public static final int FLOWINGWATER = 8;
    public static final int LAVA = 11;
    public static final int FLOWINGLAVA = 10;
    boolean debug = false;
    boolean mcmmomode = false;
    boolean lavaenabled = true;
    boolean waterenabled = true;
    boolean clayfarm = false;
    boolean savefarm = false;
    boolean customdrops = false;
    int maxclay = 6;
    int minclay = 1;
    int defaultclaydrop = 4;
    //Set delay for the max amount of clay to 2 minutes.
    long timeformaxclay = 2*60*1000;
    int farmdelay = 5;
    int maxfarmdelay = 12;
    String version = "0.7";
    LinkedList<ClayDelay> gravellist = new LinkedList<ClayDelay>();
    LinkedList<Block> ingravel = new LinkedList<Block>();
    Random generator = new Random();
    BlockFace[] waterblocks = {BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH,
			BlockFace.EAST, BlockFace.WEST};

    public ClayGen(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super();
        loadconfig();

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }
    
    public ClayGen() {
    	super();
    	loadconfig();
    }
    

   

    public synchronized void onEnable() {

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal, this);
        //Only initialize this if the clay farm is enabled... otherwise we generate a lot
        //of unnesecary events.
        if(clayfarm) {
        	pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        	loadBlocks();
            dispatchThread = new Thread(this);
            dispatchThread.start();
        }
        //only initialize the following two if custom amount of drops is enabled...
        if(customdrops) {
        	loadClayBlocks();
        	pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        	pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
        }else if(defaultclaydrop != 4 && defaultclaydrop > 0) {
        	//let's activate this if we are changing the clay drop amount
        	pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        }
       

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public synchronized void onDisable() {
    	if(savefarm && clayfarm) {
    		saveBlocks();
    	}

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("ClayGen is disabled!");
    }
    public synchronized boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }



	public synchronized int getActivationBlock() {
		return activateblock;
	}
	
	private void loadconfig() {
		File folder = new File("plugins/ClayGen");

		// check for existing file
		File configFile = new File("plugins/ClayGen/claygen.ini");
		
		//if it exists, let's read it, if it doesn't, let's create it.
		if (configFile.exists()) {
			try {
				Properties reminderSettings = new Properties();
				reminderSettings.load(new FileInputStream(new File("plugins/ClayGen/claygen.ini")));
		        
		        String activatorblock = reminderSettings.getProperty("activatorblock", "87");
		        String needactivator = reminderSettings.getProperty("needactivator", "true");
		        String wateractivator = reminderSettings.getProperty("wateractivated", "true");
		        String lavaactivator = reminderSettings.getProperty("lavaactivated", "true");
		        String defaultamount = reminderSettings.getProperty("defaultdropamount", "4");
		        String afarm = reminderSettings.getProperty("clayfarm", "false");
		        String fdelay = reminderSettings.getProperty("farmdelay", "5");
		        String mfdelay = reminderSettings.getProperty("maxdelay", "12");
		        String sfarm = reminderSettings.getProperty("savefarm", "false");
		        String cdrops = reminderSettings.getProperty("customdrops", "false");
		        String smaxclay = reminderSettings.getProperty("maxclay", "6");
		        String sminclay = reminderSettings.getProperty("minclay", "1");
		        String sclaytime = reminderSettings.getProperty("timeformaxclay", "12");
		        //If the version isn't set, the file must be at 0.2
		        String theversion = reminderSettings.getProperty("version", "0.2");
			    try {
			    	farmdelay = Integer.parseInt(fdelay.trim());
			    } catch (Exception ex) {
			    	
			    }
			    mcmmomode = !stringToBool(needactivator);
			    waterenabled = stringToBool(wateractivator);
			    lavaenabled = stringToBool(lavaactivator);
			    clayfarm = stringToBool(afarm);
			    savefarm = stringToBool(sfarm);
			    customdrops = stringToBool(cdrops);
		        try {
			    	activateblock = Integer.parseInt(activatorblock.trim());
			    	if(!mcmmomode) {
			    		System.out.println("ClayGen: Setting activation block to: " + activatorblock.trim());
			    	}
			    } catch (Exception ex) {
			    	
			    }
			    //Let's see if we need to upgrade the config file
			    double dbversion = 0.2;
			    try {
			    	dbversion = Double.parseDouble(theversion.trim());
			    } catch (Exception ex) {
			    	
			    }

			    try {
			    	defaultclaydrop = Integer.parseInt(defaultamount.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	maxclay = Integer.parseInt(smaxclay.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	minclay = Integer.parseInt(sminclay.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	timeformaxclay = Integer.parseInt(sclaytime.trim())*10*1000;
			    } catch (Exception ex) {
			    	
			    }

			    try {
			    	maxfarmdelay = Integer.parseInt(mfdelay.trim());
			    } catch (Exception ex) {
			    	
			    }
			    if(dbversion < 0.7) {
			    	updateIni();
			    }
			} catch (IOException e) {
				
			}
		}else {
			System.out.println("ClayGen: Configuration file not found");

			System.out.println("ClayGen: + creating folder plugins/ClayGen");
			folder.mkdir();

			System.out.println("ClayGen: - creating file claygen.ini");
			updateIni();
		}
	}

	private void updateIni() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/ClayGen/claygen.ini"));
			outChannel.write("#This is the main claygen config file\n" +
					"#The ActivatorBlock is the ID of the block needed\n" +
					"#under the gravel to make it into clay. Default is a Brick Block\n" +
					"#If needactivator is set to false, then any gravel\n" +
					"#block that comes in contact with flowing water\n" +
					"#gets converted into clay.\n" +
					"activatorblock = " + activateblock + "\n" +
					"needactivator = " + !mcmmomode + "\n" +
					"#Set whether water flow will trigger the change\n" +
					"wateractivated = " + waterenabled + "\n" +
					"#Set whether lava flow will trigger the change\n" +
					"lavaactivated = " + lavaenabled + "\n\n" +
					"#defaultdropamount changes the default amount of clay dropped when a block is broken\n" +
					"defaultdropamount = " + defaultclaydrop + "\n\n" +
					"#Clayfarm sets it so that the gravel turns into clay after a delay\n" +
					"clayfarm = " + clayfarm + "\n" +
					"#farmdelay sets the minimum delay in 10 second intervals. 5 = 50 seconds.\n" +
					"farmdelay = " + farmdelay + "\n" +
					"#maxdelay sets the max delay in 10 second intervals. 12 = 120 seconds.\n" +
					"maxdelay = " + maxfarmdelay + "\n" +
					"#savefarm saves all the blocks currently being farmed for clay. (Otherwise\n" +
					"#you will have to replace the gravel blocks or re-run the water after server reboot.)\n" +
					"#Only useful if you have a large (20min+) delay for the gravel turning into clay.\n" +
					"savefarm = " + savefarm + "\n" +
					"\n" +
					"#The following lines let you set a custom number of clay drops based on the amount\n" +
					"#of time that water has been running over them.\n" +
					"#customdrops, if true, enables the custom drops\n" +
					"customdrops = " + customdrops + "\n" +
					"#maxclay sets the maximum amount of clay a block can give\n" +
					"maxclay = " + maxclay + "\n" +
					"#minclay, sets the minimum amount of clay a block can give (must be more than 0!)\n" +
					"minclay = " + minclay + "\n" +
					"#timeformaxclay, sets how long the player must wait for the max amount of clay\n" +
					"# in 10 second intervals. 12 = 120 seconds.\n" +
					"timeformaxclay = " + (timeformaxclay/10/1000) + "\n" +
					"#Do not change anything below this line unless you know what you are doing!\n" +
					"version = " + version);
			outChannel.close();
		} catch (Exception e) {
			System.out.println("ClayGen: - file creation failed, using defaults.");
		}
		
	}

	public synchronized void convertBlocks(Block thewaterblock) {
		int waterid = thewaterblock.getTypeId();
		if((waterenabled && (waterid == 8 || waterid == 9)) || (lavaenabled && (waterid == 10 || waterid == 11))) {
			Block thegravelblocks[] = {thewaterblock.getFace(BlockFace.DOWN),
					thewaterblock.getFace(BlockFace.NORTH),
					thewaterblock.getFace(BlockFace.SOUTH),
					thewaterblock.getFace(BlockFace.EAST),
					thewaterblock.getFace(BlockFace.WEST)};
			if(debug) {
	    		System.out.println("Water is flowing...");
	    	}
			for(int i = 0; i < thegravelblocks.length; i++) {

		    	if(thegravelblocks[i].getTypeId() == GRAVEL) {
		        	if(debug) {
		        		System.out.println("We have a gravel block here!");
		        	}
		    		//Block activatorblock = thegravelblocks[i].getFace(BlockFace.DOWN);
		    		if(mcmmomode || getActivationBlock() == thegravelblocks[i].getFace(BlockFace.DOWN).getTypeId()) {
		    	    	if(debug) {
		    	    		System.out.println("Found the right activator, converting to clay.");
		    	    	}
		    	    	//If we have clayfarm mode enabled, let's add the blocks to the queue.
		    	    	if (clayfarm) {
		    	    		if(!ingravel.contains(thegravelblocks[i])) {
			            		ingravel.add(thegravelblocks[i]);
			            		gravellist.add(new ClayDelay(thegravelblocks[i]));
			            		//Only start the thread if nothing is waiting...
			            		//otherwise gravel placed really fast will trigger it
			            		//to update too fast.
			            		if(ingravel.size() <= 1) {
				                    dispatchThread.interrupt();
			            		}
		    	    		}
		    	    	//Otherwise, let's convert!
		    	    	}else {
			    	    	thegravelblocks[i].setTypeId(CLAY);
			    	    	//if custom amount of drops is enabled, let's add them to the queue
			    	    	if(customdrops) {
			            		clayblocks.put(compileBlockString(thegravelblocks[i]), new ClayDelay(thegravelblocks[i]));
			            		saveClayBlocks();
			            	}
		    	    	}
		    		}
		    		
		    	}
			}
		}
		
	}
	private synchronized boolean stringToBool(String thebool) {
		boolean result;
		if (thebool.trim().equalsIgnoreCase("true") || thebool.trim().equalsIgnoreCase("yes")) {
	    	result = true;
	    } else {
	    	result = false;
	    }
		return result;
	}

	@Override
	public synchronized void run() {
		boolean running = true;
		int savedelay = 0;
        while (running) {

            // If the list is empty, wait until something gets added.
            if (gravellist.size() == 0) {
            	if(debug) {
                	System.out.println("Claygen: No more gravel to process!");
            	}
            	//Let's update the list if there is no more gravel to process.
            	if(savefarm) {
                	saveBlocks();
                	savedelay = 0;
            	}
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    // Do nothing.
                }
                
            }
            //Wait 10 seconds between each iteration.
            try {
				wait(10000);
			} catch (InterruptedException e) {
				
			}
			for(int i = 0; i < gravellist.size(); i++) {
	            ClayDelay blockupdate = (ClayDelay) gravellist.get(generator.nextInt(gravellist.size()));
	            //If they took away the gravel, or activator, let's not keep it in here...
	            if(blockupdate.getBlock().getTypeId() == GRAVEL && 
	            		(mcmmomode || blockupdate.getBlock().getFace(BlockFace.DOWN).getTypeId() == activateblock)) {
		            //let's see if water farming is enabled.
		            boolean alreadyupdated = false;
		            if(waterenabled && lavaenabled) {
		            	//See if there is a water block next to it...
		            	if(hasBlockNextTo(blockupdate.getBlock(), FLOWINGWATER, WATER)) {
		            		blockupdate.upDelay(generator.nextInt(2));
			            	if(debug) {
			            		System.out.println("upped the int to: " + blockupdate.getDelay());
			            	}
		            		alreadyupdated = true;
			            }
		            }else if(waterenabled) {
		            	//See if there is a water block next to it...
		            	if(hasBlockNextTo(blockupdate.getBlock(), FLOWINGWATER, WATER)) {
		            		blockupdate.upDelay(generator.nextInt(2));
			            	if(debug) {
			            		System.out.println("upped the int to: " + blockupdate.getDelay());
			            	}
		            		alreadyupdated = true;
			            }
		            	//don't want it to happen twice as fast when there is lava...
		            }else if(lavaenabled) {
		            	//See if there is a lava block next to it...
		            	if(hasBlockNextTo(blockupdate.getBlock(), FLOWINGLAVA, LAVA)) {
			            	blockupdate.upDelay(generator.nextInt(2));
			            	if(debug) {
			            		System.out.println("upped the int to: " + blockupdate.getDelay());
			            	}
			            	alreadyupdated = true;
			            }
		            }
		            if(alreadyupdated == false) {
		            	if(debug) {
		            		System.out.println("Whoops! no more flow! Removing...");
		            	}
		            	//Remove the block, it's been updated!
		            	gravellist.remove(blockupdate);
		            	ingravel.remove(blockupdate.getBlock());
		            	//Set the pointer to the right location.
		            	i--;
		            }else if(blockupdate.getDelay() >= farmdelay || (blockupdate.getInTime()+(10000*maxfarmdelay)) <= System.currentTimeMillis()) {
		            	//Remove the block, it's been updated!
		            	gravellist.remove(blockupdate);
		            	ingravel.remove(blockupdate.getBlock());
		            	blockupdate.getBlock().setTypeId(CLAY);
		            	if(customdrops) {
		            		blockupdate.resetTimeIn();
		            		clayblocks.put(compileBlockString(blockupdate.getBlock()), blockupdate);
		            		saveClayBlocks();
		            	}
		            	//Set the pointer to the right location.
		            	i--;
		            	if(debug) {
		            		System.out.println("We now have " + gravellist.size() + " in the queue");
		            	}
		            } 
		            //Remove that non-gravel block
	            }else {
	            	gravellist.remove(blockupdate);
	            	ingravel.remove(blockupdate.getBlock());
	            	if(debug) {
	            		System.out.println("We now have " + gravellist.size() + " in the queue");
	            	}
	            }
				
			}
			savedelay++;
			if(savefarm && savedelay >= 10) {
				saveBlocks();
				savedelay = 0;
			}
            
        }
		
	}
	public synchronized boolean hasBlockNextTo(Block theblock, int blockid) {
		for (int i = 0; i < waterblocks.length; i++) {
			if(theblock.getFace(waterblocks[i]).getTypeId() == blockid) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean hasBlockNextTo(Block theblock, int blockid, int blockid2) {
		for (int i = 0; i < waterblocks.length; i++) {
			int id = theblock.getFace(waterblocks[i]).getTypeId();
			if(id == blockid || id == blockid2) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean hasBlockNextTo(Block theblock, int blockid, int blockid2, int blockid3, int blockid4) {
		for (int i = 0; i < waterblocks.length; i++) {
			int id = theblock.getFace(waterblocks[i]).getTypeId();
			if(id == blockid || id == blockid2 || id == blockid3 || id == blockid4) {
				return true;
			}
		}
		return false;
	}

	public synchronized void gravelPlaced(Block thegravelblock) {
		if(thegravelblock.getTypeId() == GRAVEL && (mcmmomode || thegravelblock.getFace(BlockFace.DOWN).getTypeId() == activateblock)) {
            //let's make sure we aren't adding duplicates...
			if(!ingravel.contains(thegravelblock)) {
				//let's see if water farming is enabled.
	            boolean alreadyupdated = false;
	            if(waterenabled) {
	            	//See if there is a water block next to it...
	            	if(hasBlockNextTo(thegravelblock, WATER, FLOWINGWATER)) {
	            		ingravel.add(thegravelblock);
	            		gravellist.add(new ClayDelay(thegravelblock));
	            		//Only start the thread if nothing is waiting...
	            		//otherwise gravel placed really fast will trigger it
	            		//to update too fast.
	            		if(ingravel.size() <= 1) {
		                    dispatchThread.interrupt();
	            		}
	            		alreadyupdated = true;
		            }
	            	//don't want it to add it twice when there is lava...
	            }if(lavaenabled && !alreadyupdated) {
	            	//See if there is a lava block next to it...
	            	if(hasBlockNextTo(thegravelblock, LAVA, FLOWINGLAVA)) {
	            		ingravel.add(thegravelblock);
	            		gravellist.add(new ClayDelay(thegravelblock));
	            		//Only start the thread if nothing is waiting...
	            		//otherwise gravel placed really fast will trigger it
	            		//to update too fast.
	            		if(ingravel.size() <= 1) {
		                    dispatchThread.interrupt();
	            		}
		            }
	            }
			}
        }
		
	}
	
	private void saveBlocks() {
		LinkedList<SaveBlock> glocations = new LinkedList<SaveBlock>();
		for(ClayDelay theblock : gravellist) {
			glocations.add(new SaveBlock(theblock));
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(blockSaveFile)));
			out.writeObject(glocations);
			out.flush();
			out.close();
		}
		catch (Exception e) {
			if(debug) {
				System.out.println("Couldn't write blocks!");
				System.out.println(e);
			}
		}
	}
	
	private void loadBlocks() {
		try {
			ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(blockSaveFile)));
			if(debug) {
				System.out.println("ClayGen: Reading file.");
			}
			LinkedList<SaveBlock> glocations = (LinkedList<SaveBlock>) out.readObject();
			if(debug) {
				System.out.println("ClayGen: Turning locations into Blocks");
			}
			gravellist.clear();
			ingravel.clear();
			for(SaveBlock blocklocation : glocations) {
				org.bukkit.World theworld = getServer().getWorld(blocklocation.getWorld());
				Location lblock = new Location(theworld, blocklocation.getX(), 
						blocklocation.getY(), blocklocation.getZ());
				Block theblock = lblock.getBlock();
				gravellist.add(new ClayDelay(theblock, blocklocation.getDelayvalue(), blocklocation.getIntime()));
				ingravel.add(theblock);
			}
			if(debug) {
				System.out.println("ClayGen: Finished reading file!");
			}
		}
	catch (Exception e) {
		// If it doesn't work, no great loss!
		if(debug) {
			System.out.println("Couldn't read save file!");
			System.out.println(e);
		}
	}
		}
		
		void saveClayBlocks() {
			LinkedList<SaveBlock> glocations = new LinkedList<SaveBlock>();
			Collection cblock = clayblocks.values();
			for(Object oblock : cblock) {
				ClayDelay theblock = (ClayDelay)oblock;
				glocations.add(new SaveBlock(theblock));
			}
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(claySaveFile)));
				out.writeObject(glocations);
				out.flush();
				out.close();
			}
			catch (Exception e) {
				if(debug) {
					System.out.println("Couldn't write blocks!");
					System.out.println(e);
				}
			}
		}
		
		private void loadClayBlocks() {
			try {
				ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(claySaveFile)));
				if(debug) {
					System.out.println("ClayGen: Reading clay file.");
				}
				LinkedList<SaveBlock> glocations = (LinkedList<SaveBlock>) out.readObject();
				if(debug) {
					System.out.println("ClayGen: Turning locations into clay Blocks");
				}
				clayblocks.clear();
				for(SaveBlock blocklocation : glocations) {
					org.bukkit.World theworld = getServer().getWorld(blocklocation.getWorld());
					Location lblock = new Location(theworld, blocklocation.getX(), 
							blocklocation.getY(), blocklocation.getZ());
					Block theblock = lblock.getBlock();
					clayblocks.put(compileBlockString(theblock), new ClayDelay(theblock, blocklocation.getDelayvalue(), blocklocation.getIntime()));
					if(debug) {
						System.out.println("ClayGen: Reading block at: " + compileBlockString(theblock));
					}
				}
				if(debug) {
					System.out.println("ClayGen: Finished reading clay file!");
				}
			}
		catch (Exception e) {
			// If it doesn't work, no great loss!
			if(debug) {
				System.out.println("Couldn't read clay save file!");
				System.out.println(e);
			}
		}
	}

	public void clayWaterRemoved(Block block) {
		// Let's see if it's one of the clay blocks we are tracking...
		if(clayblocks.containsKey(compileBlockString(block))) {
			ClayDelay theblock = clayblocks.get(compileBlockString(block));
			block.setData((byte) getNumberOfDrops(theblock));
			clayblocks.remove(compileBlockString(block));
			saveClayBlocks();
		}
	}
	public String compileBlockString(Block block) {
		return block.getWorld().getName() + "." + block.getX() + "." + block.getY() + "." + block.getZ();
	}
	
	int getNumberOfDrops(ClayDelay theblock) {
		long timeelapsed = System.currentTimeMillis() - theblock.getInTime();
		long timefor1drop = timeformaxclay/(maxclay-minclay);
		long numberofdrops = (timeelapsed/timefor1drop) + minclay;
		if(numberofdrops > maxclay) {
			numberofdrops = maxclay;
		}
		return (int) numberofdrops;
	}
}

