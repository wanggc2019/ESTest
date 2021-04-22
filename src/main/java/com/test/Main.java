package com.test;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class Main {

	private static TransportClient client;
	private static XContentBuilder doc = null;

	static {
		client = new EsClientProvider().init();
	}
	
	public static void main(String[] args) {
		EsClientProvider esClientProvider = new EsClientProvider();
		esClientProvider.getClient();
	}

}
