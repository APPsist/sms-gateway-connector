package de.appsist.service.sms.connector;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Connector for the SMS gateway service.
 * 
 * @author simon.schwantzer(at)im-c.de
 */
public class SMSGatewayConnector {
	public static final String DEFAULT_SERVICE_ID = "appsist:service:sms";
	
	private final EventBus eventBus;
	private final String address;
	
	/**
	 * Creates a new service connector.
	 * @param eventBus Event bus for sending and receiving messages.
	 * @param serviceId Event bus address of the SMS gateway service, usually {@link SMSGatewayConnector#DEFAULT_SERVICE_ID}.
	 */
	public SMSGatewayConnector(EventBus eventBus, String serviceId) {
		this.eventBus = eventBus;
		this.address = serviceId;
	}
	
	/**
	 * Send a SMS message.
	 * @param to Mobile phone number, e.h. +4916518375921
	 * @param text Text to send as SMS message.
	 * @param resultHandler Result handler to check if the operation succeeded. May be <code>null</code>.
	 */
	public void sendMessage(String to, String text, final AsyncResultHandler<Void> resultHandler) {
		JsonObject request = new JsonObject()
			.putString("action", "sendMessage")
			.putString("to", to)
			.putString("text", text);
		
		eventBus.send(address, request, new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> event) {
				final JsonObject body = event.body();
				if (resultHandler != null) resultHandler.handle(new AsyncResult<Void>() {
					
					@Override
					public boolean succeeded() {
						return "ok".equals(body.getString("status"));
					}
					
					@Override
					public Void result() {
						return null;
					}
					
					@Override
					public boolean failed() {
						return !succeeded();
					}
					
					@Override
					public Throwable cause() {
						return failed() ? new Throwable(body.getString("message")) : null;
					}
				});
			}
		});
	}}
