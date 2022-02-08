# Service Chainer
Quickly and easily chain multiple service calls together with the ability to manipulate each input payload using templates.

# Currently Supports
- [Freemarker Template Engine](https://freemarker.apache.org/)
- [Velocity Template Engine](https://velocity.apache.org/)

# Special Template Input Constants
- **document** (Freemarker/Velocity). Represents incoming data payload json
- **JSONUtils** (Velocity). Helper class used to output Java Map to JSON.
- **Integer** (Velocity). java.lang.Integer static class
- **Double** (Velocity). java.lang.Double static class
- **Float** (Velocity). java.lang.Float static class
- **Boolean** (Velocity). java.lang.Boolean static class

Sample Template Transformation
```
import java.util.Map;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateType;

public class SampleApplication {
	
	private static final String template = "{\"user\":\"${document.Username}\"}";
	
	private static final String data = "{\"Username\":\"Hello\"}";
	
	public static void main(String[] args) {
		
		System.out.println("Template:\n" + template);
		System.out.println("Data:\n" + data);
		
		// Create Service Definition
		DServiceDefinition serviceDefinition = new DServiceDefinition();
		serviceDefinition.setTransformationType(TranslateType.FREEMARKER.getName());
		serviceDefinition.setTemplateContent(template);

		// Create a runner and test service definition
		Map<String, Object> output = null;
		try {
			BasicServiceChainRunner runner = new BasicServiceChainRunner(null);
			Map<String, Object> inputPayload = JSONUtils.jsonToMap(data);
			output = runner.transformSingle(serviceDefinition, inputPayload, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// View output
		System.out.println("Output:\n" + JSONUtils.mapToJson(output));
	}
}
```
