package fr.jeci.alfresco.saod.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.opencsv.CSVWriter;

import fr.jeci.alfresco.saod.ConcurrentRunSaodException;
import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.StringUtil;
import fr.jeci.alfresco.saod.pojo.PrintNode;
import fr.jeci.alfresco.saod.service.SaodService;

/**
 * Class of HomeController
 */
@Controller
public class HomeController implements ErrorController {
	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Value("${description}")
	private String title = "Jeci SAOD";

	@Value("${version}")
	private String version = "0.0.0";

	@Value("${saod.sort.default}")
	private String defaultSort = "none";

	@Value("${saod.sort.lang}")
	private String lang = "us";

	@Autowired
	private SaodService saodService;

	@Autowired
	private ErrorAttributes errorAttributes;

	@Autowired
	private MessageSource messageSource;

	// extension du fichier à exporter
	private final String EXTENSION = ".csv";
	private final char DEFAULT_CHARACTER = ';';
	// type
	private final String DIRECTORY = "Directory";
	private final String FILE = "File";
	// private final String BOTH = "Both";

	@RequestMapping(value = { "", "/", "/init" })
	/**
	 * Update a given model
	 * 
	 * @param model
	 * @return "home"
	 */
	public String home(Model model) {
		model.addAttribute("time", new Date());
		model.addAttribute("duration", this.saodService.lastRunMessage());
		model.addAttribute("title", this.title);
		model.addAttribute("version", this.version);
		model.addAttribute("running", this.saodService.isRunning());

		return "home";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@RequestMapping("/access")
	public String access(Model model) {
		return "access";
	}

	// RESTful method
	@RequestMapping(value = "/init", method = RequestMethod.POST, produces = { "application/xml", "application/json" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Map<String, Serializable> compute() {
		long start = System.currentTimeMillis();

		Map<String, Serializable> output = new HashMap<>(1);
		try {
			saodService.loadDataFromAlfrescoDB();
			LOG.info("END - Load Data From Alfresco DB _ Duration : {} ms", (System.currentTimeMillis() - start));
			output.put("duration", System.currentTimeMillis() - start);

		} catch (ConcurrentRunSaodException e) {

			output.put("since",
					StringUtil.format(this.messageSource, "saod.service.last-run-message.running", e.getSince()));
		} catch (SaodException e) {
			output.put("error", e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}

		return output;
	}

	/**
	 * Function to permit to load informations
	 * 
	 * @throws IOException
	 * @throws SaodException
	 */
	@RequestMapping(value = "/export", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "text/csv" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody void export(HttpServletResponse response,
			@RequestParam(value = "nodeid", required = false, defaultValue = "") String nodeid,
			@RequestParam(value = "type", required = true) String type) throws IOException, SaodException {
		Long start = System.currentTimeMillis();
		// set file name and content type and permit to download it
		String typeFichier = type;
		String filename = saodService.loadPrintNode(nodeid).getLabel() + EXTENSION;
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		// creation of list of all file since a specific location
		List<PrintNode> nodes = saodService.getExport(nodeid, typeFichier);

		// create CSV
		CSVWriter csvWriter = new CSVWriter(response.getWriter(), DEFAULT_CHARACTER, CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

		// --- add header for CSV
		String headerRecord = "";
		if (FILE.equals(type)) {
			headerRecord = "Name"+DEFAULT_CHARACTER+"FullSize"+DEFAULT_CHARACTER+"FullPath";
		} else if (DIRECTORY.equals(type)) {
			headerRecord = "Name"+DEFAULT_CHARACTER+"FullSize";
		} else {
			headerRecord = "Type"+DEFAULT_CHARACTER+"Name"+DEFAULT_CHARACTER+"FullSize"+DEFAULT_CHARACTER+"FullPath";
		}
		csvWriter.writeNext(headerRecord.split(";"));
		// --- transform nodes in string content by loading informations
		for (PrintNode pn : nodes) {
			String contentRecord = "";
			// mettre un switch
			if (FILE.equals(type)) {
				contentRecord=pn.getLabel()+DEFAULT_CHARACTER+pn.getFullSize().toString()+DEFAULT_CHARACTER+saodService.computePath(nodeid, pn.getNodeid().toString(), "/");
			} else if (DIRECTORY.equals(type)) {
				contentRecord=pn.getLabel()+DEFAULT_CHARACTER+pn.getFullSize().toString();
			} else {
				contentRecord=pn.getType()+DEFAULT_CHARACTER+pn.getLabel()+DEFAULT_CHARACTER+pn.getFullSize().toString()+DEFAULT_CHARACTER+saodService.computePath(nodeid, pn.getNodeid().toString(), "/");
			}
			csvWriter.writeNext(contentRecord.split(";"));
		}
		csvWriter.close();
		LOG.info("Export Done ! Time : "+(System.currentTimeMillis()-start)+"ms");
	}

	@RequestMapping(value = "/init", method = RequestMethod.POST)
	@Secured("ROLE_ADMIN")
	public String init(Model model) {
		long start = System.currentTimeMillis();
		model.addAttribute("time", new Date());
		model.addAttribute("title", this.title);
		model.addAttribute("version", this.version);

		try {
			saodService.loadDataFromAlfrescoDB();
			LOG.info("END - Load Data From Alfresco DB _ Duration : {} ms", (System.currentTimeMillis() - start));
			model.addAttribute("duration",
					String.format("Compute done in %s ms", (System.currentTimeMillis() - start)));

		} catch (ConcurrentRunSaodException e) {
			model.addAttribute("duration", e.getMessage());
		} catch (SaodException e) {
			model.addAttribute("error", e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}

		return "home";
	}

	@RequestMapping("/print")
	@Secured("ROLE_USER")
	public String print(@RequestParam(value = "nodeid", required = false, defaultValue = "") String nodeid,
			@RequestParam(value = "sort", required = false, defaultValue = "") String sort, Model model) {
		model.addAttribute("time", new Date());
		model.addAttribute("version", this.version);

		Comparator<PrintNode> pnComparator = selectComparator(sort);

		try {
			long start = System.currentTimeMillis();

			if (StringUtils.hasText(nodeid) && Integer.decode(nodeid) > 0) {
				printFolder(nodeid, model, pnComparator);
			} else {
				printRoots(model);
			}
			LOG.info("Print duration : {}", (System.currentTimeMillis() - start));
		} catch (SaodException e) {
			model.addAttribute("error", e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}

		return "print";
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW, readOnly = true, timeout = 10)
	private void printRoots(Model model) throws SaodException {
		model.addAttribute("title", "Roots");
		model.addAttribute("nodes", this.saodService.getRoots());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW, readOnly = true, timeout = 10)
	private void printFolder(String nodeid, Model model, Comparator<PrintNode> pnComparator) throws SaodException {
		model.addAttribute("dir", this.saodService.loadPrintNode(nodeid));
		model.addAttribute("title", String.format("%s", nodeid));
		List<PrintNode> subFolders = this.saodService.getSubFolders(nodeid);
		if (pnComparator != null) {
			Collections.sort(subFolders, pnComparator);
		}
		model.addAttribute("nodes", subFolders);

		String path = this.saodService.computePath(nodeid);
		model.addAttribute("path", path);
	}

	@RequestMapping("/error")
	public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Map<String, Object> errorAttributes2 = errorAttributes.getErrorAttributes(requestAttributes, false);

		model.addAllAttributes(errorAttributes2);
		model.addAttribute("attr", errorAttributes2.entrySet());

		return getErrorPath();
	}

	@Override
	public String getErrorPath() {
		return "error";
	}

	private Collator collactor() {
		Locale langTag = (lang == null) ? Locale.US : Locale.forLanguageTag(lang);
		Collator collator = Collator.getInstance(langTag);
		collator.setStrength(Collator.PRIMARY);
		return collator;
	}

	private Comparator<PrintNode> selectComparator(final String psort) {
		Comparator<PrintNode> pnComparator;

		String[] sort;
		if (psort == null || !StringUtils.hasText(psort)) {
			sort = this.defaultSort.split("_");
		} else {
			sort = psort.split("_");
		}
		boolean reverse = sort.length == 2 && "r".equals(sort[1]);

		switch (sort[0]) {
		case "name":
			pnComparator = (o1, o2) -> collactor().compare(o1.getLabel(), o2.getLabel());
			if (reverse) {
				pnComparator = pnComparator.reversed();
			}
			break;

		case "local":
			pnComparator = (o1, o2) -> o1.getLocalSize().compareTo(o2.getLocalSize()) * -1;
			if (reverse) {
				pnComparator = pnComparator.reversed();
			}
			break;

		case "aggregate":
			pnComparator = (o1, o2) -> o1.getDirSize().compareTo(o2.getDirSize()) * -1;
			if (reverse) {
				pnComparator = pnComparator.reversed();
			}
			break;

		case "full":
			pnComparator = (o1, o2) -> o1.getFullSize().compareTo(o2.getFullSize()) * -1;
			if (reverse) {
				pnComparator = pnComparator.reversed();
			}
			break;

		case "none":
		default:
			pnComparator = null;
			break;
		}

		return pnComparator;
	}

}
