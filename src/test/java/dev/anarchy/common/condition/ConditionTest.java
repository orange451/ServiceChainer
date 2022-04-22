package dev.anarchy.common.condition;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConditionTest extends TestCase {
    public ConditionTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( ConditionTest.class );
    }

	public void testApp() {
		Map<String, Object> fruits = new HashMap<>();
		fruits.put("Apple", "apple");
		fruits.put("Banana", "banana");
		fruits.put("Orange", "orange");
    	fruits.put("Watermelon", "watermelon");

		Map<String, Object> orderType = new HashMap<>();
		orderType.put("OrderTypeId", "CallCenter Order");

		Map<String, Object> order = new HashMap<>();
		order.put("OrderId", "ASDF1234");
		order.put("IsOnHold", false);
		order.put("OrderType", orderType);

		Map<String, Object> inputPayload = new HashMap<>();
		inputPayload.put("Foo", "ASDF");
		inputPayload.put("Bar", "123");
		inputPayload.put("Fruits", fruits);
		inputPayload.put("Order", order);

		assertTrue(new Condition("Foo == 'ASDF' && Bar == '123'").evaluate(inputPayload));
		assertFalse(new Condition("Foo == 'XYZW' && col2 == '234'").evaluate(inputPayload));
		assertTrue(new Condition("['apple', 'banana', 'orange'] contains Fruits.Apple").evaluate(inputPayload));
		assertFalse(new Condition("['apple', 'banana', 'orange'] contains Fruits.Watermelon").evaluate(inputPayload));
		assertTrue(new Condition("Order.OrderType.OrderTypeId == 'CallCenter Order' || Order.OrderType.OrderTypeId == 'Ecom Order'").evaluate(inputPayload));
		assertFalse(new Condition("Order.OrderType.OrderTypeId == 'Ecom Order'").evaluate(inputPayload));
	}
}
