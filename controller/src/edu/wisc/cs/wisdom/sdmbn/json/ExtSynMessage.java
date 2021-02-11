package edu.wisc.cs.wisdom.sdmbn.json;

public class ExtSynMessage extends Message
{
	public String host;
	public int pid;
	public String stateIp;
	public short statePort;
	
	public ExtSynMessage()
	{ super(Message.EXT_RESPONSE_SYN); }

    public ExtSynMessage(AllFieldsMessage msg) throws MessageException
    {
        super(msg);
		if (!msg.type.equals(Message.EXT_RESPONSE_SYN))
		{
			throw new MessageException(
					String.format("Cannot construct %s from message of type %s",
							this.getClass().getSimpleName(), msg.type), msg); 
		}
        this.host = msg.host;
        this.pid = msg.pid;
        this.stateIp = msg.stateIp;
        this.statePort = msg.statePort;
    }
	
	public String toString()
	{ return "{id="+id+",type=\""+type+"\",host=\""+host+"\",pid="+pid+"}"; }
}
