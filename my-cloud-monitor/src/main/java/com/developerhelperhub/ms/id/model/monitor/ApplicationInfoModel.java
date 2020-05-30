package com.developerhelperhub.ms.id.model.monitor;

import java.util.List;

import com.developerhelperhub.ms.id.model.ApplicationMonitorModel.MatricGroup;

import lombok.Data;

@Data
public class ApplicationInfoModel {

	private List<MatricGroup> memory;

	private List<MatricGroup> buffer;

	private List<MatricGroup> thread;

}
