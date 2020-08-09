package com.gmeister.temp.maps;

import com.gmeister.temp.pipe.input.InputData;
import com.gmeister.temp.pipe.input.SimpleInputSink;

public abstract class ThreeFrameAnimator extends AnimatedCharMap
{
	
	private boolean selected;
	
	public class TFAUseSink extends SimpleInputSink
	{
		
		public TFAUseSink()
		{ super(0, "Use"); }
		
		@Override
		public void poolInput(InputData data)
		{ ThreeFrameAnimator.this.applyOnUse(); }
		
		@Override
		public boolean isAvailable()
		{ return ThreeFrameAnimator.this.selected; }
		
	}
	
	public class TFASelectSink extends SimpleInputSink
	{
		
		public TFASelectSink()
		{ super(2, "Select"); }
		
		@Override
		public void poolInput(InputData data)
		{}
		
		@Override
		public boolean isAvailable()
		{ return false; }
		
	}
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public ThreeFrameAnimator()
	{ super(); }
	
	public ThreeFrameAnimator(CharMap base, CharMap hover, CharMap use)
	{ super(base, hover, use); }
	
	// ------------------------------------------------ ABSTRACT METHODS
	// ------------------------------------------------ //
	
	public abstract void applyOnUse();
	
}