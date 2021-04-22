package com.test;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

/**
 * es 客户端提供类，对 es 连接做简单的封装
 */
public class EsClientProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsClientProvider.class);

	private TransportClient client = null;
	private volatile boolean inited = false;
	private String cluster_name;
	private String path_name;
	private boolean searchguard_ssl_transport_enabled;
	private String KEYSTORE_FILEPATH;
	private String TRUSTSTORE_FILEPATH;
	private String KEYSTORE_PASSWORD;
	private String TRUSTSTORE_PASSWORD;
	private boolean enforce_hostname_verification;
	private boolean resolve_hostname;
	private boolean client_transport_sniff;
	private String transport_addresses;

	public TransportClient get() {
		return this.client;
	}

	@PreDestroy
	public synchronized void close() {
		if (this.client != null) {
			this.client.close();
		}
	}

	@PostConstruct
	public synchronized TransportClient init() {
		//如果是true
		if (!inited) {
			try {
				Map<String, String> config = XmlProperties.loadFromXml("elasticsearch.xml");
				if (config == null) {
					LOGGER.error("load xml err");
					System.out.println("配置文件为空！");
					return null;
				}
				Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, String> next = iterator.next();
					if (next.getKey().equals("cluster.name")) {
						cluster_name = next.getValue();
					}
					if (next.getKey().equals("path.home")) {
						path_name = next.getValue();
					}
//					if (next.getKey().equals("searchguard.ssl.transport.enabled")) {
//						searchguard_ssl_transport_enabled = Boolean.parseBoolean(next.getValue());
//					}
//					if (next.getKey().equals("SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH")) {
//						KEYSTORE_FILEPATH = next.getValue();
//					}
//					if (next.getKey().equals("SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH")) {
//						TRUSTSTORE_FILEPATH = next.getValue();
//					}
//					if (next.getKey().equals("SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD")) {
//						KEYSTORE_PASSWORD = next.getValue();
//					}
//					if (next.getKey().equals("SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD")) {
//						TRUSTSTORE_PASSWORD = next.getValue();
//					}
//					if (next.getKey().equals("searchguard.ssl.transport.enforce_hostname_verification")) {
//						enforce_hostname_verification = Boolean.parseBoolean(next.getValue());
//					}
//					if (next.getKey().equals("searchguard.ssl.transport.resolve_hostname")) {
//						resolve_hostname = Boolean.parseBoolean(next.getValue());
//					}
					if (next.getKey().equals("client.transport.sniff")) {
						client_transport_sniff = Boolean.parseBoolean(next.getValue());
					}
					if (next.getKey().equals("transport.addresses")) {
						transport_addresses = next.getValue();
					}
				}

//				Settings settings = Settings.builder().put("path.home", path_name).put("cluster.name", cluster_name)
//						.put("searchguard.ssl.transport.enabled", searchguard_ssl_transport_enabled)
//						.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH, KEYSTORE_FILEPATH)
//						.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH, TRUSTSTORE_FILEPATH)
//						.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD, KEYSTORE_PASSWORD)
//						.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD)
//						.put("searchguard.ssl.transport.enforce_hostname_verification", enforce_hostname_verification)
//						.put("searchguard.ssl.transport.resolve_hostname", resolve_hostname)
//						.put("client.transport.sniff", client_transport_sniff).build();
//
//				TransportClient client = new PreBuiltTransportClient(settings, SearchGuardPlugin.class);
				
				
				
				Settings settings = Settings.builder().put("path.home", path_name).put("cluster.name", cluster_name)
						.put("client.transport.sniff", client_transport_sniff).build();

				TransportClient client = new PreBuiltTransportClient(settings);
				
						
						
				this.client = client;

				String[] addresses = config.get("transport.addresses").split(",");
				for (String address : addresses) {
					String[] hostAndPort = address.split(":");
					client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostAndPort[0]),
							Integer.valueOf(hostAndPort[1])));

				}
				this.inited = true;
				System.out.println("初始化完成");
			} catch (UnknownHostException e) {
				LOGGER.error(String.format("init search client err:=>msg:[%s]", e.getMessage()), e);
				if (client != null) {
					this.client.close();
				}
			}
		}
		return client;
	}

	public TransportClient getClient() {
		return client;
	}

	public void setClient(TransportClient client) {
		this.client = client;
	}

	public String getCluster_name() {
		return cluster_name;
	}

	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}

	public String getPath_name() {
		return path_name;
	}

	public void setPath_name(String path_name) {
		this.path_name = path_name;
	}

	public boolean isSearchguard_ssl_transport_enabled() {
		return searchguard_ssl_transport_enabled;
	}

	public void setSearchguard_ssl_transport_enabled(boolean searchguard_ssl_transport_enabled) {
		this.searchguard_ssl_transport_enabled = searchguard_ssl_transport_enabled;
	}

	public String getKEYSTORE_FILEPATH() {
		return KEYSTORE_FILEPATH;
	}

	public void setKEYSTORE_FILEPATH(String kEYSTORE_FILEPATH) {
		KEYSTORE_FILEPATH = kEYSTORE_FILEPATH;
	}

	public String getTRUSTSTORE_FILEPATH() {
		return TRUSTSTORE_FILEPATH;
	}

	public void setTRUSTSTORE_FILEPATH(String tRUSTSTORE_FILEPATH) {
		TRUSTSTORE_FILEPATH = tRUSTSTORE_FILEPATH;
	}

	public String getKEYSTORE_PASSWORD() {
		return KEYSTORE_PASSWORD;
	}

	public void setKEYSTORE_PASSWORD(String kEYSTORE_PASSWORD) {
		KEYSTORE_PASSWORD = kEYSTORE_PASSWORD;
	}

	public String getTRUSTSTORE_PASSWORD() {
		return TRUSTSTORE_PASSWORD;
	}

	public void setTRUSTSTORE_PASSWORD(String tRUSTSTORE_PASSWORD) {
		TRUSTSTORE_PASSWORD = tRUSTSTORE_PASSWORD;
	}

	public boolean isEnforce_hostname_verification() {
		return enforce_hostname_verification;
	}

	public void setEnforce_hostname_verification(boolean enforce_hostname_verification) {
		this.enforce_hostname_verification = enforce_hostname_verification;
	}

	public boolean isResolve_hostname() {
		return resolve_hostname;
	}

	public void setResolve_hostname(boolean resolve_hostname) {
		this.resolve_hostname = resolve_hostname;
	}

	public boolean isClient_transport_sniff() {
		return client_transport_sniff;
	}

	public void setClient_transport_sniff(boolean client_transport_sniff) {
		this.client_transport_sniff = client_transport_sniff;
	}

	public String getTransport_addresses() {
		return transport_addresses;
	}

	public void setTransport_addresses(String transport_addresses) {
		this.transport_addresses = transport_addresses;
	}

}
