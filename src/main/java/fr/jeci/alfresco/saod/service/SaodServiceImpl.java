package fr.jeci.alfresco.saod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.sql.AlfrescoDao;
import fr.jeci.alfresco.saod.sql.LocalDao;

@Component
public class SaodServiceImpl implements SaodService {

	@Autowired
	private AlfrescoDao alfrescoDao;

	@Autowired
	private LocalDao localDao;

	@Override
	public void loadDataFromAlfrescoDB() throws SaodException {
		Map<Long, Long> selectDirLocalSize = this.alfrescoDao.selectDirLocalSize();
		this.localDao.insertStatsDirLocalSize(selectDirLocalSize);

		List<Long> list = new ArrayList<>();
		list.addAll(selectDirLocalSize.keySet());
		Map<Long, Long> selectParentNodeId = this.alfrescoDao.selectParentNodeId(list);
		this.localDao.updateParentNodeId(selectParentNodeId);
	}
}
