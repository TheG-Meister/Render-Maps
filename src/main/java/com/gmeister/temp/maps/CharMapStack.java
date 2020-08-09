package com.gmeister.temp.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmeister.temp.pipe.time.TimeData;
import com.gmeister.temp.pipe.time.TimeManager;
import com.gmeister.temp.pipe.time.TimeSink;

/**
 * Provides utilities for vertically stacking RenderedCharMaps.
 *
 * @author The_G_Meister
 */

public class CharMapStack extends RenderedCharMap implements TimeManager
{
	
	private List<RenderedCharMap> maps;
	private List<ReferencePoint> offsets;
	private int[][] contourMap;
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public CharMapStack(int xCapacity, int yCapacity)
	{ this(xCapacity, yCapacity, 10); }
	
	public CharMapStack(int xCapacity, int yCapacity, int size)
	{
		super(new CharMap(xCapacity, yCapacity, ' ', false));
		
		this.maps = new ArrayList<>(size);
		this.offsets = new ArrayList<>(size);
		this.contourMap = new int[yCapacity][xCapacity];
	}
	
	public CharMapStack(int xCapacity, int yCapacity, RenderedCharMap... maps)
	{
		this(xCapacity, yCapacity, maps.length);
		this.addMaps(maps);
	}
	
	// ------------------------------------------------ GENERAL METHODS
	// ------------------------------------------------ //
	
	public List<RenderedCharMap> getMaps()
	{ return this.maps; }
	
	/**
	 * Removes {@code null} elements from the list of maps, also removing
	 * corresponding {@link ReferencePoint}s from the list of offsets.
	 * 
	 * @return the change in size of the list
	 */
	public int removeNulls()
	{
		int oldLength = this.maps.size();
		for (int i = 0; i < this.maps.size();) if (this.maps.get(i) == null)
		{
			this.maps.remove(i);
			if (i < this.offsets.size()) this.offsets.remove(i);
		}
		else i++;
		return this.maps.size() - oldLength;
	}
	
	/**
	 * Adds {@link RenderedCharMap}s to the end of the list of maps by calling {@link #addMapsAt(int, RenderedCharMap...)}.
	 * @param maps a list of maps to add
	 * @return this object
	 */
	public CharMapStack addMaps(RenderedCharMap... maps)
	{
		this.addMapsAt(this.maps.size(), maps);
		return this;
	}
	
	public void addMapsBefore(RenderedCharMap map, RenderedCharMap... maps)
	{
		if (!this.maps.contains(map))
			throw new IllegalArgumentException("Map list does not contain this RenderedCharMap");
		this.addMapsAt(this.maps.indexOf(map), maps);
	}
	
	public void addMapsAfter(RenderedCharMap map, RenderedCharMap... maps)
	{
		if (!this.maps.contains(map))
			throw new IllegalArgumentException("Map list does not contain this RenderedCharMap");
		this.addMapsAt(this.maps.indexOf(map) + 1, maps);
	}
	
	public void addMapsAt(int index, RenderedCharMap... maps)
	{
		if (index > this.offsets.size()) throw new IndexOutOfBoundsException("Index is greater than map list size");
		if (this.offsets.size() <= index)
			for (int i = 0; i < maps.length; i++) this.offsets.add(index, new ReferencePoint());
		this.maps.addAll(index, Arrays.asList(maps));
	}
	
	public void replaceMap(RenderedCharMap map, RenderedCharMap replacement)
	{
		if (!this.maps.contains(map))
			throw new IllegalArgumentException("Map list does not contain this RenderedCharMap");
		this.maps.set(this.maps.indexOf(map), replacement);
	}
	
	public ReferencePoint getOffsetOf(RenderedCharMap map)
	{
		if (!this.maps.contains(map))
			throw new IllegalArgumentException("Map list does not contain this RenderedCharMap");
		return this.offsets.get(this.maps.indexOf(map));
	}
	
	public void setOffsetOf(RenderedCharMap map, ReferencePoint offset)
	{
		if (!this.maps.contains(map))
			throw new IllegalArgumentException("Map list does not contain this RenderedCharMap");
		int index = this.maps.indexOf(map);
		if (index >= this.offsets.size())
		{
			this.trimOffsets(index);
			this.offsets.add(offset);
		}
		else this.offsets.set(index, offset);
	}
	
	public List<ReferencePoint> getOffsets()
	{ return this.offsets; }
	
	/**
	 * Makes the list of offsets equal in length to the list of maps by modifying
	 * the elements in the list of offsets. Runs {@link #trimOffsets(int)}
	 * 
	 * @return the change in size of the list
	 */
	public int trimOffsets()
	{
		return this.trimOffsets(this.maps.size());
	}
	
	/**
	 * Makes the length of the list of offsets equal to {@code length} by modifying
	 * the elements in the list of offsets. {@link ReferencePoint}s are removed if
	 * the list is shorter, whereas (0,0) {@link ReferencePoint}s are added if the
	 * list is longer.
	 * 
	 * @param length the new length to trim to
	 * @return the change in size of the list
	 */
	public int trimOffsets(int length)
	{
		if (this.offsets.size() == length) return 0;
		int oldSize = this.offsets.size();
		while (this.offsets.size() > length) this.offsets.remove(this.offsets.size() - 1);
		while (this.offsets.size() < length) this.offsets.add(new ReferencePoint());
		return this.offsets.size() - oldSize;
	}
	
	@Override
	public void setTo(CharMap c)
	{
		super.setTo(c);
		this.contourMap = new int[c.getYCapacity()][c.getXCapacity()];
	}
	
	public void setTo(CharMapStack c)
	{
		super.setTo(c);
		this.maps = c.maps;
		this.offsets = c.offsets;
		this.contourMap = c.contourMap;
	}
	
	public void update()
	{
		for (int y = 0; y < this.getYCapacity(); y++) Arrays.fill(this.contourMap[y], 0);
		this.fill(' ');
		this.getAlphaMap().fill(false);
		
		for (int z = this.maps.size() - 1; z > -1; z--)
		{
			RenderedCharMap c = this.maps.get(z);
			if (c != null)
			{
				ReferencePoint layerExt;
				if (z >= this.offsets.size()) layerExt = new ReferencePoint();
				else layerExt = this.offsets.get(z);
				ReferencePoint layerInt = c.getOffset();
				ReferencePoint alphaExt = c.getAlphaOffset();
				ReferencePoint alphaInt = c.getAlphaMap().getOffset();
				BooleanMap intersection = BooleanMap.intersectionOf(this.getAlphaMap(), c.getAlphaMap(),
						new ReferencePoint(layerExt.getX() + alphaExt.getX(), layerExt.getY() + alphaExt.getY()));
				int tempY = intersection.getOffset().getY() + this.getAlphaOffset().getY() - layerExt.getY();
				int tempX = intersection.getOffset().getX() + this.getAlphaOffset().getX() - layerExt.getX();
				
				for (int y = 0, y2 = intersection.getOffset().getY() - this.getAlphaMap().getOffset().getY(), y3 = tempY - alphaExt.getY() - alphaInt.getY(), y4 = tempY - layerInt.getY();
						y < intersection.getYCapacity(); y++, y2++, y3++, y4++)
					for (int x = 0, x2 = intersection.getOffset().getX() - this.getAlphaMap().getOffset().getX(), x3 = tempX - alphaExt.getX() - alphaInt.getX(), x4 = tempX - layerInt.getX();
							x < intersection.getXCapacity(); x++, x2++, x3++, x4++)
						if (!this.getAlphaMap().getAt(x2, y2) && c.getAlphaMap().getMapAt(x3, y3))
				{
					this.getAlphaMap().setAt(x2, y2, true);
					if (this.contourMap[y2][x2] == 0) this.contourMap[y2][x2] = z;
					this.setMapAt(x2, y2, c.getMapAt(x4, y4));
				}
			}
		}
	}
	
	/**
	 * Empties the lists of {@link RenderedCharMap}s and {@link ReferencePoint}s
	 */
	public void clear()
	{
		this.maps.clear();
		this.offsets.clear();
		for (int y = 0; y < this.getYCapacity(); y++) Arrays.fill(this.contourMap[y], 0);
	}

	@Override
	public TimeData pourTime(TimeSink caller)
	{ return null; }
	
}