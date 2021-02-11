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

import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.routing.IRoutingService;
import edu.wisc.cs.wisdom.sdmbn.Parameters.StateFWStrategy;


public abstract class StateForwardingBase
{
    protected static Logger log = LoggerFactory.getLogger("FAST.StateForwarding");

//    private Middlebox srcMb;

    protected IRoutingService routingEngine;
    protected IFloodlightProviderService floodlightProvider;


    public StateForwardingBase(IFloodlightProviderService floodlightProvider, IRoutingService rtEngine)
    {
        this.routingEngine = rtEngine;
//        this.srcMb = mb;
        this.floodlightProvider = floodlightProvider;

    }

    // flow-mod - for use in the cookie
    public static final int STATE_COOKIE = 7;
    // by a global APP_ID class
    public long appCookie = AppCookie.makeCookie(STATE_COOKIE, 0);


    /**
     *  This could be used for advanced state processing functions in controller such as monitoring and analysis
     */
    public void forwardToController(PerflowKey key) {}

    /**
     *  This could be used for central storage
      */
    public void forwardToAHost(PerflowKey key) {}


    /**
     *  Forward to a middlebox with single path
     */
    public void forwardToMiddlebox (PerflowKey key, Middlebox mb)  { }

    /**
     *  Forward to a middlebox with multipath
     */

    public void forwardMPToMiddlebox (PerflowKey key, Middlebox mb) {}

    /**
     *  Forward to multiple middleboxes (could be used for split/merge case)
     */
    public void forwardToMiddleboxes (PerflowKey key, List <Middlebox> mbList) {}
}

