package com.gmeister.temp.maps;

public final class ReferencePoint
{
	
	private final int x;
	private final int y;
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public ReferencePoint()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public ReferencePoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	// ------------------------------------------------ GENERAL METHODS
	// ------------------------------------------------ //
	
	public ReferencePoint setX(int x)
	{ return new ReferencePoint(x, this.y); }
	
	public ReferencePoint setY(int y)
	{ return new ReferencePoint(this.x, y); }
	
	public ReferencePoint add(int x, int y)
	{ return new ReferencePoint(this.x + x, this.y + y); }
	
	public ReferencePoint add(ReferencePoint r)
	{ return new ReferencePoint(this.x + r.x, this.y + r.y); }
	
	public ReferencePoint subtract(int x, int y)
	{ return new ReferencePoint(this.x - x, this.y - y); }
	
	public ReferencePoint subtract(ReferencePoint r)
	{ return new ReferencePoint(this.x + r.x, this.y + r.y); }
	
	public ReferencePoint multiply(int i)
	{ return new ReferencePoint(this.x * i, this.y * i); }
	
	public int getX()
	{ return this.x; }
	
	public int getY()
	{ return this.y; }
	
	@Override
	public String toString()
	{ return "[" + this.x + ", " + this.y + "]"; }
	
}