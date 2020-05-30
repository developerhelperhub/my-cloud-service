package com.developerhelperhub.ms.id.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxReadingExample {

	public static void main(String[] args) {
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:56573/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			System.out.println("\nDomains:");
			String domains[] = mbsc.getDomains();

			for (String domain : domains) {
				System.out.println("\tDomain = " + domain);
			}

			System.out.println("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

			System.out.println("\nMBean count = " + mbsc.getMBeanCount());
			System.out.println("\nQuery MBeanServer MBeans:");
			Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));

			for (ObjectName name : names) {
				System.out.println("\tObjectName = " + name);
			}

			ObjectName mbeanName = readData(mbsc, "org.springframework.boot:type=Endpoint,name=Info");

			Map data = (Map) mbsc.invoke(mbeanName, "info", null, null);

			System.out.println(data);

			mbeanName = readData(mbsc, "org.springframework.boot:type=Endpoint,name=Health");

			data = (Map) mbsc.invoke(mbeanName, "health", null, null);

			System.out.println(data);

			mbeanName = readData(mbsc, "org.springframework.boot:type=Endpoint,name=Metrics");

			data = (Map) mbsc.invoke(mbeanName, "listNames", null, null);

			System.out.println(data);

			mbeanName = readData(mbsc, "org.springframework.boot:type=Endpoint,name=Metrics");

			data = (Map) mbsc.invoke(mbeanName, "metric", new Object[] { "jvm.memory.max" },
					new String[] { String.class.getName() });

			System.out.println(data);

			jmxc.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ObjectName readData(MBeanServerConnection mbsc, String name) throws InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, MalformedObjectNameException, IntrospectionException {
		ObjectName mbeanName = new ObjectName(name);

		MBeanInfo info = mbsc.getMBeanInfo(mbeanName);

		for (MBeanOperationInfo op : info.getOperations()) {

			System.out.println("--------------");

			System.out.println(op.getName());
			System.out.println(op.getDescription());
			System.out.println(op.getImpact());
			System.out.println(op.getReturnType());
			System.out.println(op.getDescriptor());

			for (MBeanParameterInfo par : op.getSignature()) {
				System.out.println("=========" + par.getName());
				System.out.println("-" + par.getType());
				System.out.println("-" + par.getDescriptor());
			}
		}

		return mbeanName;
	}
}
