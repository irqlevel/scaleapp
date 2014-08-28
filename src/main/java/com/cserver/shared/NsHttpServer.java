package com.cserver.shared;

import javax.net.ssl.SSLEngine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import java.util.concurrent.Executors;


/**
 * Discards any incoming data.
 */

public class NsHttpServer implements Runnable {
    
    private static final String TAG = "NSServer";
	private int port = -1;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ServerBootstrap b = null;
    private INsHttpServerHandler handler = null;
    private String ksPath = null;
    private String ksPass = null;
    private String keyPass = null;
    private String ksType = null;
    private Thread thread = null;
    
    public NsHttpServer(int port) {
        this.port = port;
    }
    public void setup(INsHttpServerHandler handler, String ksPath, String ksPass, String keyPass, String ksType) {
    	this.handler = handler;
    	this.ksPath = ksPath;
    	this.ksPass = ksPass;
    	this.keyPass = keyPass;
    	this.ksType = ksType;
    }
    
    public void start()
    {
    	thread = new Thread(this);
    	thread.start();
    }
    
    public void shutdown()
    {	
    	if (workerGroup != null)
        	workerGroup.shutdownGracefully();
        if (bossGroup != null)
        	bossGroup.shutdownGracefully();
        
        boolean stopped = false;
        do {
	        try {
				thread.join();
				stopped = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				SLog.exception(TAG, e);
			}
        } while (!stopped);
    }
    
    public void run() {
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 if (ksPath != null) {
                		 SSLEngine engine = SSLSocketLib.createSSLEngine(ksPath, ksPass, keyPass, ksType);
                		 ch.pipeline().addLast("ssl", new SslHandler(engine));
                	 }
                	 ch.pipeline().addLast(new HttpServerCodec());
                     ch.pipeline().addLast(new NsHttpServerHandler(handler, Executors.newCachedThreadPool()));
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 10000)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
    
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
        } catch (Exception e) {
        	SLog.exception(TAG, e);
		} finally {
        }
    }
}