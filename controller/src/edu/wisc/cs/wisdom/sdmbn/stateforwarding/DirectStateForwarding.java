package edu.wisc.cs.wisdom.sdmbn.stateforwarding;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import edu.wisc.cs.wisdom.sdmbn.Parameter.StateForwardingStrategy;
import edu.wisc.cs.wisdom.sdmbn.Middlebox;
import edu.wisc.cs.wisdom.sdmbn.PerflowKey;
import edu.wisc.cs.wisdom.sdmbn.Parameters.Scope;
import edu.wisc.cs.wisdom.sdmbn.channel.BaseChannel;
import edu.wisc.cs.wisdom.sdmbn.channel.EventChannelHandler;
import edu.wisc.cs.wisdom.sdmbn.channel.StateChannelHandler;
import edu.wisc.cs.wisdom.sdmbn.channel.EventChannel;
import edu.wisc.cs.wisdom.sdmbn.channel.StateChannel;
import edu.wisc.cs.wisdom.sdmbn.Parameters.StateFWStrategy;
import edu.wisc.cs.wisdom.sdmbn.utils.FlowUtil;


import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.topology.NodePortTuple;


public class DirectStateForwarding extends StateForwardingBase
{
    private Middlebox srcMb;

    public DirectStateForwarding(IFloodlightProviderService floodlightProvider, IRoutingService rtEngine, Middlebox srcMb)
    {
        super(floodlightProvider,rtEngine);
        this.srcMb = srcMb;
    }


    @Override
    public void forwardToMiddlebox(PerflowKey key, Middlebox destMb) {
//        this.dstMb = destMb;
        if ( (this.srcMb.getStateSw() == null) || (destMb.getStateSw() == null) )
        {
            log.debug("FAST: unaware of SDN environment for state forwarding. Use L2 state forwarding");
            return;
        }
        // could get connected switch and port for each middlebox but note that the connected switch port is only for traffic port
        log.info("Start applying forwarding rules for application states");
        // add route for state
        long stateFwStart = System.currentTimeMillis();
        Route route = this.routingEngine.getRoute(this.srcMb.getStateSw().getId(), this.srcMb.getStateSwPort(), destMb.getStateSw().getId(), destMb.getStateSwPort());
        log.debug("FAST: Time to route: {}", System.currentTimeMillis() - stateFwStart);
        if (route != null)
        {
            log.debug("FAST: Route found for src={} and dst={}" + "route={}", new Object[] {this.srcMb.getStateSw().getId(), destMb.getId(), route});
//            return null;
        }
        long setupTime = System.currentTimeMillis();
        List<NodePortTuple> switchPortList = route.getPath();
        for (int indx = switchPortList.size()-1; indx > 0; indx -= 2)
        {
            long switchDPID = switchPortList.get(indx).getNodeId();
            IOFSwitch sw = this.floodlightProvider.getSwitches().get(switchDPID);
            if (sw == null) {
                if (log.isWarnEnabled()) {
                    log.error("Unable to push route, switch at DPID {} " +
                            "not available", switchDPID);
                }
//                return srcSwitchIncluded;
            }
            short[] outPorts = new short[1];
            outPorts[0] = destMb.getStateSwPort();
            FlowUtil.installForwardingRules(sw, this.srcMb.getStateSwPort(), outPorts, key);
            // reverse path
            short[] reOutPorts = new short[1];
            reOutPorts[0] = srcMb.getStateSwPort();
            FlowUtil.installForwardingRules(sw, destMb.getStateSwPort(), reOutPorts, key);

        }
        log.debug("FAST: Time to install: {}", System.currentTimeMillis() - setupTime);


//        return route;

    }


}

