package dev.anarchy.translate.runner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.runtime.parser.ParseException;

import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.condition.Condition;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.translate.util.JSONUtils;
import freemarker.template.TemplateException;

public abstract class ServiceChainRunner {
	private DServiceChain serviceChain;
	
	private DRouteElementI currentElement;
	
	private boolean chainCanUseMocks;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}
	
	/**
	 * Sets whether the chain can use service definition mocks.
	 * Mocks are used to return data from a service definition without actually invoking the service.
	 */
	public void setCanUseMocks(boolean canUseMocks) {
		this.chainCanUseMocks = canUseMocks;
	}
	
	/**
	 * Run a data payload through the chain.
	 * @return The result of the chain.
	 * @throws TemplateException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String, Object> run(Map<String, Object> inputPayload) throws ServiceChainRunnerException {
		if ( inputPayload == null )
			inputPayload = new HashMap<>();
		
		currentElement = this.serviceChain;
		
		List<DRouteElementI> routes = serviceChain.getRoutesUnmodifyable();
		
		while(currentElement != null) {
			
			ServiceChainRunnerException exception = null;
			boolean passedCondition = true;

			// Transformations
			Map<String, Object> output = inputPayload;
			
			try {
				// Transform w/ template
				if (currentElement instanceof DServiceDefinition)
					output = transformSingle(currentElement, inputPayload, chainCanUseMocks);
				
				// Condition
				passedCondition = handleCondition(routes, inputPayload, currentElement);
				
				// Augment maybe
				if ( currentElement instanceof DServiceDefinition && !StringUtils.isEmpty(((DServiceDefinition)currentElement).getAugmentPayload()) ) {
					inputPayload.put(((DServiceDefinition)currentElement).getAugmentPayload(), output);
				} else {
					inputPayload = output;
				}
			} catch (Exception e) { 
				exception = new ServiceChainRunnerException(e, currentElement);
			}
			
			// If we got an exception throw it
			if ( exception != null )
				throw exception;
			
			// Move to next node
			currentElement = RouteHelper.getNextRoute(routes, currentElement, passedCondition);
		}
		
		return inputPayload;
	}

	protected boolean handleCondition(List<DRouteElementI> routes, Map<String, Object> inputPayload, DRouteElementI element) {
		if (currentElement instanceof DConditionElement) {
			Condition condition = new Condition(((DConditionElement)currentElement).getConditionMeta().getCondition());
			return condition.evaluate(inputPayload);
		}
		
		if ( currentElement instanceof DServiceDefinition && !StringUtils.isEmpty(((DServiceDefinition)currentElement).getCondition())) {
			Condition condition = new Condition(((DServiceDefinition)currentElement).getCondition());
			return condition.evaluate(inputPayload);
		}
		
		return true;
	}

	/**
	 * Simple method to transform a single payload from the chain.
	 * @throws TemplateException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String, Object> transformSingle(DRouteElementI currentElement, Map<String, Object> inputPayload, boolean canUseMockResponese) throws ParseException, IOException, TemplateException {
		Map<String, Object> output;
		
		// Template transformation
		output = currentElement.transform(inputPayload);
		
		// Perhaps another transform is wanted
		output = onInvokeRouteElement(currentElement, output);
		
		// Mock (highest priority)
		if ( canUseMockResponese && currentElement instanceof DServiceDefinition ) {
			DServiceDefinition serviceDef = (DServiceDefinition)currentElement;
			if ( !StringUtils.isEmpty(serviceDef.getMockResponse()) ) {
				Map<String, Object> mockOutput = JSONUtils.jsonToMap(serviceDef.getMockResponse());
				if ( mockOutput != null )
					output = mockOutput;
			}
		}
		
		return output;
	}

	/**
	 * callback method whenever a route element is invoked during transformation.
	 */
	protected abstract Map<String, Object> onInvokeRouteElement(DRouteElementI routeElement, Map<String, Object> input);
}
