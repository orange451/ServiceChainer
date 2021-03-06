package dev.anarchy.common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.util.FileUtils;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.ServiceChainHelper;
import dev.anarchy.translate.util.TranslateMapService;
import dev.anarchy.translate.util.TranslateType;
import freemarker.template.TemplateException;

public class RouteHelper {

	/**
	 * Given a list of route elements, link a source element to a destination element. This will clear any previously connected routes.
	 */
	public static void linkRoutes(List<DRouteElementI> allRoutes, DRouteElementI source, DRouteElement destination) {
		// Unlink anything source is connected to
		unlinkRoutes(allRoutes, source);
		
		// Link new destination
		linkRoutes(source, destination);
	}
	
	/**
	 * Unlink all routes connected TO the specified parent route.
	 */
	public static void unlinkRoutes(List<DRouteElementI> allRoutes, DRouteElementI parent) {
		// Unlink anything source is connected to
		for (DRouteElementI element : allRoutes) {
			if ( !(element instanceof DRouteElement) )
				continue;
			
			if ( element.getSourceId() != null && element.getSourceId().equals(parent.getDestinationId()) ) {
				((DRouteElement)element).setSource(null);
				((DRouteElement)element).setSourceId(null);
			}
		}
	}

	/**
	 * Link two routes together.
	 */
	public static void linkRoutes(DRouteElementI source, DRouteElement destination) {
		if ( destination == null )
			return;
		
		destination.setSource(source.getDestination());
		destination.setSourceId(source.getDestinationId());
	}
	
	/**
	 * Return a route element with the specified destination id.
	 */
	public static DRouteElementI getRoute(List<DRouteElementI> allRoutes, String destinationId) {
		for (DRouteElementI element : allRoutes) {
			if ( element.getDestinationId() != null && element.getDestinationId().equals(destinationId) ) {
				return element;
			}
		}
		
		return null;
	}
	
	/**
	 * Return a route element that is linked TO this source element.
	 * Two elements are linked when the sources destination matches the destinations source.
	 */
	public static List<DRouteElementI> getLinkedTo(List<DRouteElementI> allRoutes, DRouteElementI source) {
		List<DRouteElementI> routes = new ArrayList<>();
		
		for (DRouteElementI element : allRoutes) {
			if ( element == source )
				continue;
			
			if ( element.getSourceId() != null && element.getSourceId().equals(source.getDestinationId()) ) {
				routes.add(element);
			}
		}
		
		return routes;
	}

	/**
	 * Return a route element that is linked FROM this source element.
	 * Two elements are linked when the sources destination matches the destinations source.
	 */
	public static DRouteElementI getLinkedFrom(List<DRouteElementI> allRoutes, DRouteElementI destination) {
		for (DRouteElementI element : allRoutes) {
			if ( element == destination )
				continue;
			
			if ( element.getDestinationId() != null && element.getDestinationId().equals(destination.getSourceId()) ) {
				return element;
			}
		}
		
		return null;
	}

	/**
	 * Transform the input payload based on a service definitions configuration
	 * @throws TemplateException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static Map<String, Object> transform(DServiceDefinition serviceDefinition, Map<String, Object> inputPayload) throws ParseException, IOException, TemplateException {
		TranslateType tType = TranslateType.match(serviceDefinition.getTransformationType());
		String json = JSONUtils.mapToJson(inputPayload);
		String output = new TranslateMapService().translate(tType, serviceDefinition.getTemplateContent(), json);
		return JSONUtils.jsonToMap(output);
	}
	
	/**
	 * Export a list of service chains to a specified output file.
	 */
	public static void export(List<DServiceChain> serviceChains, File outputFile, boolean stripMetadata) {
        DCollection collection = new DCollection();
        collection.setName(FileUtils.getFileNameFromPathWithoutExtension(outputFile.getName()));
        
        // Update ExtensionhandlerRouteId
        for (DServiceChain chain : serviceChains) {
        	int routeId = 0;
        	for (DRouteElementI routeElement : chain.getRoutesUnmodifyable()) {
        		if ( routeElement instanceof DServiceDefinition ) {
            		((DServiceDefinition)routeElement).setExtensionHandlerRouteId("ExtensionRoute" + routeId);
            		routeId += 1;
        		}
        	}
        }
        
        // Add service chains to collection
        for (DServiceChain chain : serviceChains) {
        	DServiceChain packedChain = chain.clone();
        	ServiceChainHelper.pack(packedChain);
        	collection.addChild(packedChain);
        }

        // Write to file
        if (outputFile != null) {
            try {
        		ObjectMapper objectMapper = new ObjectMapper();
        		String content = objectMapper.writeValueAsString(collection);
        		String trimmed = processExpotedJson(content, stripMetadata);
                PrintWriter writer;
                writer = new PrintWriter(outputFile);
                writer.println(trimmed);
                writer.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
	
	public static void disconnectCondition(List<DRouteElementI> routes, DConditionElement element) {
		for (DRouteElementI t : RouteHelper.getLinkedTo(routes, element)) {
			if ( t instanceof DServiceDefinition ) {
				((DServiceDefinition)t).setSource(null);
				((DServiceDefinition)t).setSourceId(null);
			}
		}
		
		element.setPassRouteId(null);
		element.setFailRouteId(null);
	}
	
	public static void connectCondition(DConditionElement condition, DRouteElement element, boolean passNode) {
		element.setSource(condition.getDestination());
		element.setSourceId(condition.getDestinationId());
		
		if ( passNode ) {
			condition.setPassRouteId(element.getDestination());
		} else {
			condition.setFailRouteId(element.getDestinationId());
		}
	}

	/**
	 * Perform additional processing on export json. This involves stripping metadata if requested, and prettifying.
	 */
	private static String processExpotedJson(String json, boolean stripMetadata) throws JsonProcessingException {
		Map<String, Object> data = JSONUtils.jsonToMap(json);
		
		if ( stripMetadata )
			data = removeInternalData(data);
		
		return JSONUtils.mapToJsonPretty(data);
	}

	/**
	 * Remove internal metadata from a json object.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> removeInternalData(Map<String, Object> data) {
		Map<String, Object> ret = new HashMap<>();
		
		for (Entry<String, Object> entry : data.entrySet()) {
			if ( entry.getKey().startsWith("_") ) 
				continue;
			
			if ( entry.getValue() != null && entry.getValue() instanceof Map ) {
				ret.put(entry.getKey(), removeInternalData((Map<String, Object>) entry.getValue()));
			} else if ( entry.getValue() != null && entry.getValue() instanceof List ) {
				List<Object> newList = new ArrayList<>();
				for (Object o : (List<Object>)entry.getValue()) {
					if ( o instanceof Map )
						newList.add(removeInternalData((Map<String, Object>)o));
						else
					newList.add(o);
				}
				ret.put(entry.getKey(), newList);
			} else {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}
	
	/**
	 * Recursively get all service chain objects within a folder element.
	 */
	public static List<DServiceChain> getServiceChains(DFolderElement root) {
		List<DServiceChain> list = new ArrayList<>();
		
		if ( root instanceof DFolder ) {
			for (DFolderElement o : ((DFolder)root).getChildrenUnmodifyable()) {
				List<DServiceChain> newChains = getServiceChains(o);
				for (DServiceChain chain : newChains)
					list.add(chain);
			}
		} else if ( root instanceof DServiceChain ) {
			list.add((DServiceChain) root);
		}
		
		return list;
	}

	public static DRouteElementI getNextRoute(List<DRouteElementI> routes, DRouteElementI currentElement) {
		return getNextRoute(routes, currentElement, true);
	}

	public static DRouteElementI getNextRoute(List<DRouteElementI> routes, DRouteElementI currentElement, boolean passedCondition) {
		if ( currentElement instanceof DConditionElement ) {
			if ( !passedCondition ) {
				return getRoute(routes, ((DConditionElement) currentElement).getFailRouteId());
			} else {
				return getRoute(routes, ((DConditionElement) currentElement).getPassRouteId());
			}
		}
		
		if ( currentElement instanceof DServiceDefinition ) {
			if ( !passedCondition ) {
				return null; // TODO how to handle this??
			}
		}
		
		List<DRouteElementI> nextRoutes = getLinkedTo(routes, currentElement);
		return nextRoutes.size() > 0 ? nextRoutes.get(0) : null;
	}
}
