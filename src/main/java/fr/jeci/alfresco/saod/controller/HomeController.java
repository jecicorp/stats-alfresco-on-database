package fr.jeci.alfresco.saod.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@Secured("ROLE_ADMIN")
	public String init(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("title", this.title);
		model.put("version", this.version);

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

	@RequestMapping("/print")
	@Secured("ROLE_USER")
	public String print(@RequestParam(value = "nodeid", required = false, defaultValue = "") String nodeid,
			Map<String, Object> model) {
		model.put("time", new Date());
		model.put("version", this.version);

		try {
			long start = System.currentTimeMillis();

			if (StringUtils.hasText(nodeid)) {
				model.put("dir", this.saodService.loadPrintNode(nodeid));
				model.put("title", String.format("-= %s =-", nodeid));
				model.put("nodes", this.saodService.getSubFolders(nodeid));

				String path = this.saodService.computePath(nodeid);
				model.put("path", path);
			} else {
				model.put("title", String.format("-= Racines =-", nodeid));
				model.put("nodes", this.saodService.getRoots());
			}
			LOG.info("Duration : " + (System.currentTimeMillis() - start));
		} catch (SaodException e) {
			model.put("error", e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}

		return "print";
	}

}
