package fr.jeci.alfresco.saod.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.service.SaodService;

@Controller
public class HomeController {
	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Value("${description}")
	private String title = "Jeci SAOD";

	@Value("${version}")
	private String version = "0.0.0";

	@Autowired
	private SaodService saodService;

	@RequestMapping("/")
	public String home(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("title", this.title);
		model.put("version", this.version);
		//
		// try {
		// model.put("selectDirLocalSize", alfrescoDao.selectDirLocalSize());
		// } catch (SaodException e) {
		// model.put("error", e.getLocalizedMessage());
		// LOG.error(e.getMessage(), e);
		// }

		return "home";
	}

	@RequestMapping("/init")
	public String init(Map<String, Object> model) {

		try {
			long start = System.currentTimeMillis();
			saodService.loadDataFromAlfrescoDB();
			LOG.info("Duration : " + (System.currentTimeMillis() - start));
		} catch (SaodException e) {
			model.put("error", e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}
		return "home";
	}

}
