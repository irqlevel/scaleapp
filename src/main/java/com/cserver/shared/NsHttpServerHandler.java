package com.cserver.shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;

class NsHttpHandlerExecTask implements Runnable {
	private static final String TAG = "NsHttpHandlerExecTask";
	private NsHttpServerHandler handler = null;
	private ChannelHandlerContext ctx = null;
	private NsHttpRequest request = null;
	
	public NsHttpHandlerExecTask(NsHttpServerHandler handler, ChannelHandlerContext ctx, NsHttpRequest request) {
		this.handler = handler;
		this.ctx = ctx;
		this.request = request;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			handler.complete(request, handler.handle(request), ctx);
		} catch (Throwable t) {
			SLog.throwable(TAG, t);
			handler.exceptionCaught(ctx, t);
		}
	}
}

public class NsHttpServerHandler extends ChannelInboundHandlerAdapter { // (1)

    private static final String TAG = "NsHttpServerHandler";
    private INsHttpServerHandler handler = null;
    private ExecutorService exec = null;
    
    public NsHttpServerHandler(INsHttpServerHandler handler, ExecutorService exec) {
    	this.handler = handler;
    	this.exec = exec;
    }
    
    public void complete(NsHttpRequest request, NsHttpResponse response, ChannelHandlerContext ctx) throws IOException {
    	HttpResponse rawResponse = response.getResponse();
    	boolean keepAlive = HttpHeaders.isKeepAlive(request.getRequest());
    	
    	if (keepAlive)
    		rawResponse.headers().set(Names.CONNECTION, Values.KEEP_ALIVE);
    	
    	ChannelFuture future = ctx.writeAndFlush(rawResponse);
    	if (!keepAlive) {
    		future.addListener(ChannelFutureListener.CLOSE);
    	}
    }
    
    public NsHttpResponse handle(NsHttpRequest request) {
    	return this.handler.handle(request);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { // (2)
        // Discard the received data silently.
    	if (msg instanceof HttpRequest) {
	    	HttpRequest req = (HttpRequest) msg;
	        if (HttpHeaders.is100ContinueExpected(req)) {
	        	ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
	        }
	    	this.exec.submit(new NsHttpHandlerExecTask(this, ctx, new NsHttpRequest(req)));
    	}
    }
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
    	
        SLog.throwable(TAG, cause);
        ctx.close();
    }
}