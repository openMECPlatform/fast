package edu.wisc.cs.wisdom.sdmbn.json;

import edu.wisc.cs.wisdom.sdmbn.PerflowKey;

//import edu.wisc.cs.wisdom.sdmbn.Middlebox;

public class DestinationInfoMessage extends Message
{
    public PerflowKey key;
//    public int raiseEvents;
    public String stateIp;
    public short statePort;

    public DestinationInfoMessage()
    { this(0, null, null, (short)0); }

    public DestinationInfoMessage(int id, PerflowKey key, String stateIp, short statePort)
    {
        super(Message.COMMAND_PUT_DESTINATION_INFO, id);
        this.key = key;
//        this.destMb = mb;
        this.stateIp = stateIp;
        this.statePort = statePort;
    }

    public DestinationInfoMessage(AllFieldsMessage msg) throws MessageException
    {
        super(msg);
        if (!msg.type.equals(Message.COMMAND_PUT_DESTINATION_INFO))
        {
            throw new MessageException(
                    String.format("Cannot construct %s from message of type %s",
                            this.getClass().getSimpleName(), msg.type), msg);
        }
        this.key = msg.key;
//        this.destMb = msg.destMb;
        this.stateIp = msg.stateIp;
        this.statePort = msg.statePort;
//        this.raiseEvents = msg.raiseEvents;
    }


//    public DestinationInfoMessage()
//    { this(0, null); }
//
//    public DestinationInfoMessage(int id, PerflowKey key)
//    {
//        super(Message.COMMAND_PUT_DESTINATION_INFO, id);
//        this.key = key;
//    }
//
//    public DestinationInfoMessage(AllFieldsMessage msg) throws MessageException
//    {
//        super(msg);
//        if (!msg.type.equals(Message.COMMAND_PUT_DESTINATION_INFO))
//        {
//            throw new MessageException(
//                    String.format("Cannot construct %s from message of type %s",
//                            this.getClass().getSimpleName(), msg.type), msg);
//        }
//        this.key = msg.key;
//    }

    public String toString()
    { return "{id="+id+",type=\""+type+"\",key="+key+"}"; }


}
