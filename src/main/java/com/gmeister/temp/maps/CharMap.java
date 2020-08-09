package com.gmeister.temp.maps;

import java.util.Arrays;

import com.gmeister.temp.pipe.render.textmap.TextArray;
import com.gmeister.temp.pipe.render.textmap.TextSource;

public class CharMap implements TextSource
{
	
	private int xCapacity;
	private int yCapacity;
	private char[][] map;
	private BooleanMap alpha;
	
	private ReferencePoint mapOffset;
	private ReferencePoint alphaOffset;
	
	// ------------------------------------------------ CONTRUCTORS
	// ------------------------------------------------ //
	
	public CharMap()
	{
		this.xCapacity = 0;
		this.yCapacity = 0;
		this.map = new char[0][0];
		this.alpha = new BooleanMap();
		
		this.mapOffset = new ReferencePoint();
		this.alphaOffset = new ReferencePoint();
	}
	
	public CharMap(int xCapacity, int yCapacity, char c, boolean b)
	{
		this.setMap(xCapacity, yCapacity, c);
		this.alpha = new BooleanMap(xCapacity, yCapacity, b);
		this.mapOffset = new ReferencePoint();
		this.alphaOffset = new ReferencePoint();
	}
	
	public CharMap(int xCapacity, int yCapacity, String s)
	{ this(xCapacity, yCapacity, s, new BooleanMap(xCapacity, yCapacity, true)); }
	
	public CharMap(int xCapacity, int yCapacity, String s, BooleanMap b)
	{
		if (s == null || b == null) throw new IllegalArgumentException();
		this.setMap(xCapacity, yCapacity, s);
		this.alpha = b;
		this.mapOffset = new ReferencePoint();
		this.alphaOffset = new ReferencePoint();
	}
	
	public CharMap(int xCapacity, int yCapacity, char[] c, BooleanMap b)
	{
		if (b == null) throw new IllegalArgumentException();
		this.setMap(xCapacity, yCapacity, c);
		this.alpha = b;
		this.mapOffset = new ReferencePoint();
		this.alphaOffset = new ReferencePoint();
	}
	
	// ------------------------------------------------ MAP REFACTORING METHODS
	// ------------------------------------------------ //
	
	public void setMap(int xCapacity, int yCapacity, char c)
	{
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
		this.map = new char[this.yCapacity][xCapacity];
		this.fill(c);
	}
	
	public void setMap(int xCapacity, int yCapacity, String s)
	{ this.setMap(xCapacity, yCapacity, s.toCharArray()); }
	
	public void setMap(int xCapacity, int yCapacity, char c[])
	{
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
		this.map = new char[this.yCapacity][];
		
		int l = (int) Math.floor(c.length / this.xCapacity);
		int y = 0;
		for (; y < l && y < this.yCapacity; y++)
			this.map[y] = Arrays.copyOfRange(c, y * this.xCapacity, (y + 1) * this.xCapacity);
		int i = y * this.xCapacity;
		for (; y < this.yCapacity; y++)
		{
			this.map[y] = new char[this.xCapacity];
			for (int x = 0; x < this.xCapacity; x++, i++) if (i < c.length) this.map[y][x] = c[i];
			else this.map[y][x] = ' ';
		}
	}
	
	public void setMap(char[][] c)
	{
		for (int i = 1; i < this.yCapacity; i++)
			if (c[i].length != this.xCapacity) throw new IllegalArgumentException("Inner char[] length is not uniform");
		
		this.xCapacity = c[0].length;
		this.yCapacity = c.length;
		this.map = c;
	}
	
	public void fill(char c)
	{ for (int y = 0; y < this.yCapacity; y++) Arrays.fill(this.map[y], c); }
	
	// ------------------------------------------------ OBJECT REPLACEMENT METHODS
	// ------------------------------------------------ //
	
	public void copyFrom(CharMap c)
	{
		this.xCapacity = c.xCapacity;
		this.yCapacity = c.yCapacity;
		this.mapOffset = c.mapOffset;
		this.map = new char[this.yCapacity][];
		for (int y = 0; y < this.yCapacity; y++) this.map[y] = Arrays.copyOf(c.map[y], this.xCapacity);
		
		this.alphaOffset = c.alphaOffset;
		this.alpha = BooleanMap.copOf(c.alpha);
	}
	
	public void setTo(CharMap c)
	{
		this.xCapacity = c.xCapacity;
		this.yCapacity = c.yCapacity;
		this.mapOffset = c.mapOffset;
		this.map = c.map;
		
		this.alphaOffset = c.alphaOffset;
		this.alpha = c.alpha;
	}
	
	// ------------------------------------------------ COPYING FACTORY
	// ------------------------------------------------ //
	
	public static CharMap convert(CharMap c)
	{
		CharMap output = new CharMap();
		output.setTo(c);
		return output;
	}
	
	public static CharMap copyOf(CharMap c)
	{
		CharMap output = new CharMap();
		output.copyFrom(c);
		return output;
	}
	
	// ------------------------------------------------ MAP RETRIEVAL AND
	// MODIFICATION METHODS ------------------------------------------------ //
	
	public void setAt(int x, int y, char c)
	{
		int x2 = x - this.mapOffset.getX();
		int y2 = y - this.mapOffset.getY();
		if (this.isWithinBoundsAt(x2, y2)) this.map[y2][x2] = c;
		else throw new MapOutOfBoundsException("CharMap does not contain coordinates " + x2 + ", " + y2);
	}
	
	public void setMapAt(int x, int y, char c)
	{
		if (this.isWithinMapAt(x, y)) this.map[y][x] = c;
		else throw new MapOutOfBoundsException("CharMap does not contain coordinates " + x + ", " + y);
	}
	
	public char[][] getMap()
	{ return this.map; }
	
	public char getAt(int x, int y)
	{
		int x2 = x - this.mapOffset.getX();
		int y2 = y - this.mapOffset.getY();
		if (this.alpha.getAt(x - this.alphaOffset.getX(), y - this.alphaOffset.getY()))
			if (this.isWithinBoundsAt(x2, y2)) return this.map[y2][x2];
			else throw new MapOutOfBoundsException("CharMap does not contain coordinates " + x2 + ", " + y2);
		else throw new NotRenderedException("CharMap is not rendered at " + x2 + ", " + y2);
	}
	
	public char getMapAt(int x, int y)
	{
		if (this.isWithinMapAt(x, y)) return this.map[y][x];
		else throw new MapOutOfBoundsException("CharMap does not contain coordinates " + x + ", " + y);
	}
	
	public boolean isWithinBoundsAt(int x, int y)
	{
		int x2 = x - this.mapOffset.getX();
		int y2 = y - this.mapOffset.getY();
		if (x2 < 0 || x2 >= this.xCapacity || y2 < 0 || y2 >= this.yCapacity) return false;
		else return true;
	}
	
	public boolean isWithinMapAt(int x, int y)
	{
		if (x < 0 || x >= this.xCapacity || y < 0 || y >= this.yCapacity) return false;
		else return true;
	}
	
	// ------------------------------------------------ CAPACITY METHODS
	// ------------------------------------------------ //
	
	public int getXCapacity()
	{ return this.xCapacity; }
	
	public int getYCapacity()
	{ return this.yCapacity; }
	
	public void setXCapacity(int xCapacity, int xOffset, char c)
	{
		char[][] tempMap = new char[this.yCapacity][xCapacity];
		
		for (int y = 0; y < this.yCapacity; y++) for (int x = 0, x2 = -xOffset; x < xCapacity; x++, x2++)
			if (x2 < 0 || x2 >= this.xCapacity) tempMap[y][x] = c;
			else tempMap[y][x] = this.map[y][x2];
			
		this.map = tempMap;
		this.xCapacity = xCapacity;
	}
	
	public void setYCapacity(int yCapacity, int yOffset, char c)
	{
		char[][] tempMap = new char[yCapacity][];
		
		for (int y = 0, y2 = -yOffset; y < yCapacity; y++, y2++) if (y2 < 0 || y2 >= this.yCapacity)
		{
			tempMap[y] = new char[this.xCapacity];
			Arrays.fill(tempMap[y], c);
		}
		else tempMap[y] = this.map[y2];
		
		this.map = tempMap;
		this.yCapacity = yCapacity;
	}
	
	public void setCapacity(int xCapacity, int yCapacity, ReferencePoint offset, char c)
	{
		char[][] tempMap = new char[yCapacity][xCapacity];
		
		for (int y = 0, y2 = -offset.getY(); y < yCapacity; y++, y2++)
			if (y < 0 || y2 >= this.yCapacity) Arrays.fill(tempMap[y], c);
			else for (int x = 0, x2 = -offset.getX(); x < xCapacity; x++, x2++)
				if (x2 < 0 || x2 >= this.xCapacity) tempMap[y][x] = c;
				else tempMap[y][x] = this.map[y2][x2];
				
		this.map = tempMap;
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
	}
	
	// ------------------------------------------------ BOOLEANMAP METHODS
	// ------------------------------------------------ //
	
	public void setBooleanMap(BooleanMap b)
	{ this.alpha = b; }
	
	public BooleanMap getAlphaMap()
	{ return this.alpha; }
	
	public void setAlphaOffset(ReferencePoint alphaOffset)
	{ this.alphaOffset = alphaOffset; }
	
	public ReferencePoint getAlphaOffset()
	{ return this.alphaOffset; }
	
	public void setAlphaAt(int x, int y, boolean b)
	{ this.alpha.setAt(x - this.alphaOffset.getX(), y - this.alphaOffset.getY(), b); }
	
	public void invertAlphaAt(int x, int y)
	{ this.alpha.invertAt(x - this.alphaOffset.getX(), y - this.alphaOffset.getY()); }
	
	public boolean getAlphaAt(int x, int y)
	{ return this.alpha.getAt(x - this.alphaOffset.getX(), y - this.alphaOffset.getY()); }
	
	public boolean isWithinAlphaBoundsAt(int x, int y)
	{ return this.alpha.isWithinBoundsAt(x - this.alphaOffset.getX(), y - this.alphaOffset.getY()); }
	
	// ------------------------------------------------ OFFSET METHODS
	// ------------------------------------------------ //
	
	public void setMapOffset(ReferencePoint mapOffset)
	{ this.mapOffset = mapOffset; }
	
	public ReferencePoint getOffset()
	{ return this.mapOffset; }
	
	// ------------------------------------------------ PRINTING METHODS
	// ------------------------------------------------ //
	
	@Override
	public String toString()
	{ return Arrays.deepToString(this.map); }
	
	public String toCleanString()
	{
		String s = "";
		for (int y = 0; y < this.yCapacity; y++) s += new String(this.map[y]);
		return s;
	}
	
	public String toCleanGridString()
	{
		String s = "";
		for (int y = 0; y < this.yCapacity; y++)
		{
			s += new String(this.map[y]);
			if (y != this.yCapacity - 1) s += System.lineSeparator();
		}
		return s;
	}
	
	public String toCleanAlphaString()
	{
		String s = "";
		for (int y = 0, y2 = -this.alphaOffset.getY(); y < this.alpha.getYCapacity(); y++, y2++)
			for (int x = 0, x2 = -this.alphaOffset.getX(); x < this.alpha.getXCapacity(); x++, x2++)
				if (this.alpha.getAt(x, y)) s += this.getMapAt(x2, y2);
				else s += ' ';
		return s;
	}
	
	public String toCleanAlphaGridString()
	{
		String s = "";
		for (int y = 0, y2 = -this.alphaOffset.getY(); y < this.alpha.getYCapacity(); y++, y2++)
		{
			for (int x = 0, x2 = -this.alphaOffset.getX(); x < this.alpha.getXCapacity(); x++, x2++)
				if (this.alpha.getAt(x, y)) s += this.getMapAt(x2, y2);
				else s += ' ';
			if (y != this.yCapacity - 1) s += System.lineSeparator();
		}
		return s;
	}
	
	// ------------------------------------------------ MERGING
	// ------------------------------------------------ //
	
	// Merge the map of the current object with the rendered characters in c.
	
	public void merge(CharMap c, ReferencePoint offset)
	{
		for (int y = 0, y2 = -offset.getY() - this.mapOffset.getY(); y < this.yCapacity; y++, y2++)
			for (int x = 0, x2 = -offset.getX() - this.mapOffset.getX(); x < this.xCapacity; x++, x2++)
				if (c.isWithinAlphaBoundsAt(x2, y2) && c.getAlphaAt(x2, y2)) this.map[y][x] = c.getAt(x2, y2);
	}

	@Override
	public TextArray pourText()
	{ return new TextArray(this.map); }
	
}