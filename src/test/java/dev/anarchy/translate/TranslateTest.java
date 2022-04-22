package dev.anarchy.translate;

import java.util.Map;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TranslateTest extends TestCase {
	
	private static final String TEMPLATE = "{\"user\":\"${document.Username}\"}";
	
	private static final String DATA = "{\"Username\":\"Hello\"}";
	
	private static final String EXPECTED_OUTPUT = "{\"user\":\"Hello\"}";
	
    public TranslateTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( TranslateTest.class );
    }

	public void testApp() {
		// Create Service Definition
		DServiceDefinition serviceDefinition = new DServiceDefinition();
		serviceDefinition.setTransformationType(TranslateType.FREEMARKER.getName());
		serviceDefinition.setTemplateContent(TEMPLATE);

		// Create a runner and test service definition
		Map<String, Object> output = null;
		try {
			Map<String, Object> inputPayload = JSONUtils.jsonToMap(DATA);
			
			BasicServiceChainRunner runner = new BasicServiceChainRunner(null);
			output = runner.transformSingle(serviceDefinition, inputPayload, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Make sure we got desired output
		assertTrue(JSONUtils.mapToJson(output).equals(EXPECTED_OUTPUT));
	}
}
