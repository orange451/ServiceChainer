package dev.anarchy.common.condition;

import java.util.Map;

import org.mvel2.MVEL;

public class Condition {
	private String expression;
	
	public Condition(String expression) {
		this.expression = expression;
	}
	
	public boolean evaluate(Map<String, Object> inputPayload) {
		return ((Boolean) MVEL.eval(expression, inputPayload)).booleanValue();
	}
}
