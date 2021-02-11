package edu.wisc.cs.wisdom.sdmbn.operation;

import net.floodlightcontroller.core.IOFSwitch;
import edu.wisc.cs.wisdom.sdmbn.PerflowKey;
import edu.wisc.cs.wisdom.sdmbn.Middlebox;  // for fast

public interface IOperationManager 
{
	public void operationFinished(Operation op);
	public void operationFailed(Operation op);
	public void installForwardingRules(IOFSwitch sw, short inPort, 
			short outPort, PerflowKey key);
	public void installForwardingRules(IOFSwitch sw, short inPort, 
			short[] outPorts, PerflowKey key);

//	public void doStateForwarding(Middlebox src, Middlebox dst, short[] outPorts, PerflowKey key);
	public IOFSwitch getSwitch(short swid);

}
