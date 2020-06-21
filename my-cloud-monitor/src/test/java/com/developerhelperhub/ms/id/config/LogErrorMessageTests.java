package com.developerhelperhub.ms.id.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.Data;

@SpringBootTest
public class LogErrorMessageTests {

	@Data
	public static class TestMessage {
		private String time;
		private String message;
	}

	@Test
	public void test() {
		List<TestMessage> list = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		messages.add("error0-1");
		messages.add("error0-2");
		
		messages.add("time1|message1");
		messages.add("time2|message2");
		messages.add("time3|message3");
		messages.add("error3-1");
		messages.add("error3-2");
		messages.add("error3-3");
		
		messages.add("time4|message4");
		messages.add("error4-1");
		messages.add("error4-2");
		messages.add("error4-3");
		messages.add("time5|message5");
		messages.add("error5-1");
		messages.add("error5-2");

		messages.forEach(i -> System.out.println(i));

		List<Integer> errorIndexs = new ArrayList<Integer>();

		list = IntStream.range(0, messages.size()).mapToObj(index -> {
			TestMessage message = new TestMessage();

			try {

				String[] text = messages.get(index).split("\\|");

				message.setTime(text[0]);
				message.setMessage(text[1]);

			} catch (IndexOutOfBoundsException e) {
				errorIndexs.add(index);
			}
			return message;
		}).collect(Collectors.toList());

		System.out.println("---------errorIndexs: " + errorIndexs);

		if (errorIndexs.size() > 0) {
			List<TestMessage> removes = new ArrayList<>();
			
			Map<Integer, String> mapping = new HashMap<>();

			int index = errorIndexs.get(0);
			int errorFor = index;
			mapping.put(errorFor, "");

			for (int errorIndex : errorIndexs) {
				String message = "";
				
				removes.add(list.get(errorIndex));

				if (errorIndex == index) {

					message = mapping.get(errorFor);

					message = message + messages.get(errorIndex) + "\n";

					index++;

				} else {

					message = messages.get(errorIndex) + "\n";

					index = errorIndex + 1;
					errorFor = errorIndex;

				}

				mapping.put(errorFor, message);
			}

			System.out.println("---------" + mapping);

			for (Map.Entry<Integer, String> entry : mapping.entrySet()) {

				int errorIndex = entry.getKey() - 1;
				if (errorIndex < 0) {
					errorIndex = 0;
				}

				TestMessage message = list.get(errorIndex);
				
				message.setMessage(message.getMessage() + "\n" + entry.getValue());
			}
			
			list.removeAll(removes);
		}

		list.forEach(i -> System.out.println(i.getTime() + " ---- " + i.getMessage()));

	}
}
