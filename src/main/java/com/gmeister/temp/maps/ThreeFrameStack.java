package com.gmeister.temp.maps;

public abstract class ThreeFrameStack extends CharMapStack
{
	
	private AnimatedCharMap eventMap;
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public ThreeFrameStack(int xCapacity, int yCapacity)
	{ super(xCapacity, yCapacity); }
	
	public ThreeFrameStack(int xCapacity, int yCapacity, int length)
	{ super(xCapacity, yCapacity, length); }
	
	public ThreeFrameStack(int xCapacity, int yCapacity, RenderedCharMap... maps)
	{ super(xCapacity, yCapacity, maps); }
	
	// ------------------------------------------------ GENERAL METHODS
	// ------------------------------------------------ //
	
	public void setEventAnimator(AnimatedCharMap a)
	{ this.eventMap = a; }
	
	public abstract void applyOnClick();
	
}