package com.developerhelperhub.ms.id.model.monitor;

import java.util.List;

import com.developerhelperhub.ms.id.model.ApplicationMonitorModel.Matric;

import lombok.Data;

@Data
public class MatricGroupModel implements Comparable<MatricGroupModel> {

	private int order;
	private String name;
	private String display;
	private List<MatricModel> matrics;

	public int compareTo(MatricGroupModel o) {
		return this.order - o.order;
	};
}