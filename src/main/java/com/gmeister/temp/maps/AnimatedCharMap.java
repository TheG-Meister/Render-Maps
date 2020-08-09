package com.gmeister.temp.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class AnimatedCharMap extends RenderedCharMap
{
	
	private Vector<CharMap> frames;
	private int currentFrameNumber;
	private int[] frameOrder;
	
	// ------------------------------------------------ CONSTRUCTORS
	// ------------------------------------------------ //
	
	public AnimatedCharMap()
	{
		super();
		
		this.frames = new Vector<CharMap>();
		this.currentFrameNumber = 0;
	}
	
	public AnimatedCharMap(CharMap... frames)
	{ this(0, frames); }
	
	public AnimatedCharMap(int currentFrameNumber, CharMap... frames)
	{
		super();
		if (currentFrameNumber > frames.length) throw new IllegalArgumentException(
				"Current frame argument \"" + currentFrameNumber + "\" is greater than the number of frames");
		
		this.frames = new Vector<CharMap>(Arrays.asList(frames));
		this.currentFrameNumber = currentFrameNumber;
		this.queueFrame(this.currentFrameNumber);
	}
	
	// ------------------------------------------------ FRAME METHODS
	// ------------------------------------------------ //
	
	public void setFrameAt(int i, CharMap c)
	{
		if (i + 1 > this.frames.size()) this.frames.setSize(i + 1);
		this.frames.set(i, c);
		if (i == this.currentFrameNumber) this.queueFrame(i);
	}
	
	public void incFrame()
	{ this.queueFrame(this.currentFrameNumber + 1); }
	
	public void decFrame()
	{ this.queueFrame(this.currentFrameNumber - 1); }
	
	public void animate()
	{ this.queueFrame(0, this.frames.size() - 1); }
	
	public int getNoOfFrames()
	{ return this.frames.size(); }
	
	public int getCurrentFrame()
	{ return this.currentFrameNumber; }
	
	// ------------------------------------------------ RENDERING METHODS
	// ------------------------------------------------ //
	
	@Override
	public void render()
	{
		int i = this.getCurrentAnimationLength();
		if (i > 0) this.currentFrameNumber = this.frameOrder[this.frameOrder.length - i];
		super.render();
	}
	
	public void queueFrame(int i)
	{
		int[] j = {i};
		this.frameOrder = j;
		if (i == -1) super.queueAnimation((Vector<CharMap>) null);
		else super.queueAnimation(this.frames.get(i));
	}
	
	public void queueFrame(int start, int end)
	{
		if (end == start) this.queueFrame(start);
		else if (end >= start)
		{
			this.frameOrder = new int[end - start + 1];
			for (int i = start; i < end + 1; i++) this.frameOrder[i] = i;
			
			super.queueAnimation(new ArrayList<CharMap>(this.frames.subList(start, end + 1)));
		}
		else
		{
			int l = start - end + 1;
			List<CharMap> frames = new ArrayList<CharMap>(l);
			this.frameOrder = new int[l];
			for (int i = start; i > end - 1; i--)
			{
				frames.add(this.frames.get(i));
				this.frameOrder[i] = i;
			}
			
			super.queueAnimation(frames);
		}
	}
	
	public void queueFrame(int[] i)
	{
		this.frameOrder = i;
		List<CharMap> frames = new ArrayList<CharMap>(i.length);
		for (int j = 0; j < i.length; j++) if (i[j] == -1) frames.add(null);
		else frames.add(this.frames.get(i[j]));
		
		super.queueAnimation(frames);
	}
	
}