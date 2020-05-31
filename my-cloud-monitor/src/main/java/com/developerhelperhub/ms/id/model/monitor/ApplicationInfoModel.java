package com.developerhelperhub.ms.id.model.monitor;

import java.util.List;

import lombok.Data;

@Data
public class ApplicationInfoModel {

	private ApplicationDiskSpaceModel diskSpace;

	private List<MatricGroupModel> memory;

	private List<MatricGroupModel> buffer;

	private List<MatricGroupModel> thread;

}
