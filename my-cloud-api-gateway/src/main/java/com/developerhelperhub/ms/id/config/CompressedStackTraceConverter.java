package com.developerhelperhub.ms.id.config;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class CompressedStackTraceConverter extends ThrowableProxyConverter {

	@Override
	protected String throwableProxyToString(IThrowableProxy tp) {
		String original = super.throwableProxyToString(tp);
		return original.replaceAll("\n", ", ");
	}
}
