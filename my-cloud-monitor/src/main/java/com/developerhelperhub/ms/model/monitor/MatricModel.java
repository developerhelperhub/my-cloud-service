package com.developerhelperhub.ms.model.monitor;

import lombok.Data;

@Data
public class MatricModel implements Comparable<MatricModel> {

	private long timestamp;
	private String time;
	private long value;

	@Override
	public int compareTo(MatricModel o) {
		return (int) (this.timestamp - o.timestamp);
	}

}