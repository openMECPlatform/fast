package edu.wisc.cs.wisdom.sdmbn.apps.testing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;


import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

import edu.wisc.cs.wisdom.sdmbn.Middlebox;
import edu.wisc.cs.wisdom.sdmbn.Parameters.Guarantee;
import edu.wisc.cs.wisdom.sdmbn.Parameters.Optimization;
import edu.wisc.cs.wisdom.sdmbn.PerflowKey;

import edu.wisc.cs.wisdom.sdmbn.json.DestinationInfoMessage;
import edu.wisc.cs.wisdom.sdmbn.json.MessageException;

public class TestFwStateMultiMig extends TestTimed
{
	private Guarantee guarantee;
	private Optimization optimization;

	private int countMigMax;


//    private boolean isStopped;

	@Override
	protected void parseArguments(FloodlightModuleContext context)
			throws FloodlightModuleException 
	{
		super.parseArguments(context);
		
		// Get arguments specific to move
		Map<String,String> config = context.getConfigParams(this);
		
		this.checkForArgument(config, "Guarantee");
		this.guarantee = Guarantee.valueOf(config.get("Guarantee"));
		log.debug(String.format("Guarantee = %s", this.guarantee.name()));
		
		this.checkForArgument(config, "Optimization");
		this.optimization = Optimization.valueOf(config.get("Optimization"));
		log.debug(String.format("Optimization = %s", this.optimization.name()));


		// for fast
		this.checkForArgument(config, "ConsecMig");
		this.countMigMax = Integer.parseInt(config.get("ConsecMig"));
		log.debug("FAST: ConsecMig is set to {}", this.countMigMax);
		if (this.countMigMax > this.numInstances)
		{
			log.error("FAST: Configuration file failed. ConsecMig must be set in range [{} {}]", 1, this.numInstances -1);
			System.exit(1);
		}
	}


//	@Override
//	public void init(FloodlightModuleContext context)
//			throws FloodlightModuleException
//	{
//		this.visitedMiddleboxes = new HashMap<String, Middlebox>();  // for fast
//
//		this.countMig = 0; // for fast
//
//		this.srcMbName = "mb1";
//
//		this.dstMbName = "";
//
////		this.isStopped = false;
//
//	}


	@Override
	protected void initialRuleSetup()
	{
		// This is applied for the first middlebox
		Middlebox mb1 = middleboxes.get("mb1");
		synchronized(this.activeMbs)
		{ this.activeMbs.add(mb1); }
	
		List<Middlebox> mbs = Arrays.asList(new Middlebox[]{mb1});
		this.changeForwarding(new PerflowKey(), mbs);
	}
	
	@Override
	protected void initiateOperation()
	{
		// Add multi-mig code here
		// each middblebox includes name (mb1, mb2,,,,) and middlebox class
//		boolean isStopped = false;
		int rmMbIndex;
		String mbName;
		Middlebox srcMb, dstMb;
		// Start with the first middlebox

        log.debug(String.format("FAST: Number of middleboxes: %d", middleboxes.size()));
        boolean found = false;
        while (!found){
			srcMb = middleboxes.get(this.srcMbName);
//			temp = srcMb;
//			log.debug("FAST: The mb name:{}", temp.getHost());
			rmMbIndex = ThreadLocalRandom.current().nextInt(2, middleboxes.size() + 1);
			mbName = "mb" + rmMbIndex;
			if (!visitedMiddleboxes.containsKey(mbName)){
				found = true;
				dstMb = middleboxes.get(mbName);
				visitedMiddleboxes.put(mbName, dstMb);
				synchronized(this.activeMbs)
				{ this.activeMbs.add(dstMb); }

				PerflowKey key = new PerflowKey();

				log.info("Key="+key);
				log.debug("FAST: Migrate from middlebox {} to middlebox {}", this.srcMbName, mbName);
//				log.debug("FAST: Dst state address info: {} {} ", dstMb.stateIp, dstMb.statePort);

//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch(InterruptedException ex)
//				{
//					Thread.currentThread().interrupt();
//				}

				log.debug("FAST: Install state forwarding rule...");
				sdmbnProvider.stateForwardingManager(srcMb,dstMb, key);
				log.debug("FAST: Successfully installed state forwarding rule.");
				// tell srcMB about the destination mb. Operation ID starts from 1
//				log.debug("FAST-STATE: Src has Sw-Port {}. Dst has Sw-Port {}.", srcMb.getStateSwPort(), dstMb.getStateSwPort());

				// Maybe some comercial SDN switch ignore nw_proto. Thus, assign it after installing fw rules.
				key.setDlType(Ethernet.TYPE_IPv4);
				key.setNwProto(IPv4.PROTOCOL_TCP);   // FAST: must change it if use UDP for state forwarding
				DestinationInfoMessage destInfo = new DestinationInfoMessage(0, key, srcMb.stateIp, srcMb.statePort);
				log.debug("Dstmb has state channel: {}", dstMb.hasStateChannel());
				try
				{ dstMb.getStateChannel().sendMessage(destInfo); }    // getPerFlow message is sent from here
				catch(MessageException e)
				{
					log.error("Failed to send destination info.");
					log.debug("Message: {}", e.toString());
				}



				int moveOpId = sdmbnProvider.move(srcMb, dstMb, key, this.scope,
						this.guarantee, this.optimization, this.traceSwitchPort);
				if (moveOpId >= 0)
				{ log.info("Initiated move"); }
				else
				{
					log.error("Failed to initiate move");
				}

				this.countMig++;
				if (this.countMig < this.countMigMax){
					this.isConsecMig = true;
				}
				else {
					this.isConsecMig = false;
				}
				this.srcMbName = mbName; // assign the src middlebox to the running one
			}
		}

//        if (this.dstMbName.isEmpty())
//		{
//			log.error("FAST: Destination middlebox not found");
//		}

	}

	@Override
	protected void terminateOperation()
	{ return; }
}
