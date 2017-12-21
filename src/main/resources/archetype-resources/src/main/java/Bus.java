#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.PublicationError;


public class Bus {

    private static MBassador bus;
    private Bus() {
        
    }
    
    public static MBassador getInstance() {
        if (bus == null) {
            bus = new MBassador(createBusConfiguration());
        }
        
        return bus;
    }
    
    private static BusConfiguration createBusConfiguration() {
        BusConfiguration config = new BusConfiguration();
        // synchronous dispatching of events
	config.addFeature(Feature.SyncPubSub.Default());
	// asynchronous dispatching of events
	config.addFeature(Feature.AsynchronousHandlerInvocation.Default());
	config.addFeature(Feature.AsynchronousMessageDispatch.Default());
        config.addPublicationErrorHandler((PublicationError error) -> {
            System.err.println(error.getMessage());
        });
        return config;
    }
}
