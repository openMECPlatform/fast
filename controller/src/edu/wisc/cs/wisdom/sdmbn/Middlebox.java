package edu.wisc.cs.wisdom.sdmbn;

import net.floodlightcontroller.core.IOFSwitch;
import edu.wisc.cs.wisdom.sdmbn.channel.EventChannel;
import edu.wisc.cs.wisdom.sdmbn.channel.StateChannel;

public class Middlebox 
{
	private String host;
	private int pid;
	private IOFSwitch sw;
	private short switchPort;
	public short statePort;   // for Fast, this should be reported by mb
	public String stateIp;   // for FAST, this should be reported by mb
	private StateChannel stateChannel;
	private EventChannel eventChannel;
	public String stateEventIp;
	public short stateEventPort;
	public String mgmtIp;
	public short mgmtPort;
	private IOFSwitch stateSw;
	private  short stateSwPort;
	
	public Middlebox(String host, int pid)
	{
		this.host = host;
		this.pid = pid;
		this.sw = null;
		this.switchPort = -1;
		this.stateChannel = null;
		this.eventChannel = null;
		this.stateIp = "1.1.1.1";
		this.statePort = 1111;
		this.mgmtIp = "0.0.0.0";
		this.mgmtPort = 0000;
		this.stateEventIp = "2.2.2.2";
		this.stateEventPort = 2222;
		this.stateSwPort = -1;
		this.stateSw = null;
 	}


	public void setStateHostInfo(String stateIP, short statePort){
		this.stateIp = stateIP;
		this.statePort = statePort;
	}

	public String getHost()
	{ return this.host; }
	
	public int getPid()
	{ return this.pid; }

	public void setSwitch(IOFSwitch sw, short switchPort)
	{
		this.sw = sw;
		this.switchPort = switchPort;
	}

	public void setStateSw(IOFSwitch sw, short switchPort)
	{
		this.stateSw = sw;
		this.stateSwPort = switchPort;
	}

	public IOFSwitch getStateSw()
	{ return this.stateSw; }

    public short getStateSwPort()
	{ return this.stateSwPort; }
	
	public IOFSwitch getSwitch()
	{ return this.sw; }

	public short getStatePort()
	{ return this.statePort;}

	public void setStatePort(short port)
	{ this.statePort = port;}

	
	public short getSwitchPort()
	{ return this.switchPort; }
	
	public String getId()
	{ return constructId(this.host,this.pid); }
	
	public static String constructId(String host, int pid)
	{ return host+"."+pid;	}
	
	public void setStateChannel(StateChannel stateChannel)
	{ this.stateChannel = stateChannel; }
	
	public StateChannel getStateChannel()
	{ return this.stateChannel; }
	
	public boolean hasStateChannel()
	{ return (this.stateChannel != null); }
	
	public void setEventChannel(EventChannel eventChannel)
	{ this.eventChannel = eventChannel; }
	
	public EventChannel getEventChannel()
	{ return this.eventChannel; }
	
	public boolean hasEventChannel()
	{ return (this.eventChannel != null); }
	
	public boolean hasSwitch()
	{ return (this.sw != null); }
	
	public boolean isFullyConnected()
	{
		return (this.hasStateChannel() && this.hasEventChannel()
				&& this.hasSwitch());
	}

	public void setStateEventAddress()
	{
		String address =  this.stateChannel.channel.getRemoteAddress().toString();
		this.stateEventIp = (address.substring(1)).split(":")[0];
		this.stateEventPort = Short.valueOf((address.substring(1)).split(":")[0]);

	}

	public void setMgmtEventAddress()
	{
		String address =  this.eventChannel.channel.getRemoteAddress().toString();
		this.mgmtIp = (address.substring(1)).split(":")[0];
		this.mgmtPort = Short.parseShort((address.substring(1)).split(":")[0]);

	}

	public String getManagementIP()
	{ 
		if (this.stateChannel != null)
		{ return this.stateChannel.getIp(); }
		if (this.eventChannel != null)
		{ return this.eventChannel.getIp(); }
		return null;
	}
	
	@Override
	public String toString()
	{ return this.getId(); }
}
