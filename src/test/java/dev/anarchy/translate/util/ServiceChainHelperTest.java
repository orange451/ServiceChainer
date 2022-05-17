package dev.anarchy.translate.util;

import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServiceChainHelperTest extends TestCase {
	
    public ServiceChainHelperTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( ServiceChainHelperTest.class );
    }

	public void testApp() {
		// Create new service chain
		DServiceChain serviceChain = new DServiceChain();
		
		// Create Service Definition
		DServiceDefinition serviceDefinition = new DServiceDefinition();
		serviceDefinition.setCondition("TestCondition = true");
		RouteHelper.linkRoutes(serviceChain.getRoutesUnmodifyable(), serviceChain, serviceDefinition);
		serviceChain.addRoute(serviceDefinition);
		
		// Pack service chain
		ServiceChainHelper.pack(serviceChain);
		
		// Ensure entry condition was packed
		assertNotNull(serviceChain.getRegisteredExtensionPoints().get(0).getEntryCondition());
	}
}
