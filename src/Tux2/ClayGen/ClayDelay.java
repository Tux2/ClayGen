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

import java.io.Serializable;

import org.bukkit.block.Block;

public class ClayDelay implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2067069668818978673L;
	Block theblock;
	int delayvalue = 0;
	long intime;
	
	public void setIntime(long intime) {
		this.intime = intime;
	}
	ClayDelay(Block yourblock) {
		theblock = yourblock;
		intime = System.currentTimeMillis();
	}
	ClayDelay(Block yourblock, int indelay, long timeelapsed) {
		theblock = yourblock;
		delayvalue = indelay;
		intime = System.currentTimeMillis() - timeelapsed;
	}
	public void upDelay() {
		delayvalue++;
	}
	public void upDelay(int amount) {
		delayvalue += amount;
	}
	public int getDelay() {
		return delayvalue;
	}
	public void setDelay(int delay) {
		delayvalue = delay;
	}
	public void resetTimeIn() {
		intime = System.currentTimeMillis();
	}
	public Block getBlock() {
		return theblock;
	}
	public long getInTime() {
		return intime;
	}

}
