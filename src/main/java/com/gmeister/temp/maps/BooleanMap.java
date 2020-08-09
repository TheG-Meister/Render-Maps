package com.gmeister.temp.maps;

import java.util.Arrays;

public class BooleanMap implements Cloneable
{
	
	public static final byte AND = 0b0001;
	public static final byte OR = 0b0111;
	public static final byte XOR = 0b0110;
	public static final byte NAND = 0b1110;
	public static final byte NOR = 0b1000;
	public static final byte XNOR = 0b1001;
	
	private int xCapacity;
	private int yCapacity;
	private ReferencePoint offset;
	private boolean[][] map;
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public BooleanMap()
	{
		this.xCapacity = 0;
		this.yCapacity = 0;
		this.offset = new ReferencePoint();
		this.map = new boolean[0][0];
	}
	
	public BooleanMap(int xCapacity, int yCapacity, boolean b)
	{
		this.setMap(xCapacity, yCapacity, b);
		this.offset = new ReferencePoint();
	}
	
	public BooleanMap(int xCapacity, int yCapacity, int length)
	{
		this.setMap(xCapacity, yCapacity, length);
		this.offset = new ReferencePoint();
	}
	
	public BooleanMap(int xCapacity, int yCapacity, String s)
	{
		this.setMap(xCapacity, yCapacity, s);
		this.offset = new ReferencePoint();
	}
	
	// ------------------------------------------------ MAP REFACTORING METHODS
	// ------------------------------------------------ //
	
	// Sets the objects booleans to b's, meaning modifications in b's booleans WILL
	// affect the object. The objects x and y values MAY NOT be conserved.
	
	public void setMap(boolean[][] b)
	{
		this.xCapacity = b[0].length;
		this.yCapacity = b.length;
		
		for (int i = 1; i < this.yCapacity; i++) if (b[i].length != this.xCapacity)
			throw new IllegalArgumentException("Inner boolean[] length is not uniform");
		
		this.map = b;
	}
	
	public void setMap(int xCapacity, int yCapacity, boolean b)
	{
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
		this.map = new boolean[yCapacity][xCapacity];
		this.fill(b);
	}
	
	public void setMap(int xCapacity, int yCapacity, int length)
	{
		if (length < 0)
		{
			this.setMap(xCapacity, yCapacity, false);
			return;
		}
		if (length >= yCapacity * xCapacity)
		{
			this.setMap(xCapacity, yCapacity, true);
			return;
		}
		
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
		this.map = new boolean[this.yCapacity][this.xCapacity];
		
		int ly = Math.floorDiv(length, this.xCapacity);
		int lx = length % this.xCapacity;
		int y2;
		for (y2 = 0; y2 < ly - 1; y2++) Arrays.fill(this.map[y2], true);
		y2++;
		for (int x2 = 0; x2 < lx; x2++) this.map[y2][x2] = true;
	}
	
	public void setMap(int xCapacity, int yCapacity, String s)
	{
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
		this.map = new boolean[this.yCapacity][this.xCapacity];
		
		int i = 0;
		for (int y2 = 0; y2 < this.yCapacity; y2++) for (int x2 = 0; x2 < this.xCapacity; x2++, i++)
		{
			char c = s.charAt(i);
			if (c == '0') this.map[y2][x2] = false;
			else if (c == '1') this.map[y2][x2] = true;
			else throw new IllegalArgumentException("The given String contains characters other than '1' and '0'");
		}
	}
	
	public void fill(boolean b)
	{ for (int y = 0; y < this.yCapacity; y++) Arrays.fill(this.map[y], b); }
	
	// Duplicates a's booleans to the object, meaning modifications in a's booleans
	// WILL NOT affect the object. The objects x and y values ARE NOT conserved
	
	public void copyFrom(BooleanMap b)
	{
		this.xCapacity = b.xCapacity;
		this.yCapacity = b.yCapacity;
		this.offset = b.offset;
		this.map = new boolean[this.yCapacity][];
		
		for (int y = 0; y < this.yCapacity; y++) this.map[y] = Arrays.copyOf(b.map[y], this.xCapacity);
	}
	
	public void setTo(BooleanMap b)
	{
		this.xCapacity = b.xCapacity;
		this.yCapacity = b.yCapacity;
		this.offset = b.offset;
		this.map = b.map;
	}
	
	public static BooleanMap copOf(BooleanMap b)
	{
		BooleanMap output = new BooleanMap();
		output.copyFrom(b);
		return output;
	}
	
	// ------------------------------------------------ MAP RETRIEVAL AND
	// MODIFICATION METHODS ------------------------------------------------ //
	
	public void setAt(int x, int y, boolean b)
	{
		int x2 = x - this.offset.getX();
		int y2 = y - this.offset.getY();
		if (this.isWithinMapAt(x2, y2)) this.map[y2][x2] = b;
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x2 + ", " + y2);
	}
	
	public void setMapAt(int x, int y, boolean b)
	{
		if (this.isWithinMapAt(x, y)) this.map[y][x] = b;
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x + ", " + y);
	}
	
	public void invert()
	{
		for (int y = 0; y < this.yCapacity; y++)
			for (int x = 0; x < this.xCapacity; x++) this.map[y][x] = !this.map[y][x];
	}
	
	public void invertAt(int x, int y)
	{
		int x2 = x - this.offset.getX();
		int y2 = y - this.offset.getY();
		if (this.isWithinMapAt(x2, y2)) this.map[y2][x2] = !this.map[y2][x2];
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x2 + ", " + y2);
	}
	
	public void invertMapAt(int x, int y)
	{
		if (this.isWithinMapAt(x, y)) this.map[y][x] = !this.map[y][x];
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x + ", " + y);
	}
	
	public boolean[][] getMap()
	{ return this.map; }
	
	public boolean getAt(int x, int y)
	{
		int x2 = x - this.offset.getX();
		int y2 = y - this.offset.getY();
		if (this.isWithinMapAt(x2, y2)) return this.map[y2][x2];
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x2 + ", " + y2);
	}
	
	public boolean getMapAt(int x, int y)
	{
		if (this.isWithinMapAt(x, y)) return this.map[y][x];
		else throw new MapOutOfBoundsException("BooleanMap does not contain coordinates " + x + ", " + y);
	}
	
	public boolean isWithinBoundsAt(int x, int y)
	{
		int x2 = x - this.offset.getX();
		int y2 = y - this.offset.getY();
		if (x2 < 0 || x2 >= this.xCapacity || y2 < 0 || y2 >= this.yCapacity) return false;
		else return true;
	}
	
	public boolean isWithinMapAt(int x, int y)
	{
		if (x < 0 || x >= this.xCapacity || y < 0 || y >= this.yCapacity) return false;
		else return true;
	}
	
	public double getMapAreaCoefficient()
	{
		int i = 0;
		for (int y = 0; y < this.yCapacity; y++) for (int x = 0; x < this.xCapacity; x++) if (this.map[y][x]) i++;
		return i / ((double) this.xCapacity * this.yCapacity);
	}
	
	// ------------------------------------------------ CAPACITY METHODS
	// ------------------------------------------------ //
	
	public int getXCapacity()
	{ return this.xCapacity; }
	
	public int getYCapacity()
	{ return this.yCapacity; }
	
	public void setXCapacity(int xCapacity, int xOffset, boolean b)
	{
		boolean[][] tempMap = new boolean[this.yCapacity][xCapacity];
		
		if (b)
		{
			for (int y = 0; y < this.yCapacity; y++) for (int x = 0, x2 = -xOffset; x < xCapacity; x++, x2++)
				if (x2 < 0 || x2 >= this.xCapacity) tempMap[y][x] = true;
				else tempMap[y][x] = this.map[y][x2];
		}
		else for (int y = 0; y < this.yCapacity; y++) for (int x = 0, x2 = -xOffset; x < xCapacity; x++, x2++)
			if (x2 >= 0 && x2 < this.xCapacity) tempMap[y][x] = this.map[y][x2];
		
		this.map = tempMap;
		this.xCapacity = xCapacity;
	}
	
	public void setYCapacity(int yCapacity, int yOffset, boolean b)
	{
		boolean[][] tempMap = new boolean[yCapacity][];
		
		if (b)
		{
			for (int y = 0, y2 = -yOffset; y < yCapacity; y++, y2++) if (y2 < 0 || y2 >= this.yCapacity)
			{
				tempMap[y] = new boolean[this.xCapacity];
				Arrays.fill(tempMap[y], true);
			}
			else tempMap[y] = this.map[y2];
		}
		else for (int y = 0, y2 = -yOffset; y < yCapacity; y++, y2++)
			if (y2 >= 0 && y2 < this.yCapacity) tempMap[y] = this.map[y2];
		
		this.map = tempMap;
		this.yCapacity = yCapacity;
	}
	
	public void setCapacity(int xCapacity, int yCapacity, ReferencePoint offset, boolean b)
	{
		boolean[][] tempMap = new boolean[yCapacity][xCapacity];
		
		if (b)
		{
			for (int y = 0, y2 = -offset.getY(); y < yCapacity; y++, y2++)
				if (y < 0 || y2 >= this.yCapacity) Arrays.fill(tempMap[y], true);
				else for (int x = 0, x2 = -offset.getX(); x < xCapacity; x++, x2++)
					if (x2 < 0 || x2 >= this.xCapacity) tempMap[y][x] = true;
					else tempMap[y][x] = this.map[y2][x2];
		}
		else for (int y = 0, y2 = -offset.getY(); y < yCapacity; y++, y2++)
			if (y2 >= 0 && y2 < this.yCapacity) for (int x = 0, x2 = -offset.getX(); x < xCapacity; x++, x2++)
				if (x2 >= 0 && x2 < this.xCapacity) tempMap[y][x] = this.map[y2][x2];
		
		this.map = tempMap;
		this.xCapacity = xCapacity;
		this.yCapacity = yCapacity;
	}
	
	// ------------------------------------------------ OFFSET METHODS
	// ------------------------------------------------ //
	
	public void setOffset(ReferencePoint offset)
	{ this.offset = offset; }
	
	public ReferencePoint getOffset()
	{ return this.offset; }
	
	// ------------------------------------------------ PRINTING METHODS
	// ------------------------------------------------ //
	
	@Override
	public String toString()
	{ return Arrays.deepToString(this.map); }
	
	public String toCleanString()
	{
		String s = "";
		for (int y = 0; y < this.yCapacity; y++) for (int x = 0; x < this.xCapacity; x++) if (this.map[y][x]) s += "1";
		else s += "0";
		return s;
	}
	
	public String toCleanGridString()
	{
		String s = "";
		for (int y = 0; y < this.yCapacity; y++)
		{
			for (int x = 0; x < this.xCapacity; x++) if (this.map[y][x]) s += "1";
			else s += "0";
			if (y != this.yCapacity - 1) s += System.lineSeparator();
		}
		return s;
	}
	
	// ------------------------------------------------ CLONING, TRANSLATION AND
	// MERGING ------------------------------------------------ //
	
	public static BooleanMap intersectionOf(BooleanMap a, BooleanMap b, ReferencePoint r)
	{
		int xCapacity = Math.max(0,
				Math.min(a.getOffset().getX() + a.getXCapacity(),
						r.getX() + b.getOffset().getX() + b.getXCapacity()) - Math.max(a.getOffset().getX(),
								r.getX() + b.getOffset().getX()));
		int yCapacity = Math.max(0,
				Math.min(a.getOffset().getY() + a.getYCapacity(),
						r.getY() + b.getOffset().getY() + b.getYCapacity()) - Math.max(a.getOffset().getY(),
								r.getY() + b.getOffset().getY()));
		BooleanMap output = new BooleanMap(xCapacity, yCapacity, false);
		output.setOffset(new ReferencePoint(Math.max(a.getOffset().getX(), r.getX() + b.getOffset().getX()),
				Math.max(a.getOffset().getY(), r.getY() + b.getOffset().getY())));
		return output;
	}
	
	public void translate(int x, int y)
	{
		boolean[][] a = this.getMap();
		boolean[][] b = BooleanMap.copOf(this).map;
		for (int y2 = 0; y2 < this.yCapacity; y2++)
		{
			int y3 = y2 - y;
			for (int x2 = 0; x2 < this.xCapacity; x2++)
			{
				int x3 = x2 - x;
				if (y3 < 0 || y3 > 53 || x3 < 0 || x3 > 95) a[y2][x2] = false;
				else a[y2][x2] = b[y3][x3];
			}
		}
		this.setMap(a);
	}
	
	/*
	 * public void mergeUpto(BooleanMap b, int dx, int dy, byte mode) { switch
	 * (mode) { case BooleanMap.AND: { for (int y = 0, y2 = -dy; y < this.yCapacity;
	 * y++, y2++) for (int x = 0, x2 = -dx; x < this.xCapacity; x++, x2++)
	 * this.map[y][x] = this.map[y][x] & b.map[y2][x2]; break; } case BooleanMap.OR:
	 * { for (int y = 0, y2 = -dy; y < this.yCapacity; y++, y2++) for (int x = 0, x2
	 * = -dx; x < this.xCapacity; x++, x2++) this.map[y][x] = this.map[y][x] |
	 * b.map[y2][x2]; break; } case BooleanMap.NAND: { for (int y = 0, y2 = -dy; y <
	 * this.yCapacity; y++, y2++) for (int x = 0, x2 = -dx; x < this.xCapacity; x++,
	 * x2++) this.map[y][x] = !(this.map[y][x] & b.map[y2][x2]); break; } case
	 * BooleanMap.NOR: { for (int y = 0, y2 = -dy; y < this.yCapacity; y++, y2++)
	 * for (int x = 0, x2 = -dx; x < this.xCapacity; x++, x2++) this.map[y][x] =
	 * !(this.map[y][x] | b.map[y2][x2]); break; } case BooleanMap.XOR: { for (int y
	 * = 0, y2 = -dy; y < this.yCapacity; y++, y2++) for (int x = 0, x2 = -dx; x <
	 * this.xCapacity; x++, x2++) this.map[y][x] = this.map[y][x] ^ b.map[y2][x2];
	 * break; } case BooleanMap.XNOR: { for (int y = 0, y2 = -dy; y <
	 * this.yCapacity; y++, y2++) for (int x = 0, x2 = -dx; x < this.xCapacity; x++,
	 * x2++) this.map[y][x] = !(this.map[y][x] ^ b.map[y2][x2]); break; } default: {
	 * boolean[] mask = {(mode & 0b1000) == 8, (mode & 0b0100) == 4, (mode & 0b0010)
	 * == 2, (mode & 0b0001) == 1}; for (int y = 0, y2 = -dy; y < this.yCapacity;
	 * y++, y2++) for (int x = 0, x2 = -dx; x < this.xCapacity; x++, x2++) { byte c
	 * = 0; if (this.map[y][x]) c += 2; if (b.map[y2][x2]) c++; this.map[y][x] =
	 * mask[c]; } } } }
	 */
	
	public void merge(BooleanMap b, ReferencePoint offset)
	{ this.merge(b, offset, BooleanMap.OR); }
	
	public void merge(BooleanMap b, ReferencePoint offset, byte mode)
	{
		BooleanMap c = BooleanMap.copOf(this);
		switch (mode)
		{
			case BooleanMap.AND:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (!b.isWithinMapAt(x2, y2)) this.map[y][x] = false;
						else this.map[y][x] = c.map[y][x] & b.map[y2][x2];
				break;
			}
			case BooleanMap.OR:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (b.isWithinBoundsAt(x2, y2)) this.map[y][x] = c.map[y][x] | b.map[y2][x2];
				break;
			}
			case BooleanMap.XOR:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (b.isWithinBoundsAt(x2, y2)) this.map[y][x] = c.map[y][x] ^ b.map[y2][x2];
				break;
			}
			case BooleanMap.NAND:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (!b.isWithinBoundsAt(x2, y2)) this.map[y][x] = true;
						else this.map[y][x] = !(c.map[y][x] & b.map[y2][x2]);
				break;
			}
			case BooleanMap.NOR:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (!b.isWithinBoundsAt(x2, y2)) this.map[y][x] = !c.map[y][x];
						else this.map[y][x] = !(c.map[y][x] | b.map[y2][x2]);
				break;
			}
			case BooleanMap.XNOR:
			{
				for (int y = 0, y2 = this.offset.getY() - offset.getY() - b.offset.getY(); y < this.yCapacity;
						y++, y2++)
					for (int x = 0, x2 = this.offset.getX() - offset.getX() - b.offset.getX(); x < this.xCapacity;
							x++, x2++)
						if (!b.isWithinBoundsAt(x2, y2)) this.map[y][x] = !c.map[y][x];
						else this.map[y][x] = !(c.map[y][x] ^ b.map[y2][x2]);
				break;
			}
			default:
				throw new IllegalArgumentException("The specified mode is not a BooleanMap constant");
		}
	}
	
}