package com.gmeister.temp.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmeister.temp.pipe.input.InputAction;
import com.gmeister.temp.pipe.input.InputData;
import com.gmeister.temp.pipe.input.InputSink;
import com.gmeister.temp.pipe.time.TimeData;
import com.gmeister.temp.pipe.time.TimeSink;

public class RenderedCharMap extends CharMap implements TimeSink, InputSink
{
	
	private List<List<CharMap>> renderQueue;
	private List<InputAction> inputActions;
	private boolean hidden;
	private CharMap lastMap;
	
	// ------------------------------------------------ CONTRUCTORS
	// ------------------------------------------------ //
	
	public RenderedCharMap()
	{
		super();
		
		this.renderQueue = new ArrayList<>();
		this.inputActions = new ArrayList<>();
		this.hidden = false;
		this.lastMap = null;
	}
	
	public RenderedCharMap(CharMap map, InputAction... inputActions)
	{
		super();
		this.setTo(map);
		
		this.renderQueue = new ArrayList<>();
		this.inputActions = new ArrayList<>(Arrays.asList(inputActions));
		this.hidden = false;
		this.lastMap = null;
	}
	
	// ------------------------------------------------ RENDERING AND TIMER METHODS
	// ------------------------------------------------ //
	
	public static RenderedCharMap convert(CharMap c)
	{
		RenderedCharMap output = new RenderedCharMap();
		output.setTo(c);
		return output;
	}
	
	public static RenderedCharMap copyOf(CharMap c)
	{
		RenderedCharMap output = new RenderedCharMap();
		output.copyFrom(c);
		return output;
	}
	
	public void queueAnimation(CharMap c)
	{ this.queueAnimation(new ArrayList<>(Arrays.asList(c))); }
	
	public void queueAnimation(List<CharMap> maps)
	{
		if (this.renderQueue.size() == 0) this.renderQueue.add(maps);
		else this.renderQueue.set(0, maps);
	}
	
	public void queueAnimation(List<CharMap> maps, List<Long> durations)
	{
		//TODO
		
	}
	
	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
		
		if (this.hidden)
		{
			this.lastMap = CharMap.copyOf(this);
			this.setTo(new CharMap());
		}
		else this.setTo(this.lastMap);
	}
	
	public boolean isHidden()
	{ return hidden; }
	
	@Override
	public void poolTimeData(TimeData data)
	{
		//TODO does not use time data
		if (this.renderQueue.size() > 0)
		{
			List<CharMap> animation = this.renderQueue.get(0);
			CharMap frame = animation.get(0);
			
			if (frame == null) frame = new CharMap();
			if (this.hidden) this.lastMap.setTo(frame);
			else this.setTo(frame);
			
			animation.remove(frame);
			if (animation.size() == 0) this.renderQueue.remove(0);
		}
	}
	
	@Override
	public TimeData getCooldown(TimeData time)
	{ return null; }
	
	@Override
	public boolean isReadyFor(TimeData data)
	{ return false; }
	
	@Override
	public List<String> getTags()
	{ return null; }
	
	@Override
	public List<InputAction> getActions()
	{ return this.inputActions; }
	
	//TODO make this better
	@Override
	public void poolInput(InputData data)
	{ for (InputAction action : this.inputActions) action.act(data); }
	
}