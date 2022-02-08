package dev.anarchy.translate.util;

import dev.anarchy.translate.type.freemarker.FreemarkerTranslateService;
import dev.anarchy.translate.type.velocity.VelocityTranslateService;

public enum TranslateType {
	VELOCITY(VelocityTranslateService.class, "Velocity"),
	FREEMARKER(FreemarkerTranslateService.class, "Freemarker");
	
	private Class<?> clazz;
	
	private String name;

	TranslateType(Class<?> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Class<?> getTranslateClass() {
		return this.clazz;
	}
	
	public static TranslateType match(String name) {
		for (TranslateType type : TranslateType.values()) {
			if ( type.toString().equalsIgnoreCase(name) ) {
				return type;
			}
		}
		
		return null;
	}
}
